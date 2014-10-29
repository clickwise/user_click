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
		
		
		code_str="%E6%B5%99%E6%B1%9F%E7%9C%81%E6%9D%AD%E5%B7%9E%E5%B8%82";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		
		code_str="%E5%B8%B8%E5%B7%9E%E9%80%9A%E6%B1%9F%E4%B8%AD%E8%B7%AF%E9%99%84%E8%BF%91%E6%9C%89%E4%BB%80%E4%B9%88%E7%8E%A9%E5%91%90";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		
		String title="e易时代拖把卡座/拖把扫把收纳整理架拖把支架墙壁魔力无痕挂钩";
		String encode=URLEncoder.encode(title);
		System.out.println("encode:"+encode);
		
	}
	
}
