package cn.clickwise.clickad.radiusReform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

public class RecordLight {
	
	private String time;
	
	private int acctStatusType;
	
	private String userName;
	
	private String framedIpAddress;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getAcctStatusType() {
		return acctStatusType;
	}

	public void setAcctStatusType(int acctStatusType) {
		this.acctStatusType = acctStatusType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFramedIpAddress() {
		return framedIpAddress;
	}

	public void setFramedIpAddress(String framedIpAddress) {
		this.framedIpAddress = framedIpAddress;
	}
	
	public String toString()
	{
		String str="";
		str=framedIpAddress+"\t"+acctStatusType+"\t"+userName.trim()+"\t"+time;
			
		return str;
	}
	
	
	
}
