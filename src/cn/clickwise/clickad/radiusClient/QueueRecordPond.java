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

	private static Queue queue = new ConcurrentLinkedQueue();

	@Override
	public synchronized void add2Pond(String record) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized String pollFromPond() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Queue getQueue() {
		return queue;
	}

	public static void setQueue(Queue queue) {
		QueueRecordPond.queue = queue;
	}

	/**
	 * 每天00:00重启解析线程
	 * 
	 * @author zkyz
	 */
	private class FieldResolve implements Runnable {
		/**
		 * 该解析线程写入的本地日志文件
		 */
		private String threadPcapFile;

		private ConfigureFactory confFactory;

		private PrintWriter parsedRecordWriter;

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

		}

		public String getThreadPcapFile() {
			return threadPcapFile;
		}

		public void setThreadPcapFile(String threadPcapFile) {
			this.threadPcapFile = threadPcapFile;
		}

	}

}
