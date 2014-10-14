package cn.clickwise.clickad.radiusClient;

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
	
	
}
