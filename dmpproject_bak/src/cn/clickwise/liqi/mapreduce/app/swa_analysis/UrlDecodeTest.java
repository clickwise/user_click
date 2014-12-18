package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.net.URLDecoder;
import java.net.URLEncoder;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.CharsetConv;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.str.regex.RegexDB;
import cn.clickwise.liqi.str.regex.RegexFind;

public class UrlDecodeTest {
	

	public static void main(String[] args) throws Exception {
		
		String input="http://f3.market.xiaomi.com/download/AppStore/21d9cf10-62f5-4445-9b0b-56892fe3ffec/%E8%B5%B6%E9%9B%86%E7%94%9F%E6%B4%BB_4.7.1_135.apk";
		String output="";
		String charset=CharsetConv.getEncoding(input);
		System.out.println("charset:"+charset);
		output=UrlCode.getDecodeUrl(input);
		
		System.out.println("output:"+output);
		//charset=CharsetConv.getEncoding(output);
		//System.out.println("charset:"+charset);
		//output=URLDecoder.decode(output);
		String name=RegexFind.findSingle("(?:(?:query)|(?:wd))\\=("+RegexDB.getChineseRg()+"?)\\&", output);
		name=CharsetConv.gbToUtf8(name);
		System.out.println("name:"+name);
		charset=CharsetConv.getEncoding(name);
		System.out.println("charset:"+charset);
		String input_s="315";
		output=URLEncoder.encode(input_s);
		System.out.println("output:"+output);
		
	}
}
