package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.lib.code.MD5Code;
import cn.clickwise.lib.string.SSO;

public class CassandraStore extends DataStore {

	private Client client = null;

	private ColumnParent cp = null;

	private ColumnPath colPathName = null;

	private String columnName = null;

	private static final String UTF8 = "UTF8";

	private static final ConsistencyLevel CL = ConsistencyLevel.ONE;

	static Logger logger = LoggerFactory.getLogger(CassandraStore.class);
	
	@Override
	public State connect(Connection con) {

		State state = new State();

		try {
			TTransport tr = new TSocket(con.getHost(), con.getPort());
			TFramedTransport tf = new TFramedTransport(tr);
			TProtocol proto = new TBinaryProtocol(tf);
			client = new Client(proto);
			tf.open();

			client.set_keyspace(con.getKeySpace());
			setCp(new ColumnParent(con.getCfName()));
			setColPathName(new ColumnPath(con.getCfName()));
			columnName = con.getColumnName();
			colPathName.setColumn(con.getColumnName().getBytes(UTF8));

			state.setStatValue(StateValue.Normal);
		} catch (Exception e) {
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}

		return state;
	}

	@Override
	public State write2db(Record rec) {
		// TODO Auto-generated method stub

		// Clock clock=new Clock(System.currentTimeMillis());
		ByteBuffer sendBuffer = null;
		try {
			sendBuffer = ByteBuffer.wrap(rec.getKey().getBytes(UTF8));
			Column column = new Column();
			column.setName(columnName.getBytes(UTF8));
			column.setValue(rec.getValue().getBytes());
			column.setTimestamp(System.currentTimeMillis());
			client.insert(sendBuffer, cp, column, CL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public State deleteExpired(TimeRange time) {
		// TODO Auto-generated method stub
		return null;
	}

	public ColumnParent getCp() {
		return cp;
	}

	public void setCp(ColumnParent cp) {
		this.cp = cp;
	}

	public ColumnPath getColPathName() {
		return colPathName;
	}

	public void setColPathName(ColumnPath colPathName) {
		this.colPathName = colPathName;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage:[host]");
			System.exit(1);
		}

		CassandraStore cs = new CassandraStore();
		Connection con = new Connection();
		con.setHost(args[0]);
		con.setPort(9160);
		con.setCfName("Urls");
		con.setKeySpace("urlstore");
		con.setColumnName("title");
		cs.connect(con);

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
				logger.info("adding:key="+md5key+",value="+tokens[1]);
				cs.write2db(rec);
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