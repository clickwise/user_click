package cn.clickwise.web.proxy;

/**
 * 未完全解析的记录池
 * 目前实现一个生产者，多个消费者
 * @author zkyz
 */
public abstract class UrlPond {
	
	/**
	 * 向记录池存入record
	 * @param record
	 */
	public abstract void add2Pond(String url);
	
	/**
	 * 从记录池取record
	 * @return
	 */
	public abstract String pollFromPond();
	
	
}
