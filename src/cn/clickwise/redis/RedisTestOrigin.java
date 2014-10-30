package cn.clickwise.redis;

import redis.clients.jedis.Jedis;

public class RedisTestOrigin {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("180.96.26.203", 6379, 1000);// redis服务器地址
		//jedis.select(10);
		jedis.ping();
		//jedis.set("md5_cb696edca40ed45e4533dc6c12cd5d30", "单肩");
		System.out.println(jedis.get("iHtEieImICyxtpYu4+ugwQ=="));
		//222.187.184.103 1       iHtEieImICyxtpYu4+ugwQ==        2014-10-28 18:25:17
		
	}
	
}
