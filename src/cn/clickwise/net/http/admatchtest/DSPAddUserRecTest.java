package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class DSPAddUserRecTest  extends AdMatchTestBase{

	public String method="/AddUserRec?";
	public String suffix="&platform=adshow";
	
	public void testDSPAddUserRec(String uid,String hurl,String refer,String title,String hostCate){
		String encode_title=URLEncoder.encode(title);	
		encode_title=encode_title.replaceAll("\\s+", "");
		String url=url_prefix+method+"uid="+uid+"&hurl="+hurl+"&refer="+refer+"&title="+encode_title+"&hostcate="+hostCate;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);		
	}
	
	public static void main(String[] args)
	{
		String uid="d6a540a9770e4fec0a375b11d9f370a3";
		String hurl="/test";
		String refer="www.baidu.com";
		String title="护肤彩妆美发";
		String hostCate="美妆";
		
		DSPAddUserRecTest daur=new DSPAddUserRecTest();
		daur.testDSPAddUserRec(uid, hurl, refer, title, hostCate);	
	}
}
