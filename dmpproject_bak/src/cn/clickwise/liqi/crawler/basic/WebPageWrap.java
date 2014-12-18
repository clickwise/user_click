package cn.clickwise.liqi.crawler.basic;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/*
 * 输入:  url
 * 输出:  
 *   1.网页源内容 
 *   2.网页纯文本内容
 *   3.网页title
 *   4.网页Meta信息(title+keywords+description)
 *   5.某个tag下的超链接和锚文本列表
 */
public class WebPageWrap {
	/**
	 * 读取网页源代码
	 * @param url
	 * @return source
	 */
	public String getSource(String url)
	{
		String source="";
		HttpClient httpclient = new DefaultHttpClient();

		/*
		double ran = Math.random();
	    String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
		int rani = -1;
		rani = (int) (ran * 10);
		HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		 httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,1000);
		*/
		
		url = url.trim();
		if ((url == null) || (url.length() < 5)) {
			return "";
		}
		if (url.indexOf("http") < 0) {
			url = "http://" + url;
		}
		
		String con = "";
		
		try {
			HttpGet httpget = new HttpGet(url);

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			Pattern charset_pat=Pattern.compile("(?:(?:charset)|(?:CHARSET))=([^\">]*)");
			String charset="";
			Header type_head = null;
			type_head=response.getFirstHeader("Content-Type");
			
			String thlv=type_head.getValue().toString().toLowerCase();
			Matcher charset_mat=charset_pat.matcher(thlv);
			//System.out.println("Content-Type:"+thlv);
			
			if(thlv.indexOf("text/html")<0)
			{
				System.err.println("["+url+"] 不是普通网页");
				return "";
			}
			
			if(charset_mat.find())
			{
				charset=charset_mat.group(1);
			}
			charset=charset.trim();
			//System.out.println("charset1:"+charset);
			
			charset=charset.trim();
			
			
			byte[] bytes = new byte[1024];
			InputStream is = entity.getContent();
			is.read(bytes);
			String us = new String(bytes);
			String ds="";
			if(charset.equals(""))
			{
				charset_pat=Pattern.compile("<(?:(?:meta)|(?:META)|(?:Meta))[^<>]*?(?:(?:charset)|(?:CHARSET))=([^\">]*)[^<>]*?>");
				charset_mat=charset_pat.matcher(us.toLowerCase());
				//System.out.println("us:"+us);
				if(charset_mat.find())
				{
					charset=charset_mat.group(1);
				}
				//System.out.println("charset2:"+charset);
				ds=new String(bytes,charset);
			}
					
			
			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				entity = response.getEntity();
				if (entity != null) {

					 con = EntityUtils.toString(entity,charset);
					//con = EntityUtils.toString(entity);
                     source=ds+con;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}			
		return source;
	}
	
	
    /**
     * 抓取某个tag下的超链接和锚文本列表
     * @param url
     * @param attrname
     * @param attrvalue
     * @param keytext
     * @param linkpat
     * @param anchorpat
     * @return anchorLinks
     */
	public String[] getAnchorLinkByAttributeValueContaining(String url,String attrname,String attrvalue,String keytext,String linkpat,String anchorpat)
	{
		String[] anchorLinks=null;
		String source="";
		source=getSource(url);
		
		Document doc = Jsoup.parse(source);
		Elements matchAttrElments=	doc.getElementsByAttributeValueContaining(attrname,attrvalue);
		//System.out.println("matchAttrElments.size:"+matchAttrElments.size());
		
		Element matchKeyTextElement=null;
	    for(Element a:matchAttrElments)  
	    {  
	    	//System.out.println(a.toString());
	    	if(a.toString().indexOf(keytext)>0)
	    	{
	    		matchKeyTextElement=a;
	    	}	    	
	    }
		
	   // System.out.println(matchKeyTextElement.toString());
	    
	    
	    Elements li_content=matchKeyTextElement.getElementsByTag("dl");  
	    
	    ArrayList al=new ArrayList();
	    String temp_anchorLink=""; 
	    
	    Pattern linkPattern=Pattern.compile(linkpat);
	    Pattern anchorPattern=Pattern.compile(anchorpat);
	    Matcher linkMatcher=null;
	    Matcher anchorMatcher=null;
	    
	    String link="";
	    String anchor="";
	    String chunk_text="";
	    
	    for(Element a:li_content)  
	    {  
	    	 Elements href_list=a.getElementsByTag("a"); 
	    	// System.out.println("==============================");
	    	 for(Element b:href_list)
	    	 {    		 
	               // temp_anchorLink=b.toString().replaceAll("<[^<>]*?>", "").trim()+"\001"+b.attr("href");
	               // al.add(temp_anchorLink);
	               
	    		    chunk_text=b.toString();
	    		   // System.out.println("chunk_text:"+chunk_text);
	                linkMatcher=linkPattern.matcher(chunk_text);
	                link="";
	                anchor="";
	                if(linkMatcher.find())
	                {
	                	link=linkMatcher.group(1);                	
	                }
	                
	                anchorMatcher=anchorPattern.matcher(chunk_text);
	                if(anchorMatcher.find())
	                {
	                	anchor=anchorMatcher.group(1);                	
	                }
	                if((!(link.equals("")))&&(!(anchor.equals(""))))
	                {
	                  al.add(anchor+"\001"+link);
	                }
	    	 }
	    }
	    
	    anchorLinks=new String[al.size()];
	    for(int i=0;i<al.size();i++)
	    {
	    	anchorLinks[i]=al.get(i)+"";
	    }
		return anchorLinks;
	}
	
	
	
    /**
     * 抓取某个tag下的一级锚文本和二级锚文本的对应关系
     * @param url
     * @param attrname
     * @param attrvalue
     * @param keytext
     * @param anchorpat
     * @return areamap
     */
	public String[] getAnchorMapByAttributeValueContaining(String url,String attrname,String attrvalue,String keytext,String anchorpat)
	{
		String[] areamap=null;
		String source="";
		source=getSource(url);
		
		Document doc = Jsoup.parse(source);
		Elements matchAttrElments=	doc.getElementsByAttributeValueContaining(attrname,attrvalue);
		//System.out.println("matchAttrElments.size:"+matchAttrElments.size());
		
		Element matchKeyTextElement=null;
	    for(Element a:matchAttrElments)  
	    {  
	    	//System.out.println(a.toString());
	    	if(a.toString().indexOf(keytext)>0)
	    	{
	    		matchKeyTextElement=a;
	    	}	    	
	    }
		
	   // System.out.println(matchKeyTextElement.toString());
	    
	    
	    Elements li_content=matchKeyTextElement.getElementsByTag("dl");  
	    
	    ArrayList al=new ArrayList();
	    String temp_anchorLink=""; 
	    
	    Pattern anchorPattern=Pattern.compile(anchorpat);
	    Matcher linkMatcher=null;
	    Matcher anchorMatcher=null;
	    
	    String link="";
	    String anchor="";
	    String chunk_text="";
	    
	    String areaMapEle="";
	    for(Element a:li_content)  
	    {  
	    	 Elements href_list=a.getElementsByTag("a"); 
	    	// System.out.println("==============================");
	    	 areaMapEle="";
	    	 for(Element b:href_list)
	    	 {    		 
	               // temp_anchorLink=b.toString().replaceAll("<[^<>]*?>", "").trim()+"\001"+b.attr("href");
	               // al.add(temp_anchorLink);
	               
	    		    chunk_text=b.toString();
	                //System.out.println("chunk_text:"+chunk_text);
	                anchorMatcher=anchorPattern.matcher(chunk_text);
	                if(anchorMatcher.find())
	                {
	                	anchor=anchorMatcher.group(1);   
	                	areaMapEle=areaMapEle+anchor+"\t";
	                }
	               
	    	 }
             if((!(areaMapEle.equals(""))))
             {
               al.add(areaMapEle);
             }
	    }
	    
	    areamap=new String[al.size()];
	    for(int i=0;i<al.size();i++)
	    {
	    	areamap[i]=al.get(i)+"";
	    }
		return areamap;
	}
	
	
	/**
	 * 按照某一属性和关键字提取某一字段
	 * @param url
	 * @param attrname
	 * @param attrvalue
	 * @param keytext
	 * @return textFields
	 */
	public String[] getTextFiledsByAttributeValue(String url,String attrname,String attrvalue,String keytext,String[] tf_pats)
	{
		String[] textFields=null;
		String source="";
		source=getSource(url);
		
		Document doc = Jsoup.parse(source);
		Elements matchAttrElments=	doc.getElementsByAttributeValueContaining(attrname,attrvalue);
		Element matchKeyTextElement=null;
		
	    String chunk_text="";
	    String textField="";
	    String[] temp_columns=null;
	    Pattern[] column_pats=new Pattern[tf_pats.length];
	    for(int i=0;i<tf_pats.length;i++)
	    {
	    	//System.out.println("tf_pats "+i+" "+tf_pats[i]);
	    	column_pats[i]=Pattern.compile(tf_pats[i]);   	
	    }
	    Matcher column_mat=null;
	    String[] columns=null;
	    int mat_num=0;
	    
	    
	    ArrayList al=new ArrayList();
	    for(Element a:matchAttrElments)  
	    {  
	    	//System.out.println(a.toString());
	    	matchKeyTextElement=null;
	    	if(!(keytext.trim().equals("")))
	    	{
	    	  if(a.toString().indexOf(keytext)>0)
	    	  {
	    		matchKeyTextElement=a;
	          }
	    	}
	    	else
	    	{
	    		matchKeyTextElement=a;
	    	}
	    	
	    	if(matchKeyTextElement==null)
	    	{
	    		continue;
	    	}
	    	
	    	chunk_text=matchKeyTextElement.toString();
	    	//System.out.println("chunk_text:"+chunk_text);
	    	textField="";
	    	columns=new String[tf_pats.length];
	    	mat_num=0;
	    	
	    	for(int t=0;t<tf_pats.length;t++)
	    	{
	    		column_mat=column_pats[t].matcher(chunk_text);
	    		if(column_mat.find())
	    		{
	    			columns[t]=column_mat.group(1);
	    			mat_num++;
	    		}	    		
	    	}
	    	
	    	if(mat_num!=tf_pats.length)
	    	{
	    		continue;
	    	}
	    	
	    	for(int t=0;t<columns.length;t++)
	    	{
	    		textField=textField+columns[t]+"\001";
	    	}
	    	textField=textField.trim();
	    	al.add(textField);
	    		
	    }
		
	    textFields=new String[al.size()];
	    for(int i=0;i<al.size();i++)
	    {
	    	textFields[i]=al.get(i)+"";
	    }
	    
		return textFields;
	}
	
	
	public static void main(String[] args)
	{
		WebPageWrap wpw=new WebPageWrap();
		
		/****getSource****/
		
		String url="http://flights.ctrip.com/international/tools/GetCities.ashx?s=MPM&a=0&t=0";
		String content="";
		content=wpw.getSource(url);
		System.out.println("content:"+content);
		
		
		/****getAnchorLinkByAttributeValueContaining****/
		/*
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-nopic";
		String keytext="天涯海角";*/
		//String linkpat="<a\\s*href=\"(http://www.tuniu.com/guide/d\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		//String anchorpat="<a\\s*href=\"http://www.tuniu.com/guide/d\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		/*
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
		}
		*/
		
		/****getAnchorLinkByAttributeValueContaining****/
		/*
		String url="http://www.tuniu.com/guide/d-hainan-900/";
		String attrname="class";
		String attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		String keytext="热门景点：";*/
		//String linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
		//String anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		/*
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
		}
		*/
		
		
		/****getAnchorMapByAttributeValueContaining****/
		/*
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-nopic";
		String keytext="天涯海角";*/
		
