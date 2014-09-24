package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class CommandHandler extends Handler  {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//String request = exchange.getRequestURI().toString();
		//System.out.println("request:"+request);
		
		InputStream is = exchange.getRequestBody();

		//exchange.setAttribute("", "");
		Command cmd=deserialization(is);
		complie(cmd,exchange);
	}

	
	public Command deserialization(InputStream is)
	{
		System.out.println("in deserialization");
		//System.out.println(is.toString());
	
	      
		Command cmd=null;
		try{
			
			//byte[] bytes=new byte[10000];
			//is.read(bytes);
			//ByteArrayInputStream bis = new ByteArrayInputStream (bytes);  
			ObjectInputStream ois = new ObjectInputStream(is);
			//System.out.println(ois.toString());
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
