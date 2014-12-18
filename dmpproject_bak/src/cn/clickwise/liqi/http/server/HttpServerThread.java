package cn.clickwise.liqi.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.apache.hadoop.hbase.util.Base64;

import cn.clickwise.liqi.str.basic.SSO;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 启动HttpServerThread的线程
 * @author zkyz
 *
 */
public class HttpServerThread implements Runnable{

	   // public Hashtable<String,String> wci_hash=null;
  
	    private int port=0;
	    private String method_name;
	    public HttpServerThread(int port,String method_name)
	    {
	    	this.port=port;
	    	this.method_name=method_name;
	    }
	    
	    @Override
	    public void run() {
	        try {
	    		try {
	    			HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
	    			
	    			HttpServerHandler hsh = new HttpServerHandler();
	    			hsh.method_name=method_name;
					hs.createContext("/"+method_name, hsh);
					
					hs.setExecutor(null);
	    			hs.start();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	
	            // Wait here until notified of shutdown.
	            synchronized (this) {
	                try {
	                    this.wait();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        } catch (Throwable t) {
	            t.printStackTrace();
	        }
	    }
	    
	    public void setPort(int port)
	    {
	    	this.port=port;
	    }
	    
	    static void shutdown() {

	        try { 
	            System.out.println("Shutting down TestServer.");            


	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }
	    
	    public static void main(String[] args)
		{
			if(args.length<1)
			{
				System.out.println("用法 : java HttpServerThread <port> [<method_name>]");
				System.exit(0);
			}
			
			int port=Integer.parseInt(args[0]);
			
			String method_name=args[1];
			
			HttpServerThread hst=new HttpServerThread(port,method_name);
			try
			{
				Thread serverThread = new Thread(hst);
				serverThread.start();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
					
		}
}



/* Responds to the /test URI. */
class HttpServerHandler implements HttpHandler {
	public static String method_name;
    public void handle(HttpExchange exchange) throws IOException {

		String title_str = exchange.getRequestURI().toString();
		//System.out.println("title_str:" + title_str);

		title_str = title_str.replaceFirst("/"+method_name+"/do\\?t=", "");
		//System.out.println("title_str:" + title_str);
		String de_title = new String(Base64.decode(title_str));//输入title
		System.out.println("de_title:" + de_title);
		String res = "";//返回结果

		try {
		
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		ServerMethod sermet=ServerMethodFactory.getServerMethod(method_name);
		String[] res_arr=sermet.method(de_title);
		String encode_res="";
		String response = "";
		OutputStream os=null;
		if(res_arr==null)
		{
			res="NA";
			encode_res = Base64.encodeBytes(res.getBytes());
		    encode_res = encode_res.replaceAll("\\s+", "");
		    System.out.println("encode_res:"+encode_res);
		    response=encode_res;
			exchange.sendResponseHeaders(200, response.length());
		    os = exchange.getResponseBody();
			os.write(response.getBytes());
		}
		else
		{
			System.out.println("res_arr.length:"+res_arr.length);
			//for(int i=0;i<res_arr.length;i++)
			//{
			    String res_ping=SSO.implode(res_arr, "\n");
				res=res_ping;
				encode_res = Base64.encodeBytes(res.getBytes());
			    encode_res = encode_res.replaceAll("\\s+", "");
			    System.out.println("encode_res:"+encode_res);
			    response=encode_res;
				exchange.sendResponseHeaders(200, response.length());
			    os = exchange.getResponseBody();
				os.write(response.getBytes());
			//}
		}
		
		os.close();		
    }
    
      
}
