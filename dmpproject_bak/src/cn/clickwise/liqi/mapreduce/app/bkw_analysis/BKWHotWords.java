package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import redis.clients.jedis.Jedis;

/*
 * 获取热门的电影,电视剧,综艺
 */
public class BKWHotWords {

    public Jedis jedis;
	public BKWHotWords()
	{
		super();
	}
	
	/*
	 * 对一个关键词进行初步的类别预测
	 */
	public void predict_all(String date,String keyword_urls_dir,String keyword_all_predict_dir) throws Exception
	{		
		File kud=new File(keyword_urls_dir);
		File[] kud_files=kud.listFiles();
		String kud_line="";
		String keyword="";
		int uvs=0;
		String urls_info="";
		String[] kud_seg=null;
		Vector urls_vec=null;
		Vector urls_top_vec=null;
		Vector trueurls_top_vec=null;
		String temp_url_info="";
		File tturl_file=new File("temp/all_"+date+".txt");
		FileWriter tturl_fw=new FileWriter(tturl_file);
		PrintWriter tturl_pw=new PrintWriter(tturl_fw);
		
		
		File novel_par=new File(keyword_all_predict_dir);
		if(!novel_par.exists())
		{
			novel_par.mkdirs();
		}
		File novel_output_file=new File(keyword_all_predict_dir+"/novel_"+date+".txt");

		FileWriter novel_fw=new FileWriter(novel_output_file);
		PrintWriter novel_pw=new PrintWriter(novel_fw);
		
		File video_url_file=new File(keyword_all_predict_dir+"/video_url"+date+".txt");
		FileWriter video_url_fw=new FileWriter(video_url_file);
		PrintWriter video_url_pw=new PrintWriter(video_url_fw);
		
		File video_output_file=new File(keyword_all_predict_dir+"/video_"+date+".txt");
		FileWriter video_fw=new FileWriter(video_output_file);
		PrintWriter video_pw=new PrintWriter(video_fw);
		
		
		String sturl="";
		String sthost="";
		URI uri=null;
		
	    jedis = new Jedis("192.168.110.180",6379);//redis服务器地址
	    jedis.ping();
	    jedis.select(8);
	    
	    String stcate="";
	    Vector video_raw=new Vector();      
		for(int i=0;i<kud_files.length;i++)
		{
			FileReader fr=new FileReader(kud_files[i]);
			BufferedReader br=new BufferedReader(fr);
			
			while((kud_line=br.readLine())!=null)
			{//遍历所有的关键字
			   kud_seg=kud_line.split("\001");
			   
			   if(kud_seg.length<3)
			   {
				   continue;
			   }
			   urls_vec=new Vector();
			   keyword=kud_seg[0].trim();
			   uvs=Integer.parseInt(kud_seg[1].trim());
			   
			   for(int j=2;j<kud_seg.length;j++)
			   {
				   temp_url_info=kud_seg[j].trim();
				   if(!(temp_url_info.equals("")))
				   {
					   urls_vec.add(temp_url_info);
				   }
			   }
			   
			   urls_top_vec=rankVector(urls_vec,10);
			   for(int ti=0;ti<urls_top_vec.size();ti++)
			   {
				   //System.out.println(urls_top_vec.get(ti));
			   }
			   trueurls_top_vec=getTrueUrls(urls_top_vec);
			   System.out.print(keyword+":  ");
			   tturl_pw.print(keyword+"\001");
			   
			   for(int ti=0;ti<trueurls_top_vec.size();ti++)
			   {
				   sturl=trueurls_top_vec.get(ti)+"";
				   sturl=sturl.trim();
				   tturl_pw.print(sturl+" ");
			   }
			   
			   stcate=predict_preliminar(trueurls_top_vec);
			   System.out.print(stcate+"  ");
			   System.out.print(isNovel(keyword,stcate)+" ");
			   if(isNovel(keyword,stcate))
			   {
				   novel_pw.print(keyword+"\001");
				   for(int ni=0;ni<trueurls_top_vec.size();ni++)
				   {
					   sturl=trueurls_top_vec.get(ni)+"";
					   sturl=sturl.trim();
					   novel_pw.print(sturl+" ");
				   }
				   novel_pw.println();
			   }
			   
			   if(isVideo(keyword,stcate))
			   {
				   video_raw.add(keyword);
				   video_url_pw.print(keyword+"\001");
				   for(int ni=0;ni<trueurls_top_vec.size();ni++)
				   {
					   sturl=trueurls_top_vec.get(ni)+"";
					   sturl=sturl.trim();
					   video_url_pw.print(sturl+" ");
				   }
				   video_url_pw.println();
			   }
			   
			   System.out.print(isVideo(keyword,stcate)+" ");			   
			   System.out.println();
			   tturl_pw.println();
			   //System.out.println();
			}//遍历所有的关键字						
		}
		
		novel_fw.close();
		novel_pw.close();
		video_url_fw.close();
		video_url_pw.close();	
		
		for(int i=0;i<video_raw.size();i++)
		{
			video_pw.println(video_raw.get(i));
		}
		
		video_fw.close();
		video_pw.close();		
	}
	
	
	public  Vector rankVector(Vector urls_ips,int topN)
	{
		Vector nv=new Vector();
		int minindex=-1;
        String tempS="";
        String minword="";
        String tempword="";
        
        String temp_ips_url="";
        String[] seg_arr=null;
        
        if(urls_ips.size()<topN)
        {
        	return urls_ips;
        }
		for(int i=0;i<topN;i++)
		{
			if(i==(urls_ips.size()-2))
			{
				break;
			}
			tempS=urls_ips.get(i)+"";
			tempS=tempS.trim();
			seg_arr=tempS.split("\t");
		    if(seg_arr.length<2)
		    {
		    	continue;
		    }
		    minword=seg_arr[1];
			minindex=i;
			for(int j=i;j<urls_ips.size();j++)
			{
				tempS=urls_ips.get(j)+"";
				tempS=tempS.trim();
				seg_arr=tempS.split("\t");
				if(seg_arr.length<2)
				{
					continue;
				}
				tempword=seg_arr[1];
				if(Integer.parseInt(tempword)>Integer.parseInt(minword))
				{
					minindex=j;
					minword=tempword;
				}				
			}
			temp_ips_url=urls_ips.get(i)+"";
			urls_ips.set(i, urls_ips.get(minindex)+"");
			nv.add(urls_ips.get(minindex));
			urls_ips.set(minindex, temp_ips_url);			
		}
					
		return nv;
	}
	
	
	public Vector getTrueUrls(Vector urls_top_vec)
	{
		Vector nv=new Vector();
		String codeurl="";
		String turl="";
		String[] seg_arr=null;
		String ourl="";
		for(int i=0;i<urls_top_vec.size();i++)
		{
			codeurl=urls_top_vec.get(i)+"";
			codeurl=codeurl.trim();
			seg_arr=codeurl.split("\t");
			if(seg_arr.length<2)
			{
				continue;
			}
			ourl=seg_arr[0].trim();
			turl=getRedirect(ourl);
			nv.add(turl);
		}		
		return nv;
	}
	
	
	   private String getRedirect(String code_url) {  
		    String red_url="";
	        DefaultHttpClient httpclient = null;  
	        code_url=code_url.trim(); 
	        String url="";
	        if(code_url.indexOf("http://")==-1)
	        {
	          url = "http://"+code_url;  
	        }
	        else{
	          url=code_url;	
	        }
	        try {  
	            httpclient = new DefaultHttpClient();  
	          //  HttpHost proxy =new HttpHost("122.72.11.131", 80, "http");
	          //  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	            httpclient.setRedirectStrategy(new RedirectStrategy() { //设置重定向处理方式  
	  
	                @Override  
	                public boolean isRedirected(HttpRequest arg0,  
	                        HttpResponse arg1, HttpContext arg2)  
	                        throws ProtocolException {  
	  
	                    return false;  
	                }  
	  
	                @Override  
	                public HttpUriRequest getRedirect(HttpRequest arg0,  
	                        HttpResponse arg1, HttpContext arg2)  
	                        throws ProtocolException {  
	  
	                    return null;  
	                }  
	            });  
	  