		//String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/d\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		//String[] areamap=wpw.getAnchorMapByAttributeValueContaining(url, attrname, attrvalue,keytext,anchorpat);
		//for(int i=0;i<areamap.length;i++)
		//{
			//System.out.println(areamap[i]);
		//}
		
		
		/****getTextFiledsByAttributeValue****/
		/*
		String url="http://www.tuniu.com/guide/d-zhejiang-3400/jingdian";
		String attrname="class";
		String attrvalue="jingdian_li_pic";
		String keytext="";*/
		//String[] tf_pats={"<[^<>]*?href=\"(http://www.tuniu.com/guide/v\\-([a-zA-Z]*?)\\-[\\d]+[/]?)\"\\s*target=\"_blank\"[^<>]*?>","<[^<>]*?href=\"http://www.tuniu.com/guide/v\\-([a-zA-Z]*?)\\-[\\d]+[/]?\"\\s*target=\"_blank\"[^<>]*?>","<[^<>]*?href=\"http://www.tuniu.com/guide/v\\-[a-zA-Z]*?\\-([\\d]+)[/]?\"\\s*target=\"_blank\"[^<>]*?>","<div\\s*class=\"jingdian_name\">(?s)\\s*<p>([^<>]*?)</p>"};
		/*
		String[] areamap=wpw.getTextFiledsByAttributeValue(url, attrname, attrvalue,keytext,tf_pats);
		for(int i=0;i<areamap.length;i++)
		{
			System.out.println(areamap[i]);
		}
		*/
		
		/****getTextFiledsByAttributeValue****/
		/*
		String url="http://www.tuniu.com/guide/d-zhejiang-3400/jingdian";
		String attrname="class";
		String attrvalue="mb20";
		String keytext="";
		String[] tf_pats={"(共[\\d]+项.*?当前[\\d]+\\-[\\d]+项)"};
		
		String[] areamap=wpw.getTextFiledsByAttributeValue(url, attrname, attrvalue,keytext,tf_pats);
		for(int i=0;i<areamap.length;i++)
		{
			System.out.println(areamap[i]);
		}
		*/
	}
	
}
