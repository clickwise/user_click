package cn.clickwise.liqi.nlp.keyword.basic;

import java.util.Properties;

/**
 * 提取关键词的基础类
 * @author lq
 *
 */
public abstract class KeywordSel {

	
	/**
	 * 加载配置文件
	 * @param prop
	 */
	public abstract  void load_config(Properties prop);
	
	/**
	 * 从分词后的文本提取关键词
	 * @param seg_s
	 * @return key_s
	 */
	public abstract String keywordFromSeg(String seg_s);
	
	/**
	 * 从词性标注后的文本提取关键词
	 * @param tag_s
	 * @return key_s
	 */
	public abstract String keywordFromTag(String tag_s);
	
	/**
	 * 从词性标注后的文本提取关键词
	 * @param tag_s
	 * @return key_s
	 */
	public abstract String keywordFromTagNoun(String tag_s);
	
	/**
	 * 对分词后的文本文件逐行提取关键词
	 * @param seg_file
	 * @param key_file
	 */
	public abstract void keywordFromSegFile(String seg_file,String key_file);
	
	
	/**
	 * 对词性标注后的文本文件逐行提取关键词
	 * @param tag_file
	 * @param key_file
	 */
	public abstract void keywordFromTagFile(String tag_file,String key_file);
	
}
