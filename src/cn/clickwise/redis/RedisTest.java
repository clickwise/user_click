package cn.clickwise.redis;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisTest {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("106.187.35.172", 16379, 10000);// redis服务器地址	
		//Jedis jedis = new Jedis("192.168.110.186", 6379, 1000);// redis服务器地址
		jedis.select(14);
	    
		jedis.ping();
		//jedis.set("md5_cb696edca40ed45e4533dc6c12cd5d30", "单肩");
		System.out.println(jedis.get("hostmatch_www.dm72.com"));
		System.out.println("dbsize:"+jedis.dbSize());
		System.exit(1);
		Set<String> s=jedis.keys("adshow*");
		Iterator it=s.iterator();
		String one_key="";
		int im=0;
		while(it.hasNext())
		{
			one_key=it.next()+"";
			try{
			   // System.out.println(one_key+":"+jedis.get(one_key));
				System.out.println(one_key);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			im++;
			if(im>1000)
			{
				break;
			}
		}
		
		
	}
	
}
