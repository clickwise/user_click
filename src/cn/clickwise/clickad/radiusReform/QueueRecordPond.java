package cn.clickwise.clickad.radiusReform;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

public class QueueRecordPond extends RecordPond {

	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();
	
	private static int zeroCount=0;
	
	private JedisPool pool;
	
	private static Logger logger = LoggerFactory.getLogger(QueueRecordPond.class);
	
    public void initForParse()
    {
    	pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1",6379);
    }
	
	@Override
	public void add2Pond(String record) {
		if(isValidRecord(record))
		{
			return;
		}
		//System.err.println("add record:"+record+" to redis");
		queue.offer(record);
	}

	@Override
	public String pollFromPond() {
		String nextElement = "";
		/*
		int queueSize=queue.size();
		
		if(queueSize!=0)
		{
			zeroCount=0;
		}
		else
		{
			zeroCount++;
		}
		*/
		//System.out.println("queue.size:"+queue.size());
		nextElement = queue.poll();
			
		return nextElement;
	}

	@Override
	public void startConsume(int threadNum) {
		initForParse();
		for (int i = 0; i < threadNum; i++) {
			FieldResolve fr = new FieldResolve();
			Thread consumeThread = new Thread(fr);
			consumeThread.setDaemon(true);
			consumeThread.start();
		}

	}
	
	public boolean isValidRecord(String record)
	{
		int ipStart = record.indexOf("08 06");
		if (ipStart < 0) {
			return false;
		}

		int statusStart = record.indexOf("28 06");
		if (statusStart < 0) {
			return false;
		}
		
		if(record.length()>500)
		{
			return false;
		}
		
		if(!(Pattern.matches("[0-9a-fA-F ]*", record)))
		{
			return false;
		}
		
		return true;
	}

	/**
	 * 每天00:00重启解析线程
	 * 
	 * @author zkyz
	 */
	private class FieldResolve implements Runnable {

		private ConfigureFactory confFactory;

		private PrintWriter parsedRecordWriter;

		private RadiusAnalysis radiusAnalysis = new RadiusAnalysis();
		
		private OnlineDatabase onlineDB=null;
		/**
		 * 每天00:00定时执行该方法
		 */
		public void initLogFiles() {
			Thread current = Thread.currentThread();

			// 解析后的record 该天应该存入的文件夹
			String todayPcapDir = confFactory.getPcapDirectory()
					+ TimeOpera.getToday();
			File tempDir = new File(todayPcapDir);
			if (!(tempDir.exists())) {
				tempDir.mkdirs();
			}
			//System.out.println("todayPcapDir:"+todayPcapDir);

			// 该解析线程写入的本地日志文件
			String todayPresentThreadPcapFile = todayPcapDir + "/radiusInfo_"+TimeOpera.getTodayStr()
					+ "-"+current.getName().replaceAll("Thread\\-", "") + ".log";
			//System.out.println("todayPresentThreadPcapFile:"+todayPresentThreadPcapFile);
			
			File tempFile = new File(todayPresentThreadPcapFile);

			try {
				// 关闭上一个打开的parsedRecordWriter
				if(parsedRecordWriter!=null)
				{
				  parsedRecordWriter.close();
				}
				// 打开新一天的parsedRecordWriter
				parsedRecordWriter = new PrintWriter(new FileWriter(tempFile,true));

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			onlineDB=confFactory.getOnlineDatabase();
			onlineDB.setPool(pool);
			Jedis jedisInstance=pool.getResource();
			onlineDB.connectJedis(jedisInstance);
			//onlineDB.connect(confFactory.getRedisCenter());

		}

		public void init() {
			confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
			//initLogFiles();

			Calendar cal = Calendar.getInstance();
			// 每天定点执行
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 30);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					initLogFiles();
				}
			}, cal.getTime(), TimeOpera.PERIOD_DAY);
		}

		@Override
		public void run() {

			init();
			parseHexRecord();
		}

		/**
		 * 继续解析未完全解析的用户记录 每天记录的格式如下： 2014-10-23 12:00:00 01 1a 68 6f 54 62 39 6d
		 * 38 6b 56 4b 39 63 4d 43 67 67 4b 55 53 33 78 77 3d 3d 08 06 72 ee e8
		 * 2d 28 06 00 00 00 01
		 */
		public void parseHexRecord() {

			while (true) {

				try {
					String record = pollFromPond();
					if (SSO.tioe(record)) {
						Thread.sleep((long)(10*Math.random()));
						continue;
					}
					/*
					if(zeroCount>100)
					{
						Thread current=Thread.currentThread();
						current.stop();
					}
					*/
					//System.out.println("record:"+record);
					RecordLight rl = radiusAnalysis.analysis(record);
					
					if(rl==null)
					{
						continue;
					}
					//System.out.println("rl:"+rl.toString());
					//parsedRecordWriter.println(rl.toString());
					//logger.info(rl.toString());
					onlineDB.update(rl);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

}
