package cn.clickwise.web.proxy;

public class ProxyFactory {

	public static UrlHandler getUrlHandler()
	{
		if(ProxyConfig.handler_type==0)
		{
			return new UrlFetchHandler();
		}
		else if(ProxyConfig.handler_type==1)
		{
			return new UrlAddHandler();
		}
		
		return new UrlHandler();
	}
	
	public static UrlPond getUrlPond()
	{
		return new QueueUrlPond();
	}
	
	public static String getMethod()
	{
		if(ProxyConfig.handler_type==0)
		{
			return "/fetch";
		}
		else if(ProxyConfig.handler_type==1)
		{
			return "/add";
		}
		
		return "";
	}
}
