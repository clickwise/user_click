package cn.clickwise.clickad.feathouse;

import java.io.File;
import java.util.Properties;
import cn.clickwise.lib.file.PropertiesUtil;
import cn.clickwise.lib.string.SSO;

public class EasyConfigureFactory extends ConfigureFactory {

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

		Properties prop = PropertiesUtil.file2properties("mysql.conf");
		MysqlConfigure myconf = new MysqlConfigure();
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

		Dmp[] dmps = new Dmp[3];
       
		dmps[0] = new Dmp();
		dmps[0].setName("山西DX");
		dmps[0].setArea(new Area("山西DX", "023"));
		dmps[0].setHost("219.149.148.86");
		dmps[0].setUserFeatureTableName("auser_cates_keys");
		dmps[0].setUidFieldName("uid");
		dmps[0].setTmpIdentify("remote_cookie");
		dmps[0].setRpcPort(2733);
		dmps[0].setDmpInquiryMethod("/hiveFetchTable");
		dmps[0].setDmpStatisticMethod("/hiveStatisticByKeys");
		dmps[0].setSourceTableName("astat");
		dmps[0].setSourceUidFieldName("user_id");
		dmps[0].setSourceIpFieldName("sip");
		dmps[0].setKeyTableName("statistic_keys");
		
		
		dmps[1] = new Dmp();
		dmps[1].setName("海南DX");
		dmps[1].setArea(new Area("海南DX", "009"));
		dmps[1].setHost("112.67.253.101");
		dmps[1].setUserFeatureTableName("auser_cates_keys");
		dmps[1].setUidFieldName("uid");
		dmps[1].setTmpIdentify("remote_cookie");
		dmps[1].setRpcPort(2733);
		dmps[1].setDmpInquiryMethod("/hiveFetchTable");
		dmps[1].setDmpStatisticMethod("/hiveStatisticByKeys");
		dmps[1].setSourceTableName("astat");
		dmps[1].setSourceUidFieldName("user_id");
		dmps[1].setSourceIpFieldName("sip");
		dmps[1].setKeyTableName("statistic_keys");
	
		dmps[2] = new Dmp();
		dmps[2].setName("浙江DX");
		dmps[2].setArea(new Area("浙江DX", "030"));
		dmps[2].setHost("192.168.10.138");
		dmps[2].setUserFeatureTableName("auser_cates_keys");
		dmps[2].setUidFieldName("uid");
		dmps[2].setTmpIdentify("remote_cookie");
		dmps[2].setRpcPort(2733);
		dmps[2].setDmpInquiryMethod("/hiveFetchTable");
		dmps[2].setDmpStatisticMethod("/hiveStatisticByKeys");
		dmps[2].setSourceTableName("astat");
		dmps[2].setSourceUidFieldName("user_id");
		dmps[2].setSourceIpFieldName("sip");
		dmps[2].setKeyTableName("statistic_keys");
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
		return new HBaseStore();
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record string2Record(String recordString) {

		String[] tokens = recordString.split("\001");
		if (tokens.length < 3) {
			tokens = recordString.split("\t");
		}
		if (tokens.length < 3) {
			return null;
		}

		String key = tokens[0];

		String value = "";
		for (int j = 1; j < tokens.length - 3; j++) {
			value += (tokens[j] + "\001");

		}
		value = value.trim();
		Record record = new Record(key, value);

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
		Properties prop = PropertiesUtil.file2properties("ardb.conf");
		ArdbConfigure myconf = new ArdbConfigure();

		myconf.setHost(prop.getProperty("host"));
		myconf.setPort(Integer.parseInt(prop.getProperty("port")));
		myconf.setDb(Integer.parseInt(prop.getProperty("db")));

		return myconf;
	}

	@Override
	public File getQueryLogDirectory() {

		return new File("queryUid");
	}

	@Override
	public Context[] getContext() {

		Context c = new Context("/queryUser");
		Context c1 = new Context("/test");

		Context[] cs = new Context[2];

		cs[0] = c;
		cs[1] = c1;

		return cs;
	}

