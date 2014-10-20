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
		String es="w0KadtqYKlfJO40FVXYxiTZimah4nZi8";
		System.out.println(Base64Code.getDecodeStr(es));
		/*
		String url="www.baidu.com";
		System.out.println(Base64Code.getEncodeStr(url));
		url="www.taobao.com";
		System.out.println(Base64Code.getEncodeStr(url));
		*/
	}
}
