package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;

public class WeiboHandler extends Handler{
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String user_info = exchange.getRequestURI().toString();
		System.out.println("user_info:"+user_info);
		user_info = user_info.replaceFirst("\\/cwb\\?s\\=", "");
		user_info=user_info.trim();
		String de_info = new String(UrlCode.getDecodeUrl(user_info));
		de_info=de_info.trim();
		System.out.println("de_title:" + de_info);
		//System.out.println("de_title.len:"+de_title.length());
		String cate_str="";
		if(de_info.length()>20)
		{
		  cate_str=classifer.cate(de_info);
		  cate_str=cate_str.trim();
		  System.err.println("cate_str:"+cate_str);
		}
		else
		{
			cate_str="title信息不足";
		}
		
		cate_str="ok";
		System.out.println("cate_str:"+cate_str);

		//encode_res = encode_res.replaceAll("\\s+", "");	
		//exchange.sendResponseHeaders(200, encode_res.length());
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		//os.write(encode_res.getBytes());
		pw.println(cate_str);
		pw.flush();
		pw.close();
		os.close();
		
	}
}
