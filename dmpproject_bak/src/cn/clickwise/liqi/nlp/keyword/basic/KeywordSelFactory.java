package cn.clickwise.liqi.nlp.keyword.basic;

import java.util.Properties;

import cn.clickwise.liqi.nlp.keyword.simple.api.SimpleKeywordSel;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTag;

/**
 * 返回不同的关键词提取模型
 * @author lq
 *
 */
public class KeywordSelFactory {

	public static KeywordSel create(Properties prop)
	{
		KeywordSel keysel=null;
		String keyword_model=prop.getProperty("keyword_model");
		if(keyword_model.equals("simple_keyword_model"))
		{
			keysel=new SimpleKeywordSel();
			keysel.load_config(prop);
		}
		
		return keysel;
	}
	
}
