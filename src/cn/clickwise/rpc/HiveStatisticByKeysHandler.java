package cn.clickwise.rpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;

import cn.clickwise.lib.linux.COMMAND;
import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpExchange;

public class HiveStatisticByKeysHandler extends Handler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		HiveStatisticByKeysCommand hfkc = HiveStatisticByKeysCommand.readObject(SSO
				.afterStr(uri.toString(), "method="));
		complie(hfkc, exchange);

	}

	public void complie(Command cmd, HttpExchange exchange) {

		HiveStatisticByKeysCommand hfkc = (HiveStatisticByKeysCommand) cmd;

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
		
		COMMAND.exec(HiveSql.createTableStatistic(hfkc));
		COMMAND.exec(HiveSql.dropOldStatistic(hfkc));
		COMMAND.exec(HiveSql.load2hiveStatistic(hfkc));
		//System.out.println("exec sql:"+HiveSql.getSqlStatistic(hfkc));
		COMMAND.exec(HiveSql.getSqlStatistic(hfkc));
		
		
		try {
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();

			File resDir = new File(hfkc.getResultRemotePath());
			File[] subFiles = resDir.listFiles();

			ArrayList<String> resList=new ArrayList<String>();
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
					if(SSO.tioe(resline))
					{
						continue;
					}
					resList.add(resline);
				}
				String resContent="";
				for(int i=0;i<resList.size();i++)
				{
					resContent=resContent+resList.get(i)+"\n";
				}
				resContent=resContent.trim();
				os.write(new String(resContent + "\n").getBytes());
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
	
	public void cleanTmpWorkplace(HiveStatisticByKeysCommand hfkc)
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
