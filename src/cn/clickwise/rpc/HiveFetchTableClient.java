package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HiveFetchTableClient extends Client{
	
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

		HiveFetchTableCommand hftc=(HiveFetchTableCommand)cmd;
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
	        FileWriter fw=new FileWriter(hftc.getResultPath());
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
	
	public static void initRandomFileName(String tmpIdentify,int day,HiveFetchTableCommand hftc)
	{
		hftc.setResultRemoteName(tmpIdentify+"_info_"+day);
		hftc.setResultRemotePath("/tmp/"+tmpIdentify+"_info_"+day);
		
	}
	
	public static void main(String[] args)
	{
		HiveFetchTableClient hftc=new HiveFetchTableClient();
		
		Connection con=new Connection();
		con.setHost("112.67.253.101");
		con.setPort(2733);
		con.setMethod("/hiveFetchTable");
		
		HiveFetchTableCommand hftcmd=new HiveFetchTableCommand();
		String tmpIdentify="remote_table_cookie";
		int day=20141103;
		hftcmd.setDay(day);
		hftcmd.setTmpIdentify(tmpIdentify);
		
		hftcmd.setTableName("auser_cates_keys");
		hftcmd.setKeyFieldName("uid");
		
		hftcmd.setResultName("local_user_table_info.txt");
		hftcmd.setResultPath("temp/local_user_table_info.txt");
		hftcmd.setAreaCode("009");
		HiveFetchTableClient.initRandomFileName(tmpIdentify, day, hftcmd);
		hftcmd.setQueryType(1);
		
		hftc.connect(con);
		hftc.execute(hftcmd);
		
		
	}
	
	
	

}
