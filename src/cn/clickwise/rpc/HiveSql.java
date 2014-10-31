package cn.clickwise.rpc;

/**
 * 根据查询条件生成hive sql 输出是一条linux命令
 * 
 * @author zkyz
 */
public class HiveSql {

	private static final String hive = "hive -e ";

	public static String createTable(HiveFetchByKeysCommand hfkc) {
		String sql = "";
		sql = " use clickwise; CREATE TABLE IF NOT EXISTS "
				+ hfkc.getKeyTableName()
				+ "("
				+ hfkc.getKeyFieldName()
				+ " string) PARTITIONED BY(dt STRING,dp string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001' LINES TERMINATED BY '\n';";
		return hive + "\"" + sql + "\"";
	}

	public static String getSql(HiveFetchByKeysCommand hfkc) {
		String sql = "";
		sql = "use clickwise; INSERT OVERWRITE LOCAL DIRECTORY '"
				+ hfkc.getResultRemotePath() + "' SELECT *  FROM "
				+ hfkc.getTableName() + " a JOIN " + hfkc.getKeyTableName()
				+ " b  ON  a." + hfkc.getKeyFieldName() + "=b."
				+ hfkc.getKeyFieldName() + " where a.dt=" + hfkc.getDay() + ";";
		return hive + "\"" + sql + "\"";
	}

	public static String getSql(HiveFetchTableCommand hftc) {
		String sql = "";

	
		if (!((hftc.getAreaCode().trim()).equals("030"))) {

			if (hftc.getQueryType() == 0)// 只返回Key Field
			{
				sql = "use clickwise; INSERT OVERWRITE LOCAL DIRECTORY '"
						+ hftc.getResultRemotePath() + "' SELECT DISTINCT "
						+ hftc.getKeyFieldName() + "  FROM "
						+ hftc.getTableName() + " where dt=" + hftc.getDay()
						+ ";";
			} else if (hftc.getQueryType() == 1)// 返回all Fields
			{
				sql = "use clickwise; INSERT OVERWRITE LOCAL DIRECTORY '"
						+ hftc.getResultRemotePath() + "' SELECT *  FROM "
						+ hftc.getTableName() + " where dt=" + hftc.getDay()
						+ ";";
			}
		} else {
			if (hftc.getQueryType() == 0)// 只返回Key Field
			{
				sql = " INSERT OVERWRITE LOCAL DIRECTORY '"
						+ hftc.getResultRemotePath() + "' SELECT DISTINCT "
						+ hftc.getKeyFieldName() + "  FROM "
						+ hftc.getTableName() + " where dt=" + hftc.getDay()
						+ ";";
			} else if (hftc.getQueryType() == 1)// 返回all Fields
			{
				sql = "INSERT OVERWRITE LOCAL DIRECTORY '"
						+ hftc.getResultRemotePath() + "' SELECT *  FROM "
						+ hftc.getTableName() + " where dt=" + hftc.getDay()
						+ ";";
			}
		}

		return hive + "\"" + sql + "\"";

	}

	public static String dropOld(HiveFetchByKeysCommand hfkc) {
		String cmd = "";
		cmd = "use clickwise;ALTER TABLE " + hfkc.getKeyTableName()
				+ "  DROP PARTITION (dt='" + hfkc.getDay() + "');";
		return hive + "\"" + cmd + "\"";
	}

	public static String load2hive(HiveFetchByKeysCommand hfkc) {
		String cmd = "";
		cmd = "use clickwise; load data inpath '" + hfkc.getHdfTmpPath()
				+ "' overwrite into table " + hfkc.getKeyTableName()
				+ " partition(dt=" + hfkc.getDay() + ",dp='part1');";
		return hive + "\"" + cmd + "\"";
	}

}
