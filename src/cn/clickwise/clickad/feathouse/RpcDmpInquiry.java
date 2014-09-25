package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.rpc.HiveFetchByKeysClient;
import cn.clickwise.rpc.HiveFetchByKeysCommand;

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
	public State fetchFromAllDmps(TimeRange timeRange) {
		// TODO Auto-generated method stub

		
		
		return null;
	}

	@Override
	public State writeRecFile2DataStore(File recordFile, Connection con) {

		State state = new State();

		DataStore dataStore = confFactory.getDataStore();
		dataStore.connect(con);

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
            	Record rec=confFactory.string2Record(line);
            	if(rec==null)
            	{
            		continue;
            	}
            	
            	dataStore.write2db(rec);
            }
            
            fis.close();
            isr.close();
            br.close();
          
		} catch (Exception e) {
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}
		
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

	public static void main(String[] args)
	{
		
		RpcDmpInquiry rdi=new RpcDmpInquiry();
		rdi.setDay(20140512);
		rdi.init();
		
		Dmp dmp=new Dmp();
		dmp.setName("186");
		dmp.setArea("local");
		dmp.setAreaCode(186);
		dmp.setHost("192.168.110.186");
		dmp.setRpcPort(2733);
		dmp.setDmpInquiryMethod("/hiveFetchByKeys");
		dmp.setUserFeatureTableName("user_se_keywords_day_ad");
		dmp.setUidFieldName("cookie");
		dmp.setTmpIdentify("remote_cookie");
		
		String keyFile="temp/test_cookie.txt";
		String recordFile="temp/local_user_info.txt";
		
		rdi.fetchFromDmp(new File(keyFile), new File(recordFile), dmp);
		
		Connection con = new Connection();
		con.setHost("192.168.110.182");
		con.setPort(9160);
		con.setCfName("Urls");
		con.setKeySpace("urlstore");
		con.setColumnName("title");
		rdi.writeRecFile2DataStore(new File(recordFile), con);
		
	}

}
