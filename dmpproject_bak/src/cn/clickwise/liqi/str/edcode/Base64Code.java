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
	    	  if(charset.equals("GB2312"))
	    	  {
		        decode_str=new String(da,"GB2312");
	    	  }
	    	  else
	    	  {
	    		  decode_str=new String(da,"GB2312");  
	    	  }
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
	
	
	
	public static void main(String[] args)
	{
		String es="MEJETXlVVEF5QVRNMDBDTTAwaU0wQVNNMm9qTTFvak16RWdUVFJWUVU5RlZDOTFVRkZrVURoVUF4SWpNdWdETnVNek51RVRNQlFqTXVFak13NFNNMmtqTDBFd2N1UVhZdkpXWXY1eVl2MVdBdk1YWmhKM1lvOXpjdzFXUGhKek13SW5MeDR5TnVRakxSZDFOaGhGV21rbWJwUlhhaFJYYTJWMlhwUldQMEpXYXVSV1o0cDNYeUFUTTBBRE55UWpKMEZtWTkwV1lzeG1KeDFUSkVWVEpGUlVKRFJVSkNkVEFvUkhkd3B6THZNbkwwRjJiaUYyYnVNMmJ0OXljbEZtY2poMlB4MVRKRVZUSkZSVUpEUlVKQ2RqSmo5V2J0Vm1iazFUWXN4bUp6TlhhazF6YzEwU1ptTVhaaEozWW85RmQ1QlhaOWtHZGwxbUp6OVdkeU5XWkpSV1AwSm1McDVHWmxobkp6QlhiOUVqTDJZVE41UWpNeDR5TjFRRE81WWpNemNqTHhZU2F1bEdkcEZHZHBaWFpmbEdaOVFuWXA1R1psaG5lZkpETXhRRE0wSUROQnNEZDlNV2E5QXpYeHNUQWtqcmltWDd0bGpyZw==";
		System.out.println(Base64Code.getDecodeStr(Base64Code.getDecodeStr(es)));
		
	}
}
