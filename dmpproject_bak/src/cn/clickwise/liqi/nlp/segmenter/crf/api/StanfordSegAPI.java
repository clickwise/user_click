package cn.clickwise.liqi.nlp.segmenter.crf.api;

import java.util.Properties;

import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;

/**
 * 提供stanford crf segmenter 的简单调用接口
 * 输入普通文本，输出分词结果
 * 实例化对象时加载新模型或调用后台server可选 
 * @author lq
 *
 */
public class StanfordSegAPI extends SegmenterSeg{
   
	private boolean isBackServer=false;
	
		
	/**
	 * 输入普通文本，输出分词结果
	 * @return
	 */
	public String seg(){
		String seg_s="";
		
			
		return seg_s;
	}


	@Override
	public void seg(String plainFile, String seg_file) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String seg(String text) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void load_config(Properties prop) {
		// TODO Auto-generated method stub
		
	}
	
	
}
