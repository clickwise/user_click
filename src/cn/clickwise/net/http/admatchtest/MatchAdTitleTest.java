package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class MatchAdTitleTest extends AdMatchTestBase{
	
	  public String method="/matchadtitle.json?";
	  public String suffix="&datatype=BAIDU,TBSEARCH&infotype=cates,attrs,items,bdcates,bdkeys&adinfotype=keywords&platform=test";
	  
	  public void testMatchAdTitle(String cookie,String title)
	  {
		String encode_text=URLEncoder.encode(title);
		encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+method+"uid="+cookie+"&title="+encode_text+suffix;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
	  }
	
	 public static void main(String[] args)
	 {
		MatchAdTitleTest matt=new MatchAdTitleTest();
		String cookie="d6a540a9770e4fec0a375b11d9f370a3";
		String title="护肤彩妆美发护发弹力素啫喱啫喱水";
		matt.testMatchAdTitle(cookie, title);	
	 }
	
}
