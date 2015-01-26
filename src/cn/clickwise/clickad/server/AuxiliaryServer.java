package cn.clickwise.clickad.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 辅助工具server端
 * @author zkyz
 */
public class AuxiliaryServer implements Runnable {

	static Logger logger = LoggerFactory.getLogger(AuxiliaryServer.class);
	private Properties properties = new Properties();

	@Override
	public void run() {

		// 配置成根据传入请求的前缀不同调用不同的处理程序
		// 每种请求对应一个handler
		
		try {
			HttpServer hs = HttpServer.create(new InetSocketAddress(Integer.parseInt(properties.getProperty("port"))), 0);

			// 设置分词 hander
			AnsjSegHandler ansj_handler = new AnsjSegHandler();
			hs.createContext("/seg", ansj_handler);

			TestHandler test_handler = new TestHandler();
			hs.createContext("/test", test_handler);

			hs.setExecutor(null);
			hs.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class AnsjSegHandler implements HttpHandler {

		Segmenter segmenter;

		public AnsjSegHandler() {
			segmenter = new Segmenter();
			segmenter.loadAnsjDic(new File(properties.getProperty("dict")));
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			// TODO Auto-generated method stub

			String request = exchange.getRequestURI().toString();
            System.out.println("request:"+request);
			
			InputStream is = exchange.getRequestBody();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			//获得请求消息体
			String body = "";
			String line = "";
			ArrayList<String> list=new ArrayList<String>();			
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				list.add(segmenter.segAnsi(line));
			}
			
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			
			String encode="";
			for(int j=0;j<list.size();j++)
			{
				encode = URLEncoder.encode(list.get(j));
				os.write(new String(encode+"\n").getBytes());
			}
			
			os.close();
		}

	}

	class TestHandler implements HttpHandler {

		Segmenter segmenter;

		public TestHandler() {
			segmenter = new Segmenter();
			segmenter.loadAnsjDic(new File(properties.getProperty("dict")));
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			String request = exchange.getRequestURI().toString();
			
			InputStream is = exchange.getRequestBody();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			
			//获得请求消息体
			String body = "";
			String line = "";
			String encode="";
			ArrayList<String> list=new ArrayList<String>();			
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				encode = URLEncoder.encode(segmenter.segAnsi(line));
				os.write(new String(encode+"\n").getBytes());
			}
			
			/*
			String encode="";
			for(int j=0;j<list.size();j++)
			{
				encode = URLEncoder.encode(list.get(j));
				os.write(new String(encode+"\n").getBytes());
			}
			*/
			os.close();
		}
	}

	public Properties getProp() {
		return properties;
	}

	public void setProp(Properties properties) {
		this.properties = properties;
	}

	public void read_input_parameters(String[] args) {
		int i;
		for (i = 0; (i < args.length) && ((args[i].charAt(0)) == '-'); i++) {
			switch ((args[i].charAt(1))) {
			case 'h':
				print_help();
				System.exit(0);
			case 'p':
				i++;
				properties.setProperty("port", args[i]);
				break;
			case 'd':
				i++;
				properties.setProperty("dict", args[i]);
				break;
			default:
				System.out.println("Unrecognized option " + args[i] + "!");
				print_help();
				System.exit(0);
			}
		}

		System.out.println(properties.toString());
	}

	public static void print_help() {
		System.out.println("usage: AuxiliaryServer [options]");
		System.out.println("options: -h  -> this help");
		System.out.println("         -p  auxiliary server port");
		System.out.println("         -d  ansj dict file");
	}

	public static void main(String[] args) {

		if(args.length<1)
		{
			print_help();
			System.exit(0);
		}
		AuxiliaryServer as = new AuxiliaryServer();
		as.read_input_parameters(args);
		Thread serverThread = new Thread(as);
		serverThread.start();

	}

}
