package cn.clickwise.liqi.nlp.classify.svm.trihier.api;

import java.util.HashMap;
import java.util.Properties;

import cn.clickwise.liqi.nlp.classify.basic.ModelTrain;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;

/**
 * SVM 模型的训练:类别结构为三层(一般为树状结构)
 * 例如：家电办公|生活电器|净水器
 * @author lq
 */
public class TriHierSVMTrain extends ModelTrain{
    
	/*****模型源代码所在的文件夹*********/
	private String src_workplace;
	
	/*****标记数据所在的文件夹**********/
	private String label_data;
	
	/*****生成模型所在的文件夹**********/
	private String model_data;
	
	/*****模型参数c****/
	public double c;
	
	/**
	 * 向量空间选项，如：
	 * 1  普通文本向量空间
	 * 2 word2vec向量空间
	 * 3 medlda转换的向量空间
	 */
	public short vec_space; 
		
	/**
	 * 读取配置文件
	 * @param config_file
	 */
	public void load_config(String config_file) throws Exception
	{
		Properties prop = null;
		prop=ConfigFileReader.getPropertiesFromFile(config_file);
		
		src_workplace=prop.getProperty("src_workplace");
		label_data=prop.getProperty("label_data");
		model_data=prop.getProperty("model_data");
		c=Double.parseDouble(prop.getProperty("c"));
		
	}
	
	/**
	 * 标记好的普通文本作为 输入,例如
	 * 家电办公|生活电器|净水器 \001普尔康弱碱性七级RO反渗透家用纯水机直饮厨房净水器自来水过滤器 
	 * 输入:标记好的普通文本文件
	 * 输出:模型文件、词典文件、类别标记编号文件例如:家电办公|生活电器|净水器 [\001]1 1 1
	 */
	public void trainFromPlainText()
	{
		
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String,String> getHMFromLabelFile()
	{
		HashMap<String,String> hm=new HashMap<String,String>();
		
		
		
		return hm;
	}
	
	
	public static void main(String[] args)
	{
		
	}

	@Override
	public void load_config(Properties prop) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
