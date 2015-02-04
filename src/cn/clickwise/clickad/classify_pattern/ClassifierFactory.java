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
		else if(ClassifierConfig.model_type==3)
		{
			return new ParamsMulticlass();
		}
		
		return null;
	}
	

	public static CallMap getCallMap()
	{
		if(ClassifierConfig.model_type==0)
		{
			return new CallMap("/test",new TestHandler());
		}
		else if(ClassifierConfig.model_type==2)
		{
			return new CallMap("/ctb",new CLTHandler());
			//return new CallMap("/ctb",null);
		}
		else if(ClassifierConfig.model_type==3)
		{
			return new CallMap("/cwb",new WeiboHandler());
		}
		return null;
	}
}
