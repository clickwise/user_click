package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class MatchTitleTest extends AdMatchTestBase{

	  public String method="/matchtitle.json?title=";
	  public String suffix="&datatype=BAIDU,TBSEARCH&infotype=cates,attrs,items,bdcates,bdkeys&adinfotype=keywords&platform=test";
	  
	  public void testMatchTitle(String title)
	  {
			String encode_text=URLEncoder.encode(title);
			encode_text=encode_text.replaceAll("\\s+", "");
			String url=url_prefix+method+encode_text+suffix;
			String response=hct.postUrl(url);
			System.out.println("response:"+response);
	  }
	  
	  public static void main(String[] args)
	  {
		    MatchTitleTest mtt=new MatchTitleTest();
		    String title="护肤彩妆美发护发弹力素啫喱啫喱水";
		    mtt.testMatchTitle(title);
	  }
	  
}
