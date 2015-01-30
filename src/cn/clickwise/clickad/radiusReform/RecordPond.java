package cn.clickwise.clickad.radiusReform;

/**
 * 未完全解析的记录池
 * 目前实现一个生产者，多个消费者
 * @author zkyz
 */
public abstract class RecordPond {
	
	/**
	 * 向记录池存入record
	 * @param record
	 */
	public abstract void add2Pond(String record);
	
	/**
	 * 从记录池取record
	 * @return
	 */
	public abstract String pollFromPond();
	
	/**
	 * 启动线程从记录池取record并彻底解析
	 * @param threadNum
	 */
	public abstract void startConsume(int threadNum);
	
	
	
	
}
