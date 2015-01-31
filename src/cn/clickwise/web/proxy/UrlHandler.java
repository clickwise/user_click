package cn.clickwise.web.proxy;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class UrlHandler extends Handler{

	public UrlHandler()
	{
		super();
	}
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		System.err.println("in url  handler");
		
	}

}
