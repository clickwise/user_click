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
		//ct.testWBCate("{\"id\":\"1789951683\",\"title\":\"投资理财者陈家进的微博_微博\",\"简介\":\"价值选股趋势选时资金管理持盈停损。欢迎开户转户过来万三佣金网上办理提供一对一的投顾免费服务请私信或留言（一切言论只属个人看法与供职单位无关）\",\"keywords\":\"投资理财者陈家进，投资理财者陈家进的微博，微博，新浪微博，weibo\",\"description\":\"投资理财者陈家进，广发证券广州黄埔大道证券营业部   片区经理。投资理财者陈家进的微博主页、个人资料、相册，广发证券。新浪微博，随时随地分享身边的新鲜事儿。\",\"标签\":\"交易技巧理念\t投资理财顾问\t股票开户转户\",\"name\":\"1789951683\",\"帖子\":\"\t 【无我】你自己怎么想其实不是最重要的，最重要的是弄清楚主流资金怎么想。不要猜测，不要把个人的情感强加在市场上，也别期望市场在乎你。市场既不敌对也不友善，它只是存在，仅此而已。应对远比预测重要，耐心倾听市场的声音，追随市场的节奏，清风明月随波逐流。抛掉心中的杂念，只按市场信号操作。 \t 【交易员的38个境界】：交易员的38个境界。再翻出来看看，你到了第几级。（翻译摘自知乎“AkaHD”）值得一读。 1. We accumulate information – buying books, going to seminars and rese...文字版>> O网页链接 （新浪长微博>> O网页链接） \t 你先得承认自己“不行”，然后围绕自己“不行”建立一套“行”的系统，而这套“行”的系统的核心必须是认为自己“不行”才行，然后你就“行”了。市场上每一个个人投资者都犹如沧海孤舟，不断修正错误重复正确，不再眼红市场眼花缭乱的干扰恪守本分，只专注属于你特定操作模式的机会，活着并自强不息。 \t 【股票开户广发证券最方便最安全最优惠，专业专心专为您！】：广发证券成立于1991年9月8日，是国内首批综合类证券公司。2010年2月12日，公司在深圳证券交易所成功上市，股票代码：000776.sz；20...文字版>> O网页链接 （新浪长微博>> O网页链接） \t 【人生不只有跑一种前行方式，还有走路和驻足。当然，前提是你一定要有梦想和行动的愿望。】 \t 复盘的时候个人必看两类个股：一是创新高个股，二是涨幅榜个股，毕竟值得我们费大精力的只有他们。值得我们关注把握的只有强势板块和强势个股，如果他们无法继续强势了，直到他们再次强势起来时才能继续考虑；如果某个板块或个股经过很长时间调整后基本横盘不往下跌了，那么我们也是很值得关注的对象。 \t 为什么要努力？！一切的努力都是为了让生活更美好、、、 \t 【拆借一个完整的牛市】牛市并非一段暴涨可以开始，也并非一个暴跌可以结束。 \t 【杰出的心性才是高手的刀】：“人的聪明绝不在投机取巧的技能方面，而在于脚踏实地，在于善良、勤奋、认真、守信和对正义、真理的执著追求和坚守。你可以踏上投机的道路，但绝不会因为善于投机...文字版>> O网页链接 （新浪长微博>> O网页链接） \t 【抱牢正确的头寸仓位】//@无欲则ZS: 转发微博 \t 资金推动是这个行情最主要的驱动，之所以对后面的行情都比较期待是因为觉得其他渠道会有大量增量资金源源不断支撑市场上涨。股票市场是资金推动的，是否有资金进入市场以及场内资金的流向和配置是研究投资的核心问题。而且，在牛市最不缺的就是牛股，我们缺的是一颗守住牛股的心，缺的是一颗专注的心！ \t 最近很少上微博更没空看那些大师们的吹嘘。其实吧，现在大盘也算是按趋势结构在走，所以跟着趋势走就是说那么多干嘛不吹会死啊！不能去猜顶，只要趋势还在就不要主观臆测不断抬高移动止盈位跟随就是。市场上盈利的投资人最终都是按照趋势投资从容地拿对正确仓位的股票，拿住正确头寸不动是最难的本事。 \t 我们一直想学会平静，其实吧，这事儿不用学，经历的多了，就平静了。人生如此，交易亦如此！ \t 买入后怎么办?沪港通今实施，本月新股申购今实施，本周又迎期指交割，致市场短线振荡。大盘不改中长线新牛市格局，客观来讲至少指数走势尚未走坏，手中个股只要趋势没有改变我们继续用移动止盈来追随，当然，如果趋势走坏头部来临该走的仓位必须走只吃鱼身鱼头鱼尾留给别人吧//@klssmmhj: 买入后怎么办 \t 【只顺势做应对】不主观预测只相信趋势走势，不意淫分析只持盈停损应对，顺势而为绝不走极端装逼预测搏眼球！如果趋势走坏头部来临该走的仓位必须走只吃鱼身鱼头鱼尾留给别人吧，只要趋势没有改变我们继续用移动止盈来追随。趋势来的时候不管他多荒谬不堪相信他，趋势走的时候不管他多难以置信追随他。\"}");
	    ct.testWBCate("{\"id\":\"2858525222\",\"title\":\"犀利話的微博_微博\",\"简介\":\" 一杯浊酒，难看红尘透。\",\"keywords\":\"犀利話，犀利話的微博，微博，新浪微博，weibo\",\"description\":\"犀利話，一杯浊酒，难看红尘透。。犀利話的微博主页、个人资料、相册。新浪微博，随时随地分享身边的新鲜事儿。\t \",\"标签\":\"文化\t心语\t誓言\t唯美\t心里话\t心情\t语录\t爱情\t情感随笔\t情感\t\",\"name\":\"2858525222\"");
	}

}
