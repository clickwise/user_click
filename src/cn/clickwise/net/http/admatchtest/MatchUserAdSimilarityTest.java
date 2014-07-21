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
		  String cookie="d6a540a95770e4fec0a375b11d9f370a3";
		  String host="0558.uc55.com";
		  String adids="001,14,16,18,21,27";
		  muast.testMatchUser(cookie, adids,host);
	  }
	  
}
