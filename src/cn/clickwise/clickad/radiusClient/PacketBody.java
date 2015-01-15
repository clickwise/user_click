package cn.clickwise.clickad.radiusClient;

public class PacketBody {

	private byte[] body;
	
	
	public PacketBody()
	{
		
	}
	
	public PacketBody(byte[] body)
	{
		this.setBody(body);
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public void destroy()
	{
		body=null;
	}
	
}
