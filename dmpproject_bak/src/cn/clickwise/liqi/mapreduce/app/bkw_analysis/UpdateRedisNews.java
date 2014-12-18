package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;


public class UpdateRedisNews {
	public Jedis jedis;
	public String redis_host_ip = "";
	public int redis_port = 6379;
	public int redis_host_cate_db = 0;
	public Jedis cated_redis;
	public String redis_cated_words_ip = "";
	public int redis_cated_words_db = 0;
	
	public void update_rnews(String info_file) throws Exception
	{
		redis_host_ip="192.168.110.180";
		redis_port = 6379;
		redis_host_cate_db = 8;
	    redis_cated_words_ip = "192.168.110.188";
	    redis_cated_words_db = 7;
			
		jedis = new Jedis(redis_host_ip, redis_port, 100000);// redis服务器地址
		jedis.ping();
		jedis.select(redis_host_cate_db);
		
		
		
		cated_redis = new Jedis(redis_cated_words_ip, redis_port, 100000);
		cated_redis.ping();
		cated_redis.select(redis_cated_words_db);
		
		FileReader fr=new FileReader(new File(info_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String[] seg_arr=null;
		String keyword="";
		String old_cate="";
		String uvs="";
		String url_info="";
		Vector url_vec=null;
		String temp_url="";
		String new_cate="";
		while((line=br.readLine())!=null)
		{
			seg_arr=(line.trim()).split("\001");
			if(seg_arr.length<3)
			{
				continue;
			}
			keyword=seg_arr[0].trim();
			old_cate=seg_arr[1].trim();
			uvs=seg_arr[2].trim();
			url_info="";
			url_vec=new Vector();
			for(int oj=3;oj<seg_arr.length;oj++)
			{
				temp_url=seg_arr[oj].trim();
				if(!(temp_url.equals("")))
				{
					url_vec.add(temp_url);
				}	
				url_info=url_info+temp_url+"\001";
			}
			url_info=url_info.trim();
			new_cate=predict_preliminar(url_vec);
			new_cate=new_cate.trim();
			
			if((new_cate.equals("新闻资讯"))&&(keyword.length()>3)&&(!Pattern.matches("[0-9a-zA-Z\\.]*", keyword))&&(keyword.indexOf("官网")==-1)&&(keyword.indexOf("参考消息")==-1)&&(keyword.indexOf("新浪")==-1)&&(keyword.indexOf("凤凰")==-1)&&(keyword.indexOf("日报")==-1)&&(keyword.indexOf("早报")==-1)&&(!keyword.equals("红十字会"))&&(!keyword.equals("腾讯微博"))&&(!keyword.equals("新浪微博"))&&(!keyword.equals("搜狐新闻"))&&(!keyword.equals("南方周末"))&&(!keyword.equals("腾讯新闻"))&&(!keyword.equals("快乐男声")))
			{
				//System.out.println(keyword+" "+new_cate+" "+uvs+" "+url_info);
				System.out.println(keyword+" "+	cated_redis.get(keyword));
				//cated_redis.set(keyword, new_cate+"\001"+uvs+"\001"+url_info);
				//jedis.set(keyword, new_cate+"\001"+uvs+"\001"+url_info);
			}
						
		}
		
		
	}
	
	public String predict_preliminar(Vector trueurls_top_vec)
			throws Exception {
		String preli_cate = "NA";
		URI sturi = null;
		String sturl = "";
		String sthost = "";
		Hashtable tag_res = new Hashtable();
		String cate_res = "";
		int old_res_c = 0;
		for (int i = 0; i < trueurls_top_vec.size(); i++) {
			sturl = trueurls_top_vec.get(i) + "";
			sturl = sturl.trim();
			try {
				sturi = new URI(sturl);
				sthost = sturi.getHost();
			} catch (Exception e) {
			}

			if (sthost == null) {
				continue;
			}

			if (sthost.equals("")) {
				continue;
			}
			sthost = sthost.trim();

			cate_res = jedis.get(sthost);
			if (cate_res == null) {
				continue;
			}
			cate_res = cate_res.trim();
			if (!tag_res.containsKey(cate_res)) {
				tag_res.put(cate_res, 1);
			} else {
				old_res_c = Integer.parseInt(tag_res.get(cate_res) + "");
				old_res_c++;
				tag_res.remove(cate_res);
				tag_res.put(cate_res, old_res_c);
			}
		}

		Enumeration tag_keys = tag_res.keys();
		String maxTag = "NA";
		int maxTagCount = 0;
		String temp_key = "";
		int temp_count = 0;
		while (tag_keys.hasMoreElements()) {
			temp_key = tag_keys.nextElement() + "";
			// System.out.println("temp_key:"+temp_key);
			temp_count = Integer.parseInt(tag_res.get(temp_key) + "");
			if (temp_count > maxTagCount) {
				maxTagCount = temp_count;
				maxTag = temp_key;
			}
		}

		if (maxTag.equals("-1")) {
			preli_cate = "NA";
		} else {
			preli_cate = maxTag;
		}
		return preli_cate;
	}
	
	
	public static void main(String[] args)
	{
	   
		UpdateRedisNews urn=new UpdateRedisNews();
		String info_file="input/video_cate_news.txt";
		try{
		urn.update_rnews(info_file);
		}
		catch(Exception e)
		{
			
		}
		
		
		
	}
	
	
	
}
