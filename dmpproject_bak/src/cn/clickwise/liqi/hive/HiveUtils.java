package cn.clickwise.liqi.hive;

import cn.clickwise.liqi.external.bash.BashCmd;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * Hive 查询命令的封装
 * @author zkyz
 *
 */
public class HiveUtils {
   
	public static String HIVE="/home/hadoop/hive/bin/hive -e ";
	public static boolean debug_mode=true;
	
	public static void debug(String msg)
	{
        if(debug_mode)
        {
        	System.out.println(msg);
        }
	}
	
    /**
     * hive_proc
     *
     * @param       hsql    the hive QL statement
     *
     * @return      true if succeed, false otherwise
     */
    public static boolean hive_proc(String hql){
            String runtime=TimeOpera.getCurrentTime();
            debug(runtime+": Run hive :$hql ");
            String ret=BashCmd.execmd(HIVE+ "\""+hql+"\" ");
            System.out.println(ret);
            return true;
    }
    
    public static void main(String[] args)
    {
    	String sql="INSERT OVERWRITE LOCAL DIRECTORY '/home/hadoop/lq/SWA_Eclipse/php/hive_opera/output/video_info' SELECT video_rn, video_sn, video_url from  video_info where video_rn like '%大%\' and dt=20140224;";
    	hive_proc(sql);
    	
    }
    
}
