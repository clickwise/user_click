package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.daguu.lib.httpsqs4j.Httpsqs4j;
import com.daguu.lib.httpsqs4j.HttpsqsClient;
import com.daguu.lib.httpsqs4j.HttpsqsStatus;

import redis.clients.jedis.Jedis;
import com.udpwork.ssdb.*;

/**
 * 从httpsqs 读取未处理的搜索词进行分析，并将分析结果存入redis中
 * @author lq
 */


public class SWAOfflineRed {

	public Jedis cw_jedis;
	public String redis_cw_ip = "";
	public int redis_port = 6379;
	public int redis_cw_db = 0;
	
	public Jedis host_jedis;
	public String redis_host_ip = "";
	public int redis_host_port = 6379;
	public int redis_host_db = 0;
	
    public SWADetectWordRed swadw=null;
	public HttpsqsClient sqsclient=null;
	public HttpsqsStatus sqsstatus=null;
	public Hashtable<String,Integer> stop_words=null;
    public SSDB ssdb = null;
    public Response resp;
	
	public void load_local_config() throws Exception
	{
		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名
		
	    String sqs_ip=""; 
	    int sqs_port=0;
	     
	    sqs_port=1218;
	    
	    
		if (address.equals("adt2")) {
			redis_cw_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			redis_cw_ip = "192.168.110.181";
			sqs_ip="192.168.110.181";
		} else if (address.equals("adt6")) {
			redis_cw_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			redis_cw_ip = "192.168.110.188";
		} else if (address.equals("adt0")) {
			redis_cw_ip = "192.168.110.180";
		} else if (address.equals("hndx_fx_100")) {
			redis_cw_ip = "192.168.1.100";
		}
		
		redis_port = 6379;
		redis_cw_db = 10;
		cw_jedis = new Jedis(redis_cw_ip, redis_port, 100000);// redis服务器地址
		cw_jedis.ping();
		cw_jedis.select(redis_cw_db);	
		
		
		
		
		swadw=new SWADetectWordRed();	
		swadw.load_local_config();
		swadw.set_cw_jedis(cw_jedis);
		
		Httpsqs4j.setConnectionInfo(sqs_ip, sqs_port, "UTF-8");
		sqsclient = Httpsqs4j.createNewClient();
		sqsstatus = sqsclient.getStatus("bd_sw");
		
		redis_host_port = 6379;
		redis_host_db = 5;
		if (address.equals("adt1")) {
			redis_host_ip = "192.168.110.182";
		}  else if (address.equals("hndx_fx_100")) {
			redis_host_ip = "192.168.1.100";
		}
		
		host_jedis = new Jedis(redis_host_ip, redis_host_port, 100000);// redis服务器地址
		host_jedis.ping();
		host_jedis.select(redis_host_db);
	
		String stw_dir="stop_words_dir";
		load_stop_words(stw_dir);
		swadw.setStopWords(stop_words);	
		
	    ssdb = new SSDB("127.0.0.1", 8888);
		
	}
	
	
	public void load_stop_words(String stop_word_dir) throws Exception
	{
		File stw_dir_file=new File(stop_word_dir);
		File[] stw_files=stw_dir_file.listFiles();

		String line="";
		String word="";
	
		stop_words=new Hashtable<String,Integer>();
		
		for(int i=0;i<stw_files.length;i++)
		{
			FileReader fr=new FileReader(stw_files[i]);
			BufferedReader br=new BufferedReader(fr);
			while((line=br.readLine())!=null)
			{
			  word=line.trim();
	          if((word==null)||(word.equals("")))
	          {
	        	  continue;
	          }
	          if(!(stop_words.containsKey(word)))
	          {
	        	  stop_words.put(word, 1);
	          }
			}
			br.close();
			fr.close();			
		}
		
		Enumeration stop_enum=stop_words.keys();
		String stop_key="";
		while(stop_enum.hasMoreElements())
		{
			stop_key=stop_enum.nextElement()+"";
			//System.out.println(stop_key);		
		}
		
	}
	
