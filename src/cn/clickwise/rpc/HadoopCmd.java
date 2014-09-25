package cn.clickwise.rpc;

/**
 * 返回hadoop操作的命令
 * @author zkyz
 *
 */
public class HadoopCmd {

	private static final String hadoop="hadoop ";
	
	public static String load2hdfs(String local_file,String hdfs_file)
	{
		String cmd="";
		cmd=" fs -put "+local_file+" "+hdfs_file;		
		return hadoop+cmd;
	}
	
	
}
