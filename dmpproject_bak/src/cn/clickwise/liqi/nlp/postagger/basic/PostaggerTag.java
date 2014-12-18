package cn.clickwise.liqi.nlp.postagger.basic;

import java.util.Properties;

/**
 * 使用词性标注模型进行词性标注的基础类
 * @author lq
 *
 */
public abstract class PostaggerTag {

	
	/**
	 * 加载配置文件
	 * @param prop
	 */
	public abstract  void load_config(Properties prop);
	
	/**
	 * 对一段分词后的文本进行词性标注
	 * @param seg_s
	 * @return tag_s
	 */
	public abstract String tag(String seg_s) throws Exception;
	
	/**
	 * 对一个分词后的普通文本文件逐行进行词性标注
	 * @param seg_file
	 * @param tag_file
	 */
	public abstract void tag(String seg_file,String tag_file) throws Exception;
}
