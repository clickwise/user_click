package cn.clickwise.clickad.classify_pattern;

public class ClassifierFactory {

	public static ModelParams getModelParams()
	{
		if(ClassifierConfig.model_type==0)
		{
		  return new ParamsMulticlass();
		}
		else if(ClassifierConfig.model_type==2)
		{
			return new ParamsThreeLayer();
		}
		
		return null;
	}
}
