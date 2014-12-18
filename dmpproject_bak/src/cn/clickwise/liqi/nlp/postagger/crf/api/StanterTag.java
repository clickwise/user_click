package cn.clickwise.liqi.nlp.postagger.crf.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTag;
import cn.clickwise.liqi.str.basic.SSO;

public class StanterTag extends  PostaggerTag {

	private boolean use_tag_server=false;
	
	private int tag_port;
	
	private String tag_server;
	
	@Override
	public void load_config(Properties prop) {
		// TODO Auto-generated method stub
		use_tag_server=Boolean.parseBoolean(prop.getProperty("use_tag_server"));
		
		if(use_tag_server==true)
		{
		   tag_port=Integer.parseInt(prop.getProperty("tag_port"));
		   tag_server=prop.getProperty("tag_server");
		}
		else
		{
			
		}
		
		
	}

	@Override
	public String tag(String seg_s) throws Exception {
		// TODO Auto-generated method stub
		if(!(SSO.tnoe(seg_s)))
		{
			return "";
		}
		String s=seg_s.trim();
		String tag_s = "";
		
		if(use_tag_server==true)
		{
			String server = tag_server;
			int port = tag_port;
			try {
				Socket socket = new Socket(server, port);
				socket.setSoTimeout(100000);
				seg_s = seg_s + "\n";
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				out.write(seg_s.getBytes());
				out.flush();

				byte[] receiveBuf = new byte[10032];
				in.read(receiveBuf);

				tag_s = new String(receiveBuf);
				socket.close();
			} catch (Exception e) {
				Thread.sleep(1000);
			}
		}
		else
		{
			
		}
		
		
		return tag_s;
	}

	@Override
	public void tag(String seg_file, String tag_file) throws Exception{
		// TODO Auto-generated method stub
		
	}

}
