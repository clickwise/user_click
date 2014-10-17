package cn.clickwise.liqi.str.test;

public class Rec {

	private String ip;
	
	private int port;

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
	
	public String toString()
	{
		String str="";
		str+="ip="+ip+";port="+port;
		return str;
	}
	
}
