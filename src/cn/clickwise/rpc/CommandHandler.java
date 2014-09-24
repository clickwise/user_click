package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class CommandHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//String request = exchange.getRequestURI().toString();
		//System.out.println("request:"+request);
		
		InputStream is = exchange.getRequestBody();
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader br=new BufferedReader(isr);
		String content="";
		String line="";
		while((line=br.readLine())!=null)
		{
			content+=line;
		}
		System.out.println("content:"+content);
		//exchange.setAttribute("", "");
		Command cmd=deserialization(is);
		complie(cmd,exchange);
	}

	
	public Command deserialization(InputStream is)
	{
		System.out.println("in deserialization");
		System.out.println(is.toString());
	
		Command cmd=null;
		try{
			ObjectInputStream ois = (ObjectInputStream)is;
			System.out.println(ois.toString());
			cmd=(Command)ois.readObject();
			
		}
		catch(Exception e)
		{
			System.out.println("error");
			e.printStackTrace();
		}
		
		System.out.println("end");
		
		return cmd;
	}
	
	public abstract void complie(Command cmd,HttpExchange exchange);
	
	
}
