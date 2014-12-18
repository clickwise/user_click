package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import redis.clients.jedis.Jedis;


public class SWASWE {

	public Jedis swa_dict_redis;
	public String swa_dict_ip;
	public int swa_dict_port;
	public int swa_dict_db;



	public String line_process(String line) {
        String new_line="";
        String[] seg_arr=null;
        line=line.trim();
        
        String url="";
        String title="";
        String seg_title="";
        String ips="";
        
        
        if((line==null)||(line.equals("")))
        {
        	return new_line;
        }
        
        seg_arr=line.split("\001");
      //  System.out.println("seg_arr.length:"+seg_arr.length);
        if(seg_arr.length!=4)
        {
        	return new_line;
        }
        
        for(int i=0;i<seg_arr.length;i++)
        {
        	url=seg_arr[0].trim();
        	title=seg_arr[1].trim();
        	seg_title=seg_arr[2].trim();
        	ips=seg_arr[3].trim();
        }
        
        
        if((seg_title==null)||(seg_title.equals("")))
        {
        	return new_line;
        }
        
        String[] word_seg=null;
      //  System.out.println("seg_title:"+seg_title);
        word_seg=seg_title.split("\\s+");
        if((word_seg==null)||(word_seg.length<1))
        {
        	return new_line;
        }
        
        String sws="";
        String temp_word="";
        
        for(int i=0;i<word_seg.length;i++)
        {
        	temp_word=word_seg[i].trim();
        	if((temp_word==null)||(temp_word.equals("")))
        	{
        		continue;
        	}
        	if(temp_word.length()<2)
        	{
        		continue;
        	}
        	if(swa_dict_redis.exists(temp_word))
        	{       		
        		sws=sws+temp_word+" ";
        	} 	
        }
        
        sws=sws.trim();
       
        new_line=url+"\001"+title+"\001"+seg_title+"\001"+sws+"\001"+ips;
        
		return new_line;
	}

	public void load_local_config() throws Exception {

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名		
		swa_dict_port = 6379;
		swa_dict_db = 2;

		address = address.trim();
		if (address.equals("adt2")) {
			swa_dict_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			swa_dict_ip = "192.168.110.181";
		} else if (address.equals("adt6")) {
			swa_dict_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			swa_dict_ip = "192.168.110.188";
		}

		swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
		swa_dict_redis.ping();
		swa_dict_redis.select(swa_dict_db);
	}

	public static void main(String[] args) throws Exception {

		SWASWE swe = new SWASWE();
		swe.load_local_config();

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			new_line = swe.line_process(line);

			if ((new_line == null) || (new_line.equals(""))) {
				continue;
			}
			pw.println(new_line);

		}
		br.close();
		isr.close();

		// pw.println(ncont); osw.close(); pw.close();

		/*
		 * SWASegDict swa = new SWASegDict(); String config_file =
		 * "dict/swa_config.properties";
		 * 
		 * swa.load_config(config_file);
		 * 
		 * InetAddress addr = InetAddress.getLocalHost(); String ip =
		 * addr.getHostAddress().toString();// 获得本机IP String address =
		 * addr.getHostName().toString();// 获得本机名
		 * 
		 * String s1 = "小说阅读网-《总裁别再玩了》- 解药②|||小说阅读网 "; // String
		 * s1="爸爸去哪儿第九期_爸爸去哪儿第九期完整版_高清直播 - 米胖百科"; // String
		 * s3="龙王令：妃卿莫属-406：孩子没爹-摘书网提供的小说免费阅读"; String seg_s = ""; seg_s =
		 * swa.seg(s1); String men_s = ""; men_s = swa.merge_sen(seg_s);
		 * 
		 * // String tag_s=""; // tag_s=swa.tag(men_s);
		 * System.out.println("seg_s:" + seg_s); System.out.println("men_s:" +
		 * men_s); // System.out.println("tag_s:"+tag_s);
		 */
	}
	
	
	
}
