package cn.clickwise.lib.file;

import java.io.File;

public class Test {

	public static void main(String[] args)
	{
		File test=new File("temp/tempTest/a.txt");
		if(!(test.exists()))
		{
			test.mkdirs();
		}
	}
}
