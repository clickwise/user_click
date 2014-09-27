package cn.clickwise.clickad.feathouse;

import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

public class AreaCode {

	private static final String[] codes={
			"﻿001=>安徽DX",
			"002=>深圳DX",
			"003=>北京DX",
			"004=>福建DX",
			"005=>甘肃DX",
			"006=>广东DX",
			"007=>广西DX",
			"008=>贵州DX",
			"009=>海南DX",
			"010=>河北DX",
			"011=>河南DX",
			"012=>黑龙江DX",
			"013=>湖北DX",
			"014=>湖南DX",
			"015=>吉林DX",
			"016=>江苏DX",
			"017=>江西DX",
			"018=>辽宁DX",
			"019=>内蒙古DX",
			"020=>宁夏DX",
			"021=>青海DX",
			"022=>山东DX",
			"023=>山西DX",
			"024=>陕西DX",
			"025=>上海DX",
			"026=>四川DX",
			"027=>天津DX",
			"028=>新疆DX",
			"029=>云南DX",
			"030=>浙江DX",
			"031=>重庆DX",
			"101=>安徽LT",
			"102=>深圳LT",
			"103=>北京LT",
			"104=>福建LT",
			"105=>甘肃LT",
			"106=>广东LT",
			"107=>广西LT",
			"108=>贵州LT",
			"109=>海南LT",
			"110=>河北LT",
			"111=>河南LT",
			"112=>黑龙江LT",
			"113=>湖北LT",
			"114=>湖南LT",
			"115=>吉林LT",
			"116=>江苏LT",
			"117=>江西LT",
			"118=>辽宁LT",
			"119=>内蒙古LT",
			"120=>宁夏LT",
			"121=>青海LT",
			"122=>山东LT",
			"123=>山西LT",
			"124=>陕西LT",
			"125=>上海LT",
			"126=>四川LT",
			"127=>天津LT",
			"128=>新疆LT",
			"129=>云南LT",
			"130=>浙江LT",
			"131=>重庆LT",
			"201=>安徽YD",
			"202=>深圳YD",
			"203=>北京YD",
			"204=>福建YD",
			"205=>甘肃YD",
			"206=>广东YD",
			"207=>广西YD",
			"208=>贵州YD",
			"209=>海南YD",
			"210=>河北YD",
			"211=>河南YD",
			"212=>黑龙江YD",
			"213=>湖北YD",
			"214=>湖南YD",
			"215=>吉林YD",
			"216=>江苏YD",
			"217=>江西YD",
			"218=>辽宁YD",
			"219=>内蒙古YD",
			"220=>宁夏YD",
			"221=>青海YD",
			"222=>山东YD",
			"223=>山西YD",
			"224=>陕西YD",
			"225=>上海YD",
			"226=>四川YD",
			"227=>天津YD",
			"228=>新疆YD",
			"229=>云南YD",
			"230=>浙江YD",
			"231=>重庆YD",
			"999=>未知"
	};
	
	
	public static Map<String,String> getCodeAreaMap()
	{
		Map<String,String> codeArea=new HashMap<String,String>();
		
		String code="";
		String area="";
		
		for(int i=0;i<codes.length;i++)
		{
		  code=SSO.beforeStr(codes[i], "=>");
		  area=SSO.afterStr(codes[i], "=>");
		  codeArea.put(code, area);
		}
		
		return codeArea;
	}
	
	public static Map<String,String> getAreaCodeMap()
	{
		
		Map<String,String> areaCode=new HashMap<String,String>();
		
		String code="";
		String area="";
		
		for(int i=0;i<codes.length;i++)
		{
		  code=SSO.beforeStr(codes[i], "=>");
		  area=SSO.afterStr(codes[i], "=>");
		  areaCode.put(area, code);
		}
		
		return areaCode;
	}
	
	
	public static void main(String[] args)
	{
		Map<String,String> codeArea=AreaCode.getCodeAreaMap();
		
		for(Map.Entry<String, String> item:codeArea.entrySet())
		{
			System.out.println(item.getKey()+"====>"+item.getValue());
		}
	}
	
}
