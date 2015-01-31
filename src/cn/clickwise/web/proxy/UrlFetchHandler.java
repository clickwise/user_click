package cn.clickwise.web.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpExchange;

public class UrlFetchHandler extends UrlHandler{
	
	UrlPond urlPond;
	
	public UrlFetchHandler()
	{
		
	}
	
	public void init()
	{
		urlPond=ProxyFactory.getUrlPond();
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String req_str = exchange.getRequestURI().toString();
		System.err.println("req_str:"+req_str);
		
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		
		String url=urlPond.pollFromPond();
		if(SSO.tioe(url))
		{
		  url="empty";	
		}
		
		pw.println(url);
		pw.flush();
		pw.close();
		os.close();
	}
	

	
}
