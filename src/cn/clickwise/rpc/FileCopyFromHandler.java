package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.File;


import com.sun.net.httpserver.HttpExchange;

public class FileCopyFromHandler extends Handler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {

		InputStream is = exchange.getRequestBody();
		Command cmd = deserialization(is);
		complie(cmd, exchange);

	}

	public Command deserialization(InputStream is) {

		Command cmd = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			cmd = (Command) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cmd;
	}

	public void complie(Command cmd, HttpExchange exchange) {

		FileCopyFromCommand fcc = (FileCopyFromCommand) cmd;

		try {
			File test = new File(fcc.getRemotePath());
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			if (test.exists()) {
				FileInputStream fis = new FileInputStream(fcc.getRemotePath());
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);

				String line = "";
				while ((line = br.readLine()) != null) {
					os.write(new String(line + "\n").getBytes());
				}
				fis.close();
				isr.close();
				br.close();
				os.close();
			} else {
				os.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
