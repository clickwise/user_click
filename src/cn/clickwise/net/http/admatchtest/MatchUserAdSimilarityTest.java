package cn.clickwise.net.http.admatchtest;

public class MatchUserAdSimilarityTest extends AdMatchTestBase{

	  public String method="/matchuseradsimilarity.json?";
	  public String suffix="&platform=adshow";
	  
	  public void testMatchUser(String cookie,String adids)
	  {
			String url=url_prefix+method+"uid="+cookie+"&adids="+adids+suffix;
			String response=hct.postUrl(url);
			System.out.println("response:"+response);
	  }
	  
	  public void testMatchUser(String cookie,String adids,String host)
	  {
			String url=url_prefix+method+"uid="+cookie+"&adids="+adids+"&host="+host+suffix;
			String response=hct.postUrl(url);
			System.out.println("response:"+response);
	  }
	
	  public static void main(String[] args)
	  {
		  MatchUserAdSimilarityTest muast=new MatchUserAdSimilarityTest();
		  String cookie="fff20764df5880a47580e3f95e5b0e20";
		  //String host="0558.uc55.com";
		  String adids="27,18,21";
		  muast.testMatchUser(cookie, adids);
	  }
	  
}
