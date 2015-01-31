package cn.clickwise.web.proxy;

import java.io.BufferedReader;
import java.io.FileReader;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.web.URL;


/**
 * 向pond 里添加url
 * @author lq
 *
 */
public class UrlToPond {

	UrlPond up=null;
	
	public UrlToPond(){
		init();
	}
	
	public void init()
	{
		up=ProxyFactory.getUrlPond();
	}
	
	public void addFile(String file)
	{
		try{
		    BufferedReader br=new BufferedReader(new FileReader(file));
		    
		    String line="";
		    while((line=br.readLine())!=null)
		    {
		    	if(SSO.tioe(line))
		    	{
		    		continue;
		    	}
		
		    	up.add2Pond(line);	
		    }
		    
		    
		    br.close();
		   
	    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public static void print_help()
	{
	  System.err.println("Usage:<way> <parm>*\n" +
	  		"           way:添加方式:    0 命令行添加\n" +
	  		"                         1 文件添加\n" +
	  		"           parm:  如果way==0,则每个parm是一个url\n" +
	  		"                  如果way==1,则每个parm是一个文件名，每个文件里是按行存放的url列表\n");	
	}
	
	public static void main(String[] args)
	{
		if(args.length<1)
		{
			UrlToPond.print_help();
			System.exit(1);
		}
		
		 UrlToPond up=new UrlToPond();
		 int way=Integer.parseInt(args[0]);
		 if(way==0)
		 {
			 for(int i=1;i<args.length;i++)
			 {
				 up.up.add2Pond(args[i]);
			 }
		 }
		 else if(way==1)
		 {
			 for(int i=1;i<args.length;i++)
			 {
				 up.addFile(args[i]);
			 }
		 }
		 
	}
}
