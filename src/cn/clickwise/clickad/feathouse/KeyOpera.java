package cn.clickwise.clickad.feathouse;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.clickwise.lib.string.SSO;

/**
 * 用户名的系列操作
 * 
 * @author zkyz
 */
public class KeyOpera {

	public static Area getAreaFromUid(String uid) {
		// to do
		return null;
	}
	
	public static String getAreaCodeFromUid(String uid) {
		
		if(SSO.tioe(uid))
		{
			return "";
		}
	
		uid=uid.trim();
		if(uid.length()!=35)
		{
			return "";
		}
		return uid.substring(uid.length()-3,uid.length());
	}

	public static String areaDayKey(int day, Area area) {
		
		return "rtb_query_"+area.getAreaCode();
	}
	
	//public static String areaCodeDayKey(int day, String areaCode) {
		
	//	return "rtb_query_"+areaCode+"_"+day;
	//}

	public static String areaCodeDayKeyPV(int day, String areaCode) {
		
		return "rtb_query_pv_"+areaCode+"_"+day;
	}
	
	public static String areaCodeDayKeyUV(int day, String areaCode) {
		
		return "rtb_query_uv_"+areaCode+"_"+day;
	}
	
	/**
	 * 从
	 * @param AreaDayUVKey
	 * @return
	 */
	public static String getCodeOfAreaFromAreaDayKeyUV(String AreaDayKeyUV)
	{
		String codeOfArea="";
		String pats="rtb_query_uv_(.*?)_[0-9]*";
		Pattern pat=Pattern.compile(pats);
		Matcher mat=pat.matcher(AreaDayKeyUV);
		if(mat.find())
		{
			codeOfArea=mat.group(1);
		}
		
		return codeOfArea;
	}
	
	public static String getTimeColunm() {
		long score = (long) ((System.currentTimeMillis()) + (Math.random() * 1000));
		return score + "";
	}

	/**
	 * String 转换 ByteBuffer
	 * 
	 * @param str
	 * @return
	 */
	public static ByteBuffer getByteBuffer(String str) {
		return ByteBuffer.wrap(str.getBytes());
	}

	/**
	 * ByteBuffer 转换 String
	 * 
	 * @param buffer
	 * @return
	 */
	public static String getString(ByteBuffer buffer) {
		Charset charset = null;
		CharsetDecoder decoder = null;
		CharBuffer charBuffer = null;
		try {
			charset = Charset.forName("UTF-8");
			decoder = charset.newDecoder();
			// charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
			charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
			return charBuffer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

}
