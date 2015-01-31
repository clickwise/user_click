package cn.clickwise.clickad.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;

public class NLPServer implements Runnable{
	
	static Logger logger = LoggerFactory.getLogger(NLPServer.class);
	
	Segmenter segmenter=null;
	
	PosTagger posTagger=null;
	
	KeyExtract ke=null;
	
	private Properties properties = new Properties();
	
	public NLPServer()
	{
		
	}
	
	
	@Override
	public void run() {
		
		try{
		    HttpServer hs = HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty("port"))), 0);
		    
			if(NLPConfig.server_type==0)
			{
				SegHandler seg=new SegHandler();
				seg.setSegmenter(segmenter);
				hs.createContext("/seg",seg);
				System.err.println("waiting to seg on port "+properties.getProperty("port"));
			}
			else if(NLPConfig.server_type==1)//初始化tag
			{
				TagHandler tag=new TagHandler();
				tag.setPosTagger(posTagger);
				hs.createContext("/tag",tag);
				System.err.println("waiting to tag on port "+properties.getProperty("port"));
			}
			else if(NLPConfig.server_type==2)//初始化keyword
			{
				KeyHandler key=new KeyHandler();
				key.setKe(ke);
				hs.createContext("/key",key);
				System.err.println("waiting to key on port "+properties.getProperty("port"));
			}
			
			hs.setExecutor(null);
			hs.start();
			    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	

}
