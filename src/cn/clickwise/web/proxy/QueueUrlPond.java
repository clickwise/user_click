package cn.clickwise.web.proxy;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.clickwise.web.URL;

public class QueueUrlPond extends UrlPond{

	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();
	
	@Override
	public void add2Pond(String url) {
		if(!(URL.isValidRecord(url)))
		{
			return;
		}
		System.err.println("add url:"+url+" to queue");
		queue.offer(url);	
	}

	@Override
	public String pollFromPond() {
		System.err.println("in pollFromPond");
		String nextElement = "";
		nextElement = queue.poll();
		System.err.println("left elements:"+queue.size());	
		return nextElement;
	}


}
