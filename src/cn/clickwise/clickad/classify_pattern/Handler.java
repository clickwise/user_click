package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handler implements HttpHandler{

	Classifier classifer=null;
	
	public void setClassifer(Classifier c)
	{
		classifer=c;
	}
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
