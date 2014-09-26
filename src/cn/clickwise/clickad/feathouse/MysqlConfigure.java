package cn.clickwise.clickad.feathouse;

/**
 * mysql的配置信息封装
 * @author zkyz
 *
 */
public class MysqlConfigure {

	private String ip;
	private String user;
	private String password;
	private int port;
	private String dbname;
	
	public MysqlConfigure()
	{
		
	}
	
	public MysqlConfigure(String ip,String user,String password,int port,String dbname)
	{
		this.ip=ip;
		this.user=user;
		this.password=password;
		this.port=port;
		this.dbname=dbname;
	}
	
	public MysqlConfigure(String ip,String user,String password,int port)
	{
		this.ip=ip;
		this.user=user;
		this.password=password;
		this.port=port;
	}
	
	
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getDbname() {
		return dbname;
	}
	
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	
}
