package cn.clickwise.clickad.classify_pattern;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

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
		//System.out.println("de_title.len:"+de_title.length());
		String cate_str="";
		if(de_title.length()>20)
		{
		  cate_str=classifer.cate(de_title);
		  cate_str=cate_str.trim();
		}
		else
		{
			cate_str="title信息不足";
		}
		
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
