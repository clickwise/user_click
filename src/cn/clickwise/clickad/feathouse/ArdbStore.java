package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.lib.code.MD5Code;
import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;
import redis.clients.jedis.Jedis;

public class ArdbStore extends DataStore{

	private Jedis jedis=null;
	
	static Logger logger = LoggerFactory.getLogger(ArdbStore.class);
	
	private ConfigureFactory confFactory;
	
	@Override
	public State connect(Connection con) {
		
		State state=new State();
		
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		
		ArdbConfigure ardbConf=confFactory.getArdbConfigure();
		
		jedis = new Jedis(ardbConf.getHost() ,ardbConf.getPort(), 10000);
		jedis.select(ardbConf.getDb());
		
		state.setStatValue(StateValue.Normal);
		return state;
	}

	@Override
	public State write2db(Record rec,int day) {
		State state=new State();	
		long score=(long)((System.currentTimeMillis()/1000)+(Math.random()*100));
		logger.info("adding to ardb:key="+rec.getKey()+",value="+rec.getValue()+",score="+score);
		
		jedis.zadd(rec.getKey(), score, rec.getValue());
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

public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Usage:[host]");
			System.exit(1);
		}

		ArdbStore as = new ArdbStore();
		Connection con = new Connection();
		con.setHost(args[0]);
		con.setPort(16379);

		as.connect(con);

		/*
		 * Record rec=new Record("testkey2","testvalue2"); cs.write2db(rec);
		 */
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		String[] tokens = null;
		try {
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				tokens = line.split("\001");
				if (tokens.length != 2) {
					continue;
				}
				
				String md5key=MD5Code.Md5(tokens[0]);
				if(SSO.tioe(md5key))
				{
					continue;
				}
				Record rec = new Record(md5key, tokens[1]);
				try{
				 as.write2db(rec,TimeOpera.getYesterday());
				}
				catch(Exception e)
				{
				  Thread.sleep(1000);	
				}
				// pw.println(seg.segAnsi(line));
			}

			isr.close();
			osw.close();
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
