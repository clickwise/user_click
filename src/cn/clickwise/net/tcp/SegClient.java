package cn.clickwise.net.tcp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import cn.clickwise.liqi.str.basic.SSO;

public class SegClient implements Runnable{

	public String server_addr="";
	public int server_port=0;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			Socket sock =new Socket(server_addr,server_port);
			InputStream in = sock.getInputStream();
			
			OutputStream out = sock.getOutputStream();
			char[] heartbeat={'0','0'};
			
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

	public static void main(String[] args)
	{
	   if(args.length!=2)
	   {
		   System.err.println("Usage:<SegClient> <server_addr> <server_pot>");
		   System.exit(1);
	   }
		
	   SegClient sc=new SegClient();
	   sc.server_addr=args[0];
	   sc.server_port=Integer.parseInt(args[1]);
	   
	   
	   
	   	
	}
	
}
