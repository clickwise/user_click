package cn.clickwise.rpc;

public class ProxyCommand {

	public static String pvUvIpByKeys(int day,String keyPath,String outPath)
	{
		String cmd="/home/clickwise/click_ana/astat/query_users.sh "+day+" "+keyPath;
		
		return cmd;
	}
	
}
