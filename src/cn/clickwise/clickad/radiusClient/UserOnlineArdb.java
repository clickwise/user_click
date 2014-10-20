package cn.clickwise.clickad.radiusClient;

import redis.clients.jedis.Jedis;

public class UserOnlineArdb extends UserOnline{

	private Jedis jedis=null;
	
	@Override
	public void connect(OnlineDatabase od) {
		// TODO Auto-generated method stub
		
		jedis = new Jedis(od.getIp(), od.getPort(), 10000);// redis服务器地址
		jedis.select(Integer.parseInt(od.getDatabase()));
		
	}

	@Override
	public void update(Record rec) {
	
		int status=rec.getAcctStatusType();
		
		
		if(status==1)//上线
		{	
			jedis.set(rec.getUserName(), rec.getFramedIpAddress());
			jedis.set(rec.getFramedIpAddress(), rec.getUserName());
		}
		else if(status==2)//下线
		{
			jedis.del(rec.getUserName());
			jedis.del(rec.getFramedIpAddress());
			
		}
		else if(status==3)//心跳
		{
			jedis.set(rec.getUserName(), rec.getFramedIpAddress());
			jedis.set(rec.getFramedIpAddress(), rec.getUserName());
		}
		
	}

	@Override
	public void close(OnlineDatabase od) {
		// TODO Auto-generated method stub
		
	}
	

}
