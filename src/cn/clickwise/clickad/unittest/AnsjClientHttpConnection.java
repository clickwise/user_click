package cn.clickwise.clickad.unittest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class AnsjClientHttpConnection extends AuxiliaryTestBase {

	@Override
	public ArrayList<String> testmul(String[] texts) {
		// TODO Auto-generated method stub
		ArrayList<String> response=new ArrayList<String>();
        
		try {
			URL url = new URL("http://127.0.0.1:8080/seg?s=");
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
			//oos.writeObject(new String("<start>"));
			for(int j=0;j<texts.length;j++)
			{
				if(j<texts.length-1)
				{
					pw.println(texts[j]);
					//oos.writeObject(texts[j]+" <br> \n");
				}
				else
				{
					//oos.writeObject(texts[j]+" <end>");
				}
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
	
	public String zipMulLines(String[] lines)
	{
	   String zip="<start>";
		for (int i = 0; i < lines.length; i++) {
			if (i < lines.length - 1) {
				zip += (lines[i] +" <br>");
			} else {
				zip += (lines[i] + "<end>");
			}
		}
	   
	   return zip;
	}
	
	public String[] unzipMulLines(String body)
	{
		body = SSO.midstrs(body, "<start>", "<end>");
		String[] lines = body.split("<br>");
		return lines;
	}
	
	public static void main(String[] args) throws Exception
	{
		AnsjClientHttpConnection achc=new AnsjClientHttpConnection();
		
		String[] unsegs = FileToArray
				.fileToDimArr("temp/seg_test/tb_test5.txt");
			
		PrintWriter pw = FileWriterUtil
				.getPW("temp/seg_test/tb_test_bat.txt");
		
		long start_time = TimeOpera.getCurrentTimeLong();
		
		ArrayList<String> bodys=achc.testmul(unsegs);
		
		for(int i=0;i<bodys.size();i++)
		{
			pw.println(bodys.get(i));
		}
		
		long end_time = TimeOpera.getCurrentTimeLong();

		System.out.println( " total doc, use time:"
				+ ((double) (end_time - start_time) / (double) 1000)
				+ " seconds");
		pw.close();
	}

	@Override
	public String test(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}

}
