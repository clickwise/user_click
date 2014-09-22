package cn.clickwise.lib.code;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Code {
	public static String Md5(String plainText) {
		String md5Text="";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			md5Text=buf.toString();
			//System.out.println("result: " + buf.toString());// 32位的加密

			// System.out.println("result: " +
			// buf.toString().substring(8,24));//16位的加密

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return md5Text;	
	}
	
	public static String makeMD5(String password) {   
		MessageDigest md;   
		   try {   
		    // 生成一个MD5加密计算摘要   
		    md = MessageDigest.getInstance("MD5");   
		    // 计算md5函数   
		    md.update(password.getBytes());   
		    // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符   
		    // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值   
		    String pwd = new BigInteger(1, md.digest()).toString(16);   
		    System.err.println(pwd);   
		    return pwd;   
		   } catch (Exception e) {   
		    e.printStackTrace();   
		   }   
		   return password;   
	}
	
	public static void main(String[] args)
	{
		String plainText="游戏";
		System.out.println(MD5Code.Md5(plainText));
		System.out.println(MD5Code.makeMD5(plainText));
	}
}
