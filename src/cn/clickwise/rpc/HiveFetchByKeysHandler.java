package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

import cn.clickwise.lib.linux.COMMAND;
import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpExchange;

public class HiveFetchByKeysHandler extends Handler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		HiveFetchByKeysCommand hfkc = HiveFetchByKeysCommand.readObject(SSO.afterStr(
				uri.toString(), "method="));
		complie(hfkc, exchange);
		
	}

	public void complie(Command cmd, HttpExchange exchange) {

		HiveFetchByKeysCommand hfkc = (HiveFetchByKeysCommand) cmd;

		InputStream is = exchange.getRequestBody();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		try {
			FileWriter fw = new FileWriter(hfkc.getRemoteTmpPath());
			PrintWriter pw = new PrintWriter(fw);

			String line="";
			while((line=br.readLine())!=null)
			{
				pw.println(line);
			}
			
			fw.close();
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String hiveSql=HiveSql.getSql(hfkc);
		
		COMMAND.exec(hiveSql);
		
		
		
	}
	
}
