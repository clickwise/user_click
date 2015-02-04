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
	    hm.put("id", "1193725273");
	    hm.put("name","cne");
	    hm.put("title", "郑渊洁的微博_微博");
	    hm.put("keywords", "郑渊洁，郑渊洁的微博，微博，新浪微博，weibo");
	    hm.put("description", "郑渊洁，作家。郑渊洁的微博主页、个人资料、相册。新浪微博，随时随地分享身边的新鲜事儿。");
	    hm.put("简介", "《童话大王》月刊马上就30岁了 邮局订阅代号22－89");
	    hm.put("标签", "童话大王月刊	淘宝皮皮鲁书店	不愿在外面用餐	中国国籍	无党派	皮皮鲁总动员	养狗	不抽烟");
	    hm.put("帖子", "置顶 我最满意的作品是这部： 郑渊洁给孙女编写的十二生肖童话 //@赢了什么酥:郑老师您写作30多年，写了2000多万字的作品，最满意的作品是哪个？//@郑渊洁:七万人看过这个演讲视频了。 	 回复@Boby-Lau:还真不是。//@Boby-Lau:傻子都能看出来这是莲藕//@郑渊洁:回复@想风儿:不是。//@想风儿:豆腐皮 	 《皮皮鲁送你100条命》第38条命：咽喉要道。 	 回复@想风儿:不是。//@想风儿:豆腐皮 	 	 《皮皮鲁送你100条命》第16条命:高处不胜寒。请告诉孩子当身体处于自己身高两倍的高度时，即属于\"高处\"，要确保使用软着陆的方式重返地。不倚靠窗户和围栏。 	 《皮皮鲁送你100条命》第56条命:骨肉相连。你骨折过吗? 	 您手中年代最久远的《童话大王》是哪期? 	 #郑在打卡#早上好。今天是2015年2月3日。请和你的上下楼互粉 	 看了电影《狼图腾》。值得一看。你会分不清哪是人哪是狼。从人身上看到了狼性，从狼身上看到了人性。惊叹导演让真狼演电影的能力，应该出自对狼的尊重:演职员名单上有真狼演员的名字羊年春节，去电影院与狼共舞吧 	 回复@z张明:正在坐车回家的路上。到家就发布观后感，告诉大家是否值得看。//@z张明:郑老师，《狼图腾》好看么，大学最喜欢的小说就是狼图腾。//@郑渊洁:回复@一笛之音:小冯演技一流。//@一笛之音:冯绍峰看皮皮鲁长大的 	 回复@一笛之音:小冯演技一流。//@一笛之音:冯绍峰看皮皮鲁长大的");
	    
	    
	    try{
	       String hjstr=getJSONString(hm);
	       System.out.println("hjstr:"+hjstr);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	 }
	
}
