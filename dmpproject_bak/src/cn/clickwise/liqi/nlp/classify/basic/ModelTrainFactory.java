package cn.clickwise.liqi.nlp.classify.basic;

import java.util.Properties;

import cn.clickwise.liqi.nlp.classify.medlda.singlehier.api.SingleMedldaClassify;
import cn.clickwise.liqi.nlp.classify.svm.singlehier.api.SingleHierSVMClassify;
import cn.clickwise.liqi.nlp.classify.svm.singlehier.api.SingleHierSVMTrain;

public class ModelTrainFactory {

	public static ModelTrain create(Properties prop)
	{
		ModelTrain mt=null;
		String classifier_train_model=prop.getProperty("classifier_train_model");
		if(classifier_train_model.equals("singlehier_medlda"))
		{
			//mt=new SingleMedldaClassify();
			//classifymodel.load_config(prop);
		}
		if(classifier_train_model.equals("singlehier_svm"))
		{
			mt=new SingleHierSVMTrain();
			try{
			mt.load_config(prop);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		return mt;
	}
	
}
