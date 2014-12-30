package cn.clickwise.web;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import cn.clickwise.lib.string.SSO;

public class QueueUrlPool extends UrlPond{
	
	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();

	private int count;
	
	private int allCount;
	
	synchronized  private  void incrCount()
	{
		setCount(getCount() + 1);
	}
	
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
		waitForComplete();
	}
	
	public void waitForComplete()
	{
		while(getCount()<allCount)
		{
			try{
			Thread.sleep(1000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void printContent(ResolveInfo resolveInfo)
	{
		/*
		System.out.println(content);
		System.out.flush();
		*/
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	private class UrlResolve implements Runnable {

		private ConfigureFactory confFactory;

		private int fetcher_opt = 0;
		
		private FetchResolve fetchResolve;

		public void init() {

			confFactory = ConfigureFactoryInstantiate.getConfigureFactory();

			fetcher_opt = confFactory.getFetcherOpt();

			fetchResolve=confFactory.getFetchResolve();
			
		}

		@Override
		public void run() {
			
			init();
			parseInfo();

		}

		public void parseInfo()
		{
			String url = "";
			String content = "";
			ResolveInfo resolveInfo = null;
			
			while (true) {

				try {
					// System.out.println("sleeptime is:"+getSleepTime());
					// Thread.sleep(getSleepTime());

					url = pollFromPond();
					
					if (SSO.tioe(url)) {
						Thread.sleep((long) (10 * Math.random()));
						continue;
					}
					
					incrCount();
				
					System.out.println("fetch url:"+url);
					resolveInfo = fetchResolve.fetchAndResolve(url);
					
					if (resolveInfo == null) {
						continue;
					}
		
					printContent(resolveInfo);
					
				} catch (Exception e) {
					incrCount();
					e.printStackTrace();
				}
			}
		}
		
		/*
		public void parseWord() {

			String url = "";
			String content = "";
			WebAbstract wa = null;
			
			while (true) {

				try {
					// System.out.println("sleeptime is:"+getSleepTime());
					// Thread.sleep(getSleepTime());

					url = pollFromPond();
					
					if (SSO.tioe(url)) {
						Thread.sleep((long) (10 * Math.random()));
						continue;
					}
					incrCount();
				
					System.out.println("fetch url:"+url);
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
					incrCount();
					e.printStackTrace();
				}
			}
		}
		
		*/

	}

}
