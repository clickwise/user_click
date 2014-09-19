package cn.clickwise.clickad.feathouse;

/**
 * 不同地区uid(cookie)查询
 * @author zkyz
 */
public abstract class AreaStatistics {

	/**
	 * 将每天的不同地区uid(cookie)查询数kv store写入mysql
	 * @param timeRange
	 * @return
	 */
	public abstract State writeData2mysql(TimeRange timeRange);
	
	public abstract State writeData2file(TimeRange timeRange);
	
	
}
