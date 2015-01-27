package cn.clickwise.clickad.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class KeyHandler implements HttpHandler{

	private KeyExtract ke=null;

	public KeyExtract getKe() {
		return ke;
	}

	public void setKe(KeyExtract ke) {
		this.ke = ke;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String tag_str = exchange.getRequestURI().toString();
		System.out.println("tag_str:"+tag_str);
		tag_str = tag_str.replaceFirst("\\/key\\?s\\=", "");
		tag_str=tag_str.trim();
		String de_tag = new String(UrlCode.getDecodeUrl(tag_str));
		de_tag=de_tag.replaceAll("_", "#");
		de_tag=de_tag.trim();
		System.out.println("de_tag:" + de_tag);
		String key_str=ke.keyword_extract_noun_ngram_vv_adj(de_tag);
		System.out.println("key_str:"+key_str);
		exchange.sendResponseHeaders(200,0);
		OutputStream os = exchange.getResponseBody();
		OutputStreamWriter osw=new OutputStreamWriter(os,"gbk");
		PrintWriter pw=new PrintWriter(osw);
		//os.write(encode_res.getBytes());
		pw.println(key_str);
		pw.flush();
		pw.close();
		os.close();
		
	}
	
	
}
