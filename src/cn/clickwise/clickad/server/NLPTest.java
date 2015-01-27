package cn.clickwise.clickad.server;

import java.net.URLEncoder;
import java.util.ArrayList;

public class NLPTest extends NLPTestBase{

	@Override
	public String test(String text) {
	
		String method = ":9009/seg?s=";
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		String response = hct.postUrl(url);
		try{
           System.out.println("response:"+response);
		}
		catch(Exception e)
		{
			
		}
		return response;
	}

	@Override
	public String test(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> testmul(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public String testSeg(String text) {
		String method = ":9009/seg?s=";
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		String response = hct.postUrl(url);
        //System.out.println("seg response:"+response);
        return response;
	
	}

	@Override
	public String testTag(String text) {
		String method = ":9010/tag?s=";
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		String response = hct.postUrl(url);

       // System.out.println("tag response:"+response);
	
		return response;
	}

	@Override
	public String testKey(String text) {
		String method = ":9011/key?s=";
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		String response = hct.postUrl(url);

       // System.out.println("key response:"+response);
	
		return response;
	}
	
	@Override
	public String testTBCate(String text) {

		String method = ":9012/ctb?s=";
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		String response = hct.postUrl(url);

       // System.out.println("key response:"+response);
	
		return response;
	}
	
	public static void main(String[] args)
	{
		NLPTest nlptest=new NLPTest();
		String title="Sony/索尼 HDR-AZ1VR 运动佩戴数码摄像机 AZ1 WiFi分享/远程监控";
		String seg=nlptest.testSeg(title);
		
		System.out.println("seg:"+seg);
		String tag=nlptest.testTag(seg);
		System.out.println("tag:"+tag);
		String key=nlptest.testKey(tag);
		System.out.println("key:"+key);
		
		String tbcate=nlptest.testTBCate(title);
		System.out.println("tbcate:"+tbcate);
		
	}



}
