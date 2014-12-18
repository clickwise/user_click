package cn.clickwise.liqi.nlp.classify.basic;

import java.util.Properties;

import cn.clickwise.liqi.datastructure.map.HashIndex;

/**
 * 调用分类模型进行分类的基础类 
 * @author lq
 *
 */
public abstract class ModelClassify {

	/**
	 * 读取配置文件
	 * @param config_file
	 */
	public abstract void load_config(String config_file) throws Exception;
	
	
	/**
	 * 加载配置文件
	 * @param prop
	 */
	public abstract  void load_config(Properties prop);
	
	/**
	 * 从普通的一行文本预测该段文本的类别
	 * @param text
	 * @return cate
	 */
	public abstract String predictFromPlainText(String text);
	
	/**
	 * 对未知分类的普通文本文件的每行进行类别预测
	 * @param unlabel_file
	 * @param predict_file
	 */
	public abstract void predictFromPlainFile(String unlabel_file,String predict_file);
	
	
}