	public long getAndAnalysis()
	{
		long unAnaNum=0;
		String one_sw="";
		try{
		one_sw=sqsclient.getString("bd_sw");
		}
		catch(Exception e){}
		
		if((one_sw==null)||((one_sw.trim()).equals("")))
		{
			return left_bdsw_num();
		}
		one_sw=one_sw.trim();
		//System.out.println("one_sw:"+one_sw);
		String res = "";
		int limit=8;
			 
		res=swadw.one_sw_process(one_sw,limit);
		//System.out.println("dw one_sw :"+one_sw+"  res:"+res);
		
		if((res.indexOf("NA"))!=-1)
		{
			//System.out.println("begin call one_sw_process");
			 limit=6;
			res=swadw.one_sw_process(one_sw,limit);
			//System.out.println("res6:" + res);
		}
		
		if((res.indexOf("NA"))!=-1)
		{
			//System.out.println("begin call one_sw_process");
			 limit=5;
			res=swadw.one_sw_process(one_sw,limit);
			//System.out.println("res5:" + res);
		}
		
		if((res.indexOf("NA"))!=-1)
		{
			//System.out.println("begin call one_sw_process");
			 limit=4;
			res=swadw.one_sw_process(one_sw,limit);
			//System.out.println("res4:" + res);
		}
		
		String url_cate="";
		
		if(isUrl(one_sw))
		{
		   url_cate=host_jedis.get(one_sw);
		   if(url_cate==null)
		   {
			   url_cate="NA";
		   }
		   res=url_cate+"\001";
		}	
		if(isLongEnSearch(one_sw))
		{
			res="NA\001";
		}
		
		//System.out.println("res end:" + res);
		//if(cw_jedis.exists(one_sw))
		//{
			//System.out.println("cw redis 存在 "+one_sw);
		//}
		
		if((one_sw!=null)&&(!(one_sw.equals("")))&&(!(cw_jedis.exists(one_sw)))&&((res.indexOf("NA"))==-1))
		{
			//System.out.println("one_sw:"+one_sw+"  res:"+res);
			//cw_jedis.set(one_sw, res);
			try{
			String one_sw_mdkey=MD5Test.MD5(one_sw);
			one_sw_mdkey=one_sw_mdkey.trim();
			ssdb.set(one_sw_mdkey, res);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		unAnaNum=left_bdsw_num();
		return unAnaNum;
	}
	
	public long left_bdsw_num()
	{
		long lbn=0;
		
		try{
		sqsstatus = sqsclient.getStatus("bd_sw");
		lbn=sqsstatus.unreadNumber;
		}
		catch(Exception e)
		{
			
		}
		return lbn;
	}
	
	public boolean isEnNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+A-Za-z]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	
	public boolean isUrl(String str){
		boolean isUrl=false;
		if(Pattern.matches("(?:(?:www)|(?:WWW))\\.[a-z0-9_A-Z\\.]*", str))
		{
			isUrl=true;
		}
		else if(Pattern.matches("http:\\/\\/(?:(?:www)|(?:WWW))\\.[a-z0-9_A-Z\\.\\/]*", str))
		{
			isUrl=true;
		}
		return isUrl;
	}
	
	public void fetchToLogFile(String log_file) throws Exception
	{
		
		FileWriter fr=new FileWriter(new File(log_file),true);
		PrintWriter pw=new PrintWriter(fr);
		String one_sw="";
		for(int i=0;i<5000;i++)
		{
			if(left_bdsw_num()>0)
			{
				one_sw=sqsclient.getString("bd_sw");
				one_sw=one_sw.trim();
				if((one_sw!=null)&&(!(one_sw.equals(""))))
				{
				  pw.println(one_sw);
				}
			}
		}
		
		fr.close();
		pw.close();
		
		
	}
	
	public boolean isLongEnSearch(String str){
		boolean isles=false;
		String les_reg="[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*";
		Pattern les_pat=Pattern.compile(les_reg);
		Matcher les_mat=les_pat.matcher(str);
		while(les_mat.find())
		{
			isles=true;
		}
		les_reg="[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*";
	    les_pat=Pattern.compile(les_reg);
		les_mat=les_pat.matcher(str);
		while(les_mat.find())
		{
			isles=true;
		}
		
		
		return isles;
	}
	
	public static void main(String[] args) throws Exception
	{
				
		SWAOfflineRed swaolr=new SWAOfflineRed();
		swaolr.load_local_config();
		
		int sleep_time=1000;
		long temp_lbn=0;
		String log_file="unprocess.txt";
		while(true)
		{
			temp_lbn=swaolr.left_bdsw_num();
			if(temp_lbn>5000)
			{
				swaolr.fetchToLogFile(log_file);
			}
			
			if(temp_lbn%51==1)
			{
			 System.out.println("temp_lbn:"+temp_lbn);
			}
		    if((swaolr.left_bdsw_num())<1)
		    {
		    	sleep_time=10000;
		    	Thread.sleep(sleep_time);
		    }
		    else
		    {
		    	if((swaolr.getAndAnalysis())<1)
		    	{
			    	sleep_time=10000;
			    	Thread.sleep(sleep_time);	
		    	}
		    }		
		}
		
	}
	
	
}
