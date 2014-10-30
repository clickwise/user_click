package cn.clickwise.clickad.radiusClient;

import redis.clients.jedis.Jedis;

import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler{
	Jedis jedis;

	
	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
}
