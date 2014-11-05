package cn.clickwise.clickad.radiusClient;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteEasyResolve {

	//private Socket connectFromClient;

	private ConfigureFactory confFactory;

	private static QueueRecordPond queuePond = new QueueRecordPond();

	private DataInputStream sockIn;

	public void init() {
		setConfFactory(ConfigureFactoryInstantiate.getConfigureFactory());
		startPond(3);
		try {
			ServerSocket sk = new ServerSocket(confFactory.getRSPort());
		

			while (true) {
				try {
					Socket connectFromClient=null;
					connectFromClient = sk.accept();
					if(connectFromClient==null)
					{
						Thread.sleep(1000);
						continue;
					}
					Thread.sleep(10);
					
					statRead(connectFromClient);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void statRead(Socket connectFromClient) {
		try {
			InputStream in = connectFromClient.getInputStream();
			sockIn = new DataInputStream(in);
			while (true) {
				try {
					int len = sockIn.readInt();
					if (len < 0) {
						// Thread.sleep(1);
						continue;
					}
					String str = "";
					for (int j = 0; j < len; j++) {
						str += sockIn.readChar();
					}
					//System.out.println("len:"+len+" str:"+str);
					queuePond.add2Pond(str);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		} catch (Exception e) {

		}
	}

	public void startPond(int threadNum) {
		queuePond.startConsume(threadNum);
	}

	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}

	public static void main(String[] args) {
		RemoteEasyResolve rs = new RemoteEasyResolve();
		rs.init();

	}



	public DataInputStream getSockIn() {
		return sockIn;
	}

	public void setSockIn(DataInputStream sockIn) {
		this.sockIn = sockIn;
	}
}
