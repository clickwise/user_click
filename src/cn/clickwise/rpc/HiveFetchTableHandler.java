package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import cn.clickwise.lib.linux.COMMAND;

import com.sun.net.httpserver.HttpExchange;

public class HiveFetchTableHandler extends Handler{

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
	
	public void complie(Command cmd, HttpExchange exchange) 
	{
		HiveFetchTableCommand hftc=(HiveFetchTableCommand)cmd;
		
		System.out.println("hftc:"+hftc.toString());
	
		COMMAND.exec(HiveSql.getSql(hftc));
		
		try {
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();

			File resDir = new File(hftc.getResultRemotePath());
			File[] subFiles = resDir.listFiles();

			for (int j = 0; j < subFiles.length; j++) {
				if(!(FileName.isValidResult(subFiles[j])))
				{
					continue;
				}
				FileInputStream resfis = new FileInputStream(subFiles[j]);
				InputStreamReader resisr = new InputStreamReader(resfis);
				BufferedReader resbr = new BufferedReader(resisr);

				String resline = "";
				while ((resline = resbr.readLine()) != null) {
					os.write(new String(resline + "\n").getBytes());
				}
				resfis.close();
				resisr.close();
				resbr.close();
			}
			cleanTmpWorkplace(hftc);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	public void cleanTmpWorkplace(HiveFetchTableCommand hftc)
	{
		File resultRemote=new File(hftc.getResultRemotePath());
		if(resultRemote.getName().indexOf(hftc.getTmpIdentify())>-1)
		{
			COMMAND.exec(FileCommand.deleteDirectory(resultRemote.getAbsolutePath()));
		}
	}
}
