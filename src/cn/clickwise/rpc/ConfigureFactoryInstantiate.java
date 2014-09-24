package cn.clickwise.rpc;


public class ConfigureFactoryInstantiate {
	
	public static ConfigureFactory getConfigureFactory()
	{
		return new EasyConfigureFactory();
	}
}
