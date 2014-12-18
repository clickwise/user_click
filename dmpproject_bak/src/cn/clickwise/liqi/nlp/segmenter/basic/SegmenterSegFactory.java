package cn.clickwise.liqi.nlp.segmenter.basic;

import java.util.Properties;

import cn.clickwise.liqi.nlp.segmenter.crf.api.StanterSeg;

/**
 * 获得不同的SegmenterSeg对象
 * @author lq
 *
 */
public class SegmenterSegFactory {
	

	public static SegmenterSeg create(Properties prop)
	{
		SegmenterSeg segseg=null;
		String seg_model=prop.getProperty("seg_model");
		seg_model=seg_model.trim();
		if(seg_model.equals("stanford_crf_segmenter"))
		{
			segseg=new StanterSeg();
			segseg.load_config(prop);
		}		
		return segseg;
	}
	
}
