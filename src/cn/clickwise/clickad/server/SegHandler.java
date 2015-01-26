package cn.clickwise.clickad.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class SegHandler implements HttpHandler{
	
	private Segmenter segmenter=null;

	public Segmenter getSegmenter() {
		return segmenter;
	}

	public void setSegmenter(Segmenter segmenter) {
		this.segmenter = segmenter;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
	
		String title_str = exchange.getRequestURI().toString();
		System.out.println("title_str:"+title_str);
		title_str = title_str.replaceFirst("\\/seg\\?s\\=", "");
		title_str=title_str.trim();
		String de_title = new String(UrlCode.getDecodeUrl(title_str));
		de_title=de_title.trim();
		System.out.println("de_title:" + de_title);
		String seg_str=segmenter.segAnsi(de_title);
		System.out.println("seg_str:"+seg_str);
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		//os.write(encode_res.getBytes());
		pw.println(seg_str);
		pw.flush();
		pw.close();
		os.close();
		
	}

}
