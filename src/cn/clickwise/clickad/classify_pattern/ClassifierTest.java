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
		System.out.println("url:"+url);
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
		ct.testWBCate("{\"id\":\"1193725273\",\"title\":\"郑渊洁的微博_微博\",\"简介\":\"《童话大王》月刊马上就30岁了 邮局订阅代号22－89\",\"keywords\":\"郑渊洁，郑渊洁的微博，微博，新浪微博，weibo\",\"description\":\"郑渊洁，作家。郑渊洁的微博主页、个人资料、相册。新浪微博，随时随地分享身边的新鲜事儿。\",\"标签\":\"童话大王月刊\t淘宝皮皮鲁书店\t不愿在外面用餐\t中国国籍\t无党派\t皮皮鲁总动员\t养狗\t不抽烟\",\"name\":\"cne\",\"帖子\":\"置顶 我最满意的作品是这部： 郑渊洁给孙女编写的十二生肖童话 //@赢了什么酥:郑老师您写作30多年，写了2000多万字的作品，最满意的作品是哪个？//@郑渊洁:七万人看过这个演讲视频了。 \t 回复@Boby-Lau:还真不是。//@Boby-Lau:傻子都能看出来这是莲藕//@郑渊洁:回复@想风儿:不是。//@想风儿:豆腐皮 \t 《皮皮鲁送你100条命》第38条命：咽喉要道。 \t 回复@想风儿:不是。//@想风儿:豆腐皮 \t \t 《皮皮鲁送你100条命》第16条命:高处不胜寒。请告诉孩子当身体处于自己身高两倍的高度时，即属于\"高处\"，要确保使用软着陆的方式重返地。不倚靠窗户和围栏。 \t 《皮皮鲁送你100条命》第56条命:骨肉相连。你骨折过吗? \t 您手中年代最久远的《童话大王》是哪期? \t #郑在打卡#早上好。今天是2015年2月3日。请和你的上下楼互粉 \t 看了电影《狼图腾》。值得一看。你会分不清哪是人哪是狼。从人身上看到了狼性，从狼身上看到了人性。惊叹导演让真狼演电影的能力，应该出自对狼的尊重:演职员名单上有真狼演员的名字羊年春节，去电影院与狼共舞吧 \t 回复@z张明:正在坐车回家的路上。到家就发布观后感，告诉大家是否值得看。//@z张明:郑老师，《狼图腾》好看么，大学最喜欢的小说就是狼图腾。//@郑渊洁:回复@一笛之音:小冯演技一流。//@一笛之音:冯绍峰看皮皮鲁长大的 \t 回复@一笛之音:小冯演技一流。//@一笛之音:冯绍峰看皮皮鲁长大的\"}");
	}

}
