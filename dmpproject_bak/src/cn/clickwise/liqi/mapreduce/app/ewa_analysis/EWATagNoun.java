package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;


public class EWATagNoun {


	public String tag_server = "";
	public int tag_port = 0;

	
	public String keyword_extract(String text) {
		String k_s = "";
		String[] seg_arr = text.split("\\s+");
		Vector new_word_arr = new Vector();
		String key_word="";
		String token="";
		for (int i = 0; i < seg_arr.length; i++) {
			
			if (((seg_arr[i].indexOf("/NN")) != -1)
					|| ((seg_arr[i].indexOf("/NR")) != -1)|| ((seg_arr[i].indexOf("/NT")) != -1)) {
				key_word = seg_arr[i];
				if ((seg_arr[i].indexOf("/NN")) != -1) {
					key_word = key_word.replaceAll("/NN", "");
				} else if ((seg_arr[i].indexOf("/NR")) != -1) {
					key_word = key_word.replaceAll("/NR", "");
				}else if ((seg_arr[i].indexOf("/NT")) != -1) {
					key_word = key_word.replaceAll("/NT", "");
				}
				
				
				
				key_word = key_word.trim();
				if (key_word.length() > 1) {
					new_word_arr.add(key_word);
				}				
			}
			else
			{
			  token=seg_arr[i].trim();
			  if((token==null)||(token.equals("")))
			  {
			    	continue;
			  }
			  token=token.replaceAll("/.*", "");
			  token=token.trim();
			  if((token==null)||(token.equals("")))
			  {
				 continue;
			  }
			  if(token.length()>3)
			  {
				 new_word_arr.add(token);
								
			  }	
			}
			
		}
		
		String word="";
		k_s="";
		for(int i=0;i<new_word_arr.size();i++)
		{
			word=new_word_arr.get(i)+"";
			word=word.trim();
			word=word.replaceAll("\\-LRB\\-", "");
			word=word.replaceAll("\\-RRB\\-", "");
			word=word.replaceAll("\\\\/", "");
			word=word.replaceAll("\\\\\\*", "");
			word=word.trim();
			if((word==null)||(word.equals("")))
			{
				continue;
			}
			if(isNumbers(word))
			{
				continue;
			}
			k_s=k_s+word+" ";
		}
		k_s=k_s.trim();
		if(k_s==null)
		{
		  k_s="";	
		}
		
		
		
		return k_s;
		
	}
	public String tag(String seg_s) throws Exception {
		String tag_s = "";
		String server = tag_server;
		int port = tag_port;
		try {
			Socket socket = new Socket(server, port);
			socket.setSoTimeout(10000);
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
		return tag_s;

	}
	
	public String line_process(String line) {

		//System.out.println("line:"+line);
		String new_line = "";
		String[] seg_arr = line.split("\001");

		String url = "";
		String cate = "";
		String title = "";
		String seg_title = "";
        String tag_s="";
        String key_s="";
		String[] temp_seg = null;
		if (seg_arr != null && seg_arr.length > 3) {
			url = seg_arr[0].trim();
			cate = seg_arr[1].trim();
		   if((cate==null)||(cate.equals("")))
		   {
			   return "";
			   
		   }
			title = seg_arr[2].trim();
			seg_title=seg_arr[3].trim();
			
			if ((seg_title != null) && (!seg_title.equals("")) && (seg_title.length() > 5)) {
				try {
	                tag_s=tag(seg_title);
					tag_s = tag_s.trim();
					if ((tag_s != null) && (!tag_s.equals("")) && (tag_s.length() > 5)) {
					key_s=keyword_extract(tag_s);
					  //System.out.println("seg_title:"+seg_title);
					  //System.out.println("tag_s:"+tag_s);
					  //System.out.println("key_s:"+key_s);
					  new_line=cate+"\001"+key_s;
					}
                  
				} catch (Exception e) {

				}

			}

		}
        new_line=new_line.trim();
        if((new_line==null))
        {
        	new_line="";
        }
		return new_line;
	}
	
	
	public String simple_line_process(String seg_line) {

		//System.out.println("line:"+line);
		String new_line = "";

		String seg_title = "";
        String tag_s="";
        String key_s="";
		seg_title=seg_line.trim();
		
	
			if ((seg_title != null) && (!seg_title.equals("")) && (seg_title.length() > 5)) {
				try {
	                tag_s=tag(seg_title);
					tag_s = tag_s.trim();
					if ((tag_s != null) && (!tag_s.equals("")) && (tag_s.length() > 5)) {
					key_s=keyword_extract(tag_s);
					  //System.out.println("seg_title:"+seg_title);
					  //System.out.println("tag_s:"+tag_s);
					  //System.out.println("key_s:"+key_s);
					 new_line=key_s;
					}
                  
				} catch (Exception e) {

				}

			}

		
        new_line=new_line.trim();
        if((new_line==null))
        {
        	new_line="";
        }
		return new_line;
	}
	
	public boolean isNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	public void load_local_config() throws Exception {

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名
		/*
		 * FileInputStream fis = new FileInputStream(config_file); Properties
		 * prop = new Properties(); prop.load(fis); seg_server =
		 * prop.getProperty("seg_server"); seg_port =
		 * Integer.parseInt(prop.getProperty("seg_port")); tag_server =
		 * prop.getProperty("tag_server"); tag_port =
		 * Integer.parseInt(prop.getProperty("tag_port"));
		 * 
		 * swa_dict_ip = prop.getProperty("swa_dict_ip"); swa_dict_port =
		 * Integer.parseInt(prop.getProperty("swa_dict_port")); swa_dict_db =
		 * Integer.parseInt(prop.getProperty("swa_dict_db"));
		 * 
		 * swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);//
		 * redis服务器地址 swa_dict_redis.ping(); swa_dict_redis.select(swa_dict_db);
		 * fis.close();
		 */

		tag_port = 8093;

		address = address.trim();
		address="adt2";
		if (address.equals("adt2")) {
			tag_server = "192.168.110.182";
		} else if (address.equals("adt1")) {
			tag_server = "192.168.110.181";
		} else if (address.equals("adt6")) {
			tag_server = "192.168.110.186";
		} else if (address.equals("adt8")) {
			tag_server = "192.168.110.188";
		}else if (address.equals("adt0")) {
			tag_server = "192.168.110.180";
		}else if (address.equals("hndx_fx_202")) {
			tag_server = "192.168.210.202";
		}
		
		//tag_server = "192.168.1.100";
		
	}
	
	public void load_config(Properties prop) throws Exception {


		tag_port = Integer.parseInt(prop.getProperty("tag_port"));
		tag_server=prop.getProperty("tag_server");
		
		//tag_server = "192.168.1.100";
		
	}
	
	public static void main(String[] args) throws Exception {

		EWATagNoun ewa = new EWATagNoun();
		ewa.load_local_config();

		InputStreamReader isr = new InputStreamReader(System.in);
		//FileReader fr=new FileReader(new File("input/test.txt"));
		BufferedReader br = new BufferedReader(isr);
		//BufferedReader br = new BufferedReader(fr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			new_line = ewa.line_process(line);

			if ((new_line == null) || (new_line.equals(""))) {
				continue;
			}
			pw.println(new_line);

		}

		br.close();
		isr.close();
		osw.close();
		pw.close();

	}
	
	
}
