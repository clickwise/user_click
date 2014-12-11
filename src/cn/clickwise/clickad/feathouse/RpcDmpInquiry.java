package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;
import cn.clickwise.rpc.HiveFetchByKeysClient;
import cn.clickwise.rpc.HiveFetchByKeysCommand;
import cn.clickwise.rpc.HiveFetchTableClient;
import cn.clickwise.rpc.HiveFetchTableCommand;

public class RpcDmpInquiry extends DmpInquiry {

	private ConfigureFactory confFactory;

	private DataStore dataStore;

	@Override
	public void init() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
	}

	@Override
	public State fetchFromDmp(File keyFile, File recordFile, Dmp dmp) {

		State state = new State();

		HiveFetchByKeysClient ec = new HiveFetchByKeysClient();
		cn.clickwise.rpc.Connection con = new cn.clickwise.rpc.Connection();
		con.setHost(dmp.getHost());
		con.setPort(dmp.getRpcPort());
		con.setMethod(dmp.getDmpInquiryMethod());

		HiveFetchByKeysCommand hfkc = new HiveFetchByKeysCommand();
		String tmpIdentify = dmp.getTmpIdentify();

		hfkc.setDay(getDay());
		hfkc.setTmpIdentify(tmpIdentify);
		hfkc.setKeyName(keyFile.getName());
		hfkc.setKeyPath(FileName.normalizePath(keyFile));

		hfkc.setTableName(dmp.getUserFeatureTableName());
		hfkc.setKeyFieldName(dmp.getUidFieldName());
		hfkc.setKeyTableName(tmpIdentify);

		hfkc.setResultName(recordFile.getName());
		hfkc.setResultPath(FileName.normalizePath(recordFile));
		HiveFetchByKeysClient.initRandomFileName(tmpIdentify, getDay(), hfkc);

		ec.setHfkc(hfkc);
		ec.connect(con);
		ec.execute(hfkc);

		state.setStatValue(StateValue.Normal);

		return state;
	}

	@Override
	public State fetchFromAllDmps(int day) {
		State state = new State();
		
		Dmp[] dmps = confFactory.getDmps();

		for (int i = 0; i < dmps.length; i++) {
			
			HiveFetchTableClient hftc = new HiveFetchTableClient();
			cn.clickwise.rpc.Connection conrpc = new cn.clickwise.rpc.Connection();
			System.out.println("host:"+dmps[i].getHost());
			conrpc.setHost(dmps[i].getHost());
			System.out.println("port:"+dmps[i].getRpcPort());
			conrpc.setPort(dmps[i].getRpcPort());
			
			conrpc.setMethod(dmps[i].getDmpInquiryMethod());

			HiveFetchTableCommand hftcmd = new HiveFetchTableCommand();
			hftcmd.setDay(day);
			hftcmd.setTmpIdentify(confFactory.getTmpIdentify());

			hftcmd.setTableName(dmps[i].getUserFeatureTableName());
			hftcmd.setKeyFieldName(dmps[i].getUidFieldName());
			//String recordFile = confFactory.getRecordFilePrefix() + day+"_"+dmps[i].getArea().getAreaCode() + ".txt";
			String recordFile=confFactory.getDmpRecordFile(day, dmps[i]);
			hftcmd.setResultName(recordFile);
			hftcmd.setResultPath(confFactory.getRecordFileDirectory()+ recordFile);
			HiveFetchTableClient.initRandomFileName(confFactory.getTmpIdentify(), day, hftcmd);
			hftcmd.setQueryType(confFactory.getQueryType());
			System.out.println("codea:"+dmps[i].getArea().getAreaCode());
			hftcmd.setAreaCode(dmps[i].getArea().getAreaCode());
			hftc.connect(conrpc);
			hftc.execute(hftcmd);		
		}
			
	    state.setStatValue(StateValue.Normal);
		return state;
	}

	@Override
	public State writeRecFile2DataStore(File recordFile, Connection con,Dmp dmp,int day) {

		State state = new State();

		DataStore dataStore = confFactory.getDataStore();
		dataStore.connect(con);

		int uv=0;
		
		try {
			FileInputStream fis = new FileInputStream(recordFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
            String line="";
            while((line=br.readLine())!=null)
            {
            	if(SSO.tioe(line))
            	{
            		continue;
            	}
            	uv++;
            	Record rec=confFactory.string2Record(line);
            	
            	if(rec==null)
            	{
            		continue;
            	}
            	       	
            	dataStore.write2db(rec,day);
            }
            
            fis.close();
            isr.close();
            br.close();
          
		} catch (Exception e) {
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}
		
		//////这个地方可能需要更改
		//****将此次更新的数据统计写入mysql*******
		/*
		InquiryReceipt receipt=new InquiryReceipt();
		receipt.setDay(day);
		receipt.setDmp(dmp);
		receipt.setUv(uv);
		receipt.setPv(uv);
		receipt.setReceiptId(System.currentTimeMillis()+"");	
		resetStatistics(receipt);
		*/
		
        state.setStatValue(StateValue.Normal);
		return state;
	}

	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	@Override
	public State resetStatistics(InquiryReceipt inquiryReceipt) {
		
		State state=new State();
		
		Table inquiryTable=confFactory.getInquiryTable();
		Mysql mysql=new Mysql();
		mysql.insertStatistics(inquiryReceipt, inquiryTable);		
		state.setStatValue(StateValue.Normal);
		
		return state;
	}
	
	public static void main(String[] args)
	{
		int day=20141020;
		RpcDmpInquiry rdi=new RpcDmpInquiry();
		rdi.setDay(day);
		rdi.init();
		/*
		Dmp dmp=new Dmp();
		dmp.setName("hn_101");
		dmp.setArea(new Area("hn","009"));
		dmp.setHost("112.67.253.101");
		dmp.setUserFeatureTableName("auser_cates_keys");
		dmp.setUidFieldName("uid");
		dmp.setTmpIdentify("remote_cookie");
		*/
		Dmp dmp=new Dmp();
		dmp=new Dmp();
		dmp.setName("浙江DX");
		dmp.setArea(new Area("浙江DX","030"));
		dmp.setHost("192.168.10.38");
		dmp.setUserFeatureTableName("auser_cates_keys");
		dmp.setUidFieldName("uid");
		dmp.setTmpIdentify("remote_cookie");
	    dmp.setRpcPort(2733);
	    dmp.setDmpInquiryMethod("/hiveFetchTable");
	    
		/*
		String keyFile="temp/test_cookie.txt";
		String recordFile="temp/local_user_info.txt";
		
		rdi.fetchFromDmp(new File(keyFile), new File(recordFile), dmp);
		*/
		HiveFetchTableClient hftc=new HiveFetchTableClient();
		
		cn.clickwise.rpc.Connection conrpc=new cn.clickwise.rpc.Connection();
		conrpc.setHost("192.168.10.38");
		conrpc.setPort(2733);
		conrpc.setMethod("/hiveFetchTable");
		
		HiveFetchTableCommand hftcmd=new HiveFetchTableCommand();
		String tmpIdentify="remote_table_cookie";
		hftcmd.setDay(day);
		hftcmd.setTmpIdentify(tmpIdentify);
		
		hftcmd.setTableName("auser_cates_keys");
		hftcmd.setKeyFieldName("uid");
		String recordFile="user_info_"+day+".txt";
		hftcmd.setResultName(recordFile);
		hftcmd.setResultPath("temp/"+recordFile);
		HiveFetchTableClient.initRandomFileName(tmpIdentify, day, hftcmd);
		hftcmd.setQueryType(1);
		hftcmd.setAreaCode("030");
		hftc.connect(conrpc);
		hftc.execute(hftcmd);
		
		/*
		Connection con = new Connection();
		con.setHost("192.168.110.182");
		con.setPort(9160);
		con.setCfName("Urls");
		con.setKeySpace("urlstore");
		con.setColumnName("title");
		*/
		
		ConfigureFactory confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
		CassandraConfigure cassConf=confFactory.getCassandraConfigure();

		Connection con = new Connection();
		con.setHost(cassConf.getHost());
		con.setPort(cassConf.getPort());
		con.setCfName(cassConf.getCfName());
		con.setKeySpace(cassConf.getKeySpace());
		con.setColumnName(cassConf.getColumnName());
			
		rdi.writeRecFile2DataStore(new File("temp/"+recordFile), con,dmp,day);
		
	}



}
