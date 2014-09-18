package cn.clickwise.clickad.feathouse;

/**
 * 数据存取接口定义
 * @author zkyz
 */
public abstract class DataStore {

	public abstract State write2db(Record rec);
	
	public abstract State deleteExpired(TimeRange time);
	
	
	
}
