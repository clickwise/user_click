package cn.clickwise.clickad.radiusClient;

/**
 * 未完全解析的记录池
 * 目前实现一个生产者，多个消费者
 * @author zkyz
 */
public abstract class RecordPond {
	
	public abstract void add2Pond(String record);
	
	public abstract String pollFromPond();
	
	
	
	
	
	
}
