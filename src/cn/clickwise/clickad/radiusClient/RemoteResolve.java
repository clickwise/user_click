package cn.clickwise.clickad.radiusClient;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * server 端 接收另一台机器传过来的未完全解析的包， 进行解析并存入redis中
 * 
 * @author zkyz
 */
public class RemoteResolve {

	private Socket sock;

	private ConfigureFactory confFactory;

	private static QueueRecordPond queuePond = new QueueRecordPond();
	
	private ArrayList<Thread> startedThread=new ArrayList<Thread>();
	

	public void init() {
		setConfFactory(ConfigureFactoryInstantiate.getConfigureFactory());
		startPond(3);
		try {
			ServerSocket sk = new ServerSocket(confFactory.getRSPort());
			Socket connectFromClient = null;
			int tn = 0;
			while (true) {
				tn++;
				try {
					connectFromClient = sk.accept();
					ServerThread st = new ServerThread(connectFromClient);
					Thread t = new Thread(st);
					t.setDaemon(true);
					System.out.println("start thread :" + t.getName());
					t.start();
					startedThread.add(t);
					if(tn>5)
					{
						for(int j=0;j<startedThread.size()-5;j++)
						{
							System.out.println("stop thread :"+startedThread.get(j).getName());
							startedThread.get(j).stop();
							startedThread.remove(j);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public void startPond(int threadNum) {
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

	private class ServerThread implements Runnable {

		private Socket connectFromClient;

		private DataInputStream sockIn;

		public ServerThread(Socket sock) {
			super();
			connectFromClient = sock;
			try {
				InputStream in = sock.getInputStream();
				sockIn = new DataInputStream(in);

				// sockIn.
			} catch (Exception e) {
				try {
					sockIn.close();
				} catch (Exception ee) {

				}
				e.printStackTrace();
			}

		}

		@Override
		public void run() {

			int c=0;
			while (true) {

				try {
					int len = sockIn.readInt();
					if (len < 0) {
						// Thread.sleep(1);
						continue;
					}
					c++;
					if(c%100000==0)
					{
					  	System.out.println("Thread ["+Thread.currentThread().getName()+"] is running");
					}
					
					String str = "";
					for (int j = 0; j < len; j++) {
						str += sockIn.readChar();
					}
					// System.out.println("len:"+len+" str:"+str);
					queuePond.add2Pond(str);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}

		}

		public void finalize() {
			try {
				connectFromClient.close();
				sockIn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		RemoteResolve rs = new RemoteResolve();
		rs.init();

	}

}
