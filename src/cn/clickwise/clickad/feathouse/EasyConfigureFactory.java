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
		
		Dmp[] dmps=new Dmp[2];
		
		dmps[0]=new Dmp();
		dmps[0].setName("浙江DX");
		dmps[0].setArea(new Area("浙江DX","030"));
		dmps[0].setHost("192.168.10.38");
		dmps[0].setUserFeatureTableName("auser_cates_keys");
		dmps[0].setUidFieldName("uid");
		dmps[0].setTmpIdentify("remote_cookie");
	    dmps[0].setRpcPort(2733);
	    dmps[0].setDmpInquiryMethod("/hiveFetchTable");
	    
		dmps[1]=new Dmp();
		dmps[1].setName("海南DX");
		dmps[1].setArea(new Area("海南DX","009"));
		dmps[1].setHost("112.67.253.101");
		dmps[1].setUserFeatureTableName("auser_cates_keys");
		dmps[1].setUidFieldName("uid");
		dmps[1].setTmpIdentify("remote_cookie");
	    dmps[1].setRpcPort(2733);
	    dmps[1].setDmpInquiryMethod("/hiveFetchTable");
	    
	    

		
		return dmps;
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
		if(tokens.length<3)
		{
			tokens=recordString.split("\t");
		}
		if(tokens.length<3)
		{
			return null;
		}
		
		String key=tokens[0];
		
		String value="";
		for(int j=1;j<tokens.length-3;j++)
		{
            value+=(tokens[j]+"\001");
			
		}
		value=value.trim();
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
		
		return new File("unkownUid");
	}

	@Override
	public ArdbConfigure getArdbConfigure() {
		Properties prop=PropertiesUtil.file2properties("ardb.conf");
		ArdbConfigure myconf=new ArdbConfigure();
		
		myconf.setHost(prop.getProperty("host"));
		myconf.setPort(Integer.parseInt(prop.getProperty("port")));
		myconf.setDb(Integer.parseInt(prop.getProperty("db")));
		
		return myconf;
	}

	@Override
	public File getQueryLogDirectory() {
		// TODO Auto-generated method stub
		return new File("queryUid");
	}

	@Override
	public Context[] getContext() {

		Context c=new Context("/queryUser");
		Context c1=new Context("/test");
		
		Context[] cs=new Context[2];
		
		cs[0]=c;
		cs[1]=c1;
		
		return cs;
	}

	@Override
	public Handler[] getHandler() {             
        Handler[] chs=new Handler[2];
        
        chs[0]=new EasyQueryHandler();
        chs[1]=new TestHandler();
		return chs;
	}

	@Override
	public CassandraConfigure getCassandraConfigure() {
		Properties prop=PropertiesUtil.file2properties("cassandra.conf");
		CassandraConfigure myconf=new CassandraConfigure();
		
		myconf.setHost(prop.getProperty("host"));
		myconf.setPort(Integer.parseInt(prop.getProperty("port")));
		myconf.setCfName(prop.getProperty("cfName"));
		myconf.setKeySpace(prop.getProperty("keySpace"));
		myconf.setColumnName(prop.getProperty("columnName"));
		
		return myconf;
	}

	@Override
	public String getTmpIdentify() {
		// TODO Auto-generated method stub
		return "remote_table_cookie";
	}

	@Override
	public String getRecordFilePrefix() {
		// TODO Auto-generated method stub
		return "user_info_";
	}

	@Override
	public String getRecordFileDirectory() {
		// TODO Auto-generated method stub
		return "temp/";
	}

	@Override
	public int getQueryType() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getDmpRecordFile(int day,Dmp dmp) {
		
		return getRecordFilePrefix() + day+"_"+dmp.getArea().getAreaCode() + ".txt";
	}
	
	

}