	            // 创建httpget.  
	            HttpGet httpget = new HttpGet(url);  
	            // 执行get请求.  
	            HttpResponse response = httpclient.execute(httpget);  
	  
	            int statusCode = response.getStatusLine().getStatusCode();  
	            if (statusCode == HttpStatus.SC_OK) {  
	                // 获取响应实体  
	                HttpEntity entity = response.getEntity();  
	                if (entity != null) {  
	                    // 打印响应内容长度  
	                  //  System.out.println("Response content length: "  
	                   //         + entity.getContentLength());  
	                    // 打印响应内容  
	                  //  System.out.println("Response content: "  
	                   //         + EntityUtils.toString(entity));  
	                }  
	            } else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY  
	                    || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {  
	                  
	               // System.out.println("当前页面发生重定向了---");  
	                  
	                Header[] headers = response.getHeaders("Location");  
	                if(headers!=null && headers.length>0){  
	                    String redirectUrl = headers[0].getValue(); 
	                    red_url= redirectUrl;
	                  //  System.out.println("重定向的URL:"+redirectUrl);  
	                    /*  
	                    redirectUrl = redirectUrl.replace(" ", "%20");  
	                    get(redirectUrl);
	                    */  
	                }  
	            }  
	  
	        } catch (ClientProtocolException e) {  
	            e.printStackTrace();  
	        } catch (ParseException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } finally {  
	            // 关闭连接,释放资源  
	            httpclient.getConnectionManager().shutdown();  
	        }
	        
	        return red_url;
	    }
	
    public String predict_preliminar(Vector trueurls_top_vec) throws Exception
	{
	   String preli_cate="NA";
	   URI sturi=null;
	   String sturl="";
	   String sthost="";
	   Hashtable tag_res=new Hashtable();
	   String cate_res="";
	   int old_res_c=0;
	   for(int i=0;i<trueurls_top_vec.size();i++)
	   {
		   sturl=trueurls_top_vec.get(i)+"";
		   sturl=sturl.trim();
		   try{
			   sturi=new URI(sturl);
			   sthost=sturi.getHost();
			   }
			   catch(Exception e){}
		
		   if(sthost==null)
		   {
			   continue;
		   }
		   
		   if(sthost.equals(""))
		   {
			   continue;
		   }
		   sthost=sthost.trim();
		   cate_res=jedis.get(sthost);
		   if(cate_res==null)
		   {
			   continue;
		   }
		   cate_res=cate_res.trim();
		   if(!tag_res.containsKey(cate_res))
		   {
			   tag_res.put(cate_res, 1);
		   }
		   else
		   {
			   old_res_c=Integer.parseInt(tag_res.get(cate_res)+"");
			   old_res_c++;
			   tag_res.remove(cate_res);
			   tag_res.put(cate_res, old_res_c);
		   }
	   }
	   
	   Enumeration tag_keys=tag_res.keys();
	   String maxTag="NA";
	   int maxTagCount=0;
	   String temp_key="";
	   int  temp_count=0;
	   while(tag_keys.hasMoreElements())
	   {
		   temp_key=tag_keys.nextElement()+"";
		   //System.out.println("temp_key:"+temp_key);
		   temp_count=Integer.parseInt(tag_res.get(temp_key)+"");
		   if(temp_count>maxTagCount)
		   {
			   maxTagCount=temp_count;
			   maxTag=temp_key;
		   }
	   }
	   
	   
	   
	   if(maxTag.equals("-1"))
	   {
		  preli_cate="NA";
	   }
	   else
	   {
	   preli_cate=maxTag;
	   }
	   return preli_cate;
	}
    
    public boolean isNovel(String word,String preli_cate)
    {
    	boolean isnov=false;
        String tag=preli_cate;
        if(tag.equals("小说")&&word.length()>1&&!Pattern.matches(".*网", word)&&!Pattern.matches(".*?文章.*", word)&&!Pattern.matches(".*?全本.*", word)&&!Pattern.matches(".*?小说.*", word)&&!Pattern.matches(".*?电子书.*", word)&&!Pattern.matches(".*?评书.*", word)&&!Pattern.matches(".*?书院.*", word)&&!Pattern.matches(".*?>书院.*", word)&&!Pattern.matches(".*?动漫.*", word)&&!Pattern.matches(".*?看书.*", word)&&!Pattern.matches("[a-zA-Z0-9]*", word)&&!Pattern.matches(".*?翻译.*", word)&&!Pattern.matches(".*?经典.*", word)&&!Pattern.matches(".*?日志.*", word)&&!Pattern.matches(".*?文学.*", word)&&!Pattern.matches(".*?短语.*", word)&&!Pattern.matches(".*?名言.*", word)&&!Pattern.matches(".*?红袖添香.*", word)&&!Pattern.matches(".*?句子.*",word))
         {
        	isnov=true;
         }
    	
    	return isnov;
    } 
    
    public boolean isVideo(String word,String preli_cate)
    {
    	boolean isvid=false;
    	preli_cate=preli_cate.trim();
    	if(preli_cate.equals("视频"))
    	{
    		isvid=true;
    	}
    	return isvid;
    }
    
    public String getBaiduCorpus(String keyword)
    {
    	String bcontent="";
    	
    	
    	
    	
    	
    	return bcontent;
    }
	public static void main(String[] args) throws Exception
	{
		if(args.length!=3)
		{
			System.out.println("输入参数: <日期> <输入文件夹> <输出文件夹>");
			System.exit(1);
		}
		
		String date_s=args[0];
		String input_dir=args[1];
		String output_dir=args[2];
										
		BKWHotWords bkwhw=new BKWHotWords();	
		bkwhw.predict_all(date_s,input_dir, output_dir);		
	}
	
}
