package cn.clickwise.clickad.radiusClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import cn.clickwise.lib.string.SSO;

public class RadiusAnalysis {

	public RecordLight analysis(String line) {
		RecordLight rec = new RecordLight();

		String time = line.substring(0, line.indexOf("\t"));
		rec.setTime(time);

		String ufaStr = line.substring(line.indexOf("\t"), line.length());

		int ipStart = ufaStr.indexOf("08 06");
		if (ipStart < 0) {
			return null;
		}
		int ipEnd = ipStart + 17;
		String ip = ufaStr.substring(ipStart, ipEnd);

		int statusStart = ufaStr.indexOf("28 06");
		if (statusStart < 0) {
			return null;
		}
		int statusEnd = statusStart + 17;
		
		String status = ufaStr.substring(statusStart, statusEnd);
		String userName = ufaStr.replaceFirst(ip, "").replaceFirst(status, "");
		
		// byte[] userBuffer = new byte[unl];
		// for (k = 0; k < unl; k++) {
		// userBuffer[k] = body[j++];
		// }
		// rec.setUserName(new String(userBuffer));
		rec.setUserName(hexes2username(userName));

		// framedIpAddress
		// for (k = 0; k < 6; k++) {
		// sixbuffer[k] = body[j++];
		// }
		// rec.setFramedIpAddress(bytes2ip(sixbuffer));
		rec.setFramedIpAddress(hexs2ip(ip));

		// acctStatusType
		// for (k = 0; k < 6; k++) {
		// sixbuffer[k] = body[j++];
		// }
		// rec.setAcctStatusType(bytes2status(sixbuffer));
		rec.setAcctStatusType(hexes2status(status));
		
		if(!(isValidUserName(rec.getUserName())))
		{
			return null;
		}
		
		
		if(!(isValidIp(rec.getFramedIpAddress())))
		{
			return null;
		}
		
		if(!(isValidStatus(rec.getAcctStatusType()+"")))
		{
			return null;
		}

		return rec;
	}

	public boolean isValidUserName(String userName)
	{
	   if(Pattern.matches("[a-zA-Z0-9\\+\\=\\/]*", userName))	
	   {
		   return true;
	   }
	   return false;
	}
	
	public boolean isValidIp(String ip)
	{
	   if(Pattern.matches("[0-9\\.]*", ip))	
	   {
		   return true;
	   }
	   return false;
	}
	
	public boolean isValidStatus(String status)
	{
	   if(Pattern.matches("[0-3]", status))	
	   {
		   return true;
	   }
	   return false;
	}
	
	public String hexs2ip(String hexs) {
		String ip = "";
		if (SSO.tioe(hexs)) {
			return "";
		}
		hexs = hexs.trim();

		String[] tokens = hexs.split("\\s+");
		if (tokens.length != 6) {
			return "";
		}
		ip = Integer.parseInt(tokens[2], 16) + "."
				+ Integer.parseInt(tokens[3], 16) + "."
				+ Integer.parseInt(tokens[4], 16) + "."
				+ Integer.parseInt(tokens[5], 16);
		tokens = null;
		return ip;
	}

	public int hexes2status(String hexs) {
		int status = -1;
		if (SSO.tioe(hexs)) {
			return -1;
		}
		hexs = hexs.trim();

		String[] tokens = hexs.split("\\s+");
		if (tokens.length != 6) {
			return -1;
		}
		status = Integer.parseInt(tokens[5], 16);
		tokens = null;
		return status;
	}

	public String hexes2username(String hexs) {
		String username = "";
		if (SSO.tioe(hexs)) {
			return "";
		}
		hexs = hexs.trim();

		String[] tokens = hexs.split("\\s+");
		if (tokens.length < 2) {
			return "";
		}

		for (int j = 2; j < tokens.length; j++) {
			username += ((char) Integer.parseInt(tokens[j], 16));
		}
		tokens = null;
		return username;
	}

	public static void main(String[] args) throws Exception {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		RadiusAnalysis ra = new RadiusAnalysis();

		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				try {
					RecordLight rl = ra.analysis(line);
					if (rl == null) {
						continue;
					}
					pw.println(rl.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		br.close();
		pw.close();

	}
}
