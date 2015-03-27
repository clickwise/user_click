package cn.clickwise.clickad.radiusReform;

import java.io.File;

import cn.clickwise.lib.file.FileStatus;
import cn.clickwise.lib.time.TimeOpera;

/*
 * 监控接收数据情况，不正常则退出
 */
public class Monitor implements Runnable{

    public String dir="/data1/radius_data";
    
    public String pindDir(String today)
    {
    	return dir+"/"+today;
    }
    
    
	public boolean isNormal()
	{
		int hour=TimeOpera.getHour();
		String today=TimeOpera.getTodayStr();
		System.out.println("today:"+today);
		System.out.println("hour:"+hour);
		String today_dir=pindDir(today);
		
		System.out.println("today_dir:"+today_dir);
	
		if(hour<1)
		{
			return true;
		}
		
		long dsize=FileStatus.getTotalSizeOfFilesInDir(new File(today_dir));
		if(dsize<100000)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public void run() {
	
		while(true)
		{
			boolean isN=isNormal();
			System.err.println("isN:"+isN);
			if(isN==false)
			{
				System.exit(1);
			}
			try{
			    Thread.sleep(1000*60);
			}
			catch(Exception e)
			{
				
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		Monitor m=new Monitor();
		m.isNormal();
	}
	

}
