package cn.clickwise.clickad.hbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import cn.clickwise.lib.code.MD5Code;
import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

/**
 * 根据md5(ip)+time+status查询 rowkey: IP+时间+状态 cf:column 为 rid:旧帐号 oip:旧ip
 * 
 * @author zkyz
 */
public class ITSRadiusStore extends RadiusStore {

	// 连接hadoop平台的配置
	public static Configuration configuration;
	public static HTablePool pool;
	public static String RID = "rid";
	public static String OIP = "oip";
	public static String TNAME = "hradius";
	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();
	
	static {

		configuration = HBaseConfiguration.create();

		/************ hn *****************/
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "192.168.10.103");
		configuration.set("hbase.master", "192.168.10.103:60000");
		/********************************/

		/***********
		 * local*******************
		 * configuration.set("hbase.zookeeper.property.clientPort", "2181");
		 * configuration.set("hbase.zookeeper.quorum", "192.168.110.80");
		 * configuration.set("hbase.master", "192.168.110.80:60000");
		 ************************************/

		pool = new HTablePool(configuration, 1000);
		String[] cfs = { RID, OIP };
		createTable(TNAME, cfs);

	}

	/**
	 * 表不存在才创建
	 * 
	 * @param tableName
	 */
	public static void createTable(String tableName, String[] cfs) {

		System.out.println("start create table ......");
		try {
			HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
			if (hBaseAdmin.tableExists(tableName)) {// 如果存在要创建的表，返回
				// hBaseAdmin.disableTable(tableName);
				// hBaseAdmin.deleteTable(tableName);
				System.out.println(tableName + " is exist");
				return;
			}
			HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
			for (int j = 0; j < cfs.length; j++) {
				tableDescriptor.addFamily(new HColumnDescriptor(cfs[j]));
			}
			hBaseAdmin.createTable(tableDescriptor);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end create table ......");

	}

	@Override
	public void write(String record) {

		String ip = "";
		String status = "";
		String radiusid = "";
		String time = "";

		String[] fields = record.split("\t");
		if (fields.length != 4) {
			return;
		}

		ip = fields[0];
		status = fields[1];
		radiusid = fields[2];
		time = fields[3];

		String md5ip = MD5Code.makeMD5(ip);

		String rowkey = md5ip + time.replaceAll("\\s+", "") + status;
		Put put = new Put(rowkey.getBytes());
		put.add(RID.getBytes(), "c".getBytes(),radiusid.getBytes());
		put.add(OIP.getBytes(), "c".getBytes(),ip.getBytes());

		try {
			
			pool.getTable(TNAME).put(put);
			System.err.println("add " + rowkey);	
		    pool.closeTablePool(TNAME);
		} catch (IOException e) {
			e.printStackTrace();
	
		}
	}
	/*
	public void flushTable()
	{
		try {
		    pool.closeTablePool(TNAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	@Override
	public List<String> get(String ip, String time) {

		String md5ip = MD5Code.makeMD5(ip);

		System.out.println("ip:"+ip+" time:"+time);
		String startkey = md5ip + TimeOpera.getOnedayBefore(time).replaceAll("\\s+", "")+"0";
		String endkey = md5ip + TimeOpera.getOnedayAfter(time).replaceAll("\\s+", "")+"1";
		
		List<String> rlist = new ArrayList<String>();
		try {
			Scan s=new Scan(startkey.getBytes(),endkey.getBytes());
			
			ResultScanner rs = pool.getTable(TNAME).getScanner(s);
			
			 for (Result r : rs) { 
	                System.out.println("获得到rowkey:" + new String(r.getRow())); 
	                for (KeyValue keyValue : r.raw()) { 
	                    System.out.println("列：" + new String(keyValue.getFamily()) 
	                            + "====值:" + new String(keyValue.getValue())); 
	                } 
	            } 
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rlist;
	}

	@Override
	public List<String> get(String ip) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String pollFromPond() {
		String nextElement = "";

		nextElement = queue.poll();
			
		return nextElement;
	}
	
	public void add2Pond(String record) {

		queue.offer(record);
	}
	
	public void startConsume(int threadNum) {
		
		for (int i = 0; i < threadNum; i++) {
			LineResolve fr = new LineResolve();
			Thread consumeThread = new Thread(fr);
			//consumeThread.setDaemon(true);
			consumeThread.start();
		}

	}
	
	private class LineResolve implements Runnable{

		@Override
		public void run() {

			parseRecord();
		}
		
		public void parseRecord() {

			while (true) {

				try {
					String record = pollFromPond();
					if(SSO.tioe(record))
					{
						Thread.sleep(1000);
					}
                    write(record);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage:<get or add> [<IP> <date> <time>|<thread num>]");
			System.exit(1);
		}

		String ga = args[0];

		String ip = "";
		String date = "";
		String time = "";
		int threadnum=0;
		
		if (args.length ==4) {
			ip = args[1];
			date = args[2];
			time = args[3];
		}
		else if(args.length==2)
		{
			threadnum=Integer.parseInt(args[1]);
		}
 			
		
		ITSRadiusStore its=new ITSRadiusStore();
		
		
		if (ga.equals("add")) {
			its.startConsume(threadnum);
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);

			String line = "";
            int count=0;
			try {
				while ((line = br.readLine()) != null) {
					try {
                        if(SSO.tioe(line))
                        {
                        	continue;
                        }
                        its.add2Pond(line);
                        
					} catch (Exception e) {

					}

				}
			} catch (Exception e) {

			}
		}
		else if(ga.equals("get")){
			List<String> rs=its.get(ip, date+" "+time);
			for(int j=0;j<rs.size();j++)
			{
				System.out.println(rs.get(j));
			}
		}

	}

}
