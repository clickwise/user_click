package cn.clickwise.clickad.feathouse;

import java.util.List;

/**
 * 查询接口定义
 * @author zkyz
 */
public abstract class DataQuery {

    /**
     * 连接kv-value server
     * @param con
     * @return
     */
	public abstract State connect(Connection con);
	
	/**
	 * 按照用户uid 查询用户记录
	 * @param key
	 * @return key对应的 record list
	 */
	public abstract List<Record> queryUid(Key key);
	
	/**
	 * 按照用户uid 查询用户记录
	 * @param key
	 * @param top 限制返回的record 数量
	 * @return key对应的 record list
	 */
	public abstract List<Record> queryUidTop(Key key,int top);
	
	/**
	 * 重置不同地区uid(cookie)查询数量
	 * @param key
	 * @return
	 */
	abstract State resetStatistics(Key key);
	
	
	abstract State logUnknownUid(Key key);
	
	abstract State logQuery(Key key);
	
	
}
