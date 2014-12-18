package cn.clickwise.liqi.nlp.segmenter.basic;

import java.util.Properties;

/**
 * 调用分词模型进行分类的基础类
 * @author lq
 *
 */
public abstract class SegmenterSeg {

	/**
	 * 加载配置文件
	 * @param prop
	 */
	public abstract  void load_config(Properties prop);
	
	/**
	 * 对一段普通文本进行分词
	 * @return
	 */
	public abstract String seg(String text) throws Exception;
	
	/**
	 * 对一个普通文本文件的每行进行分词
	 * @param plainFile
	 * @param seg_file
	 */
	public abstract void seg(String plainFile,String seg_file) throws Exception;
}
