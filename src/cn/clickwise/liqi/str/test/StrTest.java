package cn.clickwise.liqi.str.test;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class StrTest {
	
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
	
	public static void main(String[] args)
	{
		String s="/2/1005.js?vt=0.6778577605714211&area=%7Bcountry%3A%22%E4%B8%AD%E5%9B%BD%22%2Cprovince%3A%22%E5%A4%A9%E6%B4%A5%22%2Ccity%3A%22%E5%A4%A9%E6%B4%A5%22%7D&default=";
	    StrTest st=new StrTest();
		String[] words=st.wordFromLink(s);
		System.out.println("words:"+st.arrToStr(words));
	}
}
