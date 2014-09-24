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

public abstract class CommandHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		//String request = exchange.getRequestURI().toString();
		//System.out.println("request:"+request);
		
		InputStream is = exchange.getRequestBody();
        InputStreamReader isr=new InputStreamReader(is);
        BufferedReader br=new BufferedReader(isr);
        String line="";
        String content="";
        while((line=br.readLine())!=null)
        {
        	content+=line;
        	
        }
        try{
        FileWriter fw=new FileWriter("/tmp/test_serial.txt");
        PrintWriter pw=new PrintWriter(fw);
        pw.println(content);
        pw.close();
        fw.close();
        FileInputStream fis = new FileInputStream("/tmp/test_serial.txt");
        ObjectInputStream ois = new ObjectInputStream(fis);
		Command result=(Command)ois.readObject();
		FileStatusCommand fresult=(FileStatusCommand)result;
		System.out.println("get fresult name:"+fresult.getName());
		System.out.println("get fresult path:"+fresult.getPath());
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
		//exchange.setAttribute("", "");
		//cmd=deserialization(is);
		//complie(cmd,exchange);
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
