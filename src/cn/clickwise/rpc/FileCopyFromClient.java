package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileCopyFromClient extends Client {
	
	private HttpURLConnection urlCon;
	
	@Override
	public void connect(Connection con) {
		
		try {
			URL url = new URL("http://" + con.getHost() + ":" + con.getPort()
					+ con.getMethod());

			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setRequestProperty("Content-type", "application/x-java-serialized-object");
            urlCon.setUseCaches(false);
			// 设定请求的方法为"POST"，默认是GET
			urlCon.setRequestMethod("POST");
			urlCon.setConnectTimeout(1000000);
			urlCon.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public State execute(Command cmd) {
		FileCopyFromCommand fcc=(FileCopyFromCommand)cmd;
		State state=new State();
		OutputStream outputStream = null;

		try {
			outputStream = urlCon.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(cmd);
		
			oos.flush();
			oos.close();
           
           
            //读取远程文件
	        BufferedReader br = new BufferedReader(new InputStreamReader(
	        		urlCon.getInputStream()));
	        
	        //写入本地文件
	        FileWriter fw=new FileWriter(fcc.getLocalPath());
	        PrintWriter pw=new PrintWriter(fw);
	        
	        String line="";
	        while((line=br.readLine())!=null)
	        {
	        	pw.println(line);
	        }

	        br.close();
	        fw.close();
	        pw.close();
	        
	        
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	public HttpURLConnection getUrlCon() {
		return urlCon;
	}

	public void setUrlCon(HttpURLConnection urlCon) {
		this.urlCon = urlCon;
	}
	
	public static void main(String[] args)
	{
		FileCopyFromCommand fcc=new FileCopyFromCommand();
		fcc.setLocalName("log_easy_server.txt");
		fcc.setLocalPath("temp/log_easy_server.txt");
		fcc.setRemoteName("log_easy_server.txt");
		fcc.setRemotePath("/home/hadoop/lq/test/log_easy_server.txt");
		
		FileCopyFromClient ec=new FileCopyFromClient();
		Connection con=new Connection();
		con.setHost("192.168.110.186");
		con.setPort(2733);
		con.setMethod("/fileCopyFrom");
		ec.connect(con);
		ec.execute(fcc);
	}

}
