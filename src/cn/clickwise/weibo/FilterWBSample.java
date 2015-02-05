package cn.clickwise.weibo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

public class FilterWBSample {
	
	public int limit=10000;
	
	public void filter(String sample,String output)
	{
		try{
		
			BufferedReader br=new BufferedReader(new FileReader(sample));
			PrintWriter pw=new PrintWriter(new FileWriter(output));
			String line="";
			String[] fields=null;
			
		    String link="";
		    String cate="";
		    String title="";
		    String keywords="";
		    String description="";
		    String summary="";
		    String tags="";
		    String posts="";
		    
		    String field="";
		    int count=0;
			while((line=br.readLine())!=null)
			{
			    link="";
			    cate="";
			    title="";
			    keywords="";
			    description="";
			    summary="";
			    tags="";
			    posts="";
			    
				count++;
				if(count>limit)
				{
					break;
				}
				if(SSO.tioe(line))
				{
					continue;
				}
				line=line.trim();
				fields=line.split("\t");
				if(fields.length<2)
				{
					continue;
				}
				
				link=fields[0];
				cate=fields[1];
				
				for(int i=2;i<fields.length;i++)
				{
					field=fields[i];
					if(SSO.tioe(field))
					{
						continue;
					}
					
					field=field.trim();
					if(field.startsWith("title"))
					{
						title=field.replaceFirst("title:", "");
						title=trimTitle(title);
					}
					else if(field.startsWith("keywords"))
					{
						keywords=field.replaceFirst("keywords:", "");
						keywords=trimKeywords(keywords);
					}
					else if(field.startsWith("description"))
					{
						description=field.replaceFirst("description:", "");
						description=trimDescription(description);
					}
					else if(field.startsWith("简介"))
					{
						System.out.println("简介:"+field);
						summary=field.replaceFirst("简介：", "");
					}
					else if(field.startsWith("标签"))
					{
						System.out.println("标签:"+field);
						tags=field.replaceFirst("标签:", "");
					}
					else if(field.startsWith("帖子"))
					{
						posts="";
						for(int j=i+1;j<fields.length;j++)
						{
							posts=posts+fields[j]+" ";
						}
						posts=posts.trim();
						
						System.out.println("帖子:"+posts);
						posts=posts.replaceFirst("帖子:", "");
						posts=trimPosts(posts);
					}
				}
				
				pw.println(link+"\t"+cate+"\t"+title+"\t"+keywords+"\t"+description+"\t"+summary+"\t"+tags+"\t"+posts);
				
			}
			
			pw.close();
			br.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
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
	
	public static void main(String[] args)
	{
		FilterWBSample fwbs=new FilterWBSample();
		String sample="weibo/labeltext.txt";
		String output="weibo/labeltext_filter_with_post.txt";
		fwbs.filter(sample, output);
	}
	
}
