package cn.clickwise.web.bd;

import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WordLinks {

	public static boolean isDebug=false;
	
	public void getWordLinks(String word,int pageNum)
	{
		String link="http://www.baidu.com/s?wd="+URLEncoder.encode(word)+"&pn="+(10*pageNum);
		//String link="http://www.baidu.com/s?wd="+URLEncoder.encode(word);
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
			   System.out.println(word+"\001"+re.select("a").text()+"\001"+re.select("a").attr("href"));		   
		   }
		   
		   
		   
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void getWordLinksBat(String word,int totpage)
    {
		
		for(int i=0;i<totpage;i++)
		{
			getWordLinks(word,i);
		}
    }
	public static void main(String[] args)
	{
		
		if (args.length != 2) {
			System.err.println("Usage:<word> <totpage>");
			System.err.println("    word : 要抓取的词");
			System.err.println("    totpage:抓取的网页个数");
		
			System.exit(1);
		}
	
		
		String word=args[0];
		int totpage=Integer.parseInt(args[1]);
		
		WordLinks wls=new WordLinks();
		wls.getWordLinksBat(word, totpage);
	}
	
	
}
