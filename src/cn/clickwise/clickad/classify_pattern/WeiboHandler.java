package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import cn.clickwise.json.JsonUtil;
import cn.clickwise.lib.string.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;

public class WeiboHandler extends Handler{
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String user_info = exchange.getRequestURI().toString();
		System.out.println("user_info:"+user_info);
		user_info = user_info.replaceFirst("\\/cwb\\?s\\=", "");
		user_info=user_info.trim();
		String de_info = new String(UrlCode.getDecodeUrl(user_info));
		de_info=de_info.trim();
		System.out.println("de_title:" + de_info);
		/*
		Map umap=JsonUtil.getMapFromJson(de_info);
		String title=umap.get("title")+"";
		String keywords=umap.get("keywords")+"";
		String description=umap.get("description")+"";
		String summary=umap.get("简介")+"";
		String tags=umap.get("标签")+"";		
		String posts=umap.get("帖子")+"";
		
		System.out.println("title:"+title);
		System.out.println("keywords:"+keywords);
		System.out.println("description:"+description);
		System.out.println("简介:"+summary);
		System.out.println("标签:"+tags);
		System.out.println("帖子:"+posts);
		
		title=trimTitle(title);
		keywords=trimKeywords(keywords);
		description=trimDescription(description);
		summary=trimSummary(summary);
		posts=trimPosts(posts);
		
		String short_info=title+"\t"+keywords+"\t"+description+"\t"+summary+"\t"+tags+"\t"+posts;
	    */
		//System.out.println("de_title.len:"+de_title.length());
		String cate_str="";
		if(de_info.length()>20)
		{
		  cate_str=classifer.cate(de_info);
		  cate_str=cate_str.trim();
		  System.err.println("cate_str:"+cate_str);
		}
		else
		{
			cate_str="title信息不足";
		}
		
		//cate_str="ok";
		System.out.println("cate_str:"+cate_str);

		//encode_res = encode_res.replaceAll("\\s+", "");	
		//exchange.sendResponseHeaders(200, encode_res.length());
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		//os.write(encode_res.getBytes());
		pw.println(cate_str);
		pw.flush();
		pw.close();
		os.close();
		
	}
	
	public String trimTitle(String title)
	{
		if(SSO.tioe(title))
		{
			return "";
		}
		
		title=title.replaceAll("的微博_微博", "");
		title=title.replaceAll("官方微博", "");
		title=title.replaceAll("微博", "");
		title=title.replaceAll("新浪", "");
		title=title.replaceAll("收藏", "");
		return title;
	}
	
	public String trimKeywords(String keywords)
	{
		if(SSO.tioe(keywords))
		{
			return "";
		}
		
		keywords=keywords.replaceAll("的微博，微博，新浪微博，weibo", "");
		keywords=keywords.replaceAll("官方微博", "");
		keywords=keywords.replaceAll("微博", "");
		keywords=keywords.replaceAll("新浪", "");
		keywords=keywords.replaceAll("收藏", "");
		return keywords;
	}
	
	public String trimDescription(String description)
	{
		if(SSO.tioe(description))
		{
			return "";
		}
		
		description=description.replaceAll("的微博主页、个人资料、相册", "");
		description=description.replaceAll("新浪微博，随时随地分享身边的新鲜事儿。", "");
		description=description.replaceAll("官方微博", "");
		description=description.replaceAll("微博", "");
		description=description.replaceAll("个人微信：", "");
		description=description.replaceAll("公众微信：", "");
		description=description.replaceAll("新浪", "");
		description=description.replaceAll("收藏", "");
		return description;
	}
	
	public String trimSummary(String summary)
	{
		if(SSO.tioe(summary))
		{
			return "";
		}
		summary=summary.replaceAll("官方微博", "");
		summary=summary.replaceAll("微博", "");
		summary=summary.replaceAll("新浪", "");
		summary=summary.replaceAll("收藏", "");
		return summary;
	}
	
	public String trimPosts(String posts)
	{
		if(SSO.tioe(posts))
		{
			return "";
		}
		
		posts=posts.replaceAll("置顶", "");
		posts=posts.replaceAll("转发微博", "");
		posts=posts.replaceAll("新浪", "");
		posts=posts.replaceAll("收藏", "");
		return posts;
	}
}
