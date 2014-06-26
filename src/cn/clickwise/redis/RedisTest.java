package cn.clickwise.redis;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisTest {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("192.168.110.182", 6379, 10000);// redis服务器地址	
		//Jedis jedis = new Jedis("192.168.110.186", 6379, 1000);// redis服务器地址
		jedis.select(2);
	    
		jedis.ping();
		System.out.println("dbsize:"+jedis.dbSize());
		//System.exit(1);
		Set<String> s=jedis.keys("山东大学*");
		Iterator it=s.iterator();
		String one_key="";
		int im=0;
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
			im++;
			if(im>1000)
			{
				break;
			}
		}
		
		
	}
	
}
