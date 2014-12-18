package cn.clickwise.liqi.crawler.basic;

public class FilterContent {
	public static String getFilterContent(String raw_content) {
		String filter_content = "";

		filter_content = raw_content
				.replaceAll("http://www.baidu.com(?s).*<!DOCTYPE\\s*html>",
						"")
				.replaceAll("<html>(?s).*?用手机随时随地上百度</a>", "")
				.replaceAll(
						"<input type=\"submit\"\\s*value=\"百度一下\"(?s).*?</html>",
						"")
				.replaceAll("<\\s*script\\s*>(?s).*?<\\s*/script\\s*>", "")
				.replaceAll("<\\s*style\\s*>(?s).*?<\\s*/style\\s*>", "")
				.replaceAll("<(?s)[^<>]*?>", "")
				.replaceAll("\\d+-\\d+-\\d+", "")
				.replaceAll("-\\s*百度快照", "")
				.replaceAll("查看更多.{0,100}?内容", "")
				.replaceAll("下一页.{0,10}百度为您找到相关结果约[\\d\\,]*个.{0,100}?相关搜索",
						"")
				.replaceAll("查看", "")
				.replaceAll("更多", "")
				.replaceAll("内容", "")
				.replaceAll("百度", "")
				.replaceAll("猜您>喜欢", "")
				//.replaceAll("[\\.a-z\\/0-9]*\\.\\.\\.htm[l]?", "")
				//.replaceAll("[\\.A-Za-z\\/0-9]*?[a-z0-9]", " ")
				.replaceAll("[\"\\?\\,\\:\\_\\a-zA-Z0-9\\[\\]\\\\\\#\\%\\$]*=[\"\\?\\,\\:\\_\\a-zA-Z0-9\\[\\]\\\\\\#\\%\\$]*", "")
				.replaceAll("\\.\\.\\.", " ")
				.replaceAll("&nbsp;", "")
				.replaceAll("&gt;", "")
				.replaceAll("显示全部", "")
				.replaceAll("收起", "")
				.replaceAll("[&;-]", "")
				.replaceAll("[\\(\\)\\+\\|\\{\\}\\=\\*\\/<>]", "")
				//.replaceAll(
				//		"\\s[\"\\?\\,\\:\\_\\a-zA-Z0-9\\[\\]\\\\\\#\\%\\$]{1,10}\\s",
				//		"")
						.replaceAll("_::\\s", "");
		filter_content = filter_content.replaceFirst(
				"[\"\\?\\,\\:\\_\\a-zA-Z0-9\\[\\]\\\\\\#\\%\\$]*", "");

		return filter_content;
	}
}
