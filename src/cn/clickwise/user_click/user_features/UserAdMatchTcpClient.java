package cn.clickwise.user_click.user_features;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;
import cn.clickwise.net.http.admatchtest.MatchAdTest;
import cn.clickwise.net.tcp.SegClient;

public class UserAdMatchTcpClient implements Runnable{

	public String userhismatch_server_addr = "";
	public int userhismatch_server_port = 0;
	public String log_file="";
	public   MatchAdTest mat=new MatchAdTest();
	public UserAdMatchTcpClient(){
		Properties prop = null;
		try {
			prop = ConfigFileReader.getPropertiesFromFile("user_click.config");
		} catch (Exception e) {
            System.out.println(e.getMessage());
		}
		userhismatch_server_port=Integer.parseInt(prop.getProperty("userhismatch_server_port"));
		userhismatch_server_addr=prop.getProperty("userhismatch_server_addr");
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			
			FileInputStream log_fis = null;
			InputStreamReader log_isr = null;
	         
			BufferedReader log_br = null;
			String log_record = "";
			String json_record = "";
			
			
			Socket sock =new Socket(userhismatch_server_addr,userhismatch_server_port);
			InputStream in = sock.getInputStream();
			OutputStream out = sock.getOutputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			OutputStreamWriter osw = new OutputStreamWriter(out);
			PrintWriter pw = new PrintWriter(osw);

			try {
				// fr=new FileReader(input_file);
				log_fis = new FileInputStream(log_file);
				log_isr = new InputStreamReader(log_fis);
				log_br = new BufferedReader(log_isr);
				while ((log_record = log_br.readLine()) != null) {
	               if(SSO.tioe(log_record))
	               {
	            	   continue;
	               }
	              log_record=log_record.trim();	
	        	  String cookie=log_record;
	        	  pw.println(cookie+"\001"+mat.testMatchAd(cookie));
	        	  
				}
				log_br.close();

			} catch (Exception e) {
			}
			
			
            sock.close();
		} catch (Exception e) {

		}
		
		
	}

	public static void main(String[] args)
	{
		if(args.length!=1)
		{
			System.err.println("Usage:<log_file>");
			System.exit(1);
		}
		
	   UserAdMatchTcpClient uamtc=new UserAdMatchTcpClient();
	   uamtc.log_file=args[0];
	   Thread nt=new Thread(uamtc);
	   nt.start();
	   	
	}
}
