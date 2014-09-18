package cn.clickwise.clickad.feathouse;

/**
 * 查询接口定义
 * @author zkyz
 */
public abstract class Query {

	/**
	 * 按照用户uid 查询用户记录
	 * @param key
	 * @return
	 */
	public abstract Record queryUid(Key key);
	
	/**
	 * 重置不同地区uid(cookie)查询数量
	 * @param key
	 * @return
	 */
	abstract State resetStatistics(Key key);
	
	
	
	
	
}
