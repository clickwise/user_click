package cn.clickwise.net.http;

import java.net.URLDecoder;
import java.net.URLEncoder;

import cn.clickwise.liqi.str.edcode.UrlCode;

public class SegClient {

	public String url="http://192.168.110.182:7000/seg?s=";
	public HttpClientTool hct=new HttpClientTool();
	public String seg(String text)
	{
		String encode_text=URLEncoder.encode(text);
		encode_text=encode_text.replaceAll("\\s+", "");
		String req_url=url+encode_text;
		return hct.postUrl(req_url);
	}
	
	public static void main(String[] args)
	{
		SegClient sc=new SegClient();
		String s="9111秋装2013新款女韩版包臀短裙弹力蕾丝半身裙职业中裙大码裙子";
		System.out.println(URLDecoder.decode(sc.seg(s)));
	}
	
}
