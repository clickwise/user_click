package cn.clickwise.liqi.str.edcode;

import java.net.URLDecoder;

import org.apache.commons.codec.binary.Base64;



import cn.clickwise.liqi.str.basic.SSO;

public class Base64Code {

	/**
	 * 对Base64编码后的串进行解码
	 * @param encode_str
	 * @return decode_str
	 */
	public static String getDecodeStr(String encode_str)
	{
		String decode_str="";
		String charset="";
	    charset=CharsetConv.getEncoding(encode_str);
	    charset=charset.trim();
		if(!(SSO.tnoe(encode_str)))
		{
			return "";
		}
		try{
		  byte[] ea=encode_str.getBytes();
	      byte[] da=Base64.decodeBase64(ea);
	      if(da!=null)
	      {
	    	  /*
	    	  if(charset.equals("GB2312"))
	    	  {
		        decode_str=new String(da,"GB2312");
	    	  }
	    	  else
	    	  {
	    		  decode_str=new String(da,"utf-8");  
	    	  }
	    	  */
	    	  decode_str=new String(da); 
	    	  
	      }
	      else
	      {
	    	decode_str="";
	      }
		}
		catch(Exception e)
		{
			return "";
		}
		return decode_str;
	}
	
	
	/**
	 * 对字符串进行base64编码
	 * @param encode_str
	 * @return decode_str
	 */
	public static String getEncodeStr(String word)
	{
		String encode_str="";
		encode_str=new String(Base64.encodeBase64(word.getBytes()));
		return encode_str;
	}
	
	
	
	
	public static void main(String[] args)
	{
		String es="fkCK0003qk4QDyjmBO24zcqybcvRQoDoM-DIvZWzlAHYVlCKmSqh_9jUuTS5GcD7B7xLYqC4sXZiahdF0_dEQs5miTDhpOkQBVSJ6NtRSdZPqXyaOj7-AKvkhnUJ.7D_jVxqZ-ja_HZuuugguYe8y9W4mTTzs1f_IheW8lN0.IgF_5y9YIZ0lQzqLILT8n1D4ri4WUBqCIy78Xh9dmy4MQ1csPHbsgLK_mgb8pZwV0ZwV5HD1nWbdPjR0IZRqn0KLUjY10AP8IA3qPj0snHDsPWNxIZKxmLKz0A7bmvk9TLnqn6KzT1Ys0APYUHdsmywxIA--TA-9U-tYnjKxn10s0A49IZRqr0KGIA-b5HnY0A-Ypy4hUv-b5H00uLKGujYs0ZF-uMKGujYs0APsThqGujY1wWR1wbD4wjDdnWDLPHDkwjFKn16zPRm4n1b4nR7jwfKWUvdsUARqn0K9IA-b5fK9IZw45fKBIywMugwxug9spyfqn0K9pg0qfHKDPZT3n-Kri7b0Iy-s5NP_pH-awY-zubFi0APC5HD0IA7z5H60pydL5H00uZws5HD0TvN_UANzgv-b5HD0pgPxmgKs5H00mgKsgv-b5H00mLN1IjY0pgPxIv-zuyk-TLnqn0KLmgKxIZ-suHYs0ZK_5H00Uynqn19-PHNbnvc0UgmqnfK8IM0qna3snj0snj0sn0KMrHYk0AuGTMPYgLF-uv-EUWY1P0K1uyPEUhwxThNMpyq85Hm1n0K1TL0qnfK1TL0z5HD0IZws5HD0uA-1IZ0qn10snfK9mWYs0A7bXjYs0ZKhIZF9uARqnsKsTLwzmyw-5HnsPfKspyfqn0KLTA-b5H00mywYXgK-5H00TLIGujYs0ZPYXgK-5H00mLFW5HfYnH00";
		System.out.println(Base64Code.getDecodeStr(es));
		/*
		String url="www.baidu.com";
		System.out.println(Base64Code.getEncodeStr(url));
		url="www.taobao.com";
		System.out.println(Base64Code.getEncodeStr(url));
		*/
	}
}
