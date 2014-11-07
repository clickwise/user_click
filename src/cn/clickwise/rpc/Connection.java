package cn.clickwise.rpc;

public class Connection {

	private String host;
	
	private int port;
	
	private String method;
	
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
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String toString()
	{
		String str="";
		str="host:"+host+" port:"+port+" method:"+method;
		return str;
		
		
	}
	
}
