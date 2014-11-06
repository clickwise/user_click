package cn.clickwise.clickad.feathouse;

import cn.clickwise.rpc.HiveStatisticByKeysClient;
import cn.clickwise.rpc.HiveStatisticByKeysCommand;

public class RpcStatisticInquiry extends StatisticInquiry {

	private ConfigureFactory confFactory = null;

	public RpcStatisticInquiry() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();

	}

	@Override
	public StatisticStruct getDmpStatistic(Dmp dmp) {

		int day = 100;

		HiveStatisticByKeysClient ec = new HiveStatisticByKeysClient();
		cn.clickwise.rpc.Connection con = new cn.clickwise.rpc.Connection();
		con.setHost(dmp.getHost());
		con.setPort(dmp.getRpcPort());
		con.setMethod(dmp.getDmpStatisticMethod());

		HiveStatisticByKeysCommand hfkc = new HiveStatisticByKeysCommand();
		hfkc.setDay(day);
		hfkc.setTmpIdentify(confFactory.getStatisticTmpIdentify());

		hfkc.setKeyName(confFactory.getDmpUidFile(day, dmp));
		hfkc.setKeyPath(confFactory.getDmpUidDirectory() + "/"
				+ confFactory.getDmpUidFile(day, dmp));

		hfkc.setTableName(dmp.getSourceTableName());
		hfkc.setKeyFieldName(dmp.getSourceUidFieldName());
		hfkc.setIpFieldName(dmp.getSourceIpFieldName());
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
