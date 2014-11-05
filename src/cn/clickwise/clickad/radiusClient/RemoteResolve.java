package cn.clickwise.clickad.radiusClient;


import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * server 端
 * 接收另一台机器传过来的未完全解析的包，
 * 进行解析并存入redis中
 * @author zkyz
 */
public class RemoteResolve {

	private Socket sock;

	
	private ConfigureFactory confFactory;
		
	private static 	QueueRecordPond queuePond=new QueueRecordPond();
	
	public void init()
	{
		setConfFactory(ConfigureFactoryInstantiate.getConfigureFactory());
		startPond(3);
		try{
			ServerSocket sk = new ServerSocket(confFactory.getRSPort()); 
			Socket connectFromClient = null; 
			
			while(true)
			{
				connectFromClient=sk.accept();
				ServerThread st=new ServerThread(connectFromClient);
				Thread t=new Thread(st);
				t.setDaemon(true);
				t.start();	
			}
			
	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	
		
		
	}
	
	public void startPond(int threadNum)
	{
		queuePond.startConsume(threadNum);
	}
	

	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	
	
	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}


	private class ServerThread 	implements Runnable{

		private Socket connectFromClient;
		
		private DataInputStream sockIn;
		
		public ServerThread(Socket sock)
		{
			super();
			connectFromClient=sock;
			try{
				InputStream in=sock.getInputStream();
				sockIn=new DataInputStream(in);
			
				//sockIn.
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	
		}
		
		@Override
		public void run() {
			
			while(true)
			{
				try{
				  int len=sockIn.readInt();
				  String str="";
				  for(int j=0;j<len;j++)
				  {
					  str+=sockIn.readChar();
				  }
				  
				  queuePond.add2Pond(str);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	public static void main(String[] args)
	{
		RemoteResolve rs=new RemoteResolve();
		rs.init();
		
	}
	
}
