package cn.clickwise.liqi.nlp.classify.basic;

import java.util.Properties;

public abstract class ModelTrain {
	
	/**
	 * 加载配置文件
	 * @param prop
	 */
	public abstract  void load_config(Properties prop) throws Exception;
	
	/**
	 *从普通的文本文件训练模型 
	 */
	public abstract void trainFromPlainText() throws Exception;

    	
	
}
