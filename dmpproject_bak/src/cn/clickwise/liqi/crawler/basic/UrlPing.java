package cn.clickwise.liqi.crawler.basic;

import java.net.URLEncoder;

/**
 * 构建各种url
 * @author zkyz
 *
 */
public class UrlPing {

	/**
	 * 从百度搜索词构建百度搜索链接
	 * @param keyword
	 * @return
	 */
	public static String getBSUrl(String keyword)
	{
		String code_url = "";
		String code_word = "";
		code_word = URLEncoder.encode(keyword);
		code_word = code_word.trim();

		String url_prefix = "http://www.baidu.com/s?wd=";
		code_url = url_prefix + code_word;

		return code_url;
	}
	
	
}
