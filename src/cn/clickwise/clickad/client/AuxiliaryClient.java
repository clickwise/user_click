package cn.clickwise.clickad.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.clickad.server.AuxiliaryServer;
import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * 辅助工具客户端
 * @author zkyz
 */
public class AuxiliaryClient {

	static Logger logger = LoggerFactory.getLogger(AuxiliaryClient.class);
	private Properties properties = new Properties();
	
	public void remote()
	{
		int tool_type=Integer.parseInt(properties.getProperty("tool"));
		
		switch (tool_type) {
		case 0:
			segment();
			break;
		case 1:
		default:
			System.out.println("Unrecognized tool type " );
			print_help();
			System.exit(0);
		}
		
	}
	
	public void segment()
	{
		try{
		String[] unsegs = FileToArray.fileToDimArr(properties.getProperty("input"));
		
		PrintWriter pw = FileWriterUtil.getPW(properties.getProperty("output"));
		
		long start_time = TimeOpera.getCurrentTimeLong();
		
		ArrayList<String> bodys=seg_bat(unsegs);
		
		for(int i=0;i<bodys.size();i++)
		{
			pw.println(bodys.get(i));
		}
		
		long end_time = TimeOpera.getCurrentTimeLong();

		System.out.println( unsegs.length+" total doc, use time:"
				+ ((double) (end_time - start_time) / (double) 1000)
				+ " seconds");
		pw.close();
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> seg_bat(String[] texts) {
		// TODO Auto-generated method stub
		ArrayList<String> response=new ArrayList<String>();
        
		try {
			URL url = new URL("http://"+properties.getProperty("server")+":"+properties.getProperty("port")+"/seg?s=");
			System.out.println(url.toString());
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			//urlConn.setRequestProperty("Content-type","application/x-java-serialized-object");
			urlConn.setRequestProperty("Content-type","text/plain");

			// 设定请求的方法为"POST"，默认是GET
			urlConn.setRequestMethod("GET");
			urlConn.setConnectTimeout(1000000);
			urlConn.connect();
			
			OutputStream outStrm = urlConn.getOutputStream();
			// 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。
			//ObjectOutputStream oos = new ObjectOutputStream(outStrm);
            OutputStreamWriter osw=new OutputStreamWriter(outStrm);
			PrintWriter pw=new PrintWriter(osw);
            
			// 向对象输出流写出数据，这些数据将存到内存缓冲区中
			for(int j=0;j<texts.length;j++)
			{
					pw.println(texts[j]);
		
			}

			// 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
			pw.flush();		
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	        		urlConn.getInputStream()));
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	//System.out.println("res line:"+line);
	        	response.add(URLDecoder.decode(line));
	        }	        
	            
	        reader.close();
	        urlConn.disconnect();
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public void read_input_parameters(String[] args) {
		int i;
		for (i = 0; (i < args.length) && ((args[i].charAt(0)) == '-'); i++) {
			switch ((args[i].charAt(1))) {
			case 'h':
				print_help();
				System.exit(0);
			case 's':
				i++;
				properties.setProperty("server", args[i]);
				break;
			case 'p':
				i++;
				properties.setProperty("port", args[i]);
				break;
			case 't':
				i++;
				properties.setProperty("tool", args[i]);
				break;	
			case 'i':
				i++;
				properties.setProperty("input", args[i]);
				break;
			case 'o':
				i++;
				properties.setProperty("output", args[i]);
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
		System.out.println("         -s  auxiliary server ip");
		System.out.println("         -p  auxiliary server port");
		System.out.println("         -t  auxiliary tool type: <0 segmenter> <1 classify> et al.");
		System.out.println("         -i  input file");
		System.out.println("         -o  output file");
	}

	public static void main(String[] args) {

		AuxiliaryClient ac = new AuxiliaryClient();
		ac.read_input_parameters(args);
        ac.remote();

	}
	
}
