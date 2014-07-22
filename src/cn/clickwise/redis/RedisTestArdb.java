package cn.clickwise.redis;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class RedisTestArdb {

	public static void main(String[] args)
	{
		//Httpsqs_client sqs_fetch=new Httpsqs_client("192.168.110.186","1218","utf-8");
		//Jedis jedis = new Jedis("192.168.110.186", 16379, 1000);// redis服务器地址
		//Httpsqs_client sqs_fetch=new Httpsqs_client("42.62.29.25","1218","utf-8");
		Jedis jedis = new Jedis("192.168.110.186", 16379, 1000);// redis服务器地址
		jedis.select(14);
	    ////jedis.flushDB();
	    
		String one_item="521d0256e1682b6bf5edafd83370b7df";
		//String one_item="user:045f0af0387026ac421b9dc873634853";
		System.out.println("dbsize:"+jedis.dbSize());
		//////jedis.zrem(key, members);
		Set<String> js=jedis.zrangeByScore(one_item,(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
		System.out.println(js.size());
		Iterator js_it=js.iterator();
		String[] seg_arr=null;
		String rec="";
		while(js_it.hasNext())
		{
			rec=js_it.next()+"";
			System.out.println("rec :"+rec);
			seg_arr=rec.split("\001");
			
			for(int j=0;j<seg_arr.length;j++)
			{
				System.out.println("j="+j+"  "+seg_arr[j]);	
				System.out.println("j="+j+"  "+jedis.get("md5_"+seg_arr[j]));				
			}
			
		}
	}
}