package cn.clickwise.web.proxy;

import java.net.InetSocketAddress;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;


/**
 * firefox 定时从该 server 取出 url
 * 抓取并将内容返回 
 * @author lq
 */
public class UrlServer extends Server{
	
	static Logger logger = LoggerFactory.getLogger(UrlServer.class);
	
	public UrlServer(){
	  super();	
	}
	
	@Override
	public void run() {
		
		try{
			 HttpServer hs = HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty("port"))), 0);
			 UrlHandler ufh=ProxyFactory.getUrlHandler();
			 System.err.println("ufh:"+ufh.getClass().getCanonicalName());
			 hs.createContext("/fetch", ufh);
		     hs.setExecutor(null);
			 hs.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	
    public static void main(String[] args)
    {
    	//CrawlServer cs=new CrawlServer();
    	UrlServer us=new UrlServer();
    	us.read_input_parameters(args);
    	Thread t=new Thread(us);
    	t.start();
    	//UrlServer.print_help();
    	
    }
	
	
	
}
