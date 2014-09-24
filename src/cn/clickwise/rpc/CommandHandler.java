package cn.clickwise.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class CommandHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String request = exchange.getRequestURI().toString();
		InputStream is = exchange.getRequestBody();
		Command cmd=deserialization(is);
		complie(cmd,exchange);
	}

	
	public Command deserialization(InputStream is)
	{
		ObjectInputStream ois = (ObjectInputStream)is;
		Command cmd=null;
		try{
			cmd=(Command)ois.readObject();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return cmd;
	}
	
	public abstract void complie(Command cmd,HttpExchange exchange);
	
	
}
