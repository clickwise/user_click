package cn.clickwise.liqi.nlp.postagger.basic;

import java.util.Properties;

import cn.clickwise.liqi.nlp.postagger.crf.api.StanterTag;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;
import cn.clickwise.liqi.nlp.segmenter.crf.api.StanterSeg;

/**
 * 返回不同的词性标注模型
 * @author lq
 *
 */
public class PostaggerTagFactory {

	public static PostaggerTag create(Properties prop)
	{
		PostaggerTag postag=null;
		
		String tag_model=prop.getProperty("tag_model");
		tag_model=tag_model.trim();
		if(tag_model.equals("stanford_crf_postagger"))
		{
			postag=new StanterTag();
			postag.load_config(prop);
		}
			
		return postag;
	}
	
}
