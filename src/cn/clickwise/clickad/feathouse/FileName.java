package cn.clickwise.clickad.feathouse;

import java.io.File;
import java.util.Date;

/**
 * 文件名的一些操作
 * @author zkyz
 */
public class FileName {

	public static String getSubFileName(File root,String subName)
	{
		return root.getAbsolutePath()+"/"+subName;
	}
	
	public static String getNameByAreaDate(Area area,Date date)
	{
		
		return "";
	}
	
}
