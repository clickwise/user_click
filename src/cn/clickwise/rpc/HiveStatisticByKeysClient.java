package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
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

	private HiveStatisticByKeysCommand hfkc;

	@Override
	public void connect(Connection con) {

		try {
			URL url = new URL("http://" + con.getHost() + ":" + con.getPort()
					+ con.getMethod() + "?method="
					+ HiveStatisticByKeysCommand.writeObject(hfkc));
            System.out.println("re url:"+url);
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

	public static void initRandomFileName(String tmpIdentify,int day,HiveStatisticByKeysCommand hfkc)
	{
		hfkc.setRemoteTmpName(tmpIdentify+"_"+day+".txt");
		hfkc.setRemoteTmpPath("/tmp/"+tmpIdentify+"_"+day+".txt");
		hfkc.setHdfTmpName(tmpIdentify+"_hdfs_"+day);
		hfkc.setHdfTmpPath("/user/"+tmpIdentify+"/"+tmpIdentify+"_hdfs_"+day);
		hfkc.setResultRemoteName(tmpIdentify+"_info_"+day);
		hfkc.setResultRemotePath("/tmp/"+tmpIdentify+"_info_"+day);
		
	}
	
	public HttpURLConnection getUrlCon() {
		return urlCon;
	}

	public void setUrlCon(HttpURLConnection urlCon) {
		this.urlCon = urlCon;
	}

	public HiveStatisticByKeysCommand getHfkc() {
		return hfkc;
	}

	public void setHfkc(HiveStatisticByKeysCommand hfkc) {
		this.hfkc = hfkc;
	}

	public static void main(String[] args)
	{
		HiveStatisticByKeysClient ec=new HiveStatisticByKeysClient();
		Connection con=new Connection();
		con.setHost("112.67.253.101");
		con.setPort(2733);
		con.setMethod("/hiveStatisticByKeys");
		
		HiveStatisticByKeysCommand hfkc=new HiveStatisticByKeysCommand();
		String tmpIdentify="remote_statistic";
		int day=20141103;
		hfkc.setDay(day);
		hfkc.setTmpIdentify(tmpIdentify);
		hfkc.setKeyName("ttt.txt");
		hfkc.setKeyPath("temp/ttt.txt");
		
		hfkc.setTableName("astat");
		hfkc.setKeyFieldName("user_id");
		hfkc.setIpFieldName("sip");
		hfkc.setKeyTableName("statistic_keys");
		hfkc.setAreaCode("009");
		hfkc.setResultName("local_user_statistic.txt");
		hfkc.setResultPath("temp/local_user_statistic.txt");
		hfkc.initRandomFileName();
	
		ec.setHfkc(hfkc);
		ec.connect(con);
		ec.execute(hfkc);
	}

}
