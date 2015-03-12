package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;
import cn.clickwise.rpc.Connection;
import cn.clickwise.rpc.HiveStatisticByKeysClient;
import cn.clickwise.rpc.HiveStatisticByKeysCommand;

import redis.clients.jedis.Jedis;

public class CumulateQueryManager {

	private ConfigureFactory confFactory;

	private QueryLogDirectory queryLogDirectory;

	// 统计不同地区用户查询数
	private Jedis jedis = null;

	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

	private static final long PERIOD_MINUTE = 60 * 1000;

	private static final long PERIOD_HOUR = 60 * 60 * 1000;

	private Mysql mysql = null;

	private Map<String, String> codeArea;

	private Table table;

	public CumulateQueryManager() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		queryLogDirectory = new QueryLogDirectory();

		ArdbConfigure ardbConf = confFactory.getArdbConfigure();
		jedis = new Jedis(ardbConf.getHost(), ardbConf.getPort(), 10000);
		jedis.select(ardbConf.getDb());

		mysql = new Mysql();

		codeArea = AreaCode.getCodeAreaMap();

		table = confFactory.getQueryTable();

		Calendar calendar = Calendar.getInstance();
		// calendar.set(Calendar.HOUR_OF_DAY, 2);
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
			/*
			 * int day = TimeOpera.getToday();
			 * 
			 * for (Map.Entry<String, String> item : codeArea.entrySet()) { //
			 * item. QueryReceipt receipt = new QueryReceipt();
			 * receipt.setDay(day); receipt.setCodeOfArea(item.getKey());
			 * 
			 * String areaDayIdentity = KeyOpera.areaCodeDayKey(day,
			 * KeyOpera.getAreaCodeFromUid(item.getKey())); String counted_str =
			 * jedis.get(areaDayIdentity); int pv = 0; if (counted_str != null)
			 * { pv = Integer.parseInt(counted_str); } else { continue; }
			 * 
			 * receipt.setUv(pv); receipt.setPv(pv);
			 * receipt.setReceiptId(System.currentTimeMillis() + "");
			 * mysql.updateStatistics(receipt, table, codeArea);
			 * 
			 * }
			 */
			// queryPvUvStatistics();
			queryPvUvStatisticsFile();
			queryPvUvIpDmps();
		}

		// 从queryLog统计不同地区查询的Uv量，并将结果写入mysql
		public void queryPvUvStatistics() {

			int day = TimeOpera.getToday();
			HashMap<String, HashMap<String, Integer>> areaUser = new HashMap<String, HashMap<String, Integer>>();

			try {
				FileReader fr = new FileReader(
						queryLogDirectory.getQueryLogByDay(day));
				BufferedReader br = new BufferedReader(fr);
				String line = "";

				// 统计uv
				while ((line = br.readLine()) != null) {
					if (SSO.tioe(line)) {
						continue;
					}
					line = line.trim();
					String areaDayUVIdentity = KeyOpera.areaCodeDayKeyUV(day,
							KeyOpera.getAreaCodeFromUid(line));
					if (!(areaUser.containsKey(areaDayUVIdentity))) {
						areaUser.put(areaDayUVIdentity,
								new HashMap<String, Integer>());
					} else {
						if (!(areaUser.get(areaDayUVIdentity).containsKey(line))) {
							areaUser.get(areaDayUVIdentity).put(line, 1);
						}
					}
				}

				String codeOfArea = "";
				String areaDayPVIdentity = "";

				for (Map.Entry<String, HashMap<String, Integer>> areaDayUVIdentity : areaUser
						.entrySet()) {
					QueryReceipt receipt = new QueryReceipt();
					receipt.setDay(day);
					System.out.println("areaDayUVIdentity.key:"
							+ areaDayUVIdentity.getKey());

					codeOfArea = KeyOpera
							.getCodeOfAreaFromAreaDayKeyUV(areaDayUVIdentity
									.getKey());
					receipt.setCodeOfArea(codeOfArea);

					// 获取pv
					areaDayPVIdentity = KeyOpera.areaCodeDayKeyPV(day,
							codeOfArea);
					String counted_str = jedis.get(areaDayPVIdentity);

					int pv = 0;
					if (counted_str != null) {
						pv = Integer.parseInt(counted_str);
					} else {
						continue;
					}

					receipt.setUv(areaDayUVIdentity.getValue().size());
					receipt.setPv(pv);
					receipt.setReceiptId(System.currentTimeMillis() + "");
					mysql.updateStatistics(receipt, table, codeArea);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 从queryLog统计不同地区查询的Uv量，并将结果写入mysql
		public void queryPvUvStatisticsFile() {

			int day = TimeOpera.getToday();

			// 地区用户存储结构，第一个key是地区，第二个key是用户
			HashMap<String, HashMap<String, Integer>> areaUser = new HashMap<String, HashMap<String, Integer>>();

			// 地区ip存储结构，第一个key是地区，第二个key是ip
			HashMap<String, HashMap<String, Integer>> areaIp = new HashMap<String, HashMap<String, Integer>>();

			try {
				FileReader fr = new FileReader(
						queryLogDirectory.getQueryLogByDay(day));
				BufferedReader br = new BufferedReader(fr);
				String line = "";

				String[] tokens = null;
				String uid = "";
				String area = "";
				String ip = "";

				// 统计uv
				while ((line = br.readLine()) != null) {
					if (SSO.tioe(line)) {
						continue;
					}
					line = line.trim();

					tokens = line.split("\001");
					if (tokens.length != 3) {
						continue;
					}

					uid = tokens[0];
					area = tokens[1];
					ip = tokens[2];

					String areaDayUVIdentity = KeyOpera.areaCodeDayKeyUV(day,
							KeyOpera.getAreaCodeFromUid(uid));
					if (SSO.tioe(areaDayUVIdentity)) {
						continue;
					}

					// System.out.println("areaDayUVIdentity:"+areaDayUVIdentity);
					if (!(areaUser.containsKey(areaDayUVIdentity))) {
						areaUser.put(areaDayUVIdentity,
								new HashMap<String, Integer>());
						areaUser.get(areaDayUVIdentity).put(uid, 1);

						areaIp.put(areaDayUVIdentity,
								new HashMap<String, Integer>());
						areaIp.get(areaDayUVIdentity).put(ip, 1);
					} else {
						if (!(areaUser.get(areaDayUVIdentity).containsKey(uid))) {
							areaUser.get(areaDayUVIdentity).put(uid, 1);
						}

						if (!(areaIp.get(areaDayUVIdentity).containsKey(ip))) {
							areaIp.get(areaDayUVIdentity).put(ip, 1);
						}
					}
				}

				String codeOfArea = "";
				String areaDayPVIdentity = "";

				for (Map.Entry<String, HashMap<String, Integer>> areaDayUVIdentity : areaUser
						.entrySet()) {
					QueryReceipt receipt = new QueryReceipt();
					receipt.setDay(day);
					if (SSO.tioe(areaDayUVIdentity.getKey())) {
						continue;
					}
					codeOfArea = KeyOpera
							.getCodeOfAreaFromAreaDayKeyUV(areaDayUVIdentity
									.getKey());
					if (SSO.tioe(codeOfArea)) {
						continue;
					}
					System.out.println("areaDayUVIdentity.key:"
							+ areaDayUVIdentity.getKey());
					System.out.println("codeOfArea:" + codeOfArea);
					receipt.setCodeOfArea(codeOfArea);

					// 获取pv
					areaDayPVIdentity = KeyOpera.areaCodeDayKeyPV(day,
							codeOfArea);
					System.out
							.println("areaDayPVIdentity:" + areaDayPVIdentity);
					String counted_str = jedis.get(areaDayPVIdentity);

					int pv = 0;
					if (counted_str != null) {
						pv = Integer.parseInt(counted_str);
					} else {
						continue;
					}

					receipt.setUv(areaDayUVIdentity.getValue().size());
					HashMap<String, Integer> ipAreaDay = areaIp
							.get(areaDayUVIdentity.getKey());
					receipt.setPv(ipAreaDay.size());

					receipt.setReceiptId(System.currentTimeMillis() + "");
					mysql.updateStatistics(receipt, table, codeArea);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 统计RTB和各dmp能够对上的用户pv,ip,uv等信息
		 */
		public void queryPvUvIpDmps() {
			
			int day = TimeOpera.getYesterday();

			try {
				FileReader fr = new FileReader(
						queryLogDirectory.getQueryLogByDay(TimeOpera.getToday()));
				BufferedReader br = new BufferedReader(fr);
				String line = "";

				HashMap<String, String> activeDmps = new HashMap<String, String>();
				HashMap<String, PrintWriter> activeDmpPWs = new HashMap<String, PrintWriter>();
				
				String uid = "";
				String area = "";
				String ip = "";
				String[] tokens = null;

				String tempAC = "";// temp area code

				HashMap<String,Dmp> existDmps=new  HashMap<String,Dmp>();
				
				int ci=0;
				while ((line = br.readLine()) != null) {
					
					if (SSO.tioe(line)) {
						continue;
					}
					
					/*
                    ci++;
                    if(ci%1000==1)
                    {
                    	System.out.println("ci="+ci);
                    }
                    */
					
					line = line.trim();

					tokens = line.split("\001");
					if (tokens.length != 3) {
						continue;
					}

					uid = tokens[0];
					area = tokens[1];
					ip = tokens[2];

					tempAC = KeyOpera.getAreaCodeFromUid(uid);
					if (SSO.tioe(tempAC)) {
						continue;
					}

					if (!(activeDmps.containsKey(tempAC))) {
						/*
						Dmp admp=existDmps.get(tempAC);
					    if(admp==null)
					    {
					    	Dmp aadmp = confFactory.getDmpByAreaCode(tempAC);
						  if(admp==null)
						  {
							  continue;
						  }
						  else
						  {
							  existDmps.put(tempAC, aadmp);
						  }
					    }
						if(admp==null)
						{
							continue;
						}
						*/	
						String afile = confFactory.getDmpUidDirectory() + "/"
								+ confFactory.getEasyDmpUidFile(day, tempAC);
						activeDmps.put(tempAC, afile);
						try {
							activeDmpPWs.put(tempAC, new PrintWriter(
									new FileWriter(afile)));
							activeDmpPWs.get(tempAC).println(uid);
						} catch (Exception e) {
							e.printStackTrace();
						}		
					} else {
						activeDmpPWs.get(tempAC).println(uid);
					}

				}
				
				System.out.println("read log file done");
				
				for(Map.Entry<String, PrintWriter> pw:activeDmpPWs.entrySet())
				{
					pw.getValue().close();
				}
				
				RpcStatisticInquiry rsi=new RpcStatisticInquiry();
				
				System.out.println("begin dmp statistic");
				for(Map.Entry<String, String> d:activeDmps.entrySet())
				{
					try{
					 Dmp tempDmp=confFactory.getDmpByAreaCode(d.getKey());
					 if(tempDmp==null)
					 {
						continue;
					 }
					 System.out.println("process area:"+tempDmp.getArea().getAreaCode());
					 StatisticStruct stt=rsi.getDmpStatistic(confFactory.getDmpByAreaCode(d.getKey()),day);
				
					 stt.setDay(TimeOpera.getToday());
					 System.out.println("stt:"+stt.toString());
					 mysql.updateDmpStatistics(stt, new Table("InquiryReceipts"), codeArea);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			
	
			
			
            /*
			HiveStatisticByKeysClient ec = new HiveStatisticByKeysClient();
			Connection con = new Connection();
			con.setHost("112.67.253.101");
			con.setPort(2733);
			con.setMethod("/hiveStatisticByKeys");

			HiveStatisticByKeysCommand hfkc = new HiveStatisticByKeysCommand();
			String tmpIdentify = "remote_statistic";
			hfkc.setDay(day);
			hfkc.setTmpIdentify(tmpIdentify);
			hfkc.setKeyName("ttt.txt");
			hfkc.setKeyPath("temp/ttt.txt");

			hfkc.setTableName("astat");
			hfkc.setKeyFieldName("user_id");
			hfkc.setIpFieldName("sip");
			hfkc.setKeyTableName("statistic_keys");
			hfkc.setAreaCode("009");
			hfkc.setResultName("local_user_statistic.txt");
			hfkc.setResultPath("temp/local_user_statistic.txt");
			hfkc.initRandomFileName();

			ec.setHfkc(hfkc);
			ec.connect(con);
			ec.execute(hfkc);
			
			*/

		}

	}

	public static void main(String[] args) {
		CumulateQueryManager cqm = new CumulateQueryManager();
	}

}