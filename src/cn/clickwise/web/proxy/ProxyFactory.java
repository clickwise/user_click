package cn.clickwise.web.proxy;

public class ProxyFactory {

	public static UrlHandler getUrlHandler()
	{
		if(ProxyConfig.handler_type==0)
		{
			return new UrlFetchHandler();
		}
		
		return new UrlHandler();
	}
	
	public static UrlPond getUrlPond()
	{
		return new QueueUrlPond();
	}
}
