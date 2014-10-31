package cn.clickwise.clickad.feathouse;


import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import cn.clickwise.lib.time.TimeOpera;

public class CassandraQuery extends DataQuery {

	private Client client = null;

	private static final String UTF8 = "UTF8";

	private static final ConsistencyLevel CL = ConsistencyLevel.ONE;

	private ColumnParent cp = null;

	// 统计不同地区用户查询数
	private Jedis jedis = null;

	private MissesDirectory missesDirectory;

	private ConfigureFactory confFactory;

	private QueryLogDirectory queryLogDirectory;

	// 记录未查到用户的uid
	private PrintWriter supervisor = null;

	private PrintWriter querySupervisor = null;

	static Logger logger = LoggerFactory.getLogger(CassandraQuery.class);

	public void initLogFiles() {
		try {
			missesDirectory = new MissesDirectory();
			
			//file suffix with hour
			/*
			FileWriter fw = new FileWriter(
					missesDirectory.getMissesByDay(TimeOpera.getToday()) + "_"
							+ TimeOpera.getHour(), true);
			*/
			
			FileWriter fw = new FileWriter(
					missesDirectory.getMissesByDay(TimeOpera.getToday()), true);
			
			
			supervisor = new PrintWriter(fw);

			queryLogDirectory = new QueryLogDirectory();
			
			//file suffix with hour
			/*
			FileWriter qlfw = new FileWriter(
					queryLogDirectory.getQueryLogByDay(TimeOpera.getToday())
							+ "_" + TimeOpera.getHour(), true);
			*/
			
			FileWriter qlfw = new FileWriter(
					queryLogDirectory.getQueryLogByDay(TimeOpera.getToday()), true);
			
			
			
			querySupervisor = new PrintWriter(qlfw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public State connect(Connection con) {
		State state = new State();
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();

		try {
			TTransport tr = new TSocket(con.getHost(), con.getPort());
			TFramedTransport tf = new TFramedTransport(tr);
			TProtocol proto = new TBinaryProtocol(tf);
			client = new Client(proto);
			tf.open();
			client.set_keyspace(con.getKeySpace());
			setCp(new ColumnParent(con.getCfName()));

			ArdbConfigure ardbConf = confFactory.getArdbConfigure();

			jedis = new Jedis(ardbConf.getHost(), ardbConf.getPort(), 10000);
			jedis.select(ardbConf.getDb());
			/*
			 * missesDirectory=new MissesDirectory(); FileWriter fw=new
			 * FileWriter
			 * (missesDirectory.getMissesByDay(TimeOpera.getToday()),true);
			 * supervisor=new PrintWriter(fw);
			 * 
			 * queryLogDirectory=new QueryLogDirectory(); FileWriter qlfw=new
			 * FileWriter
			 * (queryLogDirectory.getQueryLogByDay(TimeOpera.getToday()),true);
			 * querySupervisor=new PrintWriter(qlfw);
			 */

			Calendar cal = Calendar.getInstance();
			// 每天定点执行
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 30);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					initLogFiles();
				}
			}, cal.getTime(), TimeOpera.PERIOD_DAY);

			state.setStatValue(StateValue.Normal);

		} catch (Exception e) {
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}

		return state;
	}

	@Override
	public List<Record> queryUid(Key key) {

		List<Record> recordList = new ArrayList<Record>();
		SlicePredicate predicate = new SlicePredicate();
		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(new byte[0]);
		sliceRange.setFinish(new byte[0]);
		predicate.setSlice_range(sliceRange);
		ByteBuffer sendBuffer = null;

		try {

			sendBuffer = ByteBuffer.wrap(key.key.getBytes(UTF8));
			List<ColumnOrSuperColumn> results = client.get_slice(sendBuffer,
					cp, predicate, CL.ONE);

			// System.out.println("key:"+key.key+"  results.size:"+results.size());
			for (ColumnOrSuperColumn result : results) {
				Column column = result.column;
				// System.out.println("key:"+key.key+" value:"+(new
				// String(column.getValue(), UTF8)).toString());
				recordList.add(new Record(key.key, new String(
						column.getValue(), UTF8)));
				// System.out.println("key:"+key+" value:"+(new String(
				// column.getValue(), UTF8)).toString());
			}

			logQuery(key);
			// 记录日志和统计
			resetStatistics(key);
			if (results.size() == 0) {
				logUnknownUid(key);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recordList;
	}

	@Override
	public List<Record> queryUidTop(Key key, int top) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	State resetStatistics(Key key) {
		State state = new State();
		int counted = 0;
		String areaDayIdentity = KeyOpera.areaCodeDayKeyPV(
				TimeOpera.getToday(), KeyOpera.getAreaCodeFromUid(key.key));
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

		State state = new State();

		supervisor.println(key.key);
		// supervisor.flush();
		state.setStatValue(StateValue.Normal);

		return state;
	}

	@Override
	State logQuery(Key key) {
		State state = new State();
		querySupervisor.println(key.key + "\001" + key.area + "\001" + key.ip);
		// supervisor.flush();
		return state;
	}

	public ColumnParent getCp() {
		return cp;
	}

	public void setCp(ColumnParent cp) {
		this.cp = cp;
	}

	public PrintWriter getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(PrintWriter supervisor) {
		this.supervisor = supervisor;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage:[host]");
			System.exit(1);
		}

		CassandraQuery cq = new CassandraQuery();
		Connection con = new Connection();
		con.setHost(args[0]);
		con.setPort(9160);
		con.setCfName("Users");
		con.setKeySpace("userstore");
		con.setColumnName("title");
		cq.connect(con);
		Key key = new Key("476cb38e3aace0ad5129a147643d8bc3009");
		List<Record> result = cq.queryUid(key);
		cq.getSupervisor().close();
		cq.getQuerySupervisor().close();

		/*
		 * InputStreamReader isr = new InputStreamReader(System.in);
		 * BufferedReader br = new BufferedReader(isr);
		 * 
		 * OutputStreamWriter osw = new OutputStreamWriter(System.out);
		 * PrintWriter pw = new PrintWriter(osw);
		 * 
		 * String line = "";
		 * 
		 * try { long total_time = 0; long query_count = 0; while ((line =
		 * br.readLine()) != null) { // if(Math.random()<0.98) // { // continue;
		 * // } if (SSO.tioe(line)) { continue; } line = line.trim(); try { Key
		 * key = new Key(line); long start = TimeOpera.getCurrentTimeLong();
		 * List<Record> result = cq.queryUid(key); long end =
		 * TimeOpera.getCurrentTimeLong(); total_time += (end - start);
		 * query_count++;
		 * 
		 * System.out.println("Use time:" + (end - start) + " ms");
		 * 
		 * for (int i = 0; i < result.size(); i++) {
		 * System.out.println(result.get(i).toString()); }
		 * 
		 * } catch (Exception e) { Thread.sleep(1000); } //
		 * pw.println(seg.segAnsi(line)); }
		 * System.out.println("average query time:" + ((double) total_time /
		 * (double) query_count)); isr.close(); osw.close(); br.close();
		 * pw.close(); } catch (Exception e) { e.printStackTrace(); }
		 */

	}

	public PrintWriter getQuerySupervisor() {
		return querySupervisor;
	}

	public void setQuerySupervisor(PrintWriter querySupervisor) {
		this.querySupervisor = querySupervisor;
	}

	public QueryLogDirectory getQueryLogDirectory() {
		return queryLogDirectory;
	}

	public void setQueryLogDirectory(QueryLogDirectory queryLogDirectory) {
		this.queryLogDirectory = queryLogDirectory;
	}

}
