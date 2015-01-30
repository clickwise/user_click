package cn.clickwise.clickad.radiusReform;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisOnlineDatabase extends OnlineDatabase {

	private Jedis jedis = null;
	
	private JedisPool pool;

	@Override
	public void connect(RedisCenter rc) {

		jedis = new Jedis(rc.getIp(), rc.getPort(), 1000);
		jedis.select(Integer.parseInt(rc.getDatabase()));
		jedis.ping();

	}
	
	@Override
	public void connectJedis(Jedis jedis) {
		this.jedis=jedis;
		//this.jedis.ping();
		
	}

	@Override
	public void update(RecordLight rec) {

		if(rec.getFramedIpAddress()==null||rec.getUserName()==null)
		{
			return;
		}
		
		if (rec.getAcctStatusType() == 1)// 上线
		{
			jedis.set(rec.getFramedIpAddress(), rec.getUserName());
			jedis.set(rec.getUserName(), rec.getFramedIpAddress());
		} else if (rec.getAcctStatusType() == 2)// 下线
		{
			jedis.del(rec.getFramedIpAddress());
			jedis.del(rec.getUserName());
		} else if (rec.getAcctStatusType() == 3)// 心跳
		{
			String oldUserName = jedis.get(rec.getFramedIpAddress());
			String oldIp = jedis.get(rec.getUserName());

			// ip对应的用户名是否发生变化
			if ((oldUserName==null)||(!(oldUserName.equals(rec.getUserName())))) {
				jedis.set(rec.getFramedIpAddress(), rec.getUserName());
			} else {

			}

			// 用户名对应的ip是否发生变化
			if ((oldIp==null)||(!(oldIp.equals(rec.getFramedIpAddress())))) {
				jedis.set(rec.getUserName(), rec.getFramedIpAddress());
			} else {

			}
		}
	}

	public void finalize()
	{
		  System.exit(0);
		  pool.returnResource(jedis);
		  pool.destroy();
	}

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}



}
