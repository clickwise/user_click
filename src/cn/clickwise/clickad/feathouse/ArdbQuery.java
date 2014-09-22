package cn.clickwise.clickad.feathouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.clickwise.lib.time.TimeOpera;

import redis.clients.jedis.Jedis;

public class ArdbQuery extends DataQuery {

	private Jedis jedis = null;
	private int day=0;

	@Override
	public State connect(Connection con) {
		State state = new State();

		jedis = new Jedis(con.getHost(), con.getPort(), 1000);
		jedis.select(con.getDb());

		state.setStatValue(StateValue.Normal);
		return state;
	}

	@Override
	public List<Record> queryUid(Key key) {
		List<Record> recordList = new ArrayList<Record>();

		Set<String> result = jedis.zrevrange(key.key, 0, -1);
		Iterator it = result.iterator();

		while (it.hasNext()) {
			recordList.add(new Record(key.key, it.next() + ""));
		}

		return recordList;
	}

	@Override
	public List<Record> queryUidTop(Key key, int top) {

		List<Record> recordList = new ArrayList<Record>();
		Set<String> result = null;
		if ((top - 1) >= 0) {
			result = jedis.zrevrange(key.key, 0, top - 1);
		} else {
			result = jedis.zrevrange(key.key, 0, -1);
		}
		Iterator it = result.iterator();

		while (it.hasNext()) {
			recordList.add(new Record(key.key, it.next() + ""));
		}

		return recordList;
	}

	@Override
	State resetStatistics(Key key) {
		State state = new State();
		int counted=0;
		String areaDayIdentity=KeyOpera.areaDayKey(TimeOpera.getToday(), KeyOpera.getAreaFromUid(key.key));
		String counted_str=jedis.get(areaDayIdentity);
		if(counted_str!=null)
		{
			counted=Integer.parseInt(counted_str);
		}
		counted++;
		jedis.set(areaDayIdentity, counted+"");
		
		state.setStatValue(StateValue.Normal);
		return state;
	}
}
