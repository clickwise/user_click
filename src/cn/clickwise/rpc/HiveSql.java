package cn.clickwise.rpc;

/**
 * 根据查询条件生成hive sql
 * 输出是一条linux命令
 * @author zkyz
 */
public class HiveSql {

	private static final String hive="hive -e ";
	
	public static String getSql(HiveFetchByKeysCommand hfkc)
	{
		String sql="";
		sql="INSERT OVERWRITE LOCAL DIRECTORY '"+hfkc.getResultRemotePath()+"' SELECT *  FROM "+hfkc.getTableName()+" a JOIN "+hfkc.getKeyTableName()+" b  ON  a.cookie=b.cookie where a.dt="+hfkc.getDay()+";";
		return hive+"\""+sql+"\"";
	}
	
	public static String load2hive(HiveFetchByKeysCommand hfkc)
	{
		String cmd="";
		cmd="load data inpath '"+hfkc.getHdfTmpPath()+"' overwrite into table "+hfkc.getKeyTableName()+" partition(dt="+hfkc.getDay()+",dp='part1');";
		return hive+"\""+cmd+"\"";
	}
	
	
	
}
