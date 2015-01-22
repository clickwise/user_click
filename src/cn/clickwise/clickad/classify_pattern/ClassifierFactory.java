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
	

	public static CallMap getCallMap()
	{
		if(ClassifierConfig.model_type==0)
		{
		    
		}
		else if(ClassifierConfig.model_type==2)
		{
			return new CallMap("ctb",new CLTHandler());
		}
		
		return null;
	}
}
