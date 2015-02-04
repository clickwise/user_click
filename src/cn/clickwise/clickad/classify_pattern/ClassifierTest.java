package cn.clickwise.clickad.classify_pattern;

import java.net.URLEncoder;
import java.util.ArrayList;

public class ClassifierTest extends ClassifierTestBase{

	@Override
	public String test(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String test(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String testSeg(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String testTag(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String testKey(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String testTBCate(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> testmul(String[] text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String testWBCate(String text) {
		String method = ":9008/cwb?s=";
		text = URLEncoder.encode(text);
		String url = auxiliary_prefix + method + text;
		String response = hct.postUrl(url);
		try{
           System.out.println("response:"+response);
		}
		catch(Exception e)
		{
			
		}
		return response;
	}
	
	public static void main(String[] args)
	{
		ClassifierTest ct=new ClassifierTest();
		ct.testWBCate("title:中青报曹林的微博_微博	keywords:中青报曹林，中青报曹林的微博，微博，新浪微博，weibo	description:中青报曹林，评论名人曹林。中青报曹林的微博主页、个人资料、相册,华中科技大学。新浪微博，随时随地分享身边的新鲜事儿。	 简介： 爱评论，爱美女，爱爬山 	帖子:	 转发微博 	 转发微博 	 今天公号推的评论是《不要传递“越左越安全”的政治错觉》：“越左越安全”为害甚广：不管对不对，跟着喊口号就是。宁愿上纲上线，宁愿走过头和扩大化，宁愿走到极端、走到让人反感的地步。对一些理论和教条，明知已脱离时代脱离现实，但只要有革命外衣，生搬硬套就是，这样最安全 	 转发微博 	 回复@c提刑司廌c:这个时候只能表扬，不有批评，红包额度要看表扬程度：） //@c提刑司廌c:不发个万把块钱，曹老对得起自己英俊的脸庞么。 	 //@摘星手010: 万里、胡耀邦等老干部正是凭借对老百姓和知识分子长期被折腾的负罪感和江山社稷的责任感，冲破左的教条。三年饥荒后，邓子恢在中央党校说：我们应该悬崖勒马了，再也不能搞左的一套了，那是祸国殃民的做法。它使成千上万人非正常死亡，凡是有一点良心的人都应该感到痛心内疚。 	 一边自以为“越左越安全”，一边坚持“越右越正义”，这两种极端的思潮不仅自说自话，在舆论场上还互相强化——站在极左那一边的，把极右当成敌人，论证自身存在的正当性和正统性。那边的面孔越左，越刺激着一些人充满正义地朝着越右的方面狂奔。 	 中国已经成为一个各方面都正常的现代国家，秉持“越左越安全”和“越右越正义”都会被人们当作与社会格格不入的怪物。 	 #中青报曹林的红包#过几天我就要给粉丝发红包了，请关注：） 	 今天公号推的评论是《不要传递“越左越安全”的政治错觉》：“越左越安全”为害甚广：不管对不对，跟着喊口号就是。宁愿上纲上线，宁愿走过头和扩大化，宁愿走到极端、走到让人反感的地步。对一些理论和教条，明知已脱离时代脱离现实，但只要有革命外衣，生搬硬套就是，这样最安全。 	 不能把党的领导作为个人以言代法、以权压法、循私枉法的挡箭牌。 	 //@摘星手010:人民日报今天头版通栏报道习的讲话：领导干部要牢固树立宪法法律至上、法律面前人人平等、权由法定、权依法使等基本法治观念。公平正义是我们党追求的一个非常崇高的价值。O网页链接 	 //@老榕:面对我面前的其基本资料外，厚厚的其1000多个帖子的所有发帖IP或电话号码，它所有的丑恶嘴脸及其同伙将无处遁形。对了，多谢18年的老朋友，IP定位专家@高春晖 。 	 今天公号推的评论是《中国网络上的“职业抵制者”》：抵制日货，抵制法货，不如抵制蠢货。中国已深深地嵌入这个世界，可一些人身上的拳民情结还是那么浓。爱国是人的本能，但不能挟“爱国”的政治正确去兜售狭隘民族主义情绪，变成一种打打杀杀、让世界无法理喻的民族戾气。 	 回复@伍皓红河微语://@伍皓红河微语:回复@vbkc:我当过省委宣传部副部长。这个是常务副部长，算我的顶头上司吧。虽然现在为官不易，但我仍努力在做一个好干部，真的是蛮拼的。");
	}

}
