package cn.clickwise.bigdata.knowledge_base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import redis.clients.jedis.Jedis;

public class Key_Word_Search implements Runnable {
	
	private static Jedis jedis = new Jedis("localhost");  //连接数据库
	
	public static Key_Word_Search serverInstance;
	public int port = 0;
	
	/**
	 * 
	 */
	
	public void run(){
		try {
			HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
			TestHandler mh = new TestHandler();
			hs.createContext("/cate_tb", mh);
			hs.setExecutor(null);
			hs.start();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		synchronized (this){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {				 
		
		Properties prop = null;
		prop.setProperty("port", "arg1");
		int port=Integer.parseInt(prop.getProperty("port"));
		
		Thread serverThread = new Thread(serverInstance);
        serverThread.start();

	}

}
class TestHandler implements HttpHandler{
	
	public Storage_File_To_Redis sftr; 
	
	
	public void handle(HttpExchange exchange){		
		String cate = "";
		cate = Storage_File_To_Redis.KeyWordSearch("词语");
		String title_str = exchange.getRequestURI().toString();
		
		System.out.println(cate);
	}
	
}
