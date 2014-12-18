package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import redis.clients.jedis.Jedis;


public class SWAWordCate {

	
	public Jedis swa_dict_redis;
	public String swa_dict_ip;
	public int swa_dict_port;
	public int swa_dict_db;
	
	public void load_local_config() throws Exception {

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名

		swa_dict_port = 6379;
		swa_dict_db = 5;

		address = address.trim();
		address="adt2";
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
	
	
	public String line_process(String line) {

		String new_line = "";
		String[] seg_arr = line.split("\001");
		Vector host_vec=new Vector();
        if((seg_arr.length)<2)
        {
        	return "NA";
        }
	 
        String host_first=seg_arr[0];
        String sw="";
        sw=seg_arr[0].trim();
        host_first=host_first.trim();
        if(( host_first==null)||( host_first.equals("")))
        {
        	return "NA";
        }
        
        String[] sw_seg=null;
        sw_seg=host_first.split("\\s+");
        if(sw_seg.length<3)
        {
        	return "NA";
        }
        sw=sw_seg[0].trim();
         
        if((sw==null)||(sw.equals("")))
        {
        	return "NA";
        }
        
        String host="";
        host=sw_seg[1].trim();
        if((host!=null)&&(!(host.equals(""))))
        {
        	host_vec.add(host);
        }
        
        String host_ips="";
        String[] temp_seg=null;
        
        for(int i=1;i<seg_arr.length;i++)
        {
        	//System.out.println("host_ips:"+host_ips);
        	host_ips=seg_arr[i].trim();
            if((host_ips==null)||(host_ips.equals("")))
            {
            	continue;
            }
            temp_seg=host_ips.split("\\s+");
            host=temp_seg[0].trim();
            if((host==null)||(host.equals("")))
            {
            	continue;
            }
            host_vec.add(host);           
        }
        if((host_vec.size())<1)
        {
        	return "NA";
        }
        new_line=getCateInfo(host_vec);
        new_line=sw+" "+new_line;
		return new_line;
	}
	
	
	public String getCateInfo(Vector vec)
	{
		String cate_info="";
		Hashtable<String,Integer> cate_hash=new Hashtable<String,Integer>();
		String host="";
		String cate="";
		int old_cnum=0;
		for(int i=0;i<vec.size();i++)
		{
			host=vec.get(i)+"";
			cate=swa_dict_redis.get(host);
			//System.out.println(host+"  "+cate);
			if(cate==null)
			{
				continue;
			}
			cate=cate.trim();
            if((cate==null)||(cate.equals("")))
            {
            	continue;
            }
            if(!(cate_hash.containsKey(cate)))
            {
            	cate_hash.put(cate, 1);
            }
            else
            {
            	old_cnum=cate_hash.get(cate);
            	old_cnum=old_cnum+1;
            	cate_hash.remove(cate);
            	cate_hash.put(cate, old_cnum);
            }		
		}
		
		
		Enumeration cate_enum=cate_hash.keys();
		String temp_cate="";
		int temp_cnum=0;
		while(cate_enum.hasMoreElements())
		{
			temp_cate=cate_enum.nextElement()+"";
			temp_cnum=cate_hash.get(temp_cate);
			cate_info=cate_info+temp_cate+" "+temp_cnum+"\001";
		}
		cate_info=cate_info.trim();
		cate_info="\001"+cate_info;
		return cate_info;
	}
	public static void main(String[] args) throws Exception
	{
		 SWAWordCate swa = new SWAWordCate(); 
		  swa.load_local_config();
		  
		  InputStreamReader isr = new InputStreamReader(System.in);
		 // FileReader fr=new FileReader(new File("input/a.txt"));
		  BufferedReader br = new BufferedReader(isr);
		  String line = "";
		  
		  OutputStreamWriter osw = new OutputStreamWriter(System.out);
		  PrintWriter pw = new PrintWriter(osw);
		  
		  String new_line = ""; 
		  while ((line = br.readLine()) != null) { 
		     line =line.trim();
		     if ((line == null) || (line.equals(""))) 
		     { 
			   continue; 
		     }
		  
		     new_line = swa.line_process(line);
		     new_line=new_line.trim();
		     if ((new_line == null) || (new_line.equals(""))||(new_line.equals("NA")))
		    { 
			  continue; 
		    }
		    pw.println(new_line);
		  
		  }
		  
		  br.close(); 
		  isr.close();
		  //fr.close();
		  osw.close(); 
		  pw.close();
		  	  
	}
}
