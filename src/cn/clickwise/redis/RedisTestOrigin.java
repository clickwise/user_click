package cn.clickwise.redis;

import redis.clients.jedis.Jedis;

public class RedisTestOrigin {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("180.96.26.203", 6379, 1000);// redis服务器地址
		jedis.select(10);
		jedis.ping();
		//jedis.set("md5_cb696edca40ed45e4533dc6c12cd5d30", "单肩");
		System.out.println(jedis.get("jhnjlhZ5b+KFZUPjuKTVjA"));
		//180.109.191.116 3       jhnjlhZ5b+KFZUPjuKTVjA==        2014-10-28 15:42:49
		
	}
	
}
