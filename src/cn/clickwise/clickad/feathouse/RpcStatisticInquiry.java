package cn.clickwise.clickad.feathouse;

import cn.clickwise.lib.string.FileToArray;
import cn.clickwise.rpc.HiveStatisticByKeysClient;
import cn.clickwise.rpc.HiveStatisticByKeysCommand;

public class RpcStatisticInquiry extends StatisticInquiry {

	private ConfigureFactory confFactory = null;

	public RpcStatisticInquiry() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();

	}

	@Override
	public StatisticStruct getDmpStatistic(Dmp dmp,int day) {

		StatisticStruct sst=new StatisticStruct();

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
		hfkc.setKeyTableName(dmp.getKeyTableName());
		hfkc.setAreaCode(dmp.getArea().getAreaCode());
		hfkc.setResultName(confFactory.getDmpStatisticResultFile(day, dmp));
		hfkc.setResultPath(confFactory.getDmpStatisticResultFile(day, dmp));
		hfkc.initRandomFileName();

		ec.setHfkc(hfkc);
		ec.connect(con);
		ec.execute(hfkc);

		String[] statistic_lines=null;
		try{
		statistic_lines=FileToArray.fileToDimArr(confFactory.getDmpStatisticResultFile(day, dmp));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(statistic_lines==null||statistic_lines.length!=1)
		{
			return null;
		}
		
		sst=confFactory.string2StatisticResult(statistic_lines[0]);
		
		return sst;
	}

}
