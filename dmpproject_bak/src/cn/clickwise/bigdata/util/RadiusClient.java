package cn.clickwise.bigdata.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Client for Radius_Server which returns
 * the user id for given ip and time
 * 
 * @author gao
 *
 */
public class RadiusClient {
	private String radius_id ;
	
	public RadiusClient(String radius_host_port) {
		radius_id ="http://"+ radius_host_port + "/radius_id.php";
	}

	/**
	 * 根据输入的time,ip获取Radius中的UserId
	 * @param time
	 * @param ip
	 * @return UserId with 32bit long, NA or null if error happens
	 */
	public String getRadiusUserID(String time,String ip) {
		try {
			//一定要分开转码，否则报 no protocol错误 
			//String strUrl="http://192.168.10.38:8000/radius_id.php?t=";
			//String param="t="+time+"&ip="+ip;
            //String paramEncode=URLEncoder.encode(param, "utf-8");
            
            String strUrl = radius_id + "?t=" + URLEncoder.encode(time,"utf-8") ;
            strUrl += "&ip="+ip;
			URL url=new URL(strUrl);
			
			HttpURLConnection htc=(HttpURLConnection) url.openConnection();
			htc.setDoOutput(true);
			htc.setRequestMethod("POST");
			InputStream is=htc.getInputStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is)); 
			return br.readLine();
		} catch (MalformedURLException e) {
			System.err.println("Invalid url:"+ e.getLocalizedMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			System.err.print("Reading radius info with exception:"+e.getLocalizedMessage());
		}
		return null;
		
	}

}
