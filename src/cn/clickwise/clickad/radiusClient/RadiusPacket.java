package cn.clickwise.clickad.radiusClient;

/**
 * radius 接收的报文块
 * @author zkyz
 */
public class RadiusPacket {

	private PacketHead packHead;
	
	private PacketBody packBody;

	public PacketHead getPackHead() {
		return packHead;
	}

	public void setPackHead(PacketHead packHead) {
		this.packHead = packHead;
	}

	public PacketBody getPackBody() {
		return packBody;
	}

	public void setPackBody(PacketBody packBody) {
		this.packBody = packBody;
	}
	
	public void destroy()
	{
		if(packHead!=null)
		{
		 packHead.destroy();
		}
		packHead=null;
		if(packBody!=null)
		{
		 packBody.destroy();
		}
		packBody=null;
	}
}
