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
	
	public static String normalizePath(File tempFile)
	{
		String path=tempFile.getAbsolutePath();
		System.out.println("old_path:"+path);
		path=path.replaceAll("\\\\", "\\/");
		return path;
	}
	
	public static void main(String[] args)
	{
		File file=new File("temp\test_cookie.txt");
        System.out.println(normalizePath(file));		
	}
	
}
