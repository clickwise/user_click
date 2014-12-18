package cn.clickwise.liqi.nlp.segmenter.crf.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 提供stanford crf segmenter 的简单调用接口
 * 输入普通文本，输出分词结果
 * 实例化对象时加载新模型或调用后台server可选 
 * @author lq
 *
 */
public class StanterSeg extends SegmenterSeg{
   
	private boolean use_seg_server=false;
	
	private int seg_port=8092;
	
	private String seg_server="";
	
	
	/**
	 * 读取配置信息
	 * @param prop
	 */
	public void load_config(Properties prop) 
	{
		use_seg_server=Boolean.parseBoolean(prop.getProperty("use_seg_server"));
		//System.out.println("use_seg_server:"+use_seg_server);
		if(use_seg_server==true)
		{
		   seg_port=Integer.parseInt(prop.getProperty("seg_port"));
		  // System.out.println("seg_port:"+seg_port);
		   seg_server=prop.getProperty("seg_server");
		  // System.out.println("seg_server:"+seg_server);
		}
		else
		{
			
		}
	}
	
	/**
	 * 输入普通文本，输出分词结果
	 * @return
	 */
	public String seg(){
		String seg_s="";
	
			
		return seg_s;
	}

	

	@Override
	public void seg(String plainFile, String seg_file) throws Exception{
		// TODO Auto-generated method stub
		
	}


	@Override
	public String seg(String text) throws Exception{
		// TODO Auto-generated method stub
		if(!(SSO.tnoe(text)))
		{
			return "";
		}
		String s=text.trim();
		String seg_s = "";
		if(use_seg_server==true)
		{
			s = s + "\n";
			
			String server = seg_server;
			int port = seg_port;
			try {
				Socket socket = new Socket(server, port);
				socket.setSoTimeout(100000);
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
		}
		else
		{
			
		}
		
		
		
		return seg_s;
	}
	
	
	/**
	 * 启动分词服务
	 */
	public void start_seg_server()
	{
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		StanterSeg ss=new StanterSeg();
		Properties prop=new Properties();
		prop.setProperty("seg_server", "42.62.29.25");
		prop.setProperty("seg_port", "8092");
		prop.setProperty("use_seg_server", "true");
		ss.load_config(prop);
		String text="北京交通大学";
		System.out.println(ss.seg(text));
	}
	
}
