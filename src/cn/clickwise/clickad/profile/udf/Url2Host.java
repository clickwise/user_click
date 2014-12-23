package cn.clickwise.clickad.profile.udf;

import java.net.URI;

import org.apache.hadoop.hive.ql.exec.UDF;

import cn.clickwise.lib.string.SSO;

public class Url2Host extends UDF{
	
	public String evaluate(String str) {

		String host="";
		try {
			if (SSO.tioe(str)) {
				return "";
			}
            URI uri=new URI(str);
	
			host=uri.getHost();

		} catch (Exception e) {
		}
		return host;
	}
	/*
	public static void main(String[] args)
	{
		Url2Host uh=new Url2Host();
		System.out.println(uh.evaluate("http://my.qzone.qq.com/app/1102083398.html?via=WAIBU.weizhi.800600&app_custom=dianxin"));
	}
	*/
}
