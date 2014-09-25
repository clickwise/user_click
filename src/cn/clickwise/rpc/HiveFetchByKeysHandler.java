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

public class HiveFetchByKeysHandler extends Handler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		HiveFetchByKeysCommand hfkc = HiveFetchByKeysCommand.readObject(SSO
				.afterStr(uri.toString(), "method="));
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

			String line = "";
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}

			fw.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
        COMMAND.exec(HadoopCmd.mkParent(hfkc.getHdfTmpPath()));
		COMMAND.exec(HadoopCmd.load2hdfs(hfkc.getRemoteTmpPath(), hfkc.getHdfTmpPath()));
		COMMAND.exec(HiveSql.createTable(hfkc));
		COMMAND.exec(HiveSql.dropOld(hfkc));
		COMMAND.exec(HiveSql.load2hive(hfkc));
		COMMAND.exec(HiveSql.getSql(hfkc));
			
		try {
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();

			File resDir = new File(hfkc.getResultRemotePath());
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
			cleanTmpWorkplace(hfkc);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void cleanTmpWorkplace(HiveFetchByKeysCommand hfkc)
	{
		if(SSO.tioe(hfkc.getTmpIdentify()))
		{
			return;
		}
		File remoteTmp=new File(hfkc.getRemoteTmpPath());
		
		if(remoteTmp.getName().indexOf(hfkc.getTmpIdentify())>-1)
		{
			remoteTmp.delete();
		}
		
		File resultRemote=new File(hfkc.getResultRemotePath());
		if(resultRemote.getName().indexOf(hfkc.getTmpIdentify())>-1)
		{
			COMMAND.exec(FileCommand.deleteDirectory(resultRemote.getAbsolutePath()));
		}
		
	}

}
