package cn.clickwise.clickad.radiusClient;

import redis.clients.jedis.Jedis;

public class RedisOnlineDatabase extends OnlineDatabase{

	private Jedis jedis=null;
	
	@Override
	public void connect(RedisCenter rc) {
		
		jedis = new Jedis(rc.getIp(), rc.getPort(), 1000);
		jedis.select(Integer.parseInt(rc.getDatabase()));
		jedis.ping();
		
	}

	@Override
	public void update(RecordLight rec) {
	
		if(rec.getAcctStatusType()==1)//上线
		{
			jedis.set(rec.getFramedIpAddress(), rec.getUserName());
			jedis.set(rec.getUserName(), rec.getFramedIpAddress());
		}
		else if(rec.getAcctStatusType()==2)//下线
		{
			jedis.del(rec.getFramedIpAddress());
			jedis.del(rec.getUserName());
		}
		else if(rec.getAcctStatusType()==3)//心跳
		{
			jedis.set(rec.getFramedIpAddress(), rec.getUserName());
			jedis.set(rec.getUserName(), rec.getFramedIpAddress());
		}
	}

}
