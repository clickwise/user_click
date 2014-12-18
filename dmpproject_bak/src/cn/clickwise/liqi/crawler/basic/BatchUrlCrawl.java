package cn.clickwise.liqi.crawler.basic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 使用多线程批量抓取网页
 * 配置项：
 *        是否使用代理，代理ip列表   
 * @author zkyz
 *
 */
public class BatchUrlCrawl {
	
	public  ArrayList<Thread> extra_threads=new  ArrayList<Thread>();
	
	/**
	 * 
	 * @param url_arr
	 * @return  url_content
	 * @throws Exception
	 */
	public  ArrayList<String> getTitleBat(String[] url_arr,boolean useProxy,String[] proxy_arr) throws Exception
	{
		  String line="";
		  String title="";
		  ArrayList<String> url_content=new ArrayList<String>();
		  String last_url="";
		  
		  ArrayList<ThreadGroup> started_groups=new ArrayList<ThreadGroup>();
		  Thread t=null;
		  for(int i=0;i<url_arr.length;i++)
		  {
			  line=url_arr[i];
			  line=line.trim();
			  Thread.sleep(50);
              Runnable r =  new  PageCrawlThreadBatch(line,url_content,useProxy,proxy_arr,"title");
              
              t =  new  Thread(r);
              started_groups.add(t.getThreadGroup());
              t.start();
              t.join();  
		  }
		  
         
         ThreadGroup tg=null;
         HashMap<String,String> eh=new HashMap<String,String>();
         for(int i=0;i<started_groups.size();i++)
         {
            tg=	started_groups.get(i);
            Thread[] tgl=new Thread[tg.activeCount()];
            tg.enumerate(tgl);
            for(int j=0;j<tgl.length;j++)
            {
            	if(tgl[j]!=null)
            	{
            	 if(!(eh.containsKey(tgl[j].getId()+"")))
            	 {
            		 extra_threads.add(tgl[j]);
                     eh.put(tgl[j].getId()+"", "1");
            	 }
            	}
            }
         }
         
        Thread ct=null;
 	    for(int i=0;i<extra_threads.size();i++)
 	    {
 	    	ct=extra_threads.get(i);
 	    	//System.out.println(ct.getName()+" "+ct.getId());
 	    	if(!((ct.getName().trim()).equals("main")))
 	    	{
 	    		ct.stop();
 	    	}
 	    }
 	    
        return url_content;
	}
	
	
	/**
	 * 
	 * @param url_arr
	 * @return  url_content
	 * @throws Exception
	 */
	public  ArrayList<String> getTitleBat(ArrayList<String> url_list,boolean useProxy,String[] proxy_arr) throws Exception
	{
		  String line="";
		  String title="";
		  ArrayList<String> url_content=new ArrayList<String>();
		  String last_url="";
		  
		  ArrayList<ThreadGroup> started_groups=new ArrayList<ThreadGroup>();
		  Thread t=null;
		  for(int i=0;i<url_list.size();i++)
		  {
			  line=url_list.get(i);
			  line=line.trim();
			  Thread.sleep(50);
              Runnable r =  new  PageCrawlThreadBatch(line,url_content,useProxy,proxy_arr,"title");
              
              t =  new  Thread(r);
              started_groups.add(t.getThreadGroup());
              t.start();
              t.join();  
		  }
		  
         
         ThreadGroup tg=null;
         HashMap<String,String> eh=new HashMap<String,String>();
         for(int i=0;i<started_groups.size();i++)
         {
            tg=	started_groups.get(i);
            Thread[] tgl=new Thread[tg.activeCount()];
            tg.enumerate(tgl);
            for(int j=0;j<tgl.length;j++)
            {
            	if(tgl[j]!=null)
            	{
            	 if(!(eh.containsKey(tgl[j].getId()+"")))
            	 {
            		 extra_threads.add(tgl[j]);
                     eh.put(tgl[j].getId()+"", "1");
            	 }
            	}
            }
         }
         
        Thread ct=null;
 	    for(int i=0;i<extra_threads.size();i++)
 	    {
 	    	ct=extra_threads.get(i);
 	    	//System.out.println(ct.getName()+" "+ct.getId());
 	    	if(!((ct.getName().trim()).equals("main")))
 	    	{
 	    		ct.stop();
 	    	}
 	    }
 	    
        return url_content;
	}
	
