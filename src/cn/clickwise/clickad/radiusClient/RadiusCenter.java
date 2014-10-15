package cn.clickwise.clickad.radiusClient;

/**
 * radius服务器端
 * @author zkyz
 */
public class RadiusCenter {

	//radius center ip
	private String ip;
	
	//radius center port
	private int port;
	
	public RadiusCenter(String ip,int port){
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
