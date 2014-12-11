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
	public StatisticStruct getDmpStatistic(Dmp dmp, int day) {

		StatisticStruct sst = new StatisticStruct();
		 System.out.println("ponit 0");
		HiveStatisticByKeysClient ec = new HiveStatisticByKeysClient();
		cn.clickwise.rpc.Connection con = new cn.clickwise.rpc.Connection();
		 System.out.println("ponit 01");
		con.setHost(dmp.getHost());
		con.setPort(dmp.getRpcPort());
		con.setMethod(dmp.getDmpStatisticMethod());
        
        System.out.println("ponit 1");
        
		HiveStatisticByKeysCommand hfkc = new HiveStatisticByKeysCommand();
		hfkc.setDay(day);
		hfkc.setTmpIdentify(confFactory.getStatisticTmpIdentify());
		System.out.println("ponit 2");
		hfkc.setKeyName(confFactory.getDmpUidFile(day, dmp));
		hfkc.setKeyPath(confFactory.getDmpUidDirectory() + "/"
				+ confFactory.getDmpUidFile(day, dmp));
		System.out.println("ponit 3");
		hfkc.setTableName(dmp.getSourceTableName());
		hfkc.setKeyFieldName(dmp.getSourceUidFieldName());
		hfkc.setIpFieldName(dmp.getSourceIpFieldName());
		hfkc.setKeyTableName(dmp.getKeyTableName());
		System.out.println("ponit 4");
		hfkc.setAreaCode(dmp.getArea().getAreaCode());
		hfkc.setResultName(confFactory.getDmpStatisticResultFile(day, dmp));
		hfkc.setResultPath(confFactory.getDmpStatisticResultDirectory()+"/"+confFactory.getDmpStatisticResultFile(day, dmp));
		hfkc.initRandomFileName();
		System.out.println("ponit 5");
		System.out.println("con:"+con.toString());
		System.out.println("hfkc:"+HiveStatisticByKeysCommand.writeObject(hfkc));
		ec.setHfkc(hfkc);
		
		ec.connect(con);
		System.out.println("connected");
		ec.execute(hfkc);
		System.out.println("ponit 6");
		String[] statistic_lines = null;
		try {
			String resFile=confFactory.getDmpStatisticResultDirectory()+"/"+confFactory
					.getDmpStatisticResultFile(day, dmp);
			System.out.println("resFile:"+resFile);
			statistic_lines = FileToArray.fileToDimArr(resFile);
			if (statistic_lines == null || statistic_lines.length < 1) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		System.out.println("line 0:"+statistic_lines[0]);
		sst = confFactory.string2StatisticResult(statistic_lines[0]);

		return sst;
	}

}
