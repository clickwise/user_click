package cn.clickwise.lib.thread;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cn.clickwise.lib.time.TimeOpera;

public class ThreadTest {

	synchronized private static void printThreadInfo(Thread current)
	{
		System.out.println("priority:"+current.getPriority());
		System.out.println("name:"+current.getName());
		System.out.println("activeCount:"+current.activeCount());
		System.out.println("id:"+current.getId());
		System.out.println("threadGroup.name:"+current.getThreadGroup().getName());
		
		System.out.println("threadGroup:"+current.getThreadGroup());
		System.out.println("stackTrace:"+current.getStackTrace());
		System.out.println("hashCode:"+current.hashCode());
		System.out.println("toString:"+current.toString());
		System.out.println("===================");
	}
	
	
	private static class SubThread implements Runnable {
		@Override
		public void run() {
 
			Thread current = Thread.currentThread();
			//printThreadInfo(current);
			
		    double rand=Math.random();
		    while(true){
		    	
		    }
		    //System.out.println("rand:"+rand);
		   // for(int i=0;i<1000000*rand;i++)
		   // {
		   // 	System.out.println("thread:"+current.getId()+"  i:"+i);
		   // }
			/*
			System.out.println(current.getPriority());
			System.out.println(current.getName());
			System.out.println(current.activeCount());
			System.out.println(current.getId());
			System.out.println(current.getThreadGroup());
			System.out.println(current.getStackTrace());
			System.out.println(current.hashCode());
			System.out.println(current.toString());
			System.out.println("===================");
			*/
		}

	}

	public static void main(String[] args) {
		/*
		for (int i = 0; i <5; i++) {
			SubThread st = new SubThread();
			Thread serverThread = new Thread(st);
			serverThread.setDaemon(true);
			serverThread.start();
		}
		*/
		
		Calendar cal = Calendar.getInstance();
		// 每天定点执行
		//cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 30);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println("test1");
			}
		}, cal.getTime(), TimeOpera.PERIOD_HOUR);
		
		System.out.println("test2");
	}

}
