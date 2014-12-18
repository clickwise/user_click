package cn.clickwise.liqi.str.app;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisTest {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("42.62.29.25", 16379, 1000);// redis服务器地址	
		//Jedis jedis = new Jedis("192.168.110.186", 6379, 1000);// redis服务器地址
		jedis.select(14);
	    
		jedis.ping();
		System.out.println("dbsize:"+jedis.dbSize());
		System.exit(1);
		String key_str="*";
		Set<String> s=jedis.keys("*");
		Iterator it=s.iterator();
		String one_key="";
		while(it.hasNext())
		{
			one_key=it.next()+"";
			try{
			System.out.println(one_key+":"+jedis.get(one_key));
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		
	}
	
}
