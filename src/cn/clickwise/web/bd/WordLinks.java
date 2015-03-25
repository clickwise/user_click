package cn.clickwise.web.bd;

import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WordLinks {

	public static boolean isDebug=false;
	
	public void getWordLinks(String word)
	{
		//String link="http://www.baidu.com/s?wd="+URLEncoder.encode(word)+"&pn=10";
		String link="http://www.baidu.com/s?wd="+URLEncoder.encode(word);
		if(isDebug==true)
		{
			System.err.println("link:"+link);
		}
		Document doc=null;
		try{
		   doc = Jsoup.connect(link).get();
		   
		   if(isDebug==true)
		   {
			   System.err.println("doc:"+doc.text());
		   }
		   
		   Elements results=doc.getElementsByClass("t");
		   if(isDebug==true)
		   {
			   System.err.println("rs.size:"+results.size());   
		   }
		   
		   for(Element re:results)
		   {
			   System.out.println(re.select("a").attr("href"));
			   System.out.println(re.select("a").text());
		   }
		   
		   
		   
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		WordLinks wls=new WordLinks();
		wls.getWordLinks("老板电器");
	}
	
	
}
