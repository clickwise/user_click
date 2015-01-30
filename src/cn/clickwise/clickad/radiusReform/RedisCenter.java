package cn.clickwise.clickad.radiusReform;

public class RedisCenter {
	
	private String ip;
	
	private int port;
	
	private String database;
	
	public RedisCenter(String ip,int port,String database)
	{
		this.setIp(ip);
		this.setPort(port);
		this.setDatabase(database);
	}

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
