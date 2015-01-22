package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;

import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;

public class TestHandler extends Handler{
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String title_str = exchange.getRequestURI().toString();
		System.out.println("title_str:"+title_str);
		title_str = title_str.replaceFirst("\\/test\\?s\\=", "");
		title_str=title_str.trim();
		String de_title = new String(UrlCode.getDecodeUrl(title_str));
		de_title=de_title.trim();
		
		System.out.println("de_title:"+de_title);
		
		String encode_res ="";
		encode_res="测试\n";
		//encode_res = encode_res.replaceAll("\\s+", "");	
		//exchange.sendResponseHeaders(200, encode_res.length());
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		//os.write(encode_res.getBytes());
		pw.println(encode_res);
		pw.flush();
		pw.close();
		os.close();
		
	}
}
