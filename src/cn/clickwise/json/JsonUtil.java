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
	 }
	
}
