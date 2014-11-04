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
		
		
		code_str="%E6%A2%A6%E5%B9%BB%E8%A5%BF%E6%B8%B8%E5%8F%8C%E5%9F%8E%E8%AE%B0";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		
		code_str="%E7%A7%A6%E6%97%B6%E6%98%8E%E6%9C%88%E4%B9%8B%E5%90%9B%E4%B8%B4%E5%A4%A9%E4%B8%8B";
		de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);	
		
		String title="e易时代拖把卡座/拖把扫把收纳整理架拖把支架墙壁魔力无痕挂钩";
		String encode=URLEncoder.encode(title);
		System.out.println("encode:"+encode);
		
	}
	
}
