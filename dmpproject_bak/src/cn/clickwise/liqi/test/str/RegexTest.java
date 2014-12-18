package cn.clickwise.liqi.test.str;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.liqi.crawler.basic.SingleUrlCrawl;
import cn.clickwise.liqi.str.basic.SSO;

public class RegexTest {

	
	public static String format_url(String host)
	{
		String fu="";
		if(!(SSO.tnoe(host)))
		{
			return "";
		}
		host=host.trim();
		if((host.indexOf("http://"))>-1)
		{
			return host;
		}
		
		fu="http://"+host;
		
		
		
		//System.out.println("fu:"+fu);
		fu=fu.trim();			
		return fu;
	}
	
	public static void main(String[] args)
	{
		/*
		String regex_str="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([\\d]+)\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53))).*?[udp sum ok]\\s*([\\d]+)[\\+]\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)))\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*";
	    String input="11:30:43.885046 IP (tos 0x0, ttl  55, id 0, offset 0, flags [DF], proto: UDP (17), length: 58) 60.177.24.229.38107 > 202.101.172.46.domain: [udp sum ok]  57856+ A? cb.baidu.com. (30)";
	    */
		
		/*
		String regex_str="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([\\d]+)\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53))).*?\\s*\\[udp\\s*sum\\s*ok\\]\\s*([\\d]+)[\\+]\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)))\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*";
	    String input="11:30:43.885678 IP (tos 0x0, ttl 121, id 25105, offset 0, flags [none], proto: UDP (17), length: 67) 60.191.45.230.49361 > 202.101.172.46.domain: [udp sum ok]  42700+ A? esnippet.e.shifen.com. (39)";
	    */
		
		/*
		String regex_str="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53)))\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([\\d]+).*?\\[udp sum ok\\]\\s*([\\d]+)\\s*q:\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)))\\s*\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*([\\d]+/[\\d]+/[\\d]+)\\s*[a-zA-Z0-9_\\-\\.]*\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)).*)$";
	    String input="11:30:43.884952 IP (tos 0x0, ttl  60, id 3676, offset 0, flags [none], proto: UDP (17), length: 540) 202.101.172.47.domain > 125.121.2.193.39785: [udp sum ok]  10 q: A? www.sina.com. 20/4/4 www.sina.com. CNAME us.sina.com.cn., us.sina.com.cn. CNAME news.sina.com.cn., news.sina.com.cn. CNAME jupiter.sina.com.cn., jupiter.sina.com.cn. CNAME taurus.sina.com.cn., taurus.sina.com.cn. A 61.172.201.20, taurus.sina.com.cn. A 61.172.201.21, taurus.sina.com.cn. A 61.172.201.24, taurus.sina.com.cn. A 61.172.201.25, taurus.sina.com.cn. A 61.172.201.36, taurus.sina.com.cn. A 61.172.201.9, taurus.sina.com.cn. A 61.172.201.10, taurus.sina.com.cn. A 61.172.201.11, taurus.sina.com.cn. A 61.172.201.12, taurus.sina.com.cn. A 61.172.201.13, taurus.sina.com.cn. A 61.172.201.14, taurus.sina.com.cn. A 61.172.201.15, taurus.sina.com.cn. A 61.172.201.16, taurus.sina.com.cn. A 61.172.201.17, taurus.sina.com.cn. A 61.172.201.18, taurus.sina.com.cn. A 61.172.201.19 ns: sina.com.cn. NS ns2.sina.com.cn., sina.com.cn. NS ns3.sina.com.cn., sina.com.cn. NS ns1.sina.com.cn., sina.com.cn. NS ns4.sina.com.cn. ar: ns1.sina.com.cn. A 202.106.184.166, ns2.sina.com.cn. A 61.172.201.254, ns3.sina.com.cn. A 123.125.29.99, ns4.sina.com.cn. A 121.14.1.22 (512)";
	    */
		
		
		String regex_str="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP\\s*\\(.*?id\\s*([\\d]+).*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53)))\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([a-zA-Z0-9_\\-\\.]*?)\\s*:.*?\\[udp sum ok\\]\\s*([\\d]+)\\s*q:\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)))\\s*\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*([\\d]+/[\\d]+/[\\d]+)\\s*[a-zA-Z0-9_\\-\\.]*\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)).*)$";
		String input="11:30:43.884957 IP (tos 0x0, ttl  59, id 41167, offset 0, flags [none], proto: UDP (17), length: 156) 202.101.172.35.53 > 125.121.154.66.39779: [udp sum ok]  1 q: A? www.google.com. 6/0/0 www.google.com. A 74.125.128.103, www.google.com. A 74.125.128.104, www.google.com. A 74.125.128.106, www.google.com. A 74.125.128.147, www.google.com. A 74.125.128.99, www.google.com. A 74.125.128.105 (128)";
	  
		
		/*
		String regex_str="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP\\s*\\(.*?id\\s*([\\d]+).*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([a-zA-Z0-9_\\-\\.]*?)\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53))).*?\\s*\\[udp\\s*sum\\s*ok\\]\\s*([\\d]+)[\\+]\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)))\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*";
		String input="11:31:11.322896 IP (tos 0x0, ttl 124, id 50574, offset 0, flags [none], proto: UDP (17), length: 61) 10.27.53.31.3050 > 202.101.172.35.53: [udp sum ok]  26514+ A? sdl.360safe.com. (33)";
		*/
		Pattern pat=Pattern.compile(regex_str);
	    Matcher mat=pat.matcher(input);
	    
	    int gnum=11;
	    
	    while(mat.find())
	    {
	    	for(int i=1;i<=gnum;i++)
	    	{
	    	//	System.out.println(mat.group(i));
	    	}
	    }
	    
	    String us="<meta charset=\"utf-8\" />";
		Pattern charset_pat=Pattern.compile("<(?:(?:meta)|(?:META)|(?:Meta))\\s*[^<>]*?(?:(?:charset)|(?:CHARSET))=\\s*([^>\\s]*)[^<>]*?\\s*>");
		Matcher charset_mat=charset_pat.matcher(us);
	    if(charset_mat.find())
	    {
	    	System.out.println("bdc:"+charset_mat.group(1));
	    }
	    
	    System.out.println(SingleUrlCrawl.getWebPageTitle("http://www.sina.com.cn/"));
		
	    //String ins="11d33.11d33.11d33.11d33.toutoulut.com"; 
	   // System.out.println("look:"+ins.indexOf("http://"));
	   // System.out.println(RegexTest.format_url("www"));
	        
	}
	
	
}
