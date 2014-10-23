package cn.clickwise.lib.thread;

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
			printThreadInfo(current);
			
		    double rand=Math.random();
		    System.out.println("rand:"+rand);
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
		for (int i = 0; i <10; i++) {
			SubThread st = new SubThread();
			Thread serverThread = new Thread(st);
			serverThread.start();
		}
	}

}
