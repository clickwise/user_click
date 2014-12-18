package cn.clickwise.liqi.str.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据正则表达式找到匹配的结果
 * @author zkyz
 *
 */
public class RegexFind {

	/**
	 * regex:匹配表达式
	 * line: 被匹配的字符串
	 * n:生成的字段数目
	 * @param regex
	 * @param line
	 * @param n
	 * @return
	 */
	public static String[] find(String regex,String line,int n)
	{
		String[] res_arr=null;
		Pattern pat=Pattern.compile(regex);
		Matcher mat=pat.matcher(line);
		
		if(mat.find())
		{
			res_arr=new String[n];
			for(int i=0;i<n;i++)
			{
				res_arr[i]=mat.group(i+1);
			}
		}
		
		return res_arr;
	}
	
	/**
	 * regex:匹配表达式
	 * line: 被匹配的字符串
	 * @param regex
	 * @param line
	 * @return
	 */
	public static String findSingle(String regex,String line)
	{
		String res="";
		Pattern pat=Pattern.compile(regex);
		Matcher mat=pat.matcher(line);
		
		if(mat.find())
		{
            res=mat.group(1);
		}
		
		return res;
	}
	
}
