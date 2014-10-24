package cn.clickwise.clickad.radiusClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.lib.bytes.BytesTransform;
import cn.clickwise.lib.time.TimeOpera;

/**
 * 轻量级的 radius 数据接收端 待修改： 目标：尽少占用内存 尽少使用new 速度尽量快 步骤尽量少
 * 
 * @author zkyz
 */
public class LightRadiusClient extends RadiusClientNew {

	private static String ip;

	private static int port;

	private static Logger logger = LoggerFactory
			.getLogger(EasyRadiusClient.class);

	private InputStream sockIn;

	private OutputStreamWriter sockOut;

	private byte[] head = new byte[16];

	private byte[] body = new byte[500];

	private RadiusPacket rp = new RadiusPacket();
	private PacketHead ph = new PacketHead();
	private PacketBody pb = new PacketBody();

	private byte[] obuffer;

	private byte[] dbuffer;

	private byte[] stbuffer = new byte[16];

	private ByteArrayInputStream bintput;

	private DataInputStream dintput;

	public void init() {
		obuffer = BytesTransform.completeBytes(new byte[1]);
		dbuffer = BytesTransform.completeBytes(new byte[2]);
		bintput = new ByteArrayInputStream(dbuffer);
		dintput = new DataInputStream(bintput);
	}

	@Override
	public State connect(RadiusCenter rc) {

		try {
			sock = new Socket(rc.getIp(), rc.getPort());
			sockIn = sock.getInputStream();

			OutputStream outputStream = sock.getOutputStream();

			sockOut = new OutputStreamWriter(outputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public State sendHeartbeat() throws IOException {

		sockOut.write(Heartbeat.heartbeat);

		return null;
	}

	@Override
	public RadiusPacket readPacket() throws Exception {
		// TODO Auto-generated method stub
		// head = new byte[16];

		// ////RadiusPacket rp = new RadiusPacket();
		// ///PacketHead ph = new PacketHead();
		// ///PacketBody pb = new PacketBody();

		// 读取消息头
		int hn = -1;
		int kl = 0;
		while (hn < 0) {
			try {
				hn = sockIn.read(head);
				kl++;
			} catch (Exception e) {

			}
			if (kl > 5) {
				System.out.println("reconnect to the server");
				throw new Exception();
			}
		}

		// System.out.println("read bytes hn:"+hn);
		// System.out.println(BytesTransform.bytes2str(head));

		ph.setHead(head);
		ph.parseBytes2Info();
		rp.setPackHead(ph);
		head = null;
		// 读取消息体
		// System.out.println("ph.length:"+ph.getPacketBodyLength());
		if (ph.getPacketBodyLength() < 12) {
			return null;

		}

		// ////byte[] body = new byte[ph.getPacketBodyLength() - 12];

		int rn = sockIn.read(body, 0, ph.getPacketBodyLength() - 12);
		if (rn < 0) {
			return null;
		}

		// System.out.println("read bytes:" + rn);
		// System.out.println(BytesTransform.bytes2str(body));
		pb.setBody(body);
		// fos.write(body);
		rp.setPackBody(pb);
		receiveNoAnalysisCompletelyPacketBody(rp, ph.getPacketBodyLength() - 12);
		// ////body = null;
		// ////rp = null;
		// ////ph = null;
		// ////pb = null;

		return rp;
	}

	@Override
	public void writePacket(RadiusPacket rp) {
		// TODO Auto-generated method stub

	}

	/**
	 * 解析消息体，从消息体解析出code、packetIdentifier、length、authenticator、 user
	 * name、framedIpAddress、acctStatusType的普通形式
	 * 
	 * @param rp
	 */
	public void receiveNoAnalysisCompletelyPacketBody(RadiusPacket rp, int blen)
			throws Exception {
		int j = 0;

		// ////byte[] body = rp.getPackBody().getBody();

		int k = 0;
		int unl = 0;

		// ////byte[] obuffer = BytesTransform.completeBytes(new byte[1]);
		// ///byte[] dbuffer = BytesTransform.completeBytes(new byte[2]);

		for (int t = 0; t < dbuffer.length; t++) {
			dbuffer[t] = 0;
		}
		// ////byte[] stbuffer = new byte[16];
		int recLen = 0;

		while (j + 44 < blen) {
			// Record rec = new Record();

			// System.out.println(BytesTransform.bytes2str(body));
			// code
			obuffer[3] = body[j++];
			// rec.setCode(BytesTransform.byteToInt2(obuffer));

			// packetIdentifier
			obuffer[3] = body[j++];
			// rec.setPacketIdentifier(BytesTransform.byteToInt2(obuffer));

			// length
			for (k = 0; k < 2; k++) {
				dbuffer[k + 2] = body[j++];
			}

			// System.out.println("dbuffer:"+BytesTransform.bytes2str(dbuffer));
			// ////recLen = BytesTransform.byteToInt2(dbuffer);
			recLen = BytesTransform.byteToInt2(dbuffer);
			// System.out.println("rec.len:"+rec.getLength());
			// authenticator
			for (k = 0; k < 16; k++) {
				stbuffer[k] = body[j++];
			}
			// rec.setAuthenticator(new String(stbuffer));

			// user name
			unl = recLen - 32;
			// unl=BytesTransform.byteToIntv(rec.getLength())-32;
			// System.out.println("unl:"+unl);
			if ((unl + 12) < 0) {
				return;
			}

			byte[] ufa = new byte[unl + 12];
			// System.out.println("j:"+j+" ufa:"+ufa.length+" unl:"+unl+" body:"+body.length);
			for (k = 0; k < ufa.length; k++) {
				ufa[k] = body[k + j];
			}

			j = j + ufa.length;// 结束循环

			logger.info(TimeOpera.getCurrentTime() + "\t"
					+ BytesTransform.bytes2str(ufa));
			ufa = null;
			// rec=null;

		}

		// ////body = null;
		// ////obuffer = null;
		// ////dbuffer = null;
		// ////stbuffer = null;
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

	@Override
	public void start(RadiusCenter rc) {

		while (true) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			connect(rc);
			long startTime = TimeOpera.getCurrentTimeLong();

			try {
				while (true) {

					if (TimeOpera.getCurrentTimeLong() - startTime > 4000) {
						startTime = TimeOpera.getCurrentTimeLong();
						System.out.println("send the heart beat");
						sendHeartbeat();
					}

					RadiusPacket rp = new RadiusPacket();
					rp = readPacket();
					if (rp == null) {
						continue;
					}
					rp = null;
					// writePacket(rp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public int byteToInt2(byte[] b) {

		int i = 0;
		try {
			// bintput.reset();
			// bintput = new ByteArrayInputStream(b);
			// dintput = new DataInputStream(bintput);
			bintput.reset();
			bintput.read(b);
			dintput.read(b);
			i = dintput.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return i;

	}
	
	public int byteArrayToInt(byte[] b, int offset) {
	       int value= 0;
	       for (int i = 0; i < 4; i++) {
	           int shift= (4 - 1 - i) * 8;
	           value +=(b[i + offset] & 0x000000FF) << shift;
	       }
	       return value;
	 }

	public static void main(String[] args) {
		RadiusCenter rc = new RadiusCenter("221.231.154.17", 9002);
		LightRadiusClient lrc = new LightRadiusClient();
		lrc.init();
		lrc.start(rc);
	}

}