	/**
	 * 
	 * @param url_arr
	 * @return  url_content
	 * @throws Exception
	 */
	public  ArrayList<String> getRedirectInfoBat(String[] url_arr,boolean useProxy,String[] proxy_arr) throws Exception
	{
		  String line="";
		  String title="";
		  ArrayList<String> url_content=new ArrayList<String>();
		  String last_url="";
		  
		  ArrayList<ThreadGroup> started_groups=new ArrayList<ThreadGroup>();
		  Thread t=null;
		  for(int i=0;i<url_arr.length;i++)
		  {
			  line=url_arr[i];
			  line=line.trim();
			  Thread.sleep(50);
              Runnable r =  new  PageCrawlThreadBatch(line,url_content,useProxy,proxy_arr,"redirectinfo");
              
              t =  new  Thread(r);
              started_groups.add(t.getThreadGroup());
              t.start();
              t.join();  
		  }
		  
         
         ThreadGroup tg=null;
         HashMap<String,String> eh=new HashMap<String,String>();
         for(int i=0;i<started_groups.size();i++)
         {
            tg=	started_groups.get(i);
            Thread[] tgl=new Thread[tg.activeCount()];
            tg.enumerate(tgl);
            for(int j=0;j<tgl.length;j++)
            {
            	if(tgl[j]!=null)
            	{
            	 if(!(eh.containsKey(tgl[j].getId()+"")))
            	 {
            		 extra_threads.add(tgl[j]);
                     eh.put(tgl[j].getId()+"", "1");
            	 }
            	}
            }
         }
         
        Thread ct=null;
 	    for(int i=0;i<extra_threads.size();i++)
 	    {
 	    	ct=extra_threads.get(i);
 	    	//System.out.println(ct.getName()+" "+ct.getId());
 	    	if(!((ct.getName().trim()).equals("main")))
 	    	{
 	    		ct.stop();
 	    	}
 	    }
 	    
        return url_content;
	}
	
	public static void main(String[] args) throws Exception {
		
		String input_file = "";
		String output_file="";
		//if (args.length != 2) {
		//	System.out.println("用法 :EWAECServer <configure file>");
		//	System.exit(1);
		//}
		//input_file=args[0];
		//output_file=args[1];
	    input_file="D:/projects/hosts/20140313/host_top.txt";
	    output_file="D:/projects/hosts/20140313/host_text.txt";
		
		BatchUrlCrawl buc=new BatchUrlCrawl();
	    String[] url_arr=FileToArray.fileToDimArr(input_file);
		//for(int i=0;i<url_arr.length;i++)
		//{
		//	System.out.println(i+"  "+url_arr[i]);
		//}
	    int index=0;
	    ArrayList<String> small_list=null;
	    small_list=new  ArrayList<String>();
	    ArrayList<String> res_list=null;
	    for(int i=0;i<url_arr.length;i++)
	    {
	    	if(SSO.tnoe(url_arr[i])&&(url_arr[i].indexOf("null"))==-1)
	    	{
	    	small_list.add(url_arr[i].trim());
	    	}
	    	if(i%100==1)
	    	{
	    		 res_list=buc.getTitleBat(small_list,false,null);
	    		  FileWriterUtil.writeArrayList(res_list, output_file, true);
	    		  small_list=new  ArrayList<String>();		  
	    	}
	    	
	    }
	    FileWriterUtil.writeArrayList(res_list, output_file, true);
		
        // Thread ct=null;
        // ct=Thread.currentThread();
	}
}


 class  PageCrawlThreadBatch  implements  Runnable {
	
    static  String url="";
    ArrayList<String> url_content=null;
    private Timer timer=new Timer();
    private int limit;//限制
    private boolean useProxy;
    private String[] proxy_arr=null;
    private String info_type="";
    

    public  PageCrawlThreadBatch(String url,ArrayList<String> url_content,boolean useProxy,String[] proxy_arr,String info_type) {
          this.url=url;
          this.url_content=url_content;
          this.useProxy=useProxy;
          this.proxy_arr=proxy_arr;
          this.info_type=info_type;
    }

    public   void  run() {
    
            try  {
             
                String title="";
                int status=-1;
                if(info_type.equals("title"))
                {
                	//title=SingleUrlCrawl.getWebPageTitle(url);
                	 status=SingleUrlCrawl.getWebPageHeadInfo(url);
                	 if(status==200)
                	 {
                	    title=SingleUrlCrawl.getWebPageTitle(url,useProxy, this.proxy_arr);	
                	    if(!(SSO.tnoe(title)))
                	    {
                	    	title="get null";
                	    }
                		// title=SingleUrlCrawl.getWebPageTitle(url); 
                	 }
                	 else
                	 {
                		title="Error:"+status;
                	 }
                }
                else  if(info_type.equals("redirectinfo"))
                {
                	 title=SingleUrlCrawl.getRedirectInfo(url,useProxy, this.proxy_arr);	
                }
                System.out.println("crawl url :"+url);
                if(SSO.tnoe(title))
                {
                 url_content.add(url+"\001"+title);
                 System.out.println(url+":"+title);
                } 
            }  catch  (Exception e) {
            	    
                    e.printStackTrace();                
                    return;
            }
            
            
    }
}
