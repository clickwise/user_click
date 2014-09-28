package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.clickwise.lib.code.MD5Code;
import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

import redis.clients.jedis.Jedis;

public class ArdbQuery extends DataQuery {

	private Jedis jedis = null;
	
	private int day = 0;

	private ConfigureFactory confFactory;
	
	@Override
	public State connect(Connection con) {
		State state = new State();

		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		
		ArdbConfigure ardbConf=confFactory.getArdbConfigure();
		
		jedis = new Jedis(ardbConf.getHost() ,ardbConf.getPort(), 10000);
		jedis.select(ardbConf.getDb());
		
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

		resetStatistics(key);

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

		resetStatistics(key);

		return recordList;
	}

	@Override
	State resetStatistics(Key key) {
		State state = new State();
		int counted = 0;
		String areaDayIdentity = KeyOpera.areaDayKey(TimeOpera.getToday(),
				KeyOpera.getAreaFromUid(key.key));
		String counted_str = jedis.get(areaDayIdentity);
		if (counted_str != null) {
			counted = Integer.parseInt(counted_str);
		}
		counted++;
		jedis.set(areaDayIdentity, counted + "");

		state.setStatValue(StateValue.Normal);
		return state;
	}

	@Override
	State logUnknownUid(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Usage:[host]");
			System.exit(1);
		}

		ArdbQuery aq = new ArdbQuery();
		Connection con = new Connection();
		con.setHost(args[0]);
		con.setPort(16379);
		aq.connect(con);

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";

		try {
			long total_time = 0;
			long query_count = 0;
			while ((line = br.readLine()) != null) {
				if (Math.random() < 0.98) {
					continue;
				}
				if (SSO.tioe(line)) {
					continue;
				}
				line = line.trim();
				try {
					Key key = new Key(line);
					long start = TimeOpera.getCurrentTimeLong();
					List<Record> result = aq.queryUid(key);
					long end = TimeOpera.getCurrentTimeLong();
					total_time += (end - start);
					query_count++;
					System.out.println("Use time:" + (end - start) + " ms");

					for (int i = 0; i < result.size(); i++) {
						System.out.println(result.get(i).toString());
					}
				} catch (Exception e) {
					Thread.sleep(1000);
				}
				// pw.println(seg.segAnsi(line));
			}
			System.out.println("average query time:"
					+ ((double) total_time / (double) query_count));
			isr.close();
			osw.close();
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	State logQuery(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

}
