package cn.clickwise.liqi.nlp.classify.basic;

import java.util.Properties;

import cn.clickwise.liqi.nlp.classify.medlda.singlehier.api.SingleMedldaClassify;
import cn.clickwise.liqi.nlp.classify.svm.singlehier.api.SingleHierSVMClassify;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTag;

/**
 * 返回不同的分类模型 
 * @author lq
 *
 */
public class ModelClassifyFactory {
	
	public static ModelClassify create(Properties prop)
	{
		ModelClassify classifymodel=null;
		System.out.println("classify_model before");
		String classify_model=prop.getProperty("classify_model");
		System.out.println("classify_model:"+classify_model);
		if(classify_model.equals("singlehier_medlda"))
		{
			classifymodel=new SingleMedldaClassify();
			classifymodel.load_config(prop);
		}
		if(classify_model.equals("singlehier_svm"))
		{
			classifymodel=new SingleHierSVMClassify();
			classifymodel.load_config(prop);
		}
		return classifymodel;
	}
	
}
