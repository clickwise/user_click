package cn.clickwise.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

public class JsonUtil {
	
	private static void setDataFormat2JAVA(){
		//设定日期转换格式
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"}));
	} 
	
	public static Map getMapFromJson(String jsonString) {
		setDataFormat2JAVA();
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map map = new HashMap();
		for(Iterator iter = jsonObject.keys(); iter.hasNext();){
		       String key = (String)iter.next();
		       map.put(key, jsonObject.get(key));
		}
		return map;
	 } 
	
	/**
	* 把数据对象转换成json字符串
	* DTO对象形如：{"id" : idValue, "name" : nameValue, ...}
	* 数组对象形如：[{}, {}, {}, ...]
	* map对象形如：{key1 : {"id" : idValue, "name" : nameValue, ...}, key2 : {}, ...}
	* @param object
	* @return
	*/
	public static String getJSONString(Object object) throws Exception{
	   String jsonString = null;
	   //日期值处理器
	   JsonConfig jsonConfig = new JsonConfig();
	   jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor());
	   if(object != null){
	     if(object instanceof Collection || object instanceof Object[]){
	        jsonString = JSONArray.fromObject(object, jsonConfig).toString();
	     }else{
	        jsonString = JSONObject.fromObject(object, jsonConfig).toString();
	     }
	   }
	   
	   return jsonString == null ? "{}" : jsonString;
	} 
	
	 public static void main(String[] args)
	 {
	    String jstr="{\"id\" : \"johncon\", \"name\" : \"小强\"} ";
	    Map m=getMapFromJson(jstr);
	    System.out.println("id:"+m.get("id"));
	    System.out.println("name:"+m.get("name"));
	    
	    HashMap<String,String> hm=new HashMap<String,String>();
	    hm.put("id", "2858525222");
	    hm.put("name","2858525222");
	    hm.put("title", "犀利話的微博_微博");
	    hm.put("keywords", "犀利話，犀利話的微博，微博，新浪微博，weibo");
	    hm.put("description", "犀利話，一杯浊酒，难看红尘透。。犀利話的微博主页、个人资料、相册。新浪微博，随时随地分享身边的新鲜事儿。	 ");
	    hm.put("简介", " 一杯浊酒，难看红尘透。");
	    hm.put("标签", "文化	心语	誓言	唯美	心里话	心情	语录	爱情	情感随笔	情感	");
	    hm.put("帖子", "	 你永远都不知道身边的傻逼多久才能遇完 但每个傻逼都会让你成长 	 一个懂你的人，能带来一段彼此舒服的爱。一个不懂你的人，最终会让你懂得一个道理：人生中，懂，比爱，更重要 	 不管闹多少次别扭，最后还是会因为舍不得而和好如初，这种感觉真好。 	 “你觉得爱情是什么？” ——“舒适且不尴尬的沉默 	 当二货不再犯二时 说明他不开心了 	 男人被束缚了自由，女人即使对他再好，他也是厌烦。 	 学会一个人生活，不论身边是否有人疼爱。做好自己该做的，有爱或无爱，都安然对待 	 越有本事的男人越没脾气… 	 如果 有一天我遇见了过去的自己 我一定一巴掌扇上去 “你做了太多错事了” 	 最可悲的不是单身，而是心里连一个喜欢的人都没有。(net) 	 再有钱的男人也比不上一个知冷知热疼你的男人重要 再多的爱马仕也抵不过 每天早晨跑几条街为你买豆浆油条的幸福 再好的甜言蜜语也不如你需要时陪在身边肩并肩的相伴 世界上最奢侈的人 是肯花时间陪你的人 对的人 总会遇到 无论在哪里 	 语言很多时候都是假的，一起经历的事情才是真的。 	 我不怕苦不怕累不怕失败不怕鼻青脸肿不怕遍体鳞伤不怕最后不知所终,我只怕自己白发苍苍的时候没有半点值得回忆的东西,只怕自己有梦却没从努力过. 	 有时候，你说的话可能会伤到别人，但有时候，你的沉默会让人伤得更深。 	 永远也不要高估你在别人心中的地位，其实你什么都不是，多你一个也不多，少你一个也不少————致自己！");
	    
	    
	    try{
	       String hjstr=getJSONString(hm);
	       System.out.println("hjstr:"+hjstr);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    String jjstr="{\"title\":\"郑渊洁的微博_微博\",\"简介\":\"《童话大王》月刊马上就30岁了 邮局订阅代号22－89\",\"keywords\":\"郑渊洁，郑渊洁的微博，微博，新浪微博，weibo\",\"description\":\"郑渊洁，作家。郑渊洁的微博主页、个人资料、相册。新浪微博，随时随地分享身边的新鲜事儿。\",\"标签\":\"童话大王月刊  淘宝皮皮鲁书店  不愿在外面用餐  中国国籍        无党派  皮皮鲁总动员    养狗    不抽烟\",\"帖子\":\"置顶 我最满意的作品是这部： 郑渊洁给孙女编写的十二生肖童话 //@赢了什么酥:郑老师您写作30多年，写了2000多万字的作品，最满意的作品是哪个？//@郑渊洁:七万人看过这个演讲视频了。   回复@Boby-Lau:还真不是。//@Boby-Lau:傻子都能看出来这是莲藕//@郑渊洁:回复@想风儿:不是。//@想风儿:豆腐皮          《皮皮鲁送你100条命》第38条命：咽喉要道。       回复@想风儿:不是。//@想风儿:豆腐皮           《皮皮鲁送你100条命》第16条命:高处不胜寒。请告诉孩子当身体处于自己身高两倍的高度时，即属于\"高处\"，要确保使用软着陆的方式重返地。不倚靠窗户和围栏。      《皮皮鲁送你100条命》第56条命:骨肉相连。你骨折过吗?          您手中年代最久远的《童话大王》是哪期?   #郑在打卡#早上好。今天是2015年2月3日。请和你的上下楼互粉        看了电影《狼图腾》。值得一看。你会分不清哪是人哪是狼。从人身上看到了狼性，从狼身上看到了人性。惊叹导演让真狼演电影的能力，应该出自对狼的尊重:演职员名单上有真狼演员的名字羊年春节，去电影院与狼共舞吧        回复@z张明:正在坐车回家的路上。到家就发布观后感，告诉大家是否值得看。//@z张明:郑老师，《狼图腾》好看么，大学最喜欢的小说就是狼图腾。//@郑渊洁:回复@一笛之音:小冯演技一流。//@一笛之音:冯绍峰看皮皮鲁长大的          回复@一笛之音:小冯演技一流。//@一笛之音:冯绍峰看皮皮鲁长大的\"}";
	 }
	
}
