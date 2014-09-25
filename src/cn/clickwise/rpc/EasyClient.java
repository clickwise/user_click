package cn.clickwise.rpc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EasyClient extends Client {

	private HttpURLConnection urlCon;
	private Result result;

	@Override
	public void connect(Connection con) {
		try {
			URL url = new URL("http://" + con.getHost() + ":" + con.getPort()
					+ con.getMethod());

			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setRequestProperty("Content-type", "application/x-java-serialized-object");
            urlCon.setUseCaches(false);
			// 设定请求的方法为"POST"，默认是GET
			urlCon.setRequestMethod("POST");
			urlCon.setConnectTimeout(1000000);
			urlCon.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public State execute(Command cmd) {
		State state=new State();
		OutputStream outputStream = null;

		try {
			outputStream = urlCon.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(cmd);
		
			oos.flush();
			oos.close();
           
            InputStream is=urlCon.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
			result=(Result)ois.readObject();
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	public State execute_test(Command cmd) {
		State state=new State();
		OutputStream outputStream = null;

		try {
			FileStatus fs=new FileStatus();
	
			FileOutputStream fos = new FileOutputStream("test_serial.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cmd);
			oos.flush();
            oos.close();
            
            FileInputStream fis = new FileInputStream("test_serial.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
			Command result=(Command)ois.readObject();
			FileStatusCommand fresult=(FileStatusCommand)result;
			System.out.println(fresult.getName());
			System.out.println(fresult.getPath());
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public HttpURLConnection getUrlCon() {
		return urlCon;
	}

	public void setUrlCon(HttpURLConnection urlCon) {
		this.urlCon = urlCon;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	
	public static void main(String[] args)
	{
		EasyClient ec=new EasyClient();
		Connection con=new Connection();
		con.setHost("192.168.110.186");
		con.setPort(2733);
		con.setMethod("/fileStatus");
		ec.connect(con);
		
		FileStatusCommand fsc=new FileStatusCommand();
		fsc.setName("test");
		fsc.setPath("/home/hadoop/lq/test");
		ec.execute(fsc);
		
		FileStatus fs=(FileStatus)ec.getResult();
		
		System.out.println("fs:" + fs.getName());
		for (FileStatus sfs : fs.getChildren()) {
			System.out.println("sfs:" + sfs.getName());
		}
		
	}
}
