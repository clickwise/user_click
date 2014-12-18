package cn.clickwise.liqi.str.regex;

import java.util.HashMap;

/**
 * 存放各种匹配的表达式
 * 按照key获取
 *  urlcode : urlencode 编码
 *  url: 合法的url规则
 *  chinese:汉字的匹配规则
 *  punc:标点的匹配规则
 * @author zkyz
 *
 */
public class RegexDB {

	public  static HashMap<String,String> regexMap=new HashMap<String,String>();
	
	public RegexDB()
	{
		
	}
	
	public static void initRegex()
	{
		
		regexMap.put("urlcode", "[A-Z\\+%0-9]*");
		regexMap.put("url", "[a-zA-Z0-9\\:\\=\\?\\/\\._\\&\\%\\+\\|\\-]*");
		regexMap.put("chinese", "[a-zA-Z0-9_\u4e00-\u9fa5\\s]*");
	}
	
	/**
	 * urlencode 编码
	 * @return
	 */
	public static String getUrlCodeRg()
	{
		initRegex();
		return regexMap.get("urlcode");
	}
	
	/**
	 * 合法的url规则
	 * @return
	 */
	public static String getUrlRg()
	{
		initRegex();
		return regexMap.get("url");
	}
	
	
	public static String getChineseRg()
	{
		initRegex();
		return regexMap.get("chinese");
	}
	
	
}
