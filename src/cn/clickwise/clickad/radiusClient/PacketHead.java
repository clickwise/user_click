package cn.clickwise.clickad.radiusClient;

import cn.clickwise.lib.bytes.BytesTransform;

public class PacketHead {

	private byte[] head;
	
	private int packetBodyLength;

	private String sourceIp;
	
	private String destIp;
	
	private int sourcePort;
	
	private int destPort;
	
	public PacketHead()
	{
		
	}
	
	public PacketHead(byte[] head)
	{
		this.head=head;
	}
	
	/**
	 * 从head byte数组解析出各个字段
	 */
	public void parseBytes2Info()
	{
		byte[] dbyte=new byte[2];
		byte[] fbyte=new byte[4];
		
		int pos=0;
		
		//packet body length
		for(int i=0;i<4;i++)
		{
			fbyte[i]=head[pos++];
		}	
		setPacketBodyLength(BytesTransform.byteToInt2(fbyte));
		//setPacketBodyLength(BytesTransform.byteToIntv(fbyte));
		//source ip
		for(int i=0;i<4;i++)
		{
			fbyte[i]=head[pos++];
		}
		setSourceIp(new String(fbyte));
		
		//dest ip
		for(int i=0;i<4;i++)
		{
			fbyte[i]=head[pos++];
		}
		setDestIp(new String(fbyte));
		
		//source port
		for(int i=0;i<2;i++)
		{
			dbyte[i]=head[pos++];
		}
		setSourcePort(BytesTransform.byteToIntv(dbyte));
		
		//dest port
		for(int i=0;i<2;i++)
		{
			dbyte[i]=head[pos++];
		}
		setDestPort(BytesTransform.byteToIntv(dbyte));
		
		dbyte=null;
		fbyte=null;
		
	}
     
    
	public byte[] getHead() {
		return head;
	}

	public void setHead(byte[] head) {
		this.head = head;
	}

	public int getPacketBodyLength() {
		return packetBodyLength;
	}

	public void setPacketBodyLength(int packetBodyLength) {
		this.packetBodyLength = packetBodyLength;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getDestIp() {
		return destIp;
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	public int getDestPort() {
		return destPort;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}
	
	
	public void destroy()
	{
		head=null;
		sourceIp=null;
		destIp=null;
	}
	
}
