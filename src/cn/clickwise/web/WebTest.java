package cn.clickwise.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebTest {

	public String getTitle(String url)
	{
		Document doc=null;
		try {

			//////String content=fetcher.getSourceEasyProxy(url,getProxy());
			//////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			if(doc==null)
			{
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc.title();
	}
	
	public WebAbstract getAbstract(String url)
	{
		Document doc=null;
		WebAbstract wa=null;
		try {

			//////String content=fetcher.getSourceEasyProxy(url,getProxy());
			//////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			if(doc==null)
			{
				return null;
			}
			
			wa.setTitle(doc.title());
			wa.setKeywords(doc.head().select("meta[name=keywords]").attr("content"));
			wa.setDescription(doc.head().select("meta[name=description]").attr("content"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return wa;
	}
	
	public static void main(String[] args)
	{
		String url="http://club.autohome.com.cn";
		//WebTest wt=new WebTest();
		//System.out.println(wt.getTitle(url));
		Fetcher f=new Fetcher();
		System.out.println(f.getAbstract(url).toString());
	}
}
