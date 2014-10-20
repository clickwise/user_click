package cn.clickwise.clickad.radiusClient;

public abstract class UserOnline {

	/**
	 * 连接在线数据库
	 * @param od
	 */
	public abstract void connect(OnlineDatabase od);
	
	/**
	 * 根据每条record更新在线数据库状态
	 * @param rec
	 */
	public abstract void update(Record rec);
	
	/**
	 * 关闭在线数据库
	 * @param od
	 */
	public abstract void close(OnlineDatabase od);
	
	
	
	
	
}
