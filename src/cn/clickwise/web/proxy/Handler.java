package cn.clickwise.web.proxy;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handler implements HttpHandler{

	public Handler()
	{
		super();
		init();
	}
	
	public  void init(){
		
	}
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		// TODO Auto-generated method stub
		System.err.println("inhandler");
	}
	
	

}
