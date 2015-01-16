package cn.clickwise.clickad.radiusReform;
/**
 * 各种buffer 数组
 * @author zkyz
 */
public class Buffer {

	//接收packet 的头信息
	public static byte[] head = new byte[16];
	
	//接收packet 的体信息
	public static byte[] body = new byte[256];
	
	//body的总长度=bodylen+12
	public static  int packetbodylen;
	
	//body的实际长度
	public static  int bodylen;
	
	//实际读取的body长度
	public static int rn=0;
	
	//dbuffer
	public static byte[] dbuffer=new byte[4];
	
	//stbuffer
	public static byte[] stbuffer = new byte[16];
	
	//fbuffer
	public static byte[] fbyte=new byte[4];
	
	//user ip state
	public static byte[] ufa = new byte[256];
	
	public static int recLen;
	
	public static int unl;
	
	public static int ufalen=0;
	
	public static String str="";
	
	public static  String hv="";
	
}
