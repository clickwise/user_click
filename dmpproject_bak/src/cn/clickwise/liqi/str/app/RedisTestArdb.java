package cn.clickwise.liqi.str.app;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import cn.clickwise.liqi.datastructure.sqs.Httpsqs_client;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class RedisTestArdb {

	public static void main(String[] args)
	{
		//Httpsqs_client sqs_fetch=new Httpsqs_client("192.168.110.186","1218","utf-8");
		//Jedis jedis = new Jedis("192.168.110.186", 16379, 1000);// redis服务器地址
		//Httpsqs_client sqs_fetch=new Httpsqs_client("42.62.29.25","1218","utf-8");
		Jedis jedis = new Jedis("42.62.29.25", 16379, 1000);// redis服务器地址
		jedis.select(14);
	    /////jedis.flushDB();
		String one_item="730bdddf11e9d87dd8cf4f7b6a8b0275";
		//String one_item="user:045f0af0387026ac421b9dc873634853";
		System.out.println("dbsize:"+jedis.dbSize());
		//////jedis.zrem(key, members);
		Set<String> js=jedis.zrangeByScore(one_item,(long) ((double)TimeOpera.str2long("2014-02-08 15:29:05")/(double)1000), (long)((double)(TimeOpera.getCurrentTimeLong()+100000)/(double)1000));
		System.out.println(js.size());
		Iterator js_it=js.iterator();
		while(js_it.hasNext())
		{
			System.out.println(js_it.next());
		}
	}
}
