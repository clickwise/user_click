package cn.clickwise.clickad.feathouse;

import java.io.File;
import java.util.Properties;

import cn.clickwise.lib.file.PropertiesUtil;

public class EasyConfigureFactory extends ConfigureFactory{

	@Override
	public MissesStore getMissesStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MissesTmpStore getMissesTmpStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserRecordTmpStore getUserRecordTmpStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MysqlConfigure getMysqlConfigure() {
	
		Properties prop=PropertiesUtil.file2properties("mysql.conf");
		MysqlConfigure myconf=new MysqlConfigure();
		myconf.setIp(prop.getProperty("ip"));
		myconf.setPort(Integer.parseInt(prop.getProperty("port")));
		myconf.setUser(prop.getProperty("user"));
		myconf.setPassword(prop.getProperty("password"));
		myconf.setDbname(prop.getProperty("dbname"));
		
		return myconf;
	}



	@Override
	public Dmp[] getDmps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dmp getDmpById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dmp getDmpByArea(String area) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DataStore getDataStore() {
		// TODO Auto-generated method stub
		return new CassandraStore();
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record string2Record(String recordString) {
		
		String[] tokens=recordString.split("\001");
		if(tokens.length!=9)
		{
			return null;
		}
		
		String key=tokens[0];
		String value=tokens[3];
		Record record=new Record(key,value);
		// TODO Auto-generated method stub
		return record;
	}

	@Override
	public Table getQueryTable() {
		
		return new Table("QueryReceipts");
	}

	@Override
	public Table getInquiryTable() {

		return new Table("InquiryReceipts");
	}

	@Override
	public File getMissesRootDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

}
