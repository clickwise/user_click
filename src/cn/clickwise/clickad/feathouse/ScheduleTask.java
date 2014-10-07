package cn.clickwise.clickad.feathouse;

import java.io.File;

import cn.clickwise.lib.time.TimeOpera;
import cn.clickwise.rpc.HiveFetchTableClient;
import cn.clickwise.rpc.HiveFetchTableCommand;

/**
 * 每天定时执行的任务
 * 
 * @author zkyz
 */
public class ScheduleTask {

	private ConfigureFactory confFactory;

	private int day;

	private RpcDmpInquiry rdi;

	//连接cassandra
	private Connection con;

	public void init() {
		
		day=TimeOpera.getYesterday();
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
		
		CassandraConfigure cassConf = confFactory.getCassandraConfigure();
		con = new Connection();
		con.setHost(cassConf.getHost());
		con.setPort(cassConf.getPort());
		con.setCfName(cassConf.getCfName());
		con.setKeySpace(cassConf.getKeySpace());
		con.setColumnName(cassConf.getColumnName());
		
		rdi=new RpcDmpInquiry();
		rdi.setDay(day);
		rdi.init();
	}

	public void dmpInquiries() {
		
		Dmp[] dmps = confFactory.getDmps();

		for (int i = 0; i < dmps.length; i++) {
			HiveFetchTableClient hftc = new HiveFetchTableClient();
			cn.clickwise.rpc.Connection conrpc = new cn.clickwise.rpc.Connection();
			conrpc.setHost(dmps[i].getHost());
			conrpc.setPort(dmps[i].getRpcPort());
			conrpc.setMethod(dmps[i].getDmpInquiryMethod());

			HiveFetchTableCommand hftcmd = new HiveFetchTableCommand();
			hftcmd.setDay(day);
			hftcmd.setTmpIdentify(confFactory.getTmpIdentify());

			hftcmd.setTableName(dmps[i].getUserFeatureTableName());
			hftcmd.setKeyFieldName(dmps[i].getUidFieldName());
			String recordFile = confFactory.getRecordFilePrefix() + day+"_"+dmps[i].getArea().getAreaCode() + ".txt";
			hftcmd.setResultName(recordFile);
			hftcmd.setResultPath(confFactory.getRecordFileDirectory()+ recordFile);
			HiveFetchTableClient.initRandomFileName(confFactory.getTmpIdentify(), day, hftcmd);
			hftcmd.setQueryType(confFactory.getQueryType());

			hftc.connect(conrpc);
			hftc.execute(hftcmd);

			rdi.writeRecFile2DataStore(new File(confFactory.getRecordFileDirectory() + recordFile), con,
					dmps[i]);
		}
	}

	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}

}
