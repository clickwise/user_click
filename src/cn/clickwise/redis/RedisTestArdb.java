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
		//Jedis jedis = new Jedis("192.168.110.186", 16379, 10000);// redis服务器地址
		Jedis jedis = new Jedis("192.168.110.186", 16379, 10000);// redis服务器地址
		jedis.select(10);
	    ////jedis.flushDB();
	    
		String one_item="ae51f2757a48c0af4c0d0628c78f437f";
		//String one_item="user:045f0af0387026ac421b9dc873634853";
		System.out.println("dbsize:"+jedis.dbSize());
		jedis.set(one_item, "test_value");
		//jedis.del(one_item);
		System.out.println(jedis.get(one_item));
		//////jedis.zrem(key, members);
		//jedis.zrange(key, start, end);
		long start=TimeOpera.getCurrentTimeLong();
		Set<String> js=jedis.zrangeByScore(one_item,(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
		//Set<String> js=jedis.zrevrange(one_item, 0, 0);
		System.currentTimeMillis();
		long end=TimeOpera.getCurrentTimeLong();
		System.out.println("Use time:"+(end-start)+" ms");
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
			  System.out.print(" j="+j+"  "+seg_arr[j]);	
			}
			System.out.println();
			for(int j=0;j<seg_arr.length;j++)
			{
				System.out.print(" j="+j+"  "+jedis.get("md5_"+seg_arr[j]));				
			}
			
			System.out.println();
		}
	}
}