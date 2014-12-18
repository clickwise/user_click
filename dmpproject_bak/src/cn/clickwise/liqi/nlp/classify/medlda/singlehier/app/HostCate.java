package cn.clickwise.liqi.nlp.classify.medlda.singlehier.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import cn.clickwise.liqi.crawler.basic.MetaInfoCrawl;
import cn.clickwise.liqi.crawler.basic.WebPageWrap;
import cn.clickwise.liqi.nlp.classify.basic.ModelClassify;
import cn.clickwise.liqi.nlp.classify.basic.ModelClassifyFactory;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;

import redis.clients.jedis.Jedis;

/**
 * 对host按照流量从大到小进行分析，
 * 1.查找host是否在已分类host列表
 * 2.查找host是否在crawl_ban列表
 * 3.对host进行抓取
 * @author zkyz
 *
 */
public class HostCate {
	
	private Jedis redis_ch=null;
	private String redis_ch_ip=null;
	private int redis_ch_port=0;
	private int redis_ch_db=0;
	private WebPageWrap wpw=null;
	private ModelClassify classifier=null;
	
    public void load_config(Properties prop) 
    {
    	redis_ch_ip=prop.getProperty("redis_ch_ip");
    	redis_ch_port=Integer.parseInt(prop.getProperty("redis_ch_port"));
    	redis_ch_db=Integer.parseInt(prop.getProperty("redis_ch_db"));
    	redis_ch = new Jedis(redis_ch_ip, redis_ch_port, 100000);
    	redis_ch.ping(); 
    	redis_ch.select(redis_ch_db);	
    	wpw=new WebPageWrap();
    	
        classifier=ModelClassifyFactory.create(prop);
    }
	
    /**
     * 查找topN 某类别的host
     * @param input_file
     * @param output_file
     * @param topN
     * @param cate
     * @throws Exception
     */
	public void hostCateTop(String input_file,String output_file,int topN,String cate) throws Exception
	{
		 // updateTopHosts(input_file,20000);
		  FileReader fr=new FileReader(new File(input_file));
		  BufferedReader br=new BufferedReader(fr);
		  String line="";
		  FileWriter fw=new FileWriter(new File(output_file));
		  PrintWriter pw=new PrintWriter(fw);
		  
		  String[] seg_arr=null;
		  String host="";
		  String pv="";

		  String rcate="";
		  cate=cate.trim();
		  int n=0;
		  String source="";
		  while((line=br.readLine())!=null)
		  {
			  line=line.trim();
              if(!(SSO.tnoe(line)))
              {
            	  continue;
              }
              seg_arr=line.split("\001");
              if(seg_arr.length!=2)
              {
            	  continue;
              }
              
              host=seg_arr[0].trim();
              pv=seg_arr[1].trim();
              
              if(!(SSO.tnoe(host)))
              {
            	  continue;
              }
              
              if(!(SSO.tnoe(pv)))
              {
            	  continue;
              }
              
              rcate=redis_ch.get(host);
              if(SSO.tnoe(rcate))
              {
                  rcate=rcate.trim();
                  if(rcate.equals(cate))
                  {
                	  n++;
                	  if(n>topN)
                	  {
                		  break;
                	  }
                	  System.out.println(line);
                	  pw.println(line);
                  }  
              }
              else
              {
            	  //source=wpw.getSource(host);
            	  //if(SSO.tnoe(source))
            	  //{
            		//  System.out.println(host+":"+source);
            	 // }
              }
    
		  }
		  fw.close();
		  pw.close();
		  br.close();
		  fr.close();
	  	
	  	
	}
	
	  /**
     * 查找topN 某类别的host
     * @param input_file
     * @param output_file
     * @param topN
     * @param cate
     * @throws Exception
     */
	public void hostCateOnlyHost(String input_file,String output_file,int topN,String cate) throws Exception
	{
		 // updateTopHosts(input_file,20000);
		  FileReader fr=new FileReader(new File(input_file));
		  BufferedReader br=new BufferedReader(fr);
		  String line="";
		  FileWriter fw=new FileWriter(new File(output_file));
		  PrintWriter pw=new PrintWriter(fw);
		  
		  String[] seg_arr=null;
		  String host="";


		  String rcate="";
		  cate=cate.trim();
		  int n=0;
		  String source="";
		  while((line=br.readLine())!=null)
		  {
			  line=line.trim();
              if(!(SSO.tnoe(line)))
              {
            	  continue;
              }

              
              host=line;
              
              if(!(SSO.tnoe(host)))
              {
            	  continue;
              }
              
         
              rcate=redis_ch.get(host);
              if(SSO.tnoe(rcate))
              {
                  rcate=rcate.trim();
                  if(rcate.equals(cate))
                  {
                	  n++;
                	  if(n>topN)
                	  {
                		  break;
                	  }
                	  System.out.println(line);
                	  pw.println("http://"+line);
                  }  
              }
              else
              {
            	  //source=wpw.getSource(host);
            	  //if(SSO.tnoe(source))
            	  //{
            		//  System.out.println(host+":"+source);
            	 // }
              }
    
		  }
		  fw.close();
		  pw.close();
		  br.close();
		  fr.close();
	  	
	  	
	}
	
	/**
	 * 更新input_file topN host ,若没有分类，则抓取进行分类，并存入redis
	 * @param input_file
	 * @param N
	 */
	public void updateTopHosts(String input_file,int N) throws Exception
	{
		String line="";
		String[] seg_arr=null;
		String host="";
		String pv="";
		MetaInfoCrawl pc=new MetaInfoCrawl();
		/*

		FileReader fr=new FileReader(input_file);
		BufferedReader br=new BufferedReader(fr);
		String temp_file="../temp/hosts/20140225/temp_host.txt";
		
		FileWriter fw=new FileWriter(new File(temp_file));
		PrintWriter pw=new PrintWriter(fw);
		

		  
		int c=0;
		while((line=br.readLine())!=null)
		{
			  line=line.trim();
              if(!(SSO.tnoe(line)))
              {
            	  continue;
              }
              seg_arr=line.split("\001");
              if(seg_arr.length!=2)
              {
            	  continue;
              }
              host=seg_arr[0].trim();
              if(!(SSO.tnoe(host)))
              {
            	  continue;
              }
              c++;
              if(c>N)
              {
            	  break;
              }
              if(!(redis_ch.exists(host)))
              {
            	  pw.println(host);
              }         
		}
		
		fr.close();
		br.close();
		fw.close();
		pw.close();
		*/
		String output_file="D:/projects/hosts/20140402/host_ot.txt";
		//pc.getTitleBat(temp_file, output_file);
		

		FileReader fr_un=new FileReader(output_file);
		BufferedReader br_un=new BufferedReader(fr_un);
		String host_un="";
		String host_info="";
		
		String cate="";
		while((line=br_un.readLine())!=null)
		{
			  line=line.trim();
              if(!(SSO.tnoe(line)))
              {
            	  continue;
              }
              seg_arr=line.split("\001");
              if(seg_arr.length<2)
              {
            	  continue;
              }
              
              host_un=seg_arr[0].trim();
              if(redis_ch.exists(host_un))
              {
            	  continue;
              }
              host_info="";
              for(int j=1;j<seg_arr.length;j++)
              {
            	  host_info=host_info+seg_arr[j]+" ";
              }
              host_info= host_info.trim();
              cate=classifier.predictFromPlainText(host_info);
              System.out.println("cate:"+cate);
              if(!(SSO.tnoe(cate)))
              {
            	  continue;
              }
              cate=cate.trim();
              if((!(redis_ch.exists(host_un)))&&(!(cate.equals("NA")))&&(!(cate.equals("-1"))))
              {
            	  System.out.println("set redis:"+host_un+" "+cate);
            	  redis_ch.set(host_un, cate);
              }
              
		}
		
		fr_un.close();
		br_un.close();
			
	}
	
	public static void main(String[] args) throws Exception
	{
		String config_file="config/hosts/host_cate_classify.properties";
	    Properties prop = null;
		prop=ConfigFileReader.getPropertiesFromFile(config_file);
		
		/*
		HostCate hc=new HostCate();
		hc.load_config(prop);
		String input_file="D:/projects/hosts/20140402/host_ot.txt";
		hc.updateTopHosts(input_file, 10);
		*/
		
		HostCate hc=new HostCate();
		String input_file="D:/projects/spread_data/youxi/data0629/dt0629_top500_youxihost.txt";
		String output_file="D:/projects/spread_data/youxi/data0629/novel.txt";
		int topN=1000;
		String cate="小说";
		hc.load_config(prop);
		hc.hostCateOnlyHost(input_file, output_file, topN, cate);	
		
	}
	
}









