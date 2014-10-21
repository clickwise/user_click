package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * method: /hiveStatisticByKeys
 * @author zkyz
 */
public class HiveStatisticByKeysClient extends Client{
	
	private HttpURLConnection urlCon;
	
	private HiveStatisticByKeysCommand hskc;
	
	private RpcReceipt rpcReceipt;
	
	@Override
	public void connect(Connection con) {
		
		try {
			URL url = new URL("http://" + con.getHost() + ":" + con.getPort()
					+ con.getMethod() + "?method="
					+ HiveStatisticByKeysCommand.writeObject(hskc));

			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setRequestProperty("Content-type", "text/plain");
			// urlCon.setUseCaches(false);
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
		State state = new State();
		OutputStream outputStream = null;

		rpcReceipt=null;
		
		try {
			FileInputStream fis = new FileInputStream(hskc.getKeyPath());
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			outputStream = urlCon.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(outputStream);
			PrintWriter pw = new PrintWriter(osw);

			String line = "";
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}

			fis.close();
			isr.close();
			br.close();
			osw.close();
			pw.close();

			ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
			rpcReceipt = (RpcReceipt) ois.readObject();
			
			/*
			// 读取远程文件
			BufferedReader resbr = new BufferedReader(new InputStreamReader(
					urlCon.getInputStream()));
            */
			
			/*
			// 写入本地文件
			FileWriter resfw = new FileWriter(hskc.getResultPath());
			PrintWriter respw = new PrintWriter(resfw);

			String resline = "";
			while ((resline = resbr.readLine()) != null) {
				respw.println(resline);
			}

			resfw.close();
			respw.close();
			*/
			
			//resbr.close();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	public static void initRandomFileName(String tmpIdentify,int day,HiveStatisticByKeysCommand hskc)
	{
		hskc.setRemoteTmpName(tmpIdentify+"_"+day+".txt");
		hskc.setRemoteTmpPath("/tmp/"+tmpIdentify+"_"+day+".txt");
		hskc.setResultRemoteName(tmpIdentify+"_info_"+day);
		hskc.setResultRemotePath("/tmp/"+tmpIdentify+"_info_"+day);
		
	}
	
	public HttpURLConnection getUrlCon() {
		return urlCon;
	}

	public void setUrlCon(HttpURLConnection urlCon) {
		this.urlCon = urlCon;
	}

	public HiveStatisticByKeysCommand getHskc() {
		return hskc;
	}

	public void setHskc(HiveStatisticByKeysCommand hskc) {
		this.hskc = hskc;
	}

}
