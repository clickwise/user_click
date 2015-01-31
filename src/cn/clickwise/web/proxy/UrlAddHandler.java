package cn.clickwise.web.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpExchange;

public class UrlAddHandler extends UrlHandler{

	UrlPond urlPond;
	
	public UrlAddHandler()
	{
		super();
	}
	
	public void init()
	{
		urlPond=ProxyFactory.getUrlPond();
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.err.println("in url add handler");
		String req_str = exchange.getRequestURI().toString();
		System.err.println("req_str:"+req_str);
		String url=req_str.replaceFirst("\\/add\\?s\\=", "");
		urlPond.add2Pond(url);
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		pw.println("ok");
		pw.flush();
		pw.close();
		os.close();
	}
}
