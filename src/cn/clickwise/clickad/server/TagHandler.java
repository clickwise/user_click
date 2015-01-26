package cn.clickwise.clickad.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class TagHandler implements HttpHandler{

	private PosTagger posTagger=null;

	public PosTagger getPosTagger() {
		return posTagger;
	}

	public void setPosTagger(PosTagger posTagger) {
		this.posTagger = posTagger;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String seg_str = exchange.getRequestURI().toString();
		System.out.println("seg_str:"+seg_str);
		seg_str = seg_str.replaceFirst("\\/tag\\?s\\=", "");
		seg_str=seg_str.trim();
		String de_seg = new String(UrlCode.getDecodeUrl(seg_str));
		de_seg=de_seg.trim();
		System.out.println("de_seg:" + de_seg);
		String tag_str=posTagger.tag(de_seg);
		tag_str=tag_str.replaceAll("#", "_");
		System.out.println("tag_str:"+tag_str);
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		//os.write(encode_res.getBytes());
		pw.println(tag_str);
		pw.flush();
		pw.close();
		os.close();
		
	}
	
}
