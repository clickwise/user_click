package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class SaveUserInfo {

	public static void save_one_user_record(String info)
	{
		HttpClient httpclient = new DefaultHttpClient();
		String encode_info=URLEncoder.encode(info);
		encode_info=encode_info.replaceAll("\\s+", "");
		//System.out.println("encode_info:"+encode_info);
		String prefix="http://42.62.29.25:8000/adduserrecord?s=";
		String url=prefix+encode_info;
		
		String con="";
		try{
			HttpGet httpget = new HttpGet(url);
			HttpPost httppost=new HttpPost(url);
			//System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httppost);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println("statusCode:"+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					con = EntityUtils.toString(entity);
					con=con.replaceAll("\\s+", "");
					//System.out.println("con:"+con);
				}
			}		
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static String add_datatype(String info)
	{
		String paddedinfo="";
		String datatype="";
		if(info.indexOf("bdkeys")>-1)
		{
			datatype="BAIDU";
		}
		else if(info.indexOf("attrs")>-1)
		{
			datatype="TBSEARCH";
		}
		info=info.replace("{", "");
		info=SSO.replaceLast(info, "}", "");
		if(SSO.tioe(info))
		{
			return "";
		}
		info=info+",\"datatype\":\""+datatype+"\"";
		info="{"+info+"}";
		paddedinfo=info;
		return paddedinfo;
	}
	
	public static void test_local() throws Exception
	{
		//String[] line_arr=FileToArray.dirToDimArr("/home/hadoop/lq/SWA_Eclipse/cplus/userad/trunk/docs/data");
		String[] line_arr=FileToArray.dirToDimArr("D:/projects/admatch/user_data/data0508");
		String eff_field="";
		FileWriter fw=new FileWriter(new File("temp/admatch/log.txt"));
		PrintWriter pw=new PrintWriter(fw);
		
		System.out.println("line_arr.length:"+line_arr.length);
		String padded_info="";
		////20000
	    for(int i=0;i<100;i++)
	    {
	    	pw.println("i="+i+"  "+line_arr[i]);
	    	pw.flush();
	    	if(i%100==0)
	    	{
	    		System.out.println("i="+i);
	    	}
	    	eff_field=SSO.afterStr(line_arr[i], "[INFO ] ZADD");
	    	eff_field=SSO.afterStr(eff_field, "{");
	    	eff_field="{"+eff_field;
	    	padded_info=add_datatype(eff_field);
	    	//System.out.println("i="+i+":  "+add_datatype(eff_field));
	    	if(SSO.tioe(padded_info))
	    	{
	    		continue;
	    	}
	    	Thread.sleep(10);
	    	save_one_user_record(add_datatype(eff_field));
	    }
	    
	    fw.close();
	    pw.close();
	}
	
	public static void saveUserHistory(String input_file) throws Exception
	{
		FileReader fr=new FileReader(new File(input_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String eff_field="";
		String padded_info="";
		while((line=br.readLine())!=null)
		{
		   	eff_field=SSO.afterStr(line, "[INFO ] ZADD");
	    	eff_field=SSO.afterStr(eff_field, "{");
	    	eff_field="{"+eff_field;
	    	if(eff_field.indexOf("datatype")>-1)
	    	{
	    		padded_info=eff_field;
	    	}
	    	else
	    	{
	    		padded_info=add_datatype(eff_field);
	    	}
	    	//System.out.println("i="+i+":  "+add_datatype(eff_field));
	    	if(SSO.tioe(padded_info))
	    	{
	    		continue;
	    	}
	    	save_one_user_record(add_datatype(eff_field));  	
		}
		
		fr.close();
		br.close();
		
	}
	
	
	public static void  main(String[] args) throws Exception
	{
		//test_local();
		if(args.length<1)
		{
			System.out.println("usage:<inputfile>");
			System.exit(1);
		}
		
		String input_file="";
		input_file=args[0];
		saveUserHistory(input_file);
		
	}
	
}
