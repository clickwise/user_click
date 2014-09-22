package cn.clickwise.clickad.feathouse;

import redis.clients.jedis.Jedis;

public class ArdbStore extends DataStore{

	private Jedis jedis=null;
	
	@Override
	public State connect(Connection con) {
		
		State state=new State();
		
		jedis=new Jedis(con.getHost(), con.getPort(), 1000);
		jedis.select(con.getDb());
		
		state.setStatValue(StateValue.Normal);
		return state;
	}

	@Override
	public State write2db(Record rec) {
		State state=new State();
		jedis.zadd(rec.getKey(), System.currentTimeMillis()+(Math.random()*100), rec.getValue());
		state.setStatValue(StateValue.Normal);
		return state;
	}

	@Override
	public State deleteExpired(TimeRange time) {
		// TODO Auto-generated method stub
		return null;
	}

	public Jedis getJedis() {
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

}
