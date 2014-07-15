package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

import cn.clickwise.liqi.time.utils.TimeOpera;

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
		    long starttime=TimeOpera.getCurrentTimeLong();
		    for(int i=0;i<100;i++)
		    {
		    String title="护肤彩妆美发";
		    mtt.testMatchTitle(title);
		    }
		    long endtime=TimeOpera.getCurrentTimeLong();
		    System.out.println("usertime:"+(endtime-starttime));
	  }
	  
}
