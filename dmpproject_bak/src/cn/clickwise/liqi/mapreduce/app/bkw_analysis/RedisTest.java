package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.liqi.time.utils.TimeOpera;

import redis.clients.jedis.Jedis;

public class RedisTest {
	public static void main(String[] args) throws Exception {
		FileWriter fw=new FileWriter(new File("hc.txt"));
		PrintWriter pw=new PrintWriter(fw);
		Jedis jedis = new Jedis("192.168.110.186", 16379, 1000);// redis服务器地址	
		//jedis.select(14);
		jedis.ping();
		System.out.println("dbsize:"+jedis.dbSize());
		/*
		HashMap temp_map=new HashMap();
		temp_map.put(10.0, "javaval");
		jedis.zadd("javatest", temp_map);
	    */
		
		//Set<String> js=jedis.zrangeByScore("only_test35", TimeOpera.str2long("2014-02-08 15:29:05"), TimeOpera.getCurrentTimeLong()+10000);
		System.out.println("start:"+TimeOpera.str2long("2014-02-08 15:29:05"));
		System.out.println("end:"+TimeOpera.getCurrentTimeLong());
		//Set<String> js=jedis.zrangeByScore("only_test35", 1397629000 , 1397629982 );
		//Set<String> js=jedis.zrangeByScore("tbi:6e8c22a4adbdb7d3147919529852f815",(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
		Set<String> js=jedis.zrangeByScore("tbi:f5d1118d462d46a4d309b2001a173086",(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
		System.out.println(js.size());
		Iterator js_it=js.iterator();
		while(js_it.hasNext())
		{
			System.out.println(js_it.next());
		}
		System.exit(0);
		String key_str="*";
		Set<String> s=jedis.keys("a*");
		Iterator sit=s.iterator();
		int i=0;
		String host="";
		String cate="";
		String[] scs={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		System.out.println(scs.length);
		
		for(int k=0;k<scs.length;k++)
		{
			key_str=scs[k]+"*";
			s=jedis.keys(key_str);
			sit=s.iterator();
			while(sit.hasNext())
			{
			  host=sit.next()+"";
			  host=host.trim();
			  cate=jedis.get(host);
			  pw.println(host+"\001"+cate);
			}
			
		}
		while(sit.hasNext())
		{
			System.out.println(sit.next());
		}
		fw.close();
		pw.close();
		//jedis.set("在线观看", "5");
        
		/*
		String url="http://detail.tmall.com/item.htm?id=22137055836&ali_trackid=2:mm_29987156_4066078_13208600:1386082255_3k6_619589038&upsid=7b4163f382f27b2c1f3583309d1dc154&clk1=7b4163f382f27b2c1f3583309d1dc154";
		String prefix="http://detail.tmall.com/item.htm?";
		Pattern id_pat=Pattern.compile("(id=\\d+)");
		Matcher id_mat=id_pat.matcher(url);
		String item_id="";
		if(id_mat.find())
		{
			item_id=id_mat.group(1);
		}
		
		System.out.println("itemid:"+item_id);
		
		String nurl=prefix+item_id;
		nurl=nurl.trim();
		
		System.out.println("nurl:"+nurl);
		*/
		
		/*
		String url="http://item.taobao.com/item.htm?spm=a230r.1.14.200.yratV1&id=35209212716&_u=3vep7l5217c";
		String prefix="http://item.taobao.com/item.htm?";
		Pattern id_pat=Pattern.compile("(id=\\d+)");
		Matcher id_mat=id_pat.matcher(url);
		String item_id="";
		if(id_mat.find())
		{
			item_id=id_mat.group(1);
		}
		
		System.out.println("itemid:"+item_id);
		
		String nurl=prefix+item_id;
		nurl=nurl.trim();
		
		System.out.println("nurl:"+nurl);
		*/
		
			
	}
}