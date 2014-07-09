package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class AddUserRecordTest extends AdMatchTestBase{
	

	public void testAddUserRecord(String text)
	{
		String encode_text=URLEncoder.encode(text);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+encode_text;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
	}
	
	public static void main(String[] args)
	{
		
	}
	
	
}
