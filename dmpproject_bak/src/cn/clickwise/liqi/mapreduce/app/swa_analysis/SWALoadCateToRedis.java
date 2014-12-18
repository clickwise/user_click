package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;

import redis.clients.jedis.Jedis;


public class SWALoadCateToRedis {

	
	public Jedis cw_jedis;
	public String redis_cw_ip = "";
	public int redis_port = 6379;
	public int redis_cw_db = 0;
	
	public int load_cate_to_redis(String wci_dir) throws Exception
	{		
		int res=0;
		File wci_dir_file=new File(wci_dir);
		File[] wci_files=wci_dir_file.listFiles();
		long rdb_size=0;
		rdb_size=cw_jedis.dbSize();
		System.out.println("old_db_size:"+rdb_size);
		/*
		if(rdb_size>0)
		{
			System.out.println("redis db has been loaded");
			return 1;
		}
		*/
		String line="";
		String word="";
		String cate="";
		String hn="";
		String small_words="";
		String[] seg_arr=null;
		String wch_info="";
		String sw_info="";
		String[] temp_seg=null;
		
		for(int i=0;i<wci_files.length;i++)
		{
			FileReader fr=new FileReader(wci_files[i]);
			BufferedReader br=new BufferedReader(fr);
			while((line=br.readLine())!=null)
			{
			
				seg_arr=line.split("\001");
				//System.out.println(line+":"+seg_arr.length);
				if((seg_arr.length)<1)
				{
					continue;
				}
				
				wch_info=seg_arr[0].trim();
				sw_info="";
				if(seg_arr.length>1)
				{
				  sw_info=seg_arr[1].trim();
				}
				if((wch_info==null)||(wch_info.equals("")))
				{
					continue;
				}
				temp_seg=wch_info.split("\\s+");
				
				if((temp_seg.length)!=3)
				{
					continue;
				}
				
				word=temp_seg[0].trim();
				cate=temp_seg[1].trim();
				hn=temp_seg[2].trim();
				if(cate.equals("店商"))
				{
					cate="电商";
				}
				if(!(cw_jedis.exists(word)))
				{
					cw_jedis.set(word, cate+"\001"+sw_info);
				}				
			}
			br.close();
			fr.close();			
		}
		
		rdb_size=cw_jedis.dbSize();
		System.out.println("new_db_size:"+rdb_size);
		
		return res;
	}
	
	public void load_local_config() throws Exception
	{	     

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名

		address = address.trim();
	    System.out.println("address in swatsmr:"+address);
		if (address.equals("adt2")) {
			redis_cw_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			redis_cw_ip = "192.168.110.181";
		} else if (address.equals("adt6")) {
			redis_cw_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			redis_cw_ip = "192.168.110.188";
		} else if (address.equals("adt0")) {
			redis_cw_ip = "192.168.110.180";
		} else if (address.equals("hndx_fx_100")) {
			redis_cw_ip = "192.168.1.100";
		}

		redis_port = 6379;
		redis_cw_db = 10;
		
		cw_jedis = new Jedis(redis_cw_ip, redis_port, 100000);// redis服务器地址
		cw_jedis.ping();
		cw_jedis.select(redis_cw_db);
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		SWALoadCateToRedis swalctr=new SWALoadCateToRedis();
		swalctr.load_local_config();
		String wci_dir="output_secondpart_small";
		swalctr.load_cate_to_redis(wci_dir);
	}
	
	
}