	@Override
	public Handler[] getHandler() {
		Handler[] chs = new Handler[2];

		chs[0] = new EasyQueryHandler();
		chs[1] = new TestHandler();
		return chs;
	}

	@Override
	public CassandraConfigure getCassandraConfigure() {
		Properties prop = PropertiesUtil.file2properties("cassandra.conf");
		CassandraConfigure myconf = new CassandraConfigure();

		myconf.setHost(prop.getProperty("host"));
		myconf.setPort(Integer.parseInt(prop.getProperty("port")));
		myconf.setCfName(prop.getProperty("cfName"));
		myconf.setKeySpace(prop.getProperty("keySpace"));
		myconf.setColumnName(prop.getProperty("columnName"));

		return myconf;
	}
	
	
	

	@Override
	public String getTmpIdentify() {

		return "remote_table_cookie";
	}

	@Override
	public String getRecordFilePrefix() {

		return "user_info_";
	}

	@Override
	public String getRecordFileDirectory() {

		return "temp/";
	}

	@Override
	public String getUidFilePrefix() {

		return "uid_";
	}

	@Override
	public int getQueryType() {

		return 1;
	}

	@Override
	public String getDmpRecordFile(int day, Dmp dmp) {

		return getRecordFilePrefix() + day + "_" + dmp.getArea().getAreaCode()
				+ ".txt";
	}
	
	@Override
	public String getDmpUidPrefix() {
		// TODO Auto-generated method stub
		return "dmpuid_";
	}
	
	@Override
	public String getDmpUidDirectory() {

		return "dmpUid";
	}

	@Override
	public String getDmpUidFile(int day, Dmp dmp) {
		// TODO Auto-generated method stub

		return  getDmpUidPrefix() + day + "_" + dmp.getArea().getAreaCode()
				+ ".txt";
	}
	
	@Override
	public String getEasyDmpUidFile(int day, String areaCode) {
		// TODO Auto-generated method stub
		return getDmpUidPrefix() + day + "_" + areaCode
				+ ".txt";
	}
	
	 /*
	@Override
	public String getDmpStatisticDirectory() {
		// TODO Auto-generated method stub
		return null;
	}
   
	@Override
	public String getDmpStatisticFile(int day, Dmp dmp) {
		// TODO Auto-generated method stub
		return null;
	}
    */
	
	@Override
	public String getDmpSResultPrefix() {
	
		return "dmp_statis_result";
	}
	
	@Override
	public String getDmpStatisticResultDirectory() {

		return "dmpStatistic";
	}
   
	@Override
	public String getDmpStatisticResultFile(int day, Dmp dmp) {
	
		return getDmpSResultPrefix() + day + "_" + dmp.getArea().getAreaCode()
				+ ".txt";
	}

	@Override
	public Dmp getDmpByAreaCode(String areaCode) {

		Dmp[] dmps = getDmps();
		Dmp dmp = null;

		for (int i = 0; i < dmps.length; i++) {
			dmp = dmps[i];
			if ((dmp.getArea().getAreaCode()).equals(areaCode)) {
				return dmp;
			}
		}

		return null;
	}

	@Override
	public String getStatisticTmpIdentify() {
		// TODO Auto-generated method stub
		return "remote_statistic";
	}

	@Override
	public StatisticStruct string2StatisticResult(String statistic_line) {

		StatisticStruct sst = new StatisticStruct();

		String[] tokens = null;
		if (SSO.tioe(statistic_line)) {
			return null;
		}

		statistic_line = statistic_line.trim();
		tokens = statistic_line.split("\001");

		if (tokens.length != 5) {
			return null;
		}
		sst.setDay(Integer.parseInt(tokens[0]));
		sst.setCodeOfArea(tokens[1]);
		sst.setPv(Integer.parseInt(tokens[2]));
		sst.setUv(Integer.parseInt(tokens[3]));
		sst.setIp(Integer.parseInt(tokens[4]));

		return sst;
	}

	@Override
	public HBaseConfigure getHBaseConfigure() {
		
		HBaseConfigure hc=new HBaseConfigure();
		hc.setClientPort(2181);
		hc.setMaster("192.168.10.129");
		hc.setQuorum("192.168.10.128:60000");
		return hc;
	}




}
