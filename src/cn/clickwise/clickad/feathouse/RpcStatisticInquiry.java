package cn.clickwise.clickad.feathouse;

import cn.clickwise.rpc.HiveStatisticByKeysClient;
import cn.clickwise.rpc.HiveStatisticByKeysCommand;


public class RpcStatisticInquiry extends StatisticInquiry{

	@Override
	public StatisticStruct getDmpStatistic(Dmp dmp) {
		
		int day=100;
		
		HiveStatisticByKeysClient ec = new HiveStatisticByKeysClient();
		cn.clickwise.rpc.Connection con = new cn.clickwise.rpc.Connection();
		con.setHost(dmp.getHost());
		con.setPort(dmp.getRpcPort());
		con.setMethod(dmp.getDmpStatisticMethod());

		HiveStatisticByKeysCommand hfkc = new HiveStatisticByKeysCommand();
		
		String tmpIdentify = "remote_statistic";
		hfkc.setDay(day);
		hfkc.setTmpIdentify(tmpIdentify);
		hfkc.setKeyName("ttt.txt");
		hfkc.setKeyPath("temp/ttt.txt");

		hfkc.setTableName("astat");
		hfkc.setKeyFieldName("user_id");
		hfkc.setIpFieldName("sip");
		hfkc.setKeyTableName("statistic_keys");
		hfkc.setAreaCode("009");
		hfkc.setResultName("local_user_statistic.txt");
		hfkc.setResultPath("temp/local_user_statistic.txt");
		hfkc.initRandomFileName();

		ec.setHfkc(hfkc);
		ec.connect(con);
		ec.execute(hfkc);
		
		
		return null;
	}

}
