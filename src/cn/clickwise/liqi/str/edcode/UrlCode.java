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
		code_str="%E9%84%B1%E9%98%B3%E6%B9%96%20";
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
