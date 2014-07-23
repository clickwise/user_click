package cn.clickwise.net.http.admatchtest;

/***用户匹配广告*****/
public class MatchAdTest extends AdMatchTestBase{
	
  public String method="/matchad.json?uid=";
  public String suffix="&platform=adshow";
	
  public String testMatchAd(String cookie)
  {
		String url=url_prefix+method+cookie+suffix;
		String response=hct.postUrl(url);
		return response;
		//System.out.println("response:"+response);
  }
	
  public static void main(String[] args)
  {
	  MatchAdTest mat=new MatchAdTest();
	  String cookie="6622c24f3fce5b79e7db05862e56e13f";
	  mat.testMatchAd(cookie);
  }
  
	
}
