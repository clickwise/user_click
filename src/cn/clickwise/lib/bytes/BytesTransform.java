package cn.clickwise.lib.bytes;

//byte 数组转换成各种类型的数据
public class BytesTransform {

	public static int byteToInt2(byte[] b) {

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

}
