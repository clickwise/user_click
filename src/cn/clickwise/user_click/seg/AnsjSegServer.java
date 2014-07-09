package cn.clickwise.user_click.seg;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.file.uitls.JarFileReader;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;
import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class AnsjSegServer implements Runnable {
	static Logger logger =  LoggerFactory.getLogger(AnsjSegServer.class);
    private static AnsjSegServer serverInstance;
    private HttpServer        httpServer;
    private ExecutorService   executor;
    public  int port=0;
    public AnsjSeg ansjseg;
    @Override
    
    public void run() {
        try {
    		try {
    			HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
    			TestHandler mh = new TestHandler();
    			mh.setSeg(ansjseg);
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
    	
		if (args.length != 1) {
			System.out.println("用法 :AnsjSegServer <port>");
			System.exit(1);
		}
	    
		int port=Integer.parseInt(args[0]);
		
        serverInstance = new AnsjSegServer();
        serverInstance.ansjseg = new AnsjSeg();
		JarFileReader jfr=new JarFileReader();
		String seg_dict_file="five_dict_uniq.txt";
		String stop_dict_file="cn_stop_words_utf8.txt";
		HashMap<String,String> seg_dict=jfr.jarFile2Hash(seg_dict_file);
		HashMap<String,String> stop_dict=jfr.jarFile2Hash(stop_dict_file);
	
		serverInstance.ansjseg.setSeg_dict(seg_dict);
		serverInstance.ansjseg.setStop_dict(stop_dict);
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
	public AnsjSeg my_ansjseg;

	public void setSeg(AnsjSeg temp_sseg) {
		this.my_ansjseg = temp_sseg;
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
			res = my_ansjseg.seg(de_title);
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
    	 AnsjSegServer.shutdown();
    }
}