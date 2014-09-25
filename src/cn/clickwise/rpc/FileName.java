package cn.clickwise.rpc;
import java.io.File;
public class FileName {

	public static boolean isValidResult(File resFile)
	{
		String name=resFile.getName();
		if(name.startsWith("\\."))
		{
			return false;
		}
		else if(name.indexOf("crc")>-1)
		{
			return false;
		}
		else if(resFile.isDirectory())
		{
			return false;
		}
		else if(name.startsWith("_"))
		{
			return false;
		}
		else if(name.indexOf("logs")>-1)
		{
			return false;
		}
		return true;
	}
}
