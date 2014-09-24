package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpExchange;

public class FileCopyToHandler extends Handler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		FileCopyToCommand fcc = FileCopyToCommand.readObject(SSO.afterStr(
				uri.toString(), "method="));
		complie(fcc, exchange);
	}

	public void complie(Command cmd, HttpExchange exchange) {
		FileCopyToCommand fcc = (FileCopyToCommand) cmd;

		InputStream is = exchange.getRequestBody();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		try {
			FileWriter fw = new FileWriter(fcc.getRemotePath());
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
		
		State state=new State();
		state.setState(0);
		
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try {
			exchange.sendResponseHeaders(200, 0);
			os = exchange.getResponseBody();
			oos = new ObjectOutputStream(os);
			oos.writeObject(state);
			oos.flush();
			//oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
