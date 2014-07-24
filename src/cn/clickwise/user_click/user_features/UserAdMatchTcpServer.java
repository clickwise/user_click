package cn.clickwise.user_click.user_features;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

import cn.clickwise.liqi.file.uitls.JarFileReader;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;
import cn.clickwise.user_click.seg.AnsjSeg;
import cn.clickwise.user_click.seg.AnsjSegTcpServer;

public class UserAdMatchTcpServer implements Runnable{

	public int userhismatch_server_port = 0;

	public UserAdMatchTcpServer()
	{
		Properties prop = null;
		try {
			prop = ConfigFileReader.getPropertiesFromFile("user_click.config");
		} catch (Exception e) {
            System.out.println(e.getMessage());
		}
		userhismatch_server_port=Integer.parseInt(prop.getProperty("userhismatch_server_port"));	
	}
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(userhismatch_server_port);

			while (true) {
				try {
					Socket sock = servSock.accept();
					InputStream in = sock.getInputStream();
					OutputStream out = sock.getOutputStream();
					InputStreamReader isr = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(isr);
					OutputStreamWriter osw = new OutputStreamWriter(out);
					PrintWriter pw = new PrintWriter(osw);
                    String line="";
                    String seg_text="";
                    while((line=br.readLine())!=null)
                    {      
                       if(SSO.tioe(seg_text))
                       {
                    	   seg_text="";
                       }
                       pw.println(seg_text);
                       pw.flush();
                    }
                    sock.close();
				} catch (Exception e) {

				}

			}

		} catch (Exception e) {

		}

	}

	public static void main(String[] args) {

		
		UserAdMatchTcpServer uamts = new UserAdMatchTcpServer();
		
		Thread serverThread = new Thread(uamts);
		serverThread.start();

	}
	
	
}
