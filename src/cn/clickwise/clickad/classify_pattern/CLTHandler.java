package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;
import java.io.OutputStream;

import cn.clickwise.liqi.str.edcode.UrlCode;

import com.sun.net.httpserver.HttpExchange;

public class CLTHandler extends Handler{
	
	//ClassifierLayerThree cf = null;
	
	/*
	public CLTHandler()
	{
		 new ClassifierLayerThree();
	}
	*/
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String title_str = exchange.getRequestURI().toString();
		System.out.println("title_str:"+title_str);
		title_str = title_str.replaceFirst("\\/ctb\\?s\\=", "");
		title_str=title_str.trim();
		String de_title = new String(UrlCode.getDecodeUrl(title_str));
		de_title=de_title.trim();
		System.out.println("de_title:" + de_title);
		String cate_str=classifer.cate(de_title);
		cate_str=cate_str.trim();
		
		System.out.println("cate_str:"+cate_str);
		exchange.sendResponseHeaders(200, cate_str.length());
		OutputStream os = exchange.getResponseBody();
		os.write(cate_str.getBytes());
		os.close();
		
	}
	
	
}
