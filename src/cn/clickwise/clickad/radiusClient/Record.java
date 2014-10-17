package cn.clickwise.clickad.radiusClient;

import cn.clickwise.lib.time.TimeOpera;


public class Record {

	private int code;
	
	private int packetIdentifier;
	
	private int length;
	
	private String authenticator;
	
	private int acctStatusType;
	
	private String userName;
	
	private String framedIpAddress;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getPacketIdentifier() {
		return packetIdentifier;
	}

	public void setPacketIdentifier(int packetIdentifier) {
		this.packetIdentifier = packetIdentifier;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
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


	public String getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(String authenticator) {
		this.authenticator = authenticator;
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
		/*
		str+=" code:"+code
			+" packetIdentifier:"+packetIdentifier
			+" length:"+length
			+" userName:"+userName.trim()
			+" framedIpAddress:"+framedIpAddress
			+" acctStatusType:"+acctStatusType;
		*/	
		str=framedIpAddress+"\t"+acctStatusType+"\t"+userName.trim()+"\t"+TimeOpera.getCurrentTime();
			
		return str;
	}
	
}
