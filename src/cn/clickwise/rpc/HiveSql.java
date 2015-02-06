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

	public static String createTableStatistic(HiveStatisticByKeysCommand hfkc) {
		String sql = "";
		if (!((hfkc.getAreaCode().trim()).equals("030"))) {// 非浙江
			sql = " use clickwise; CREATE TABLE IF NOT EXISTS "
					+ hfkc.getKeyTableName()
					+ "("
					+ hfkc.getKeyFieldName()
					+ " string) PARTITIONED BY(dt STRING,dp string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001' LINES TERMINATED BY '\n';";
		} else {
			sql = "  use clickwise;CREATE TABLE IF NOT EXISTS "
					+ hfkc.getKeyTableName()
					+ "("
					+ hfkc.getKeyFieldName()
					+ " string) PARTITIONED BY(dt STRING,dp string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001' LINES TERMINATED BY '\n';";
		}
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

	/**
	 * 这个方法不同于getSql
	 * 
	 * @param hfkc
	 * @return
	 */
	public static String getSqlStatistic(HiveStatisticByKeysCommand hfkc) {
		String sql = "";

		if (!((hfkc.getAreaCode().trim()).equals("030"))) {// 非浙江
			// sql =
			// "use clickwise; INSERT OVERWRITE LOCAL DIRECTORY '/home/clickwise/lq/statistic_keys/"+hfkc.getDay()+"' SELECT a.dt,'"+hfkc.getAreaCode()+"',count(1), count(distinct user_id), count(distinct sip) FROM astat a JOIN statistic_keys b  ON  a.user_id=b.uid where a.user_id IS NOT NULL and b.uid IS NOT NULL and b.dt=20141103 and a.dt=20141103   group by a.dt,'009';";
			sql = "use clickwise; INSERT OVERWRITE LOCAL DIRECTORY '"
					+ hfkc.getResultRemotePath() + "' SELECT a.dt,'"
					+ hfkc.getAreaCode() + "',count(1), count(distinct a."
					+ hfkc.getKeyFieldName() + "), count(distinct a."
					+ hfkc.getIpFieldName() + ") FROM " + hfkc.getTableName()
					+ " a JOIN " + hfkc.getKeyTableName() + " b  ON  a."
					+ hfkc.getKeyFieldName() + "=b." + hfkc.getKeyFieldName()
					+ " where a." + hfkc.getKeyFieldName()
					+ " IS NOT NULL and b." + hfkc.getKeyFieldName()
					+ " IS NOT NULL and b.dt=" + hfkc.getDay() + " and a.dt="
					+ hfkc.getDay() + " group by a.dt,'" + hfkc.getAreaCode()
					+ "';";
			System.out.println("statistic sql:" + sql);
		} else {// 浙江
			sql = "use clickwise; INSERT OVERWRITE LOCAL DIRECTORY '"
					+ hfkc.getResultRemotePath() + "' SELECT a.dt,'"
					+ hfkc.getAreaCode() + "',count(1), count(distinct a."
					+ hfkc.getKeyFieldName() + "), count(distinct a."
					+ hfkc.getIpFieldName() + ") FROM " + hfkc.getTableName()
					+ " a JOIN " + hfkc.getKeyTableName() + " b  ON  a."
					+ hfkc.getKeyFieldName() + "=b." + hfkc.getKeyFieldName()
					+ " where a." + hfkc.getKeyFieldName()
					+ " IS NOT NULL and b." + hfkc.getKeyFieldName()
					+ " IS NOT NULL and b.dt=" + hfkc.getDay() + " and a.dt="
					+ hfkc.getDay() + " group by a.dt,'" + hfkc.getAreaCode()
					+ "';";
			System.out.println("statistic sql:" + sql);
		}

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
				sql = " use clickwise;INSERT OVERWRITE LOCAL DIRECTORY '"
						+ hftc.getResultRemotePath() + "' SELECT DISTINCT "
						+ hftc.getKeyFieldName() + "  FROM "
						+ hftc.getTableName() + " where dt=" + hftc.getDay()
						+ ";";
			} else if (hftc.getQueryType() == 1)// 返回all Fields
			{
				sql = "use clickwise;INSERT OVERWRITE LOCAL DIRECTORY '"
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

	public static String dropOldStatistic(HiveStatisticByKeysCommand hfkc) {
		String cmd = "";
		if (!((hfkc.getAreaCode().trim()).equals("030"))) {
			cmd = "use clickwise;ALTER TABLE " + hfkc.getKeyTableName()
					+ "  DROP PARTITION (dt='" + hfkc.getDay() + "');";
		} else {
			cmd = "use clickwise;ALTER TABLE " + hfkc.getKeyTableName()
					+ "  DROP PARTITION (dt='" + hfkc.getDay() + "');";
		}
		return hive + "\"" + cmd + "\"";
	}

	public static String load2hive(HiveFetchByKeysCommand hfkc) {
		String cmd = "";
		cmd = "use clickwise; load data inpath '" + hfkc.getHdfTmpPath()
				+ "' overwrite into table " + hfkc.getKeyTableName()
				+ " partition(dt=" + hfkc.getDay() + ",dp='part1');";
		return hive + "\"" + cmd + "\"";
	}

	public static String load2hiveStatistic(HiveStatisticByKeysCommand hfkc) {
		String cmd = "";
		if (!((hfkc.getAreaCode().trim()).equals("030"))) {
			cmd = "use clickwise; load data inpath '" + hfkc.getHdfTmpPath()
					+ "' overwrite into table " + hfkc.getKeyTableName()
					+ " partition(dt=" + hfkc.getDay() + ",dp='part1');";
		} else {
			cmd = " use clickwise;load data inpath '" + hfkc.getHdfTmpPath()
					+ "' overwrite into table " + hfkc.getKeyTableName()
					+ " partition(dt=" + hfkc.getDay() + ",dp='part1');";
		}
		return hive + "\"" + cmd + "\"";
	}
}
