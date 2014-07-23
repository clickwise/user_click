package cn.clickwise.net.http.admatchtest;

import java.net.URLEncoder;

public class AddAdRecordTest extends AdMatchTestBase {

	public String method="/addadrecord?s=";
	public String suffix="&platform=test";
	
	public void testAddAdRecord(String text)
	{
		String encode_text=URLEncoder.encode(text);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+method+encode_text+suffix;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);		
	}
	
	public static void main(String[] args)
	{
		AddAdRecordTest aart=new AddAdRecordTest();
		//String text="{\"keywords\":[\"啫喱\",\"啫喱水\"],\"adid\":\"001\"}";
		//aart.testAddAdRecord(text);
		
		

		String text1="{\"adid\":\"14\",\"keywords\":[\"生发\"]}";
		aart.testAddAdRecord(text1);
		String text2="{\"adid\":\"16\",\"keywords\":[\"头发\",\"养生\",\"控油\",\"生发\",\"掉发\",\"养发\",\"排毒\",\"固发\",\"护发\",\"健康\"]}";
		aart.testAddAdRecord(text2);
		String text3="{\"adid\":\"18\",\"keywords\":[\"成人用品\",\"计生用品\"]}";
		aart.testAddAdRecord(text3);
		
		String text4="{\"adid\":\"21\",\"keywords\":[\"国际手机\",\"旅行国际旅游\",\"电话卡\",\"上网卡\",\"3g上网卡\",\"手机卡\",\"旅游\",\"国际电话卡\",\"sim卡\"]}";
		aart.testAddAdRecord(text4);
		String text5="{\"adid\":\"27\",\"keywords\":[\"游戏\"]}";
		aart.testAddAdRecord(text5);
		
		
	}
	
}
