package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileCopyToClient extends Client{

	private HttpURLConnection urlCon;
	
	private FileCopyToCommand fcc;
	
	private Result result;
	
	@Override
	public void connect(Connection con) {
		
		try {
			URL url = new URL("http://" + con.getHost() + ":" + con.getPort()
					+ con.getMethod()+"?method="+FileCopyToCommand.writeObject(fcc));

			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setRequestProperty("Content-type", "text/plain");
            //urlCon.setUseCaches(false);
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
			FileInputStream fis=new FileInputStream(fcc.getLocalPath());
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			
			outputStream = urlCon.getOutputStream();
	        OutputStreamWriter osw=new OutputStreamWriter(outputStream);
			PrintWriter pw=new PrintWriter(osw);
			
			String line="";
			while((line=br.readLine())!=null)
			{
				pw.println(line);
			}
			
			fis.close();
			isr.close();
			br.close();
			osw.close();
			pw.close();
            
            InputStream is=urlCon.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
			setResult((Result)ois.readObject());
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	
	}
	
	public static void main(String[] args)
	{
		
		FileCopyToCommand fcc=new FileCopyToCommand();
		fcc.setLocalName("hcn.txt");
		fcc.setLocalPath("temp/host_cate/hcn.txt");
		fcc.setRemoteName("hcn.txt");
		fcc.setRemotePath("/home/test/hcn.txt");
		
		FileCopyToClient ec=new FileCopyToClient();
		ec.setFileCopyToCommand(fcc);
		Connection con=new Connection();
		con.setHost("192.168.110.182");
		con.setPort(2733);
		con.setMethod("/fileCopyTo");
		ec.connect(con);
		ec.execute(fcc);
		
		State fs=(State)ec.getResult();
		System.out.println("fs:"+fs.getState());
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	
	
	public FileCopyToCommand getFileCopyToCommand()
	{
		return fcc;
	}

	
	public void setFileCopyToCommand(FileCopyToCommand fcc)
	{
		this.fcc=fcc;
	}
}
