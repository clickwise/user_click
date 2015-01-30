package cn.clickwise.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.clickwise.lib.string.SSO;

public class WebTest {

	private Fetcher fetcher;

	public WebTest() {
		init();
	}

	public void init() {
		fetcher = new Fetcher();
	}

	public String getTitle(String url) {
		Document doc = null;
		try {

			// ////String content=fetcher.getSourceEasyProxy(url,getProxy());
			// ////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			if (doc == null) {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc.title();
	}

	public WebAbstract getAbstract(String url) {
		Document doc = null;
		WebAbstract wa = null;
		try {

			// ////String content=fetcher.getSourceEasyProxy(url,getProxy());
			// ////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			if (doc == null) {
				return null;
			}

			wa.setTitle(doc.title());
			wa.setKeywords(doc.head().select("meta[name=keywords]")
					.attr("content"));
			wa.setDescription(doc.head().select("meta[name=description]")
					.attr("content"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return wa;
	}

	public void pageInOut(int opt) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
			String line = "";
			// Set<WordEntry> sws=null;
			WebAbstract wa = new WebAbstract();
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}

				System.err.println("fetching url:" + line);
				wa = fetcher.getAbstract(line);
				if (wa == null) {
					continue;
				}
				if (opt == 0) {
					pw.println(line + "\001" + wa.getTitle());
				} else if (opt == 1) {
					pw.println(line + "\001" + wa.toRegularString());
				}

			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pageMulInOut(int threadNum) {
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String line = "";

			QueueUrlPond qud = new QueueUrlPond();
			qud.startConsume(threadNum);
			ArrayList<String> urllist = new ArrayList<String>();

			while ((line = br.readLine()) != null) {
				System.err.println("add line:" + line);
				if (SSO.tioe(line)) {
					continue;
				}
				urllist.add(line);
			}

			int k = 0;
			qud.setAllCount(urllist.size());
			while ((qud.getCount() < urllist.size())) {
				
				try{
				   qud.add2Pond(urllist.get(k));
				   Thread.sleep(10);
				
				   if ((k <( urllist.size()-1))) {
					  k++;
				   }
				
				   if(k==( urllist.size()-1))
				   {
					 Thread.sleep(1000);
				   }
				
				 }
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		/*
		if (args.length != 1) {
			System.err.println("Usage:<opt>" + "opt:" + "0->title"
					+ "1->title、keywords、description");
			System.exit(1);
		}

		int opt = 0;
		opt = Integer.parseInt(args[0]);
        */
		
		String url = "http://weibo.com/nabuzhun?from=feed&loc=nickname"; 
		WebTest wt=new WebTest(); 
		// System.out.println(wt.getTitle(url)); 
		Fetcher f = new Fetcher();
		System.out.println(f.getSource(url).toString());
		
		
		/*
		WebTest wt = new WebTest();
		wt.pageMulInOut(opt);
        */
	}

	public Fetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(Fetcher fetcher) {
		this.fetcher = fetcher;
	}
}
