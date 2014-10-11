package cn.clickwise.clickad.feathouse;

/**
 * 数据存取接口定义
 * @author zkyz
 */
public abstract class DataStore {
	
    /**
     * 连接kv-value server
     * @param con
     * @return
     */
	public abstract State connect(Connection con);
	
	public abstract State write2db(Record rec,int day);
	
	public abstract State deleteExpired(TimeRange time);
	
	
	
}
