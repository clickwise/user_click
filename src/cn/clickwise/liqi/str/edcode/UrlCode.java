package cn.clickwise.liqi.str.edcode;

import java.net.URLDecoder;

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
		String code_str="%2F+seg+%3F+s+%3D+%E6%BE%B3%E5%A4%A7%E5%88%A9%E4%BA%9A+%E6%96%B9%E9%9D%A2+%E5%BD%93%E5%9C%B0+%E6%97%B6%E9%97%B4+4%E6%97%A5+%E7%A7%B0+%EF%BC%8C+%E6%90%9C%E5%AF%BB+%E9%A9%AC%E8%88%AA+MH+370+%E8%88%AA%E7%8F%AD+%E5%87%BA%E7%8E%B0+%E6%96%B0+%E7%BA%BF%E7%B4%A2+%E3%80%82+%E4%B8%80%E5%90%8D+%E8%8B%B1%E5%9B%BD%E7%B1%8D+%E5%A5%B3%E5%AD%90+%E8%BF%91%E6%97%A5+%E6%8A%A5%E5%91%8A+%E7%A7%B0+%EF%BC%8C+%E5%A5%B9+%E4%BB%8A%E5%B9%B43%E6%9C%88%E4%BB%BD+%E5%9C%A8+%E5%8D%B0%E5%BA%A6%E6%B4%8B+%E9%A9%BE%E8%88%B9+%E8%88%AA%E8%A1%8C+%E6%9C%9F%E9%97%B4+%EF%BC%8C+%E6%9B%BE+%E7%9B%AE%E5%87%BB+%E4%B8%80+%E6%9E%B6+%E8%B5%B7%E7%81%AB+%E7%9A%84+%E9%A3%9E%E6%9C%BA+%E9%A3%9E%E8%BF%87+%EF%BC%8C+%E8%AF%A5+%E9%A3%9E%E6%9C%BA+%E6%88%96+%E4%B8%BA+%E9%A9%AC%E8%88%AA+MH+370+%E5%AE%A2%E6%9C%BA+%E3%80%82";
		String de_str=UrlCode.getDecodeUrl(code_str);
		System.out.println("de_str:"+de_str);
		
	}
	
}
