package cn.clickwise.net.http.admatchtest;

public class MatchUserAdSimilarityTest extends AdMatchTestBase{

	  public String method="/matchuseradsimilarity.json?";
	  public String suffix="&platform=test";
	  
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
		  String cookie="521d0256e1682b6bf5edafd83370b7df";
		  String host="0558.uc55.com";
		  String adids="23";
		  muast.testMatchUser(cookie, adids);
	  }
	  
}
