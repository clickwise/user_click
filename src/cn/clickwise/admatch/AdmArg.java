package cn.clickwise.admatch;

import java.util.HashMap;
import java.util.Map;

import cn.clickwise.redis.MyRedis;
import cn.clickwise.user_click.seg.AnsjSeg;
import redis.clients.jedis.Jedis;

public class AdmArg {

	public String ardb_host;
	public int ardb_port;
	public Jedis redis;
   
	public int topn;
	public MyRedis m_redis;
	public MatchStrategyIntegrated m_strategy;
	
	public STATS server_status;
	
	public HashMap<String,Integer> datatype_map;
	public HashMap<String,Integer> infotype_map;
	public HashMap<String,Integer> adinfotype_map;
	
	public int startstore;
	public String platform;
	
	public int match_time_length;
	public int maxretnum;
	
	public int timeadd;
	
	public AnsjSeg ansjseg;
	
}
