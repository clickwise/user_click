package cn.clickwise.net.http.admatchtest;

import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import cn.clickwise.liqi.str.configutil.ConfigFileReader;
import cn.clickwise.net.http.HttpClientTool;

public class AdMatchTestBase {

	public HttpClientTool hct = new HttpClientTool();
	 public String url_prefix="http://106.187.35.172:8000";
	// public String url_prefix="http://192.168.110.181:8000";
	//public String url_prefix = "http://42.62.29.25:8000";

	public AdMatchTestBase() {
		Properties prop = null;
		try {
			prop = ConfigFileReader.getPropertiesFromFile("user_click.config");
		} catch (Exception e) {
            System.out.println(e.getMessage());
		}
		url_prefix=prop.getProperty("url_prefix");
		System.out.println("url_prefix="+url_prefix);
	}

}
