package cn.clickwise.rpc;

import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class CommandHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
	}

	public abstract Command deserialization(InputStream is);
	
	public abstract void complie(Command cmd,HttpExchange exchange);
	
	
}
