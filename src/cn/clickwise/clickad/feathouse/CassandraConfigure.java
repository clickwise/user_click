package cn.clickwise.clickad.feathouse;

public class CassandraConfigure {

	private String host;
	
	private int port;
	
	private String cfName;
	
	private String keySpace;
	
	private String columnName;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getCfName() {
		return cfName;
	}
	public void setCfName(String cfName) {
		this.cfName = cfName;
	}
	public String getKeySpace() {
		return keySpace;
	}
	public void setKeySpace(String keySpace) {
		this.keySpace = keySpace;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
}
