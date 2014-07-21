package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class AddUserRecordTest extends AdMatchTestBase{
	
	public String method="/adduserrecord?s=";

	public void testAddUserRecord(String text)
	{
		String encode_text=URLEncoder.encode(text);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+method+encode_text;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
	}
	
	public static void main(String[] args)
	{
		AddUserRecordTest aurt=new AddUserRecordTest();
		String s="{\"attrs\":[\"定型\"],\"cates\":[\"护肤彩妆\",\"美发护发\",\"洗发水\"],\"items\":[\"啫喱\",\"啫喱水\"],\"time\":\"1399564830\",\"uid\":\"d6a540a9770e4fec0a375b11d9f370a3\",\"datatype\":\"TBSEARCH\"}";
		aurt.testAddUserRecord(s);	
	}
	
	
}
