package cn.clickwise.clickad.unittest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import cn.clickwise.liqi.str.configutil.ConfigFileReader;
import cn.clickwise.net.http.HttpClientTool;

public abstract class AuxiliaryTestBase {

	public HttpClientTool hct = new HttpClientTool();
	public String auxiliary_prefix="http://192.168.110.182:8080";


	public AuxiliaryTestBase() {
		Properties prop = null;
		try {
			prop = ConfigFileReader.getPropertiesFromFile("user_click.config");
		} catch (Exception e) {
           System.out.println(e.getMessage());
		}
		auxiliary_prefix=prop.getProperty("auxiliary_prefix");
		System.out.println("url_prefix="+auxiliary_prefix);
	}
	
	
	public abstract String test(String text);
	public abstract String test(String[] text);
	
	public abstract ArrayList<String> testmul(String[] text);
	
}
