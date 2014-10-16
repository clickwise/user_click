package cn.clickwise.clickad.radiusClient;

import cn.clickwise.lib.bytes.BytesTransform;

public class Recordn {
	
    private byte[] code;
	
	private byte[] packetIdentifier;
	
	private byte[] length;
	
	private byte[] authenticator;
	
	private byte[] acctStatusType;
	
	private byte[] userName;
	
	private byte[] framedIpAddress;

	
	public byte[] getCode()
	{
		return code;
	}
	
	public void setCode(byte[] code)
	{
		this.code=code;
	}
	
	
	public byte[] getPacketIdentifier()
	{
		return packetIdentifier;
	}
	
	public void setPacketIdentifier(byte[] packetIdentifier)
	{
		this.packetIdentifier=packetIdentifier;
	}
	
	public byte[] getLength()
	{
		return length;
	}
	
	public void setLength(byte[] length)
	{
		this.length=length;
	}
	
	public byte[] getAuthenticator()
	{
		return authenticator;
	}
	
	public void setAuthenticator(byte[] authenticator)
	{
		this.authenticator=authenticator;
	}
	
	public byte[] getAcctStatusType()
	{
		return acctStatusType;
	}
	
	public void setAcctStatusType(byte[] acctStatusType)
	{
		this.acctStatusType=acctStatusType;
	}

	
	public byte[] getUserName()
	{
		return userName;
	}
	
	public void setUserName(byte[] userName)
	{
		this.userName=userName;
	}


	public byte[] getFramedIpAddress() {
		return framedIpAddress;
	}

	public void setFramedIpAddress(byte[] framedIpAddress) {
		this.framedIpAddress = framedIpAddress;
	}
	
	public String toString()
	{
		String str="";
		str+=" code:"+BytesTransform.bytes2str(code)
			+" packetIdentifier:"+BytesTransform.bytes2str(packetIdentifier)
			+" length:"+BytesTransform.bytes2str(length)
			+" authenticator:"+BytesTransform.bytes2str(authenticator)
			+" userName:"+BytesTransform.bytes2str(userName)
			+" framedIpAddress:"+BytesTransform.bytes2str(framedIpAddress)
			+" acctStatusType:"+BytesTransform.bytes2str(acctStatusType);
		
		return str;
	}
	
	
}
