package cn.clickwise.clickad.feathouse;

import java.io.File;

import cn.clickwise.lib.time.TimeOpera;
import cn.clickwise.rpc.HiveFetchTableClient;
import cn.clickwise.rpc.HiveFetchTableCommand;

/**
 * 每天定时执行的任务
 * 
 * @author zkyz
 */
public class ScheduleTask {

	private ConfigureFactory confFactory;

	private int day;

	private RpcDmpInquiry rdi;

	//连接cassandra
	private Connection con;

	public void init() {
       	
		day=TimeOpera.getYesterday();
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		
		//连接用户特征cassandra库
		CassandraConfigure cassConf = confFactory.getCassandraConfigure();
		con = new Connection();
		con.setHost(cassConf.getHost());
		con.setPort(cassConf.getPort());
		con.setCfName(cassConf.getCfName());
		con.setKeySpace(cassConf.getKeySpace());
		con.setColumnName(cassConf.getColumnName());
		
		//初始化rpcdmpinquery
		rdi=new RpcDmpInquiry();
		rdi.setDay(day);
		rdi.init();
		
	}
	
	public void initHbase() {
       	
		day=TimeOpera.getYesterday();
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		
		//连接用户特征cassandra库
		HBaseConfigure hConf = confFactory.getHBaseConfigure();
		con = new Connection();
        con.setClientPort(hConf.getClientPort());
        con.setQuorum(hConf.getQuorum());
        con.setMaster(hConf.getMaster());	
		//初始化rpcdmpinquery
		rdi=new RpcDmpInquiry();
		rdi.setDay(day);
		rdi.init();
		
	}

	public void dmpInquiries() {
		
		/*
		Dmp[] dmps = confFactory.getDmps();

		for (int i = 0; i < dmps.length; i++) {
			
			HiveFetchTableClient hftc = new HiveFetchTableClient();
			cn.clickwise.rpc.Connection conrpc = new cn.clickwise.rpc.Connection();
			conrpc.setHost(dmps[i].getHost());
			conrpc.setPort(dmps[i].getRpcPort());
			conrpc.setMethod(dmps[i].getDmpInquiryMethod());

			HiveFetchTableCommand hftcmd = new HiveFetchTableCommand();
			hftcmd.setDay(day);
			hftcmd.setTmpIdentify(confFactory.getTmpIdentify());

			hftcmd.setTableName(dmps[i].getUserFeatureTableName());
			hftcmd.setKeyFieldName(dmps[i].getUidFieldName());
			//String recordFile = confFactory.getRecordFilePrefix() + day+"_"+dmps[i].getArea().getAreaCode() + ".txt";
			String recordFile=confFactory.getDmpRecordFile(day, dmps[i]);
			hftcmd.setResultName(recordFile);
			hftcmd.setResultPath(confFactory.getRecordFileDirectory()+ recordFile);
			HiveFetchTableClient.initRandomFileName(confFactory.getTmpIdentify(), day, hftcmd);
			hftcmd.setQueryType(confFactory.getQueryType());

			hftc.connect(conrpc);
			hftc.execute(hftcmd);

			rdi.writeRecFile2DataStore(new File(confFactory.getRecordFileDirectory() + recordFile), con,
					dmps[i],day);
			
		}
		*/
		
		//从各dmp取回用户特征数据存入本地文件
		try{
		rdi.fetchFromAllDmps(day);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Dmp[] dmps = confFactory.getDmps();
		for (int i = 0; i < dmps.length; i++) {
			try{
			File dmpRecFile=new File(confFactory.getRecordFileDirectory() + confFactory.getDmpRecordFile(day, dmps[i]));
			if(!(dmpRecFile.exists()))
			{
				continue;
			}
			//rdi.writeRecFile2DataStore(new File(confFactory.getRecordFileDirectory() + confFactory.getDmpRecordFile(day, dmps[i])), con,
			//		dmps[i],day);//用户特征数据写入cassandra
			System.out.println("i="+i+" "+dmpRecFile.getAbsolutePath());
			rdi.writeRecFile2DataStore(dmpRecFile, con,dmps[i],day);//用户特征数据写入cassandra
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
	}

	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}

	public static void main(String[] args)
	{
		ScheduleTask st=new ScheduleTask();
		st.initHbase();
		st.dmpInquiries();
	}
	
}
