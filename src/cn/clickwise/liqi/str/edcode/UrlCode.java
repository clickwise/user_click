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
		//String code_str="dq.baidu.com|||/nocipher?pid=rtlog&ver=1.0&cdn=wangsu&service=91bcs&type=access&log=220.180.227.146%20-%20bcs.91rb.com%20%5b20%2FApr%2F2014%3A05%3A31%3A41%20%2B0800%5d%201%20%22HEAD%20%2Frbreszy%2Fandroid%2Fsoft%2F2014%2F3%2F30%2Ffe4cbe285d3042d5ac9a4d93959caf27%2Fcom.sina.weibo_1026_4.3.0_635322331845862939.apk%20HTTP/1.1%22%20200%200%20%22-%22%20-%20220.180.227.146%20115.231.148.18%20%22-%22%201397943101.944%201&node=dx_zhejiang";
		//String code_str="dq.baidu.com|||/nocipher?pid=rtlog&ver=1.0&cdn=wangsu&service=sjzs&type=access&log=180.155.118.57%20-%20dl.sj.91.com%20%5b20%2FApr%2F2014%3A05%3A31%3A33%20%2B0800%5d%203030%20%22GET%20%2Fmsoft%2F91assistant_3.9.6_295_417.apk%3F%20HTTP/1.1%22%20200%205037354%20%22-%22%20-%20-%2060.191.196.209%20%22-%22%201397943093.377%201&node=dx_zhejiang";
		String code_str="%E6%99%93%E6%9D%BE%E5%A5%87%E8%B0%88%C2%A020140926%C2%A0%E4%B8%80%E6%88%981913%EF%BC%88%E4%B8%8B%EF%BC%89%20%E6%95%A2%E7%88%B1%E6%95%85%E4%BA%8B%E4%B9%8B%E6%96%87%E8%89%BA%E6%97%B6%E4%BB%A3-%E8%A7%86%E9%A2%91%E5%9C%A8%E7%BA%BF%E8%A7%82%E7%9C%8B-%E6%99%93%E6%9D%BE%E5%A5%87%E8%B0%88-%E7%BB%BC%E8%89%BA-%E7%88%B1%E5%A5%87%E8%89%BA";
		String de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
		String title="e易时代拖把卡座/拖把扫把收纳整理架拖把支架墙壁魔力无痕挂钩";
		String encode=URLEncoder.encode(title);
		System.out.println("encode:"+encode);
		
	}
	
}
