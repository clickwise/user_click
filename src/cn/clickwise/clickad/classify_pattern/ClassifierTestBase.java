package cn.clickwise.clickad.classify_pattern;

import java.util.ArrayList;
import cn.clickwise.net.http.HttpClientTool;

public abstract class ClassifierTestBase {

	public HttpClientTool hct = new HttpClientTool();
	public String auxiliary_prefix="http://192.168.110.182";
	//public String auxiliary_prefix="http://110.96.34.211";

	public ClassifierTestBase() {
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
	public abstract String testWBCate(String text);
	
	
	public abstract ArrayList<String> testmul(String[] text);
	
}
