package cn.clickwise.liqi.str.test;

import java.lang.reflect.Method;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class StrTest {
	
	public static String jsonTest() throws JSONException{  
	    JSONObject json=new JSONObject();  
	    JSONArray jsonMembers = new JSONArray();  
	    JSONObject member1 = new JSONObject();  
	    member1.put("loginname", "zhangfan");  
	    member1.put("password", "userpass");  
	    member1.put("email","10371443@qq.com");  
	    member1.put("sign_date", "2007-06-12");  
	    jsonMembers.put(member1);  
	  
	    JSONObject member2 = new JSONObject();  
	    member2.put("loginname", "zf");  
	    member2.put("password", "userpass");  
	    member2.put("email","8223939@qq.com");  
	    member2.put("sign_date", "2008-07-16");  
	    jsonMembers.put(member2);  
	    json.put("users", jsonMembers);  
	  
	    return json.toString();  
	}  
	
	public static String jsonTest1() throws JSONException{  
	    JSONObject json=new JSONObject();  
	    json.put("uid", "0000e184f710bb30629e7fc166d05ca2");  
	    json.put("datatype", "HOST_CATE"); 
	    json.put("addtime", "1404005327000"); 
	    String seg_text="高清 爱奇 综艺 录片 纪录片 爱奇艺 奇艺 观看 视频 纪录";
	    JSONArray jsonMembers = new JSONArray();  
	    String[] seg_arr=seg_text.split("\\s+");
	    String item="";
	    for(int i=0;i<seg_arr.length;i++)
	    {
	    	item=seg_arr[i];
	    	jsonMembers.put(item);
	    }
	    
	    json.put("info", jsonMembers);
	    	  
	    return json.toString();  
	}  
	
	
	public String[] wordFromLink(String link)
	{
		String urlcode_pat_str="([A-F\\+\\%0-9]*)";
		Pattern urlcode_pat=Pattern.compile(urlcode_pat_str);
		Matcher urlcode_match=urlcode_pat.matcher(link);
		Vector<String> words=new Vector();
		String decodeword="";
		String encodestr="";
		while(urlcode_match.find())
		{
			encodestr=urlcode_match.group(1);
			decodeword=UrlCode.getDecodeUrl(encodestr);
			if(SSO.tioe(decodeword))
			{
				continue;
			}
			System.out.println("decode:"+decodeword);
			char first_char = decodeword.charAt(0);
			if(isChinese(first_char))
			{
				words.add(decodeword);
			}
			else
			{
				continue;
			}
		}
		
		String[] arr=new String[words.size()];
		for(int i=0;i<arr.length;i++)
		{
			arr[i]=words.get(i);
		}		
		return arr;
	}
	
	public boolean isChinese(char a) {
		int v = (int) a;
		return ((v >= 19968 && v <= 171941)||(v==22)||(v==123)||(v==58));
	}

	public String arrToStr(String[] arr)
	{
		String arr_str="";
		String word="";
		for(int i=0;i<arr.length;i++)
		{
			word=arr[i];
			if(SSO.tioe(word))
			{
				continue;
			}
			word=word.trim();
			arr_str=arr_str+word+" ";
		}
		arr_str=arr_str.trim();
		return arr_str;
	}
	
	public static void charInt()
	{
	      int a=1320942;
	      Integer i=new Integer(a);
	      System.out.println(Integer.toHexString(a));
	      
	      char[] cc=(new String(a+"")).toCharArray();
	      System.out.println("cc.len:"+cc.length);
	      char c=(char)a;
	      int b=c;
	      System.out.println("b="+b);
	}
	
	public static void main(String[] args) throws Exception
	{
		/*
		String s="/2/1005.js?vt=0.6778577605714211&area=%7Bcountry%3A%22%E4%B8%AD%E5%9B%BD%22%2Cprovince%3A%22%E5%A4%A9%E6%B4%A5%22%2Ccity%3A%22%E5%A4%A9%E6%B4%A5%22%7D&default=";
	    StrTest st=new StrTest();
		String[] words=st.wordFromLink(s);
		System.out.println("words:"+st.arrToStr(words));
		*/
		
		/*
		try{
			Class myClass = Class.forName("cn.clickwise.user_click.field.STR2LONGTIME");
			Method myMethod=myClass.getMethod("fieldFunc", new Class[]{String.class,String.class});
			Object returnValue = myMethod.invoke(myClass.newInstance(),"2014-02-08 15:29:05", ""); 
			
		    System.out.println(returnValue+"");
		}
		catch(Exception e)
		{
			
		}
		*/
		/*
		double ran=Math.random();
		String a="d|adb";
		System.out.println(a);
		System.out.println(jsonTest1());
		*/
		 charInt();
	}
}
