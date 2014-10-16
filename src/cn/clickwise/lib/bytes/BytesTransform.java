package cn.clickwise.lib.bytes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

//byte 数组转换成各种类型的数据
public class BytesTransform {
    
	//不足4位的应该从低字节开始置0
	public static int byteToInt2(byte[] b) {
        /*
		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < b.length; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		*/
		int i=0;
		try{
		ByteArrayInputStream bintput = new ByteArrayInputStream(b);
		DataInputStream dintput = new DataInputStream(bintput);
		 i = dintput.readInt();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return i;
	}
	
	public static int byteToIntv(byte[] b) {
        
		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < b.length; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
	
		return n;

	}
	public static String bytes2str(byte[] b) {
		String str = "";
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() == 1) {
				hv = '0' + hv;
			}
			str+=(hv+" ");
		}
		return str;
	}
	
	public static int byteArrayToInt(byte[] b, int offset) {
	       int value= 0;
	       for (int i = 0; i < 4; i++) {
	           int shift= (4 - 1 - i) * 8;
	           value +=(b[i + offset] & 0x000000FF) << shift;//往高位游
	       }
	       return value;
	 }

}
