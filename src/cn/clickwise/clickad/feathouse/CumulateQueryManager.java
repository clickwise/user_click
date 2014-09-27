package cn.clickwise.clickad.feathouse;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.clickwise.lib.time.TimeOpera;

import redis.clients.jedis.Jedis;


public class CumulateQueryManager {

	private ConfigureFactory confFactory;

	//统计不同地区用户查询数
	private Jedis jedis = null;
	
	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

	private static final long PERIOD_MINUTE = 60 * 1000;
	
	private static final long PERIOD_HOUR =60 * 60 * 1000;

	private Mysql mysql=null;
	
	private Map<String,String>  codeArea;
	
	private Table table;
	
	public CumulateQueryManager() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		
		ArdbConfigure ardbConf=confFactory.getArdbConfigure();
		jedis = new Jedis(ardbConf.getHost() ,ardbConf.getPort(), 10000);
		jedis.select(ardbConf.getDb());
		
		mysql=new Mysql();
		
		codeArea=AreaCode.getCodeAreaMap();
		
		table=confFactory.getQueryTable();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long ctime = System.currentTimeMillis();
		Date date = new Date(ctime);
		// Date date=TimeOpera.getCurrentTime()
		Timer timer = new Timer();
		CumulateQuery cq = new CumulateQuery();
		timer.schedule(cq, date, PERIOD_HOUR);
	}

	// 累加当天的查询统计
	public class CumulateQuery extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("timer test");
			
			int day=TimeOpera.getToday();
			
			
			
			for(Map.Entry<String, String> item:codeArea.entrySet())
			{
				//item.
				QueryReceipt receipt=new QueryReceipt();
				receipt.setDay(day);
				receipt.setCodeOfArea(item.getKey());
				
				String areaDayIdentity = KeyOpera.areaCodeDayKey(day,KeyOpera.getAreaCodeFromUid(item.getKey()));
				String counted_str = jedis.get(areaDayIdentity);
				int pv=0;
				if (counted_str != null) {
					pv = Integer.parseInt(counted_str);
				}
				else
				{
					continue;
				}
				
				receipt.setUv(pv);
				receipt.setPv(pv);
				receipt.setReceiptId(System.currentTimeMillis()+"");	
			    mysql.updateStatistics(receipt, table,codeArea);
				
			}
			
				
		}

	}
	
	public static void main(String[] args)
	{
		CumulateQueryManager cqm=new CumulateQueryManager();
	}

}