package cn.clickwise.clickad.feathouse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class TestHandler extends Handler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		String uid = SSO.afterStr(uri.toString(), "uid=");
		query(uid,exchange);
	}

	@Override
	public void setCassandraQuery(CassandraQuery cq) {
		// TODO Auto-generated method stub
		
	}
	
	public void query(String uid,HttpExchange exchange){
		OutputStream os;
		try{
			
			Headers headers=exchange.getResponseHeaders();
			//exchange.getResponseHeaders().add("Content-type", "text/plain; charset=utf-8");
			
			headers.set("Content-type", "text/plain; charset=utf-8");
			String encode="";
			String resultstr="测试";
			String s="Content-typetext/plain;charset=utf-8";

			exchange.sendResponseHeaders(200, (long)(resultstr.getBytes().length));
			os = exchange.getResponseBody();
			os.write(new String(resultstr).getBytes());
			
			os.flush();
			//PrintWriter pw=new PrintWriter(os);
			//pw.println(resultstr);
			
			os.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	

}
