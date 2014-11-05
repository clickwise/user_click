package cn.clickwise.clickad.radiusClient;

public class ResolveCenter {

	//resolve center ip
	private String ip;
	
	//resolve center port
	private int port;
	
	public ResolveCenter(String ip,int port){
		this.ip=ip;
		this.port=port;
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
}
