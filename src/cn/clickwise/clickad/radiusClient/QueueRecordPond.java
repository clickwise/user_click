package cn.clickwise.clickad.radiusClient;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.clickwise.lib.time.TimeOpera;

public class QueueRecordPond extends RecordPond {

	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();

	@Override
	public synchronized void add2Pond(String record) {
		queue.add(record);
	}

	@Override
	public synchronized String pollFromPond() {
		String nextElement="";
		synchronized(queue) {

		    if(!queue.isEmpty()) {

		       nextElement=queue.poll();

		    }
		}
		
		return nextElement;
	}

	@Override
	public void startConsume(int threadNum) {
		for (int i = 0; i <10; i++) {
			FieldResolve fr = new FieldResolve();
			Thread consumeThread = new Thread(fr);
			consumeThread.start();
		}
		
	}
	
	/**
	 * 每天00:00重启解析线程
	 * 
	 * @author zkyz
	 */
	private class FieldResolve implements Runnable {
	
		private ConfigureFactory confFactory;

		private PrintWriter parsedRecordWriter;
		
		private RadiusAnalysis radiusAnalysis=new RadiusAnalysis();

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

			// 该解析线程写入的本地日志文件	 
			String todayPresentThreadPcapFile = todayPcapDir + "/"
					+ current.getName() + "_radius.txt";
			File tempFile = new File(todayPresentThreadPcapFile);

			try {
				// 关闭上一个打开的parsedRecordWriter
				parsedRecordWriter.close();

				// 打开新一天的parsedRecordWriter
				parsedRecordWriter = new PrintWriter(new FileWriter(tempFile));

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void init() {

			confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
			initLogFiles();

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
		 * 继续解析未完全解析的用户记录
		 * 每天记录的格式如下：
		 * 2014-10-23 12:00:00     01 1a 68 6f 54 62 39 6d 38 6b 56 4b 39 63 4d 43 67 67 4b 55 53 33 78 77 3d 3d 08 06 72 ee e8 2d 28 06 00 00 00 01
		 */
		public void parseHexRecord()
		{
			String record=pollFromPond();
			RecordLight rl = radiusAnalysis.analysis(record);
			parsedRecordWriter.println(rl.toString());
		}
		
	}



}
