package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class AddAdRecordTest extends AdMatchTestBase {

	public String method="/addadrecord?s=";
	public String suffix="&platform=test";
	
	public void testAddAdRecord(String text)
	{
		String encode_text=URLEncoder.encode(text);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+method+encode_text+suffix;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);		
	}
	
	public static void main(String[] args)
	{
		AddAdRecordTest aart=new AddAdRecordTest();
		String text="{\"keywords\":[\"啫喱\",\"啫喱水\"],\"adid\":\"001\"}";
		aart.testAddAdRecord(text);
		
	}
	
}
