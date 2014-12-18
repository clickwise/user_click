package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EWAECServer {
	public EWAPredict ewa;

	public static void main(String[] args) throws Exception {
		EWAECServer ewa_se = new EWAECServer();
		if (args.length != 1) {
			System.out.println("用法 :EWAECServer <port>");
		}
		int port = 8096;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			port = 8092;
		}
		String model_path = "model_dir/model";
		String sls_path = "model_dir/lll.txt";
		String first_level_path = "model_dir/fhc.txt";
		String second_level_path = "model_dir/shc.txt";
		String third_level_path = "model_dir/thc.txt";
		String cw_file = "input/ec_ckws_num.txt";
		ewa_se.ewa = new EWAPredict();
		ewa_se.ewa.read_model(model_path, sls_path, first_level_path,
				second_level_path, third_level_path);
		ewa_se.ewa.load_config();
		ewa_se.ewa.load_cate_wrods(cw_file);

		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("PosTagProxy is listenning on port:" + port);
		int count_line = 0;
		while (true) {

			Socket incoming = serverSocket.accept();
			Runnable r = new ThreadedEchoHandler(incoming, ewa_se.ewa);
			Thread t = new Thread(r);
			t.start();

			serverSocket.close();
			Thread.sleep(10000);
			serverSocket = new ServerSocket(port);
		}

	}

}

class ThreadedEchoHandler implements Runnable {

	private Socket socket;

	private InputStream in = null;
	private OutputStream out = null;
	private static final int BUFSIZE = 10032 * 64;
	public  EWAPredict ewa;

	public ThreadedEchoHandler(Socket i, EWAPredict ewa) {
		socket = i;
		this.ewa=ewa;
	}

	public void run() {
		try {

			byte[] receiveBuf = new byte[BUFSIZE];
			int recvMsgSize;
			String clientStr = "";
			String res = "";
			int tnum = 0;
			while (true) {
				in = socket.getInputStream();
				out = socket.getOutputStream();
				recvMsgSize = in.read(receiveBuf);
				clientStr = new String(receiveBuf);
				clientStr = clientStr.trim();			
				res=this.ewa.predict_from_seg_line(clientStr);
				out.write(res.getBytes());
				out.flush();
				// socket.close();
				out.close();
				in.close();
				socket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
