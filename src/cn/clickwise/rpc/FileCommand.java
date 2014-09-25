package cn.clickwise.rpc;

public class FileCommand {

	public static String deleteDirectory(String directory)
	{
		String delcmd=" rm -rf "+directory;
		return delcmd;
	}
}
