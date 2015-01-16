package cn.clickwise.clickad.radiusReform;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

//byte 数组转换成各种类型的数据
public class BytesTransform {

	public static int byteToInt2(byte[] b) {

		int i = 0;
		try {
			ByteArrayInputStream bintput = new ByteArrayInputStream(b);
			DataInputStream dintput = new DataInputStream(bintput);
			i = dintput.readInt();
			bintput.close();
			dintput.close();
			bintput = null;
			dintput = null;
		} catch (Exception e) {
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
			str += (hv + " ");
			hv=null;
		}
		return str;
	}
	
	
	public static void bytes2str(byte[] b,int len) {
		Buffer.str = "";
		for (int i = 0; i < len; i++) {
			int v = b[i] & 0xFF;
			Buffer.hv = Integer.toHexString(v);
			if (Buffer.hv.length() == 1) {
				Buffer.hv = '0' + Buffer.hv;
			}
			Buffer.str += (Buffer.hv + " ");
			//hv=null;
		}
		//return str;
	}
	
	/**
	 * 补全byte数组至四个字节 不足4个字节的应该从低字节开始置0
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] completeBytes(byte[] b) {
		byte[] cb = new byte[4];
		for (int i = 0; i < 4 - b.length; i++) {
			cb[i] = 0;
		}

		for (int i = 4 - b.length; i < 4; i++) {
			cb[i] = b[i + b.length - 4];
		}

		return cb;
	}

	/*
	 * public static int byteArrayToInt(byte[] b, int offset) { int value= 0;
	 * for (int i = 0; i < 4; i++) { int shift= (4 - 1 - i) * 8; value +=(b[i +
	 * offset] & 0x000000FF) << shift;//往高位游 } return value; }
	 */

}
