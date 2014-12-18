package cn.clickwise.liqi.nlp.classify.svm.singlehier.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import cn.clickwise.liqi.crawler.basic.FilterContent;
import cn.clickwise.liqi.crawler.basic.SingleUrlCrawl;
import cn.clickwise.liqi.file.utils.FileReaderUtil;
import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 训练推广数据的分类器
 * @author zkyz
 *
 */
public class SpreadClassifer {

   /**
	 * 生成自媒体的分类样本 
	 * @param samp_dir
	 */
   public void gen_zimeiti(String[] samp_dirs,String output_file) throws Exception
   {
	   File[] all_files=null;
	   File coffee_file=null;
	   File mocha_file=null;
	   File mugua_file=null;
	   File p57_file=null;
	   File sifu_file=null;
	   File raw_file=null;
	   File sifu_raw_file=null;
	   HashMap<String,String> cmmp_hm=new HashMap<String,String>();
	   String[] temp_arr=null;
	   String temp_url="";
	   for(int i=0;i<samp_dirs.length;i++)
	   {
		   System.out.println(samp_dirs[i]);
		   File samp_dir=new File(samp_dirs[i]);
		   all_files=samp_dir.listFiles();
		  // cmmp_hm=new HashMap<String,String>();
           for(int j=0;j<all_files.length;j++)
           {
        	    if((all_files[j].getName()).equals("coffee.txt"))
        	    {
        	    	coffee_file=all_files[j];
        	    	temp_arr=FileToArray.fileToDimArr(coffee_file);
        	    	
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
        	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "coffee");
        	    		}
        	    	}
        	    }
        	    else if((all_files[j].getName()).equals("mocha.txt"))
        	    {
        	    	mocha_file=all_files[j];	
        	    	temp_arr=FileToArray.fileToDimArr(mocha_file);
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
        	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "mocha");
        	    		}
        	    	}
        	    }
        	    else if((all_files[j].getName()).equals("mugua.txt"))
        	    {
        	    	mugua_file=all_files[j];	 
        	    	temp_arr=FileToArray.fileToDimArr(mugua_file);
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
           	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "mugua");
        	    		}
        	    	}
        	    }
        	    else if((all_files[j].getName()).equals("p57.txt"))
        	    {
        	    	p57_file=all_files[j];	  
        	    	temp_arr=FileToArray.fileToDimArr(p57_file);
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
           	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "p57");
        	    		}
        	    	}
        	    }
        	   
        	    else if((all_files[j].getName()).equals("sifu.txt"))
        	    {
        	    	sifu_file=all_files[j];
        	    	temp_arr=FileToArray.fileToDimArr(sifu_file);
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
           	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "sifu");
        	    		}
        	    	}
        	    }
        	   
        	    else if(Pattern.matches("url_[\\d]+\\.2\\.txt",(all_files[j].getName())))
        	    {
        	    	raw_file=all_files[j];	    	
           	    	temp_arr=FileToArray.fileToDimArr(raw_file);
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
           	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "noise");
        	    		}
        	    	} 	
        	    }
        	
        	    else if(Pattern.matches("sifu_[\\d]+_1\\.txt",(all_files[j].getName())))
        	    {
        	    	sifu_raw_file=all_files[j];	    	
           	    	temp_arr=FileToArray.fileToDimArr(sifu_raw_file);
        	    	for(int k=0;k<temp_arr.length;k++)
        	    	{
           	    		temp_url=format_url(temp_arr[k]);
        	    		temp_url=temp_url.trim();	
        	    		if(!(cmmp_hm.containsKey(temp_url)))
        	    		{
        	    			cmmp_hm.put(temp_url, "noise");
        	    		}
        	    	} 	
        	    }
        	 
        	    
           }
           
    	   System.out.println("coffee_file:"+coffee_file.getName());
    	   System.out.println("mocha_file:"+mocha_file.getName());
    	   System.out.println("mugua_file:"+mugua_file.getName());
    	   System.out.println("p57_file:"+p57_file.getName());
    	   //System.out.println("sifu_file:"+sifu_file.getName());
    	   System.out.println("raw_file:"+raw_file.getName());
    	  // System.out.println("sifu_raw_file:"+sifu_raw_file.getName()); 
	   }
	   
	   Iterator it=cmmp_hm.entrySet().iterator();
	   while(it.hasNext())
	   {
		   Map.Entry entry=(Map.Entry)it.next();
		   Object key=entry.getKey();
		   Object val=entry.getValue();
	   }
	   FileWriterUtil.writeHashMapUniq(cmmp_hm, output_file);
   }
   
	public static String format_url(String url)
	{
		String fu="";
		if(!(SSO.tnoe(url)))
		{
			return "";
		}
		url=url.trim();
		if((url.indexOf("http://"))>-1)
		{
			return url;
		}
		
		fu="http://"+url;
		
		//System.out.println("fu:"+fu);
		fu=fu.trim();			
		return fu;
	}
	
   public void gen_train_file(String tag_file,String text_file,String output_file) throws Exception
   {
	   HashMap<String,String> url_label=FileReaderUtil.getHashFromPlainFile(tag_file);
	   FileWriter fw=new FileWriter(new File(output_file));
	   PrintWriter pw=new PrintWriter(fw);
	   
	   FileReader fr=new FileReader(new File(text_file));
	   BufferedReader br=new BufferedReader(fr);
	   
	   String line="";
	   String[] seg_arr=null;
	   String url="";
	   String text="";
	   String label="";
	  HashMap<String,String> url_hash=new HashMap<String,String>();
	   while((line=br.readLine())!=null)
	   {
		   line=line.trim();
				   
		   seg_arr=line.split("\\s+");
		   if(seg_arr.length<2)
		   {
			   continue;
		   }
		   url=seg_arr[0].trim();
		   text="";
		   for(int j=1;j<seg_arr.length;j++)
		   {
			 text=text+seg_arr[j]+" ";   
		   }
		   text=text.trim();
		   if(!(SSO.tnoe(url)))
		   {
			   continue;
		   }
		   if(!(SSO.tnoe(text)))
		   {
			   continue;
		   }
		   label=url_label.get(url);
		   if(!(SSO.tnoe(label)))
		   {
			   continue;
		   }
		   text=text.replaceAll("\001", "");
		   text=FilterContent.getFilterContent(text);
		   if(!(url_hash.containsKey(url)))
		   {
		   pw.println(label+"\001"+text);
		   url_hash.put(url, "1");
		   }		   
	   }
	   
	   fr.close();
	   br.close();
	   fw.close();
	   pw.close();
	   
   }
   

	
	public static void main(String[] args) throws Exception
	{
		
		
		SpreadClassifer sc=new SpreadClassifer();
	    /*
		String[] samp_dirs={"D:/projects/spread_data/zimeiti/data0225","D:/projects/spread_data/zimeiti/data0304","D:/projects/spread_data/zimeiti/zimeiti_0105","D:/projects/spread_data/zimeiti/zimeiti_0211","D:/projects/spread_data/zimeiti/zimeiti_0218","D:/projects/spread_data/zimeiti/data0311"};
		String output_file="D:/projects/spread_data/zimeiti/csamp.txt";
		sc.gen_zimeiti(samp_dirs,output_file);
		*/
		
		String tag_file="D:/projects/spread_data/zimeiti/csamp.txt";
		String text_file="D:/projects/spread_data/zimeiti/crawl_result.txt";
		String output_file="D:/projects/spread_data/zimeiti/csamp_train3.txt";
		sc.gen_train_file(tag_file, text_file, output_file);
		
	}
	
	
}
