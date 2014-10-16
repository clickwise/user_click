package cn.clickwise.clickad.radiusClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import cn.clickwise.lib.bytes.BytesTransform;
import cn.clickwise.lib.time.TimeOpera;


public class EasyRadiusClient extends RadiusClient {

	private InputStream sockIn;

	private OutputStreamWriter sockOut;

	private FileOutputStream fos;
	
	private ConfigureFactory confFactory;
	
	
	@Override
	public State connect(RadiusCenter rc) {

		State state = new State();

		confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
		
		try {
			sock = new Socket(rc.getIp(), rc.getPort());
			sockIn = sock.getInputStream();

			OutputStream outputStream = sock.getOutputStream();
			
			sockOut = new OutputStreamWriter(outputStream);
            fos=new FileOutputStream(confFactory.getPcapDirectory()+confFactory.getPcapFile());
			
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
		
		RadiusPacket rp=new RadiusPacket();		
		PacketHead ph=new PacketHead();
		PacketBody pb=new PacketBody();
		
		try {
			sockIn.read(head);
            ph.setHead(head);
			ph.parseBytes2Info();
			rp.setPackHead(ph);
			System.out.println("ph.length:"+ph.getPacketBodyLength());
			byte[] body=new byte[ph.getPacketBodyLength()];
			sockIn.read(body);
			pb.setBody(body);
			//fos.write(body);
			rp.setPackBody(pb);
			parsePacketBody(rp);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rp;
	}
	
	//处理消息体
	public void parsePacketBody(RadiusPacket rp)
	{
	     int j=0;
	     
	     byte[] obuffer=new byte[1];
	     byte[] dbuffer=new byte[2];
	     byte[] stbuffer=new byte[16];
	     byte[] sixbuffer=new byte[6];
	     
	     byte[] body=rp.getPackBody().getBody();
	     
	     int k=0;
	     
	     while(j<body.length)
	     {
	    	 Record rec=new Record();
	    	
	    	 //code
	    	 obuffer[0]=body[j++];
	    	 rec.setCode(BytesTransform.byteToInt2(obuffer));
	    	 
	    	 //packetIdentifier
	    	 obuffer[0]=body[j++];
	    	 rec.setPacketIdentifier(BytesTransform.byteToInt2(obuffer));
	    	 
	    	 //length
	    	 for(k=0;k<2;k++)
	    	 {
	    		 dbuffer[k]=body[j++];
	    	 }
	    	 rec.setLength(BytesTransform.byteToInt2(dbuffer));
	    	 
	    	 //authenticator
	    	 for(k=0;k<16;k++)
	    	 {
	    		 stbuffer[k]=body[j++];
	    	 }
	    	 rec.setAuthenticator(new String(stbuffer));
	    	 
	    	 //acctStatusType
	    	 for(k=0;k<6;k++)
	    	 {
	    		 sixbuffer[k]=body[j++];
	    	 }
	    	 rec.setAcctStatusType(BytesTransform.byteToInt2(sixbuffer));
	    	 
	    	 byte[] userBuffer=new byte[rec.getLength()];
	    	 for(k=0;k<rec.getLength();k++)
	    	 {
	    		 userBuffer[k]=body[j++];
	    	 }
	    	 rec.setUserName(new String(userBuffer));
	    	 
	    	 byte[] sixnbuffer=new byte[6];
	    	 for(k=0;k<6;k++)
	    	 {
	    		 sixnbuffer[k]=body[j++];
	    	 }
	    	 rec.setFramedIpAddress(sixnbuffer);
	    	 
	    	 
	    	 System.out.println(rec.toString());
	    	 
	     }
	     
	     
	     
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
		long startTime=TimeOpera.getCurrentTimeLong();
		while(true){
			if(TimeOpera.getCurrentTimeLong()-startTime>4000)
			{
				startTime=TimeOpera.getCurrentTimeLong();
				sendHeartbeat();
			}
			
			RadiusPacket rp=new RadiusPacket();
			rp=readPacket();
			writePacket(rp);		
		}
		
	}
	

	
	public static void main(String[] args)
	{
		RadiusCenter rc=new RadiusCenter("221.231.154.17",9002);
		EasyRadiusClient erc=new EasyRadiusClient();
		erc.start(rc);
	}

}
