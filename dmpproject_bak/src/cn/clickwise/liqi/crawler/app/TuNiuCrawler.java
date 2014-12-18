package cn.clickwise.liqi.crawler.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.liqi.crawler.basic.WebPageWrap;

/**
 * 抓取途牛的省份、地区的链接及主要景点
 * @author lq
 *
 */
public class TuNiuCrawler {
	
	/**
	 * 抓取途牛的省份、地区的链接及主要景点,国内旅游
	 */
   public void getAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
	   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-nopic";
		String keytext="天涯海角";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/d\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/d\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			//System.out.println(anchorLinks[i]);
		}
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			allAreaAttrs=getAreaAllAttractions(link);
			for(int j=0;j<allAreaAttrs.length;j++)
			{
				pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			}
		}
		
		fw.close();
		pw.close();
		
   }
   
   
   /**
    * 获取马尔代夫的主要景点
    * @param output_file
    */
   public void getMaldivesAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-onepic";
		String keytext="马尔代夫";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			//System.out.println(anchorLinks[i]);
			pw.println(anchorLinks[i]);
		}		
		fw.close();
		pw.close();
   }
   
   
   /**
    * 获取欧洲的主要地区和对应的景点
    * @param output_file
    */
   public void getEuropeAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-onepic";
		String keytext="欧洲";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
			//pw.println(anchorLinks[i]);
		}
		
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
		
		
		fw.close();
		pw.close();
   }
   
   
   /**
    * 获取美洲的主要地区和对应的景点
    * @param output_file
    */
   public void getAmericaAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-onepic";
		String keytext="美洲";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
			//pw.println(anchorLinks[i]);
		}
		
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
		
		
		fw.close();
		pw.close();
   }
   
   
   
   /**
    * 获取美洲的主要地区和对应的景点
    * @param output_file
    */
   public void geKoreaJapanAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-twopic";
		String keytext="韩国";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
			//pw.println(anchorLinks[i]);
		}
		
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
		
		
		fw.close();
		pw.close();
	   
	   
   }
  
   
   /**
    * 获取东南亚、南亚、泰国的主要地区和对应的景点
    * @param output_file
    * @throws Exception
    */
   public void getSoutheastAsiaAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-threepic";
		String keytext="东南亚";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
			//pw.println(anchorLinks[i]);
		}
		
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
		
		
		fw.close();
		pw.close();
	      
   }
   
   
   
   /**
    * 获取香港、澳门、台湾的主要地区和对应的景点
    * @param output_file
    * @throws Exception
    */
   public void getHongKongAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-twopic";
		String keytext="港澳";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
			//pw.println(anchorLinks[i]);
		}
		
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
		
		
		fw.close();
		pw.close();
	      
   }
   
   
   /**
    * 获取澳洲、中东非洲的主要地区和对应的景点
    * @param output_file
    * @throws Exception
    */
   public void getAustraliaAreaAttraction(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);   
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-twopic";
		String keytext="澳洲";
		String linkpat="<a[^<>]*?href=\"(http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?)\"\\s*target=\"_blank\"[^<>]*?>";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/[dv]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] anchorLinks=wpw.getAnchorLinkByAttributeValueContaining(url, attrname, attrvalue,keytext,linkpat,anchorpat);
		for(int i=0;i<anchorLinks.length;i++)
		{
			System.out.println(anchorLinks[i]);
			//pw.println(anchorLinks[i]);
		}
		
		
		String anchorLink="";
		String[] al_seg=null;
		String anchor="";
		String link="";
		attrname="class";
		attrvalue="prop_item needed_filter mult_select hasMoreChoice";
		keytext="热门景点：";
	    linkpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"([\\d]+)\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>";
	    anchorpat="<input(?s)\\s*type=\"checkbox\"(?s)\\s*value=\"[\\d]+\"(?s)\\s*name=\"viewpoint\"(?s)\\s*/>((?s)[^<>]*?)</a>";
		
		
		String[] subanchorLinks=null;
		String attstr="";
		
		
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
		
		
		fw.close();
		pw.close(); 
   }
   
   
   /**
    * 获取国外的主要地区和对应的景点
    * @param output_file
    * @throws Exception
    */
   public void getForeignAreaAttraction(String output_file) throws Exception
   {
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
	    String anchorLink="";
	    String[] subanchorLinks=null;
		String[] al_seg=null;
		String attstr="";
		String anchor="";
		String link="";
		//String[] anchorLinks={"马尔代夫\001http://www.tuniu.com/guide/d-maerdaifu-3922/","欧洲\001http://www.tuniu.com/guide/d-ouzhou-3600/","韩国\001http://www.tuniu.com/guide/d-hanguo-3904/","日本\001http://www.tuniu.com/guide/d-riben-3905/","台湾\001http://www.tuniu.com/guide/d-taiwan-2900/"};
		String[] anchorLinks={"韩国\001http://www.tuniu.com/guide/d-hanguo-3904/","日本\001http://www.tuniu.com/guide/d-riben-3905/","台湾\001http://www.tuniu.com/guide/d-taiwan-2900/","欧洲\001http://www.tuniu.com/guide/d-ouzhou-3600/"};
		String[] allAreaAttrs=null;
		for(int i=0;i<anchorLinks.length;i++)
		{
			anchorLink=anchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		   // subanchorLinks=wpw.getAnchorLinkByAttributeValueContaining(link, attrname, attrvalue,keytext,linkpat,anchorpat);
		  //  attstr=getAttractionStr(subanchorLinks);
		   // pw.println(anchor+"\001"+link+"\001"+attstr);
			
			if(isAreaLink(link))
			{
			  allAreaAttrs=getAreaAllAttractions(link);
			  for(int j=0;j<allAreaAttrs.length;j++)
			  {
			  	pw.println(anchor+"\001"+link+"\001"+allAreaAttrs[j].trim());
			  }
			}
			else
			{
				pw.println(anchor+"\001"+link);
			}
					
		}
   }
   
   /**
    * 序列化景点字符串
    * @param subanchorLinks
    * @return attstr
    */
   public String getAttractionStr(String[] subanchorLinks) 
   {
	   String attstr="";
	   String attname="";
	   String attrid="";
	   String anchorLink="";
	   String[] al_seg=null;
	   String anchor="";
	   String link="";
		
	   for(int i=0;i<subanchorLinks.length;i++)
	   {
			anchorLink=subanchorLinks[i];
			al_seg=anchorLink.split("\001");
			if(al_seg.length!=2)
			{
				continue;
			}
			anchor=al_seg[0].trim();
			link=al_seg[1].trim();
		    attstr=attstr+anchor+"|"+link+"\t";
	   }
	   attstr= attstr.trim();
	     
	   return attstr;
   }
   
   /**
    * 获取地区之间的映射关系
    * @param output_file
    * @throws Exception
    */
   public void getAreaMap(String output_file) throws Exception
   {
	    WebPageWrap wpw=new WebPageWrap();
		String url="http://www.tuniu.com/";
		String attrname="class";
		String attrvalue="i-mc fast-item-nopic";
		String keytext="天涯海角";
		String anchorpat="<a[^<>]*?href=\"http://www.tuniu.com/guide/d\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?\"\\s*target=\"_blank\"[^<>]*?>([^<>]*?)</a>";
		String[] areamap=wpw.getAnchorMapByAttributeValueContaining(url, attrname, attrvalue,keytext,anchorpat);
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		for(int i=0;i<areamap.length;i++)
		{
			pw.println(areamap[i]);
		}
		
		fw.close();
		pw.close();
		
   }
   
   public String[] getAreaAllAttractions(String area_url)
   {
	    WebPageWrap wpw=new WebPageWrap();
	    String[] allattr=null;
		ArrayList<String> attraction_list=new ArrayList<String>();
		
	    String std_area_url=area_url.replaceAll("#cat_[\\d]+", "");
	   // System.out.println("std_area_url:"+std_area_url);
	   
	    String first_jd_url=std_area_url+"jingdian";
		//String url="http://www.tuniu.com/guide/d-zhejiang-3400/jingdian";
		String jd_attrname="class";
		String jd_attrvalue="jingdian_li_pic";
		String jd_keytext="";
		String[] jd_tf_pats={"<[^<>]*?href=\"(http://www.tuniu.com/guide/v\\-([a-zA-Z]*?)\\-[\\d]+[/]?)\"\\s*target=\"_blank\"[^<>]*?>","<[^<>]*?href=\"http://www.tuniu.com/guide/v\\-([a-zA-Z]*?)\\-[\\d]+[/]?\"\\s*target=\"_blank\"[^<>]*?>","<[^<>]*?href=\"http://www.tuniu.com/guide/v\\-[a-zA-Z]*?\\-([\\d]+)[/]?\"\\s*target=\"_blank\"[^<>]*?>","<div\\s*class=\"jingdian_name\">(?s)\\s*<p>([^<>]*?)</p>"};
		
		System.out.println("first_jd_url:"+first_jd_url);
		String[] jd_areamap=wpw.getTextFiledsByAttributeValue(first_jd_url, jd_attrname, jd_attrvalue,jd_keytext,jd_tf_pats);
		for(int i=0;i<jd_areamap.length;i++)
		{
			//System.out.println(jd_areamap[i]);
			attraction_list.add(jd_areamap[i]);
		}
	   
		String next_attrname="class";
		String next_attrvalue="mb20";
		String next_keytext="";
		String[] next_tf_pats={"(共[\\d]+项.*?当前[\\d]+\\-[\\d]+项)"};
		
	
		
		String[] next_areamap=wpw.getTextFiledsByAttributeValue(first_jd_url, next_attrname, next_attrvalue,next_keytext,next_tf_pats);
		String next_info="";
		for(int i=0;i<next_areamap.length;i++)
		{
			if((next_areamap[i]!=null)&&(!(next_areamap[i].equals(""))))
			{
				next_info=next_areamap[i];
			}
		//	System.out.println(next_areamap[i]);
		}
		
	    int tot_jd_num=0;
	    int cra_jd_num=0;
	    String tot_jd_str="";
	    String cra_jd_str="";
	    
	    Pattern tot_jd_pat=Pattern.compile("共\\s*?([\\d]+)\\s*?项");
	    Matcher tot_jd_mat=tot_jd_pat.matcher(next_info);
	    if(tot_jd_mat.find())
	    {
	    	tot_jd_str=tot_jd_mat.group(1);
			if((tot_jd_str!=null)&&(!(tot_jd_str.equals(""))))
			{
				tot_jd_num=Integer.parseInt(tot_jd_str);
			}		
	    }
	    
	    Pattern cra_jd_pat=Pattern.compile("当前\\s*?[\\d]+\\s*?-\\s*?([\\d]+)\\s*?项");
	    Matcher cra_jd_mat=cra_jd_pat.matcher(next_info);
	    if(cra_jd_mat.find())
	    {
	    	cra_jd_str=cra_jd_mat.group(1);
			if((cra_jd_str!=null)&&(!(cra_jd_str.equals(""))))
			{
				cra_jd_num=Integer.parseInt(cra_jd_str);
			}		
	    }  
	   
	   // System.out.println("tot_jd_num:"+tot_jd_num);
	   // System.out.println("cra_jd_num:"+cra_jd_num);
	    
	    
	    int current_page=2;
	    String next_jd_url="";
	    while((cra_jd_num+1)<tot_jd_num)
	    {
	      	next_jd_url=first_jd_url+"/"+current_page+"/";
	      	System.out.println("next_jd_url:"+next_jd_url);
	      	jd_areamap=wpw.getTextFiledsByAttributeValue(next_jd_url, jd_attrname, jd_attrvalue,jd_keytext,jd_tf_pats);
			for(int i=0;i<jd_areamap.length;i++)
			{
			//	System.out.println(jd_areamap[i]);
				attraction_list.add(jd_areamap[i]);
			}
	      	
			next_areamap=wpw.getTextFiledsByAttributeValue(next_jd_url, next_attrname, next_attrvalue,next_keytext,next_tf_pats);
			next_info="";
			for(int i=0;i<next_areamap.length;i++)
			{
				if((next_areamap[i]!=null)&&(!(next_areamap[i].equals(""))))
				{
					next_info=next_areamap[i];
				}
				//System.out.println(next_areamap[i]);
			}
			
			
			tot_jd_mat=tot_jd_pat.matcher(next_info);
			if(tot_jd_mat.find())
			{
			    tot_jd_str=tot_jd_mat.group(1);
				if((tot_jd_str!=null)&&(!(tot_jd_str.equals(""))))
				{
					tot_jd_num=Integer.parseInt(tot_jd_str);
				}		
			}
			    
			cra_jd_mat=cra_jd_pat.matcher(next_info);
			if(cra_jd_mat.find())
			{
			   	cra_jd_str=cra_jd_mat.group(1);
			    if((cra_jd_str!=null)&&(!(cra_jd_str.equals(""))))
				{
						cra_jd_num=Integer.parseInt(cra_jd_str);
				}		
			}  
			
		    //System.out.println("tot_jd_num:"+tot_jd_num);
		  //  System.out.println("cra_jd_num:"+cra_jd_num);
		    current_page++;
	    }
	    
	    
	    allattr=new String[attraction_list.size()];
	    for(int i=0;i<attraction_list.size();i++)
	    {
	    	allattr[i]=attraction_list.get(i);
	    }
	    	    
	   return allattr;
   }
   
   
   public boolean isAreaLink(String link)
   {
	   boolean ial=false;
	   if(Pattern.matches("http://www.tuniu.com/guide/[d]\\-[a-zA-Z]*\\-[\\d]+[/]?(?:#cat_[\\d]+)?", link))
	   {
		   ial=true;
	   }   
	   return ial;
   }
   
   public static void main(String[] args) throws Exception
   {
	   TuNiuCrawler tnc=new TuNiuCrawler();
	   
	   /***************getAreaAttraction******************/
	   /*
	   String output_file="output/indrep/tuniu/areaAttraction_all.txt";
	   tnc.getAreaAttraction(output_file);
	   */
	   
	   
	   //output_file="output/indrep/tuniu/areaMap.txt";
	   //tnc.getAreaMap(output_file);
	   
	   
	   /***************getMaldivesAreaAttraction******************/	
	   /*
	   String output_file="output/indrep/tuniu/maldivesAreaAttraction_all.txt";
	   tnc.getMaldivesAreaAttraction(output_file);
	   */
	   
	   
	   /***************isAreaLink******************/	
	   /*
	   String link="http://www.tuniu.com/guide/d-haikou-902/";
	   System.out.println(tnc.isAreaLink(link));
	   */
	   
	   /***************getEuropeAreaAttraction******************/	
	   /*
	   String output_file="output/indrep/tuniu/europeAreaAttraction_all.txt";
	   tnc.getEuropeAreaAttraction(output_file);
	   */
	   
	   /***************getAmericaAreaAttraction*****************/
	   /*
	   String output_file="output/indrep/tuniu/americaAreaAttraction_all.txt";
	   tnc.getAmericaAreaAttraction(output_file);
	   */
	   
	   
	   /***************geKoreaJapanAreaAttraction***************/
	   /*
	   String output_file="output/indrep/tuniu/koreaJapanAreaAttraction_all.txt";
	   tnc.geKoreaJapanAreaAttraction(output_file);
	   */
	   
	   
	   /***************getSoutheastAsiaAreaAttraction***********/
	   /*
	   String output_file="output/indrep/tuniu/SoutheastAsiaAreaAttraction_all.txt";
	   tnc.getSoutheastAsiaAreaAttraction(output_file); 
	   */
	   
	   /***************getAustraliaAreaAttraction****************/
	   /*
	   String output_file="output/indrep/tuniu/AustraliaAreaAttraction_all.txt";
	   tnc.getAustraliaAreaAttraction(output_file); 
	   */
	   
	   /***************getForeignAreaAttraction******************/
	   String output_file="output/indrep/tuniu/ForeignAreaAttraction_all2.txt";
	   tnc.getForeignAreaAttraction(output_file);  
	   	  	   
   }
   
}
