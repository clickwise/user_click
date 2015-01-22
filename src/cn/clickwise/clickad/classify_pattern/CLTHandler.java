package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

public class CLTHandler extends Handler{
	
	ClassifierLayerThree cf = null;
	
	public CLTHandler()
	{
		 new ClassifierLayerThree();
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String title_str = exchange.getRequestURI().toString();
		title_str = title_str.replaceFirst("\\/ctb\\?s\\=", "");
		title_str=title_str.trim();
		
		String cate_str=cf.cate(title_str);
		cate_str=cate_str.trim();
		
		exchange.sendResponseHeaders(200, cate_str.length());
		OutputStream os = exchange.getResponseBody();
		os.write(cate_str.getBytes());
		os.close();
		
	}
	
	
}
