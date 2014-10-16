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
	
	private RadiusCenter rc;
	
	
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
			
			//读取消息头
			int hn=-1;
			while(hn<0)
			{
			  hn=sockIn.read(head);
			  if(hn<0)
			  {
				  try{
				  System.out.println("sleep ten second!");	  
				  Thread.sleep(confFactory.getResetConnectionSuspend());
				  }
				  catch(Exception e)
				  {
					  e.printStackTrace();
				  }
				  start(rc);
			  }
			}
			
			
			//System.out.println("read bytes hn:"+hn);
			//System.out.println(BytesTransform.bytes2str(head));
			
            ph.setHead(head);
			ph.parseBytes2Info();
			rp.setPackHead(ph);
			
			
			//读取消息体
			//System.out.println("ph.length:"+ph.getPacketBodyLength());
			byte[] body=new byte[ph.getPacketBodyLength()-12];
			int rn=sockIn.read(body);
			System.out.println("read bytes:"+rn);
			System.out.println(BytesTransform.bytes2str(body));
			pb.setBody(body);
			//fos.write(body);
			rp.setPackBody(pb);
			analysisPacketBody(rp);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rp;
	}
	
	/**
	 * 处理消息体
	 * @param rp
	 */
	public void parsePacketBody(RadiusPacket rp)
	{
	     int j=0;

	     byte[] body=rp.getPackBody().getBody();
	     
	     int k=0;
	     int unl=0;
	     
	     while(j+32<body.length)
	     {
	    	 Recordn rec=new Recordn();
	    	
	    	 //code
	    	 byte[] codeBuffer=new byte[1];
	    	 codeBuffer[0]=body[j++];
	    	 rec.setCode(codeBuffer);
	    	 
	    	 //packetIdentifier
	    	 byte[] identifierBuffer=new byte[1];
	    	 identifierBuffer[0]=body[j++];
	    	 rec.setPacketIdentifier(identifierBuffer);
	    	 
	    	 //length
	    	 byte[] lengthBuffer=new byte[4];
	    	 lengthBuffer[0]=0;
	    	 lengthBuffer[1]=0;
	    	 for(k=0;k<2;k++)
	    	 {
	    		 lengthBuffer[k+2]=body[j++];
	    	 }

	    	 rec.setLength(lengthBuffer);
	    	 
	    	 //authenticator
	    	 byte[] authenticatorBuffer=new byte[16];
	    	 for(k=0;k<16;k++)
	    	 {
	    		 authenticatorBuffer[k]=body[j++];
	    	 }
	    	 rec.setAuthenticator(authenticatorBuffer);
	    	 
	    	 //user name
	    	 unl=BytesTransform.byteToInt2(rec.getLength())-32;
	    	 //unl=BytesTransform.byteToIntv(rec.getLength())-32;
	    	 System.out.println("unl:"+unl);
	    	 byte[] userBuffer=new byte[ unl];
	    	 for(k=0;k<unl;k++)
	    	 {
	    		 userBuffer[k]=body[j++];
	    	 }
	    	 rec.setUserName(userBuffer);
	    	 

	    	 //framedIpAddress
	    	 byte[] framedIpAddressbuffer=new byte[6];
	    	 for(k=0;k<6;k++)
	    	 {
	    		 framedIpAddressbuffer[k]=body[j++];
	    	 }
	    	 rec.setFramedIpAddress(framedIpAddressbuffer);
	    	 
	    	 //acctStatusType
	    	 byte[] acctStatusTypeBuffer=new byte[6];
	    	 for(k=0;k<6;k++)
	    	 {
	    		 acctStatusTypeBuffer[k]=body[j++];
	    	 }
	    	 rec.setAcctStatusType(acctStatusTypeBuffer);
	    	 
	    	 System.out.println(rec.toString());
	    	 
	     }  
	}

	/**
	 * 解析消息体，从消息体解析出code、packetIdentifier、length、authenticator、
	 * user name、framedIpAddress、acctStatusType的普通形式
	 * @param rp
	 */
	public void analysisPacketBody(RadiusPacket rp)
	{
	     int j=0;

	     byte[] body=rp.getPackBody().getBody();
	     
	     int k=0;
	     int unl=0;
	     
	     while(j<body.length)
	     {
	    	 Recordn rec=new Recordn();
	    	
	    	 //code
	    	 byte[] codeBuffer=new byte[1];
	    	 codeBuffer[0]=body[j++];
	    	 rec.setCode(codeBuffer);
	    	 
	    	 //packetIdentifier
	    	 byte[] identifierBuffer=new byte[1];
	    	 identifierBuffer[0]=body[j++];
	    	 rec.setPacketIdentifier(identifierBuffer);
	    	 
	    	 //length
	    	 byte[] lengthBuffer=new byte[4];
	    	 lengthBuffer[0]=0;
	    	 lengthBuffer[1]=0;
	    	 for(k=0;k<2;k++)
	    	 {
	    		 lengthBuffer[k+2]=body[j++];
	    	 }

	    	 rec.setLength(lengthBuffer);
	    	 
	    	 //authenticator
	    	 byte[] authenticatorBuffer=new byte[16];
	    	 for(k=0;k<16;k++)
	    	 {
	    		 authenticatorBuffer[k]=body[j++];
	    	 }
	    	 rec.setAuthenticator(authenticatorBuffer);
	    	 
	    	 //user name
	    	 unl=BytesTransform.byteToInt2(rec.getLength())-32;
	    	 //unl=BytesTransform.byteToIntv(rec.getLength())-32;
	    	 System.out.println("unl:"+unl);
	    	 byte[] userBuffer=new byte[ unl];
	    	 for(k=0;k<unl;k++)
	    	 {
	    		 userBuffer[k]=body[j++];
	    	 }
	    	 rec.setUserName(userBuffer);
	    	 

	    	 //framedIpAddress
	    	 byte[] framedIpAddressbuffer=new byte[6];
	    	 for(k=0;k<6;k++)
	    	 {
	    		 framedIpAddressbuffer[k]=body[j++];
	    	 }
	    	 rec.setFramedIpAddress(framedIpAddressbuffer);
	    	 
	    	 //acctStatusType
	    	 byte[] acctStatusTypeBuffer=new byte[6];
	    	 for(k=0;k<6;k++)
	    	 {
	    		 acctStatusTypeBuffer[k]=body[j++];
	    	 }
	    	 rec.setAcctStatusType(acctStatusTypeBuffer);
	    	 
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
