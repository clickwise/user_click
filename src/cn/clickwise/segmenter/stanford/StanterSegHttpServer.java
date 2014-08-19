package cn.clickwise.segmenter.stanford;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.liqi.str.configutil.ConfigFileReader;
import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class StanterSegHttpServer implements Runnable {
	
	static Logger logger =  LoggerFactory.getLogger(StanterSegHttpServer.class);
    private static StanterSegHttpServer serverInstance;
    private HttpServer        httpServer;
    private ExecutorService   executor;
    public  int port=0;
    public StanterSeg sseg;
    
    @Override
    public void run() {
        try {
    		try {
    			HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
    			TestHandler mh = new TestHandler();
    			mh.setSeg(sseg);
    			hs.createContext("/seg", mh);
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
            serverInstance.httpServer.stop(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (serverInstance) {
            serverInstance.notifyAll();
        }

    }

    public static void main(String[] args) throws Exception {
    	String config_file="";
		if (args.length != 1) {
			System.out.println("用法 :StanterSegHttpServer <configure file>");
			System.exit(1);
		}
	
		config_file=args[0];
		Properties prop = null;
		prop=ConfigFileReader.getPropertiesFromFile(config_file);
	    
		int port=Integer.parseInt(prop.getProperty("port"));
		
        serverInstance = new StanterSegHttpServer();
        serverInstance.sseg = new StanterSeg();
        serverInstance.sseg.load_config(prop);
        serverInstance.setPort(port);
        
        Thread serverThread = new Thread(serverInstance);
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new OnShutdown());

        try {
            serverThread.join();
        } catch (Exception e) { }
    }

}

/* Responds to the /test URI. */
class TestHandler implements HttpHandler {
	public StanterSeg my_sseg;

	public void setSeg(StanterSeg temp_sseg) {
		this.my_sseg = temp_sseg;
	}
    boolean debug = Boolean.getBoolean("test.debug");

    public void handle(HttpExchange exchange) throws IOException {

		String title_str = exchange.getRequestURI().toString();
		//System.out.println("title_str:" + title_str);
		title_str = title_str.replaceFirst("\\/seg\\?s\\=", "");
		title_str=title_str.trim();
		System.out.println("title_str:" + title_str);
		String de_title = new String(UrlCode.getDecodeUrl(title_str));
		de_title=de_title.trim();
		System.out.println("de_title:" + de_title);
		String res = "";
		try {
			res = my_sseg.seg_inte(de_title);
		} catch (Exception e) {
		}
		res=res.trim();
		System.out.println("res is :" + res);
		String encode_res ="";
		encode_res=URLEncoder.encode(res);
		encode_res = encode_res.replaceAll("\\s+", "");
		//System.out.println("encode_res:" + encode_res);
		String response = encode_res;
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
		
    }
}

/* Responds to a JVM shutdown by stopping the server. */
class OnShutdown extends Thread {
    public void run() {
    	 StanterSegHttpServer.shutdown();
    }
}