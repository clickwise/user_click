package cn.clickwise.web;

import org.jmlp.file.utils.FileWriterUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SingleCrawl {

	public static boolean isDebug=false;
	
	public void singleCrawl(String url)
	{
		
		Document doc=null;
		try {

			//////String content=fetcher.getSourceEasyProxy(url,getProxy());
			//////doc=Jsoup.parse(content);
			if(isDebug==true)
			{
				System.err.println("crawling the url "+url);
			}
			doc = Jsoup.connect(url).get();
            System.out.println(url+"\001"+doc.html());
            FileWriterUtil.writeContent(doc.html(), "bd_test.txt");
            
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

    public static void main(String[] args)
	{
	   String url="http://www.baidu.com/s?wd=%E8%80%81%E6%9D%BF%E7%94%B5%E5%99";		
	   SingleCrawl sc=new SingleCrawl();
	   sc.singleCrawl(url);

	}
}
