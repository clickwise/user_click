package cn.clickwise.web;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.clickwise.lib.string.SSO;

public class QueueUrlPond extends UrlPond {

	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();

	@Override
	public void add2Pond(String url) {

		if (!(isValidUrl(url))) {
			return;
		}
		// System.out.println("record:"+record);
		queue.offer(url);

	}

	public boolean isValidUrl(String url) {
		return true;
	}

	@Override
	public String pollFromPond() {
		String nextElement = "";

		// System.out.println("queue.size:"+queue.size());
		nextElement = queue.poll();
		return nextElement;
	}

	@Override
	public void startConsume(int threadNum) {
		for (int i = 0; i < threadNum; i++) {
			UrlResolve fr = new UrlResolve();
			fr.init();
			Thread consumeThread = new Thread(fr);
			consumeThread.setDaemon(true);
			consumeThread.start();
		}

	}
	
	private synchronized void printContent(String content)
	{
		System.out.println(content);
	}

	private class UrlResolve implements Runnable {

		private ConfigureFactory confFactory;

		private Fetcher fetcher = new Fetcher();

		private int fetcher_opt = 0;

		public void init() {

			confFactory = ConfigureFactoryInstantiate.getConfigureFactory();

			fetcher_opt = confFactory.getFetcherOpt();

		}

		@Override
		public void run() {
			init();
			parseWord();

		}

		public void parseWord() {

			String url = "";

			String content = "";
			WebAbstract wa = null;
			while (true) {

				try {
					// System.out.println("sleeptime is:"+getSleepTime());
					// Thread.sleep(getSleepTime());

					url = pollFromPond();

					// System.out.println("fetch word:"+word);
					if (SSO.tioe(url)) {
						Thread.sleep((long) (10 * Math.random()));
						continue;
					}

					wa = fetcher.getAbstract(url);
					if (wa == null) {
						continue;
					}

					if (fetcher_opt == 0) {
						content =url+"\001"+ wa.getTitle();
					} else if (fetcher_opt == 1) {
						content = url+"\001"+wa.toRegularString();
					}
					
					printContent(content);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}

	}

}
