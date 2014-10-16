package cn.clickwise.clickad.radiusClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import cn.clickwise.lib.bytes.BytesTransform;
import cn.clickwise.lib.time.TimeOpera;

/**
 * 读取tcp输入流的前n个字节，并将每个字节转换成两位十六进制
 * @author zkyz
 *
 */
public class ReadHeadTcp {
	
	private InputStream sockIn;

	private OutputStreamWriter sockOut;

	private FileOutputStream fos;
	
	private ConfigureFactory confFactory;
	
	Socket sock;

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
	
	public void readPacket() {
		// TODO Auto-generated method stub
		byte[] head = new byte[16];
		
		try {
			int hn=sockIn.read(head);
			System.out.println(BytesTransform.bytes2str(head));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(RadiusCenter rc,int n) {
		
		connect(rc);
		long startTime=TimeOpera.getCurrentTimeLong();
		int j=0;
		while(true){
			if(TimeOpera.getCurrentTimeLong()-startTime>4000)
			{
				startTime=TimeOpera.getCurrentTimeLong();
				sendHeartbeat();
			}
			
			RadiusPacket rp=new RadiusPacket();
		    readPacket();
			j+=16;
			if(j>n)
			{
				break;
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		if(args.length!=1)
		{
			System.err.println("Usage:ReadHeadTcp  <n>");
			System.exit(1);
		}
		

		int n=Integer.parseInt(args[0]);
		RadiusCenter rc=new RadiusCenter("221.231.154.17",9002);
		ReadHeadTcp rht=new ReadHeadTcp();
		rht.start(rc,n);
		
		
	}
}
