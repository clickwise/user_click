package cn.clickwise.liqi.crawler.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import cn.clickwise.liqi.crawler.basic.BatchUrlCrawl;
import cn.clickwise.liqi.database.kv.KVDB;
import cn.clickwise.liqi.database.kv.KVDBFactory;
import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 抓取文件列表里所有host的meta信息
 * @author zkyz
 *
 */
public class HostMeta {
   
   public KVDB ssdb=null;
	
   public void load_config()
   {
		Properties prop=new Properties();
		prop.setProperty("ssdb_ip", "192.168.110.182");
		prop.setProperty("ssdb_port","8888");
		prop.setProperty("kvdb_type","ssdb");
		
		ssdb=KVDBFactory.create(prop);
   }
	
   public void selAndCrawl(String input_file,String output_file) throws Exception
   {	
		BatchUrlCrawl buc=new BatchUrlCrawl();
	    String[] url_arr=FileToArray.fileToDimArr(input_file);
		//for(int i=0;i<url_arr.length;i++)
		//{
		//	System.out.println(i+"  "+url_arr[i]);
		//}
	    int index=0;
	    ArrayList<String> small_list=null;
	    small_list=new  ArrayList<String>();
	    ArrayList<String> res_list=null;
	    
	    String temp_url="";
	    
	    for(int i=0;i<url_arr.length;i++)
	    {
	    	if(SSO.tnoe(url_arr[i])&&(url_arr[i].indexOf("null"))==-1)
	    	{
	            temp_url=url_arr[i].trim();
	           
	           //如果本地库里没有改host信息，则进行抓取
	           if(!(ssdb.exist(temp_url)))
	           {
	    	     small_list.add(url_arr[i].trim());
	    	     index++;
	           }
	    	}
	    	if(index%100==1)
	    	{
	    		res_list=buc.getTitleBat(small_list,false,null);
	    		FileWriterUtil.writeArrayList(res_list, output_file, true);
	    		ssdb.mulset(res_list);
	    		small_list=new  ArrayList<String>();		  
	    	}  	
	    }
	    FileWriterUtil.writeArrayList(res_list, output_file, true);
		ssdb.mulset(res_list);
   }

   /**
    * 从文件更新本地host库
    * @param input_file
    * @throws Exception
    */
   public void updateHostDBFromFile(String input_file) throws Exception
   {
	   ssdb.mulsetfromfile(input_file);
   }
   
   
   public void genMetaText(String input_file,String output_file) throws Exception
   {
	   
	    String[] url_arr=FileToArray.fileToDimArr(input_file);
	    String title="";
	    String temp_url="";
	    ArrayList<String> selList=new ArrayList<String>();
	    String[] seg_arr=null;
	    String t="";
	    for(int i=0;i<url_arr.length;i++)
	    {
	    	if(SSO.tnoe(url_arr[i])&&(url_arr[i].indexOf("null"))==-1)
	    	{
	    	    temp_url=url_arr[i].trim();
	    	    if(!(SSO.tnoe(temp_url)))
	    	    {
	    	    	continue;
	    	    }
	    	    title=ssdb.get(temp_url);
	    	    if((SSO.tnoe(title))&&(title.indexOf("Error")==-1)&&(title.indexOf("get null")==-1)&&(title.indexOf("Welcome to")==-1))
	    	    {
	    	    	seg_arr=title.split("\001");
	    	    	if(seg_arr.length<1)
	    	    	{
	    	    		continue;
	    	    	}
	    	    	t=seg_arr[0].trim();
	    	    	if(SSO.tnoe(t))
	    	    	{
	    	    	 selList.add(temp_url+"\001"+t);
	    	    	}
	    	    }
	    	} 	
	   }
	    
	    FileWriterUtil.writeArrayList(selList, output_file, true);
	    
	    
   }
   
   public static void main(String[] args) throws Exception
   {
	   /*
	   String input_file="D:/projects/hosts/20140402/host_text.txt";
	   HostMeta hm=new HostMeta();
	   hm.load_config();
	   hm.updateHostDBFromFile(input_file);
	   */
	   /*
	   HostMeta hm=new HostMeta();
	   hm.load_config();
	   String input_file="D:/projects/hosts/20140402/host_top.txt";
	   String output_file="D:/projects/hosts/20140402/host_text.txt";
	   hm.selAndCrawl(input_file, output_file);
	   */
	
	   HostMeta hm=new HostMeta();
	   hm.load_config();
	   String input_file="D:/projects/hosts/20140402/host_top.txt";
	   String output_file="D:/projects/hosts/20140402/host_ot.txt";
	   hm.genMetaText(input_file, output_file);
	 
   }
	
}
