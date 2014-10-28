package cn.clickwise.clickad.radiusClient;

/**
 * 在线数据库存储用户账户、ip的当前状态
 * @author zkyz
 */
public abstract class OnlineDatabase {

	private String ip;
	
	private int port;
	
	private String database;
	

	/**
	 * 连接数据库
	 */
	public abstract void connect(RedisCenter rc);
	
	/**
	 * 根据rec的类型更新在线数据库
	 */
	public abstract void update(RecordLight rec);
	
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
