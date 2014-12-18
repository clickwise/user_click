package cn.clickwise.liqi.database.kv;

import java.util.Properties;

import cn.clickwise.liqi.database.ssdb.KVSSDB;


public class KVDBFactory {

	public static KVDB create(Properties prop) 
	{
		KVDB kvdb=null;
		String kvdb_type=prop.getProperty("kvdb_type");
		if(kvdb_type.equals("ssdb"))
		{
			try{
			kvdb=new KVSSDB();		
			kvdb.load_config(prop);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}			
		}

		return kvdb;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		Properties prop=new Properties();
		prop.setProperty("ssdb_ip", "192.168.110.182");
		prop.setProperty("ssdb_port","8888");
		prop.setProperty("kvdb_type","ssdb");
		
		KVDB ssdb=KVDBFactory.create(prop);
		ssdb.set("testk", "testv");
		System.out.println(ssdb.get("tf.360.cn"));
		System.out.println(ssdb.exist("testk"));
		System.out.println(ssdb.exist("www.baidu.com"));	
		
	}
	
}
