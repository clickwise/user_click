package cn.clickwise.clickad.radiusClient;

import java.io.IOException;
import java.net.Socket;

public abstract class RadiusClientNew {

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
	public abstract State sendHeartbeat() throws IOException;

	/**
	 * 读取一个报文块
	 * @return
	 */
	public abstract RadiusPacket readPacket() throws Exception;
	
	
	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}
	
	public abstract void writePacket(RadiusPacket rp);
	
	public abstract void start(RadiusCenter rc);
}
