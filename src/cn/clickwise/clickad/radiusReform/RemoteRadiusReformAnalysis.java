package cn.clickwise.clickad.radiusReform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

public class RemoteRadiusReformAnalysis {
	
	private InputStream sockIn;

	private OutputStreamWriter sockOut;

	private ConfigureFactory confFactory;

	private RadiusCenter rc;
		
	private OutputStream outputStream;
	
	private static Logger logger = LoggerFactory.getLogger(RemoteRadiusReform.class);
	
	Socket sock;
	
	String ctimestr;
	
	String rawRecord;
	
	long startTime;
	long gcstartTime ;
	
	//resolve
	private Socket resolveSock;
	
	//private InputStream resolveSockIn;
	
	private DataOutputStream resolveSockOut;
	
	private ResolveCenter rece;

	public void init()
	{
		startTime = TimeOpera.getCurrentTimeLong();
	    gcstartTime = TimeOpera.getCurrentTimeLong();
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
	}

	public void connect(RadiusCenter rc) {

		try {
			sock = new Socket(rc.getIp(), rc.getPort());
			sockIn = sock.getInputStream();
			outputStream = sock.getOutputStream();
			sockOut = new OutputStreamWriter(outputStream);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void connectResolve()
	{
	
		
		try{
			/*
			if(resolveSock!=null)
			{
				resolveSock.close();
			}
			*/
			resolveSock=new Socket(rece.getIp(),rece.getPort());
			//resolveSockIn=resolveSock.getInputStream();
			
			OutputStream outputStream = resolveSock.getOutputStream();	
			resolveSockOut=new DataOutputStream(outputStream);
			System.err.println("connect resolve successful");
			
		    
		}catch(Exception e)
		{
			System.err.println("connect resolve failed");
			e.printStackTrace();
		}
				
	
	}

	public void sendHeartbeat() {

		//State state = new State();

		try {
			if(sockOut!=null)
			{
			 sockOut.write(Heartbeat.heartbeat);
			}
			//state.setStatValue(StateValue.Normal);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	public void readPacket() {
		
		try {

			// 读取消息头
			int hn = -1;
			while (hn < 0) {
				hn = sockIn.read(Buffer.head);
				if (hn < 0) {
					//add outmem
					restart("head length is -1");
				}
			}


			// 读取消息体
			// System.out.println("ph.length:"+ph.getPacketBodyLength());
			parseBytes2Info();
			
			if (Buffer.packetbodylen < 12) {
				// System.out.println("body length is below 12");
				// return null;
				//add outmem
				restart("body length is below 12");
			}

			//System.err.println("body.len:"+(Buffer.packetbodylen - 12));
			if((Buffer.packetbodylen-12)>Buffer.body.length)
		    {
				restart("body length is below 12");
			}
			Buffer.rn = sockIn.read(Buffer.body,0,Buffer.packetbodylen-12);
			
			if (Buffer.rn < 0) {

				restart("body length is -1");
			}

			Buffer.bodylen=Buffer.packetbodylen-12;
			
			receiveNoAnalysisCompletelyPacketBody();


		} catch (IOException e) {

			//System.out.println("error in read packet");
			e.printStackTrace();
		}

	}

	
	/**
	 * 解析消息体，从消息体解析出code、packetIdentifier、length、authenticator、 user
	 * name、framedIpAddress、acctStatusType的普通形式
	 * @param rp
	 */
	public void receiveNoAnalysisCompletelyPacketBody() {
		int j = 0;
		int k = 0;

	
		//byte[] dbuffer = BytesTransform.completeBytes(new byte[2]);
		
		for (int t = 0; t < Buffer.dbuffer.length; t++) {
			Buffer.dbuffer[t] = 0;
		}
		

		try {
			//System.err.println("Buffer.bodylen:"+Buffer.bodylen);
			while (j + 44 < Buffer.bodylen) {
				//Record rec = new Record();
                 
                j++;
                j++;
				//rec.setPacketIdentifier(BytesTransform.byteToInt2(obuffer));

				// length
				for (k = 0; k < 2; k++) {
					Buffer.dbuffer[k + 2] = Buffer.body[j++];
				}

				// System.out.println("dbuffer:"+BytesTransform.bytes2str(dbuffer));
				Buffer.recLen=BytesTransform.byteToIntv(Buffer.dbuffer);
				// System.out.println("rec.len:"+rec.getLength());
				// authenticator
				for (k = 0; k < 16; k++) {
					Buffer.stbuffer[k] = Buffer.body[j++];
				}
				//rec.setAuthenticator(new String(stbuffer));

				// user name
				Buffer.unl = Buffer.recLen - 32;
				Buffer.ufalen=Buffer.unl + 12;
				
				
				// System.out.println("j:"+j+" ufa:"+ufa.length+" unl:"+unl+" body:"+body.length);
				if((j+Buffer.ufalen>=256))
				{
					restart("error in analysisPacketBody");
				}
				
				for (k = 0; k <Buffer.ufalen; k++) {
					Buffer.ufa[k] = Buffer.body[k + j];
				}

				j = j + Buffer.ufalen;// 结束循环
				//System.err.println("Buffer.ufalen:"+Buffer.ufalen);
				if(j<1)
				{
					break;
				}
				
			    ctimestr=TimeOpera.getCurrentTime();
			    BytesTransform.bytes2str(Buffer.ufa,Buffer.unl + 12);
				rawRecord=ctimestr+"\t"+Buffer.str;
				if(SSO.tioe(rawRecord))
				{
					break;
				}
				logger.info(rawRecord);
				//rawRecord=null;
				//ctimestr=null;
			    //System.err.println("j="+j);
				resolveSockOut.writeInt(rawRecord.length());
				
				for(int m=0;m<rawRecord.length();m++)
				{
					resolveSockOut.writeChar(rawRecord.charAt(m));	
				}
			
				//204 stop
				System.err.println("send raw data to 204："+rawRecord);
				resolveSockOut.writeChars(rawRecord);
							
			}
		} catch (Exception e) {

			e.printStackTrace();

			restart("error in analysisPacketBody");
		}

	}
	



	public void start(RadiusCenter rc) {

		connect(rc);
		//System.err.println("connect to radius server successful");
		connectResolve();
	
		while (true) {

			if (TimeOpera.getCurrentTimeLong() - startTime > 4000) {
				startTime = TimeOpera.getCurrentTimeLong();
				sendHeartbeat();
			}
			
			if (TimeOpera.getCurrentTimeLong() - gcstartTime > 60000) {
				gcstartTime = TimeOpera.getCurrentTimeLong();
				System.out.println("Start Garbage Collection");
				System.gc();
			}	
			
			readPacket();

		}

	}

	public void restart(String message) {
	
		try{
		//System.out.println(message + "----sleep one second!");
			//System.gc();
		     
	     /* 
	    if(outputStream!=null)
	    {
	    	outputStream.close();
	    }
	    outputStream=null;
	    
	    if(sockOut!=null)
	    {
	    	sockOut.close();
	    }
	    sockOut=null;
	    
		if(sockIn!=null)
		{
			sockIn.close();
		}
	    sockIn=null;
	    */
		if(sock!=null)
		{
			sock.close();
		}
		sock=null;
		
		if(resolveSock!=null)
		{
			resolveSock.close();
		}
		resolveSock=null;
	  
	    Thread.sleep(confFactory.getResetConnectionSuspend());
		}
		catch(Exception e)
		{
			//System.out.println("error in restart");
			e.printStackTrace();
		}
		
		start(rc);
	}

	//向resolve写入文件里的未解析记录，用于测试
	public void test(String file,ResolveCenter rece)
	{
		init();
		setRece(rece);
		connectResolve();
		
		String testRecord="";
		
		try{
			
		  BufferedReader br=new BufferedReader(new FileReader(file));
		  String ufaStr="";
		  while((testRecord=br.readLine())!=null)
		  {
			if(SSO.tioe(testRecord))
			{
				continue;
			}
			
			ufaStr = testRecord.substring(testRecord.indexOf("\t"), testRecord.length());
		    resolveSockOut.writeInt(ufaStr.length());
		
		    /*
		    for(int m=0;m<ufaStr.length();m++)
		   {
			 resolveSockOut.writeChar(ufaStr.charAt(m));	
		   }
	        */
		   //204 stop
		   System.err.println("send raw data to 204："+ufaStr);
		   resolveSockOut.writeChars(ufaStr);
		  }
		  
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		RadiusCenter rc = new RadiusCenter("221.231.154.17", 9002);
		RemoteRadiusReformAnalysis erc = new RemoteRadiusReformAnalysis();
		ResolveCenter rece=new ResolveCenter("180.96.26.204",9035);
		erc.test("temp/testa.txt", rece);
		
		erc.init();
		erc.setRc(rc);
		erc.setRece(rece);
		erc.start(rc);
		
	}

	public void parseBytes2Info()
	{	
		int pos=0;
		
		//packet body length
		for(int i=0;i<4;i++)
		{
			Buffer.fbyte[i]=Buffer.head[pos++];
		}	
		
		Buffer.packetbodylen=BytesTransform.byteToIntv(Buffer.fbyte);

	}
	
	public RadiusCenter getRc() {
		return rc;
	}

	public void setRc(RadiusCenter rc) {
		this.rc = rc;
	}
	
	public ResolveCenter getRece() {
		return rece;
	}

	public void setRece(ResolveCenter rece) {
		this.rece = rece;
	}


}
