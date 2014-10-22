package cn.clickwise.clickad.radiusClient;

import java.net.Socket;

public abstract class RadiusClient {

	Socket sock;
	
	/**
	 * 和radiusCenter建立连接
	 * @param rc
	 * @return
	 */
	public abstract State connect(RadiusCenter rc);

	/**
	 * 发送心跳信息
	 * @return
	 */
	public abstract State sendHeartbeat();

	/**
	 * 读取一个报文块
	 * @return
	 */
	public abstract RadiusPacket readPacket();
	
	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}
	
	public abstract void writePacket(RadiusPacket rp);
	
	public abstract void start(RadiusCenter rc);

}
