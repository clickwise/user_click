package cn.clickwise.net.http.admatchtest;

/**广告匹配用户**/
public class MatchUserTest extends AdMatchTestBase{

	  public String method="/matchuser.json?adid=";
	  public String suffix="&datatype=BAIDU,TBSEARCH&infotype=cates,attrs,items,bdcates,bdkeys&adinfotype=keywords&platform=test";
	  
	  public void testMatchUser(String adid)
	  {
			String url=url_prefix+method+adid+suffix;
			String response=hct.postUrl(url);
			System.out.println("response:"+response);
	  }
	
	  public static void main(String[] args)
	  {
		  MatchUserTest mut=new MatchUserTest();
		  String adid="001";
		  mut.testMatchUser(adid);
	  }
	  
}
