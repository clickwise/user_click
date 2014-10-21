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

import cn.clickwise.lib.linux.COMMAND;
import cn.clickwise.lib.string.SSO;

import com.sun.net.httpserver.HttpExchange;

public class HiveStatisticByKeysHandler extends Handler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		HiveStatisticByKeysCommand hskc = HiveStatisticByKeysCommand.readObject(SSO
				.afterStr(uri.toString(), "method="));
		complie(hskc, exchange);
		
	}

	public void complie(Command cmd, HttpExchange exchange) {

		HiveStatisticByKeysCommand hskc = (HiveStatisticByKeysCommand) cmd;

		InputStream is = exchange.getRequestBody();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		try {
			FileWriter fw = new FileWriter(hskc.getRemoteTmpPath());
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
		
        COMMAND.exec(ProxyCommand.pvUvIpByKeys(hskc.getDay(), hskc.getRemoteTmpPath(), hskc.getResultRemotePath()));
		
		try {
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();

			//将统计的地区编号、地区、时间、pv、uv、ip等写入Receipt对象
			ObjectOutputStream oos = new ObjectOutputStream(os);
			RpcReceipt receipt=new RpcReceipt();
			
			File resDir = new File(hskc.getResultRemotePath());
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
					if(SSO.tioe(resline))
					{
						continue;
					}
					receipt=line2receipt(resline);
					oos.writeObject(receipt);
					//os.write(new String(resline + "\n").getBytes());
				}
				resfis.close();
				resisr.close();
				resbr.close();
			}
			cleanTmpWorkplace(hskc);
			os.close();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void cleanTmpWorkplace(HiveStatisticByKeysCommand hskc)
	{
		if(SSO.tioe(hskc.getTmpIdentify()))
		{
			return;
		}
		File remoteTmp=new File(hskc.getRemoteTmpPath());
		
		if(remoteTmp.getName().indexOf(hskc.getTmpIdentify())>-1)
		{
			remoteTmp.delete();
		}
		
		File resultRemote=new File(hskc.getResultRemotePath());
		if(resultRemote.getName().indexOf(hskc.getTmpIdentify())>-1)
		{
			COMMAND.exec(FileCommand.deleteDirectory(resultRemote.getAbsolutePath()));
		}	
	}
	
	public RpcReceipt line2receipt(String line)
	{
		return null;
	}
	
}
