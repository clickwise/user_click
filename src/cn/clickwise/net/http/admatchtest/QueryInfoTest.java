package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class QueryInfoTest extends AdMatchTestBase{

	public String method="/queryuserinfo.json?uid=";
	public String suffix="&query_type=BAIDU";
	
	public void testQueryInfo(String cookie)
	{
		//String encode_text=URLEncoder.encode(cookie);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		//encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+method+cookie+suffix;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
	}
	
	public static void main(String[] args)
	{
		QueryInfoTest qit=new QueryInfoTest();
		String cookie="d6a540a9770e4fec0a75b11d9f370a3";
		qit.testQueryInfo(cookie);	
	}
		
}
