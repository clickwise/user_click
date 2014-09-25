package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HiveFetchByKeysClient extends Client {

	private HttpURLConnection urlCon;

	private HiveFetchByKeysCommand hfkc;

	@Override
	public void connect(Connection con) {

		try {
			URL url = new URL("http://" + con.getHost() + ":" + con.getPort()
					+ con.getMethod() + "?method="
					+ HiveFetchByKeysCommand.writeObject(hfkc));

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

		try {
			FileInputStream fis = new FileInputStream(hfkc.getKeyPath());
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

			// 读取远程文件
			BufferedReader resbr = new BufferedReader(new InputStreamReader(
					urlCon.getInputStream()));

			// 写入本地文件
			FileWriter resfw = new FileWriter(hfkc.getResultPath());
			PrintWriter respw = new PrintWriter(resfw);

			String resline = "";
			while ((resline = resbr.readLine()) != null) {
				respw.println(resline);
			}

			resbr.close();
			resfw.close();
			respw.close();

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

	public HiveFetchByKeysCommand getHfkc() {
		return hfkc;
	}

	public void setHfkc(HiveFetchByKeysCommand hfkc) {
		this.hfkc = hfkc;
	}

	public static void main(String[] args)
	{
		HiveFetchByKeysClient ec=new HiveFetchByKeysClient();
		Connection con=new Connection();
		con.setHost("192.168.110.186");
		con.setPort(2733);
		con.setMethod("/hiveFetchByKeys");
		
		HiveFetchByKeysCommand hfkc=new HiveFetchByKeysCommand();
		hfkc.setDay(20140512);
		hfkc.setKeyName("test_cookie.txt");
		hfkc.setKeyPath("temp/test_cookie.txt");
		hfkc.setTableName("user_se_keywords_day_ad");
		hfkc.setKeyFieldName("cookie");
		hfkc.setKeyTableName("remote_cookie");
		hfkc.setRemoteTmpName("remote_cookie_20140512.txt");
		hfkc.setRemoteTmpPath("/tmp/remote_cookie_20140512.txt");
		hfkc.setHdfTmpName("remote_cookie_hdfs_20140512");
		hfkc.setHdfTmpPath("/user/remote_cookie/remote_cookie_hdfs_20140512");
		hfkc.setResultName("local_user_info.txt");
		hfkc.setResultPath("temp/local_user_info.txt");
		hfkc.setResultRemoteName("remote_user_info_20140512");
		hfkc.setResultRemotePath("/tmp/remote_user_info_20140512");
		
		ec.setHfkc(hfkc);
		ec.connect(con);
		ec.execute(hfkc);
	}
	
}
