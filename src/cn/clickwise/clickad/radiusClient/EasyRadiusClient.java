package cn.clickwise.clickad.radiusClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import cn.clickwise.lib.bytes.BytesTransform;
import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

public class EasyRadiusClient extends RadiusClient {

	private InputStream sockIn;

	private OutputStreamWriter sockOut;

	private FileOutputStream fos;

	private ConfigureFactory confFactory;

	private RadiusCenter rc;

	@Override
	public State connect(RadiusCenter rc) {

		State state = new State();

		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();

		try {
			sock = new Socket(rc.getIp(), rc.getPort());
			sockIn = sock.getInputStream();

			OutputStream outputStream = sock.getOutputStream();

			sockOut = new OutputStreamWriter(outputStream);
			fos = new FileOutputStream(confFactory.getPcapDirectory()
					+ confFactory.getPcapFile());

			state.setStatValue(StateValue.Normal);
		} catch (Exception e) {
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}

		return state;
	}

	@Override
	public State sendHeartbeat() {

		State state = new State();

		try {
			sockOut.write(Heartbeat.heartbeat);
			state.setStatValue(StateValue.Normal);
		} catch (IOException e) {
			state.setStatValue(StateValue.Error);
		}

		return state;
	}

	@Override
	public RadiusPacket readPacket() {
		// TODO Auto-generated method stub
		byte[] head = new byte[16];

		RadiusPacket rp = new RadiusPacket();
		PacketHead ph = new PacketHead();
		PacketBody pb = new PacketBody();

		try {

			// 读取消息头
			int hn = -1;
			while (hn < 0) {
				hn = sockIn.read(head);
				if (hn < 0) {
					restart();
				}
			}

			// System.out.println("read bytes hn:"+hn);
			// System.out.println(BytesTransform.bytes2str(head));

			ph.setHead(head);
			ph.parseBytes2Info();
			rp.setPackHead(ph);

			// 读取消息体
			// System.out.println("ph.length:"+ph.getPacketBodyLength());

			if (ph.getPacketBodyLength() < 12) {
				restart();
			}

			byte[] body = new byte[ph.getPacketBodyLength() - 12];
			int rn = sockIn.read(body);
			if (rn < 0) {
				restart();
			}

			System.out.println("read bytes:" + rn);
			System.out.println(BytesTransform.bytes2str(body));
			pb.setBody(body);
			// fos.write(body);
			rp.setPackBody(pb);
			analysisPacketBody(rp);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return rp;
	}

	/**
	 * 处理消息体
	 * 
	 * @param rp
	 */
	public void parsePacketBody(RadiusPacket rp) {
		int j = 0;

		byte[] body = rp.getPackBody().getBody();

		int k = 0;
		int unl = 0;

		boolean standardOrder = true;

		while (j + 32 < body.length) {
			Recordn rec = new Recordn();

			// code
			byte[] codeBuffer = new byte[1];
			codeBuffer[0] = body[j++];
			rec.setCode(codeBuffer);

			// packetIdentifier
			byte[] identifierBuffer = new byte[1];
			identifierBuffer[0] = body[j++];
			rec.setPacketIdentifier(identifierBuffer);

			// length
			byte[] lengthBuffer = new byte[4];
			lengthBuffer[0] = 0;
			lengthBuffer[1] = 0;
			for (k = 0; k < 2; k++) {
				lengthBuffer[k + 2] = body[j++];
			}

			rec.setLength(lengthBuffer);

			// authenticator
			byte[] authenticatorBuffer = new byte[16];
			for (k = 0; k < 16; k++) {
				authenticatorBuffer[k] = body[j++];
			}
			rec.setAuthenticator(authenticatorBuffer);

			// user name
			unl = BytesTransform.byteToInt2(rec.getLength()) - 32;
			if (unl < 0) {
				restart();
			}
			System.out.println("unl:" + unl);

			if (standardOrder == true) {
				// unl=BytesTransform.byteToIntv(rec.getLength())-32;
				byte[] userBuffer = new byte[unl];
				for (k = 0; k < unl; k++) {
					userBuffer[k] = body[j++];
				}
				rec.setUserName(userBuffer);

				// framedIpAddress
				byte[] framedIpAddressbuffer = new byte[6];
				for (k = 0; k < 6; k++) {
					framedIpAddressbuffer[k] = body[j++];
				}
				rec.setFramedIpAddress(framedIpAddressbuffer);

				// acctStatusType
				byte[] acctStatusTypeBuffer = new byte[6];
				for (k = 0; k < 6; k++) {
					acctStatusTypeBuffer[k] = body[j++];
				}
				rec.setAcctStatusType(acctStatusTypeBuffer);
			}

			System.out.println(rec.toString());

		}
	}

	/**
	 * 解析消息体，userName、Framed IP Address、 Accounting Status没有固定顺序
	 * 
	 * @param rp
	 */
	public void parsePacketBodyNoUFAOrder(RadiusPacket rp) {
		int j = 0;

		byte[] body = rp.getPackBody().getBody();

		int k = 0;
		int unl = 0;

		while (j + 32 < body.length) {
			Recordn rec = new Recordn();

			// code
			byte[] codeBuffer = new byte[1];
			codeBuffer[0] = body[j++];
			rec.setCode(codeBuffer);

			// packetIdentifier
			byte[] identifierBuffer = new byte[1];
			identifierBuffer[0] = body[j++];
			rec.setPacketIdentifier(identifierBuffer);

			// length
			byte[] lengthBuffer = new byte[4];
			lengthBuffer[0] = 0;
			lengthBuffer[1] = 0;
			for (k = 0; k < 2; k++) {
				lengthBuffer[k + 2] = body[j++];
			}
			rec.setLength(lengthBuffer);

			// authenticator
			byte[] authenticatorBuffer = new byte[16];
			for (k = 0; k < 16; k++) {
				authenticatorBuffer[k] = body[j++];
			}
			rec.setAuthenticator(authenticatorBuffer);

			// user name
			unl = BytesTransform.byteToInt2(rec.getLength()) - 32;
			if (unl < 0) {
				restart();
			}
			System.out.println("unl:" + unl);
			

			// unl=BytesTransform.byteToIntv(rec.getLength())-32;
			byte[] userBuffer = new byte[unl];
			for (k = 0; k < unl; k++) {
				userBuffer[k] = body[j++];
			}
			rec.setUserName(userBuffer);

			// framedIpAddress
			byte[] framedIpAddressbuffer = new byte[6];
			for (k = 0; k < 6; k++) {
				framedIpAddressbuffer[k] = body[j++];
			}
			rec.setFramedIpAddress(framedIpAddressbuffer);

			// acctStatusType
			byte[] acctStatusTypeBuffer = new byte[6];
			for (k = 0; k < 6; k++) {
				acctStatusTypeBuffer[k] = body[j++];
			}
			rec.setAcctStatusType(acctStatusTypeBuffer);

			System.out.println(rec.toString());

		}
	}

	/**
	 * 解析消息体，从消息体解析出code、packetIdentifier、length、authenticator、 user
	 * name、framedIpAddress、acctStatusType的普通形式
	 * 
	 * @param rp
	 */
	public void analysisPacketBody(RadiusPacket rp) {
		int j = 0;

		byte[] body = rp.getPackBody().getBody();

		int k = 0;
		int unl = 0;

		byte[] obuffer = BytesTransform.completeBytes(new byte[1]);
		byte[] dbuffer = BytesTransform.completeBytes(new byte[2]);
		for(int t=0;t<dbuffer.length;t++)
		{
			dbuffer[t]=0;
		}
		byte[] stbuffer = new byte[16];
		byte[] sixbuffer = new byte[6];

		while (j + 44 < body.length) {
			Record rec = new Record();

			System.out.println(BytesTransform.bytes2str(body));
			// code
			obuffer[3] = body[j++];
			rec.setCode(BytesTransform.byteToInt2(obuffer));

			// packetIdentifier
			obuffer[3] = body[j++];
			rec.setPacketIdentifier(BytesTransform.byteToInt2(obuffer));

			// length
			for (k = 0; k < 2; k++) {
				dbuffer[k + 2] = body[j++];
			}
			rec.setLength(BytesTransform.byteToInt2(dbuffer));
            System.out.println("rec.len:"+rec.getLength());
			// authenticator
			for (k = 0; k < 16; k++) {
				stbuffer[k] = body[j++];
			}
			rec.setAuthenticator(new String(stbuffer));

			// user name
			unl = rec.getLength() - 32;
			// unl=BytesTransform.byteToIntv(rec.getLength())-32;
			// System.out.println("unl:"+unl);
				
			byte[] ufa=new byte[unl+12];
			System.out.println("j:"+j+" ufa:"+ufa.length+" unl:"+unl+" body:"+body.length);
			for(k=0;k<ufa.length;k++)
			{
				ufa[k]=body[k+j];
			}
			
			String ufaStr=BytesTransform.bytes2str(ufa);
			
		    int ipStart=ufaStr.indexOf("08 06");
		    int ipEnd=ipStart+17;
		    String ip=ufaStr.substring(ipStart, ipEnd);
		    
		    
		    int statusStart=ufaStr.indexOf("28 06");
		    int statusEnd=statusStart+17;   
		    String status=ufaStr.substring(statusStart, statusEnd);
		    
		    String userName=ufaStr.replaceFirst(ip, "").replaceFirst(status, "");
		    
			//byte[] userBuffer = new byte[unl];
			//for (k = 0; k < unl; k++) {
			//	userBuffer[k] = body[j++];
			//}
			//rec.setUserName(new String(userBuffer));
		    rec.setUserName(hexes2username(userName));

			// framedIpAddress
			//for (k = 0; k < 6; k++) {
			//	sixbuffer[k] = body[j++];
			//}
			//rec.setFramedIpAddress(bytes2ip(sixbuffer));
			rec.setFramedIpAddress(hexs2ip(ip));

			// acctStatusType
			//for (k = 0; k < 6; k++) {
			//	sixbuffer[k] = body[j++];
			//}
			//rec.setAcctStatusType(bytes2status(sixbuffer));
			rec.setAcctStatusType(hexes2status(status));
			
			System.out.println(rec.toString());

		}
	}

	public String bytes2ip(byte[] b) {
		String ip = "";
		if (b.length != 6) {
			return "";
		}

		byte[] ob = new byte[1];
		for (int i = 2; i < 6; i++) {
			ob[0] = b[i];
			if (i != 5) {
				ip += (BytesTransform.byteToInt2(BytesTransform
						.completeBytes(ob)) + ".");
			} else {
				ip += (BytesTransform.byteToInt2(BytesTransform
						.completeBytes(ob)));
			}
		}

		return ip;
	}

	public int bytes2status(byte[] b) {
		int status = -1;
		if (b.length != 6) {
			return -1;
		}

		byte[] ob = new byte[1];

		ob[0] = b[5];

		status = (BytesTransform.byteToInt2(BytesTransform.completeBytes(ob)));

		return status;
	}
	

	public String hexs2ip(String hexs) {
		String ip = "";
		if(SSO.tioe(hexs))
		{
			return "";
		}
		hexs=hexs.trim();
		
	    String[] tokens=hexs.split("\\s+");
		if(tokens.length!=6)
		{
			return "";
		}
        ip=Integer.parseInt(tokens[2],16)+"."+Integer.parseInt(tokens[3],16)+"."+Integer.parseInt(tokens[4],16)+"."+Integer.parseInt(tokens[5],16);
		return ip;
	}

	public int hexes2status(String hexs) {
		int status = -1;
		if(SSO.tioe(hexs))
		{
			return -1;
		}
		hexs=hexs.trim();
		
	    String[] tokens=hexs.split("\\s+");
		if(tokens.length!=6)
		{
			return -1;
		}
		status=Integer.parseInt(tokens[5],16);
		return status;
	}
	
	public String hexes2username(String hexs) {
		String username = "";
		if(SSO.tioe(hexs))
		{
			return "";
		}
		hexs=hexs.trim();
		
	    String[] tokens=hexs.split("\\s+");
		if(tokens.length<2)
		{
			return "";
		}

		for(int j=2;j<tokens.length;j++)
		{
			username+=((char)Integer.parseInt(tokens[j],16));
		}
		
		return username;
	}
	
	@Override
	public void writePacket(RadiusPacket rp) {

		try {
			fos.write(rp.getPackHead().getHead());
			fos.write(rp.getPackBody().getBody());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(RadiusCenter rc) {

		connect(rc);
		long startTime = TimeOpera.getCurrentTimeLong();
		while (true) {
			if (TimeOpera.getCurrentTimeLong() - startTime > 4000) {
				startTime = TimeOpera.getCurrentTimeLong();
				sendHeartbeat();
			}

			RadiusPacket rp = new RadiusPacket();
			rp = readPacket();
			writePacket(rp);
		}

	}

	public void restart() {
		try {
			System.out.println("sleep ten second!");
			Thread.sleep(confFactory.getResetConnectionSuspend());
		} catch (Exception e) {
			e.printStackTrace();
		}
		start(rc);
	}

	public static void main(String[] args) {
		RadiusCenter rc = new RadiusCenter("221.231.154.17", 9002);
		EasyRadiusClient erc = new EasyRadiusClient();
		erc.setRc(rc);
		erc.start(rc);
	}

	public RadiusCenter getRc() {
		return rc;
	}

	public void setRc(RadiusCenter rc) {
		this.rc = rc;
	}

}
