package cn.clickwise.clickad.server;

import java.util.ArrayList;
import cn.clickwise.net.http.HttpClientTool;

public abstract class NLPTestBase {

	public HttpClientTool hct = new HttpClientTool();
	public String auxiliary_prefix="http://183.136.168.79";


	public NLPTestBase() {
		/*
		Properties prop = null;
		
		try {
			prop = ConfigFileReader.getPropertiesFromFile("user_click.config");
		} catch (Exception e) {
           System.out.println(e.getMessage());
		}
		auxiliary_prefix=prop.getProperty("auxiliary_prefix");
		System.out.println("url_prefix="+auxiliary_prefix);
		*/
		
	}
	
	
	public abstract String test(String text);
	public abstract String test(String[] text);
	
	public abstract String testSeg(String text);
	public abstract String testTag(String text);
	public abstract String testKey(String text);
	public abstract String testTBCate(String text);
	
	public abstract ArrayList<String> testmul(String[] text);
	
}
