package cn.clickwise.net.http.admatchtest;

/***用户匹配广告*****/
public class MatchAdTest extends AdMatchTestBase{
	
  public String method="/matchad.json?uid=";
  public String suffix="&platform=test";
	
  public void testMatchAd(String cookie)
  {
		String url=url_prefix+method+cookie+suffix;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
  }
	
  public static void main(String[] args)
  {
	  MatchAdTest mat=new MatchAdTest();
	  String cookie="d6a540a9770e4fec0a375b11d9f370a3";
	  mat.testMatchAd(cookie);
  }
  
	
}
