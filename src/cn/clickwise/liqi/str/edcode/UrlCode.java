package cn.clickwise.liqi.str.edcode;

import java.net.URLDecoder;
import java.net.URLEncoder;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * Urlcode 的编码解码
 * @author zkyz
 *
 */
public class UrlCode {

	/**
	 * 对Urlencode后的串进行解码
	 * @param encode_str
	 * @return decode_str
	 */
	public static String getDecodeUrl(String encode_str)
	{
		String decode_str="";
		if(!(SSO.tnoe(encode_str)))
		{
			return "";
		}
		try{
		decode_str=URLDecoder.decode(encode_str);
		decode_str=URLDecoder.decode(decode_str);
		}
		catch(Exception e)
		{
			return "";
		}
		return decode_str;
	}
	
	/**
	 * 对Urlencode后的串进行解码
	 * 字符集encode
	 * @param encode_str
	 * @return decode_str
	 */
	public static String getDecodeUrl(String encode_str,String encode)
	{
		String decode_str="";
		if(!(SSO.tnoe(encode_str)))
		{
			return "";
		}
		try{
		decode_str=URLDecoder.decode(encode_str,encode);
		decode_str=URLDecoder.decode(decode_str,encode);
		}
		catch(Exception e)
		{
			return "";
		}
		return decode_str;
	}
	
	
	public static void main(String[] args)
	{
		
		String code_str="";
		String de_str="";
		
		
		code_str="%E6%9D%8E%E7%99%BD";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		code_str="%E6%9D%8E%E7%99%BD%E5%93%AA%E9%87%8C%E4%BA%BA";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		code_str="keywords=+%09%09%09%09%E5%9F%BA%E6%9C%AC%E4%BF%A1%E6%81%AF%09%09%09%09%09%E6%98%B5%E7%A7%B0%EF%BC%9Akittiewong89330%09%E6%89%80%E5%9C%A8%E5%9C%B0%EF%BC%9A%E6%B1%9F%E8%8B%8F+%E5%8D%97%E4%BA%AC%09%E6%80%A7%E5%88%AB%EF%BC%9A%E5%A5%B3%09%E7%94%9F%E6%97%A5%EF%BC%9A1989%E5%B9%B43%E6%9C%8830%E6%97%A5%09%09%E7%AE%80%E4%BB%8B%EF%BC%9A%09%E5%96%B5%E5%96%B5%09%09%09%E6%B3%A8%E5%86%8C%E6%97%B6%E9%97%B4%EF%BC%9A%09%092009-09-23%09%09%09%09%09%09%09%09%09%09%09%09%09%09%E6%95%99%E8%82%B2%E4%BF%A1%E6%81%AF%09%09%09%09%09%E5%A4%A7%E5%AD%A6%EF%BC%9A%09%09%E5%8D%97%E4%BA%AC%E9%82%AE%E7%94%B5%E5%A4%A7%E5%AD%A6+%282007%E5%B9%B4%29%09%09%09%09%09%09%09%09%09%09%09%09%09%E6%A0%87%E7%AD%BE%E4%BF%A1%E6%81%AF%09%09%09%09%09%E6%A0%87%E7%AD%BE%EF%BC%9A%09%09%09%09%09%E4%B9%90%E8%BF%B7%09%09%09%09%E8%90%8C%E7%89%A9%E6%8E%A7%09%09%09%09%E5%BF%A0%E5%AE%9EPerfume%E7%B2%89%09%09%09%09%09%09%09%09%09&id=1252451131&title=";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		code_str="%E6%9D%8E%E7%99%BD%E5%93%AA%E9%87%8C%E4%BA%BA";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		code_str="%E6%9D%8E%E7%99%BD";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		code_str="%E5%B9%BF%E5%BE%B7%E4%BA%BA%E5%8F%A3";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		code_str="%E5%B9%BF%E5%BE%B7";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		code_str="%E5%B9%BF%E5%BE%B7";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		code_str="%E5%85%89%E7%9A%84%E6%A3%8D";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		code_str="%E6%94%AF%E4%BB%98%E5%AE%9D";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		System.out.println("de_str:"+de_str);
		
		code_str="%E4%BD%B3%E8%83%BD%E5%8D%95%E5%8F%8D";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		code_str="70d%E7%A0%B4%E8%A7%A3";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
	
		code_str="70d%E7%A0%B4%E8%A7%A3";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		code_str="70d%E7%A0%B4%E8%A7%A3";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		code_str="70d%E7%A0%B4%E8%A7%A3";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		
		
		
		
		
		
		String title="e易时代拖把卡座/拖把扫把收纳整理架拖把支架墙壁魔力无痕挂钩";
		String encode=URLEncoder.encode(title);
		System.out.println("encode:"+encode);
		
	}
	
}
