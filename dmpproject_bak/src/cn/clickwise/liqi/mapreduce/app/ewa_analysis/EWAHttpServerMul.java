package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.apache.hadoop.hbase.util.Base64;

import cn.clickwise.liqi.str.configutil.ConfigFileReader;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EWAHttpServerMul implements Runnable {

    private static EWAHttpServerMul serverInstance;
    private HttpServer        httpServer;
    private ExecutorService   executor;
    public  int port=0;
    public EWAPredict ewa;
    @Override
    
    public void run() {
        try {
    		try {
    			HttpServer hs = HttpServer.create(new InetSocketAddress(port), 0);
    			TestHandler mh = new TestHandler();
    			mh.setEwa(ewa);
    			hs.createContext("/cate_tb", mh);
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
			System.out.println("用法 :EWAHttpServer <configure file>");
			System.exit(1);
		}
	
		config_file=args[0];
		Properties prop = null;
		prop=ConfigFileReader.getPropertiesFromFile(config_file);
		String model_data=prop.getProperty("model_data");
		String model_path = model_data+"/model";
		String sls_path = model_data+"/lll.txt";
		String first_level_path = model_data+"/fhc.txt";
		String second_level_path = model_data+"/shc.txt";
		String third_level_path = model_data+"/thc.txt";
		String cw_file = prop.getProperty("cw_file");
	    
		int port=Integer.parseInt(prop.getProperty("port"));
        serverInstance = new EWAHttpServerMul();
        serverInstance.ewa = new EWAPredict();
        serverInstance.ewa.read_model(model_path, sls_path, first_level_path,
				second_level_path, third_level_path);
        serverInstance.ewa.load_config(prop);
        serverInstance.ewa.load_cate_wrods(cw_file);
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
	public EWAPredict my_ewa;

	public void setEwa(EWAPredict temp_ewa) {
		this.my_ewa = temp_ewa;
	}
    boolean debug = Boolean.getBoolean("test.debug");

    public void handle(HttpExchange exchange) throws IOException {

		String title_str = exchange.getRequestURI().toString();
		//System.out.println("title_str:" + title_str);

		title_str = title_str.replaceFirst("/cate_tb/do\\?t=", "");
		//System.out.println("title_str:" + title_str);
		String de_title = new String(Base64.decode(title_str));
		System.out.println("de_title:" + de_title);
		String res = "";
		try {
			res = my_ewa.predict_from_seg_line(de_title);
		} catch (Exception e) {
		}
		;
		System.out.println("res is :" + res);
		String encode_res = Base64.encodeBytes(res.getBytes());
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
    	EWAHttpServerMul.shutdown();
    }
}
