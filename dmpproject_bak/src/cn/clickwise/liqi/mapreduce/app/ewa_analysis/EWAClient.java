package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;


public class EWAClient {

	
	public String getCateStr(String s) throws Exception
	{
		//String s="冬季新款 以纯男装棉服 韩版加厚 冬装 撞色棉衣 潮 棉袄 男士 冬衣外套";
		String seg_server="110.96.34.211";
		int seg_port=8096;
		String server = seg_server;
		int port = seg_port;
		String seg_s="";
		try {
			Socket socket = new Socket(server, port);
			socket.setSoTimeout(10000);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(s.getBytes());
			out.flush();

			byte[] receiveBuf = new byte[10032 * 8];
			in.read(receiveBuf);

			seg_s = new String(receiveBuf);
			socket.close();
		} catch (Exception e) {
			Thread.sleep(1000);
		}
		return seg_s;
		//System.out.println("seg_s:"+seg_s);
	}
	public static void main(String[] args) throws Exception
	{
		 EWAClient ewa=new  EWAClient ();
		
		 InputStreamReader isr=new  InputStreamReader(System.in);
		 BufferedReader br=new BufferedReader(isr);
			    String s;
			    String cate="";
			    String word="";
			    try {
			     System.out.println("type a line:");
			     String input_s="";
			     String a="";
			     while(true)
			     {
			           input_s="";
			          
			    	   while(!(((a=br.readLine()).trim()).equals("")))
			    	   {
			    		   input_s=input_s+a;
			    	   }
			    	   input_s=input_s.trim();
			    	 
				    	System.out.println("input_line is:"+input_s);
				        String res=ewa.getCateStr(input_s);
				        res=res.trim();
                                        System.out.println("res is :"+res);
				        String[] seg_arr=res.split("\001");
				        if(seg_arr.length!=2)
				        {
				        	System.out.println("error");
				        }
				        else
				        {
				        	cate=seg_arr[0].trim();
				        	word=seg_arr[1].trim();
				           System.out.println("cate_str is:"+cate+"|||"+word);
				        }
			    
			     }
			    }
			    catch(Exception e)
			    {
			    	
			    }

	}
}
