package cn.clickwise.rpc;
import java.io.File;
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
	
	public static String mkParent(String hdfs_file)
	{
		File tmp=new File(hdfs_file);
		String cmd="";
		cmd=" fs -mkdir "+tmp.getParentFile().getAbsolutePath();
		return hadoop+cmd;
	}
	
}
