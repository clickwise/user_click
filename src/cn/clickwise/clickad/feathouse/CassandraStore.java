package cn.clickwise.clickad.feathouse;



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


public class CassandraStore extends  DataStore{

	private Client client=null;
	
	private ColumnParent cp=null;
	
	private ColumnPath colPathName=null;
	
	private static final String UTF8="UTF8";
	
	private static final ConsistencyLevel CL=ConsistencyLevel.ONE;
	
	@Override
	public State connect(Connection con) {
		
		State state=new State();
		
		try{
			TTransport tr=new TSocket(con.getHost(),con.getPort());
			TFramedTransport tf=new TFramedTransport(tr);
			TProtocol proto=new TBinaryProtocol(tf);
			client=new Client(proto);
			tf.open();
		
			client.set_keyspace(con.getKeySpace());
			setCp(new ColumnParent(con.getCfName()));
			setColPathName(new ColumnPath(con.getCfName()));
			colPathName.setColumn(con.getColumnName().getBytes(UTF8));
		
		
			state.setStatValue(StateValue.Normal);
		}
		catch(Exception e)
		{
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}
		
		return state;
	}

	@Override
	public State write2db(Record rec) {
		// TODO Auto-generated method stub
		
		//Clock clock=new Clock(System.currentTimeMillis());
		ByteBuffer sendBuffer=null;
		try{
		  sendBuffer=ByteBuffer.wrap(rec.getKey().getBytes(UTF8));
		  Column column=new Column();
		  column.setName("title".getBytes(UTF8));
		  column.setValue("testvalue".getBytes());
		  column.setTimestamp(System.currentTimeMillis());
		  client.insert(sendBuffer, cp, column, CL);
		}
		catch(Exception e)
		{
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
	
	public static void main(String[] args)
	{
		
		CassandraStore cs=new CassandraStore();
		Connection con=new Connection();
		con.setHost("192.168.110.181");
		con.setPort(9160);
		con.setCfName("Urls");
		con.setKeySpace("urlstore");
		con.setColumnName("title");
		cs.connect(con);
		
		Record rec=new Record("testkey","testvalue");
		cs.write2db(rec);
		
		
	}

}
