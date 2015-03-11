package cn.clickwise.clickad.feathouse;
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;

public class HBaseStore extends DataStore{
	
	 public static Configuration configuration; 
	 public String tableName="userFeat";
	 
	  HTablePool pool ; 
      HTable table ; 
      public String cfName="info";
      public String column="access";
	 
	@Override
	public State connect(Connection con) {
		   configuration = HBaseConfiguration.create(); 
	       configuration.set("hbase.zookeeper.property.clientPort", con.getClientPort()+""); 
	       configuration.set("hbase.zookeeper.quorum", con.getQuorum()); 
	       configuration.set("hbase.master", con.getMaster());
	       pool= new HTablePool(configuration, 1000);
	       table = (HTable) pool.getTable(tableName);
		return null;
	}

	@Override
	public State write2db(Record rec, int day) {
		
		Put put = new Put(rec.getKey().getBytes());
		put.add(cfName.getBytes(),column.getBytes(), rec.getValue().getBytes());
		return null;
	}

	@Override
	public State deleteExpired(TimeRange time) {
		// TODO Auto-generated method stub
		return null;
	}

}
