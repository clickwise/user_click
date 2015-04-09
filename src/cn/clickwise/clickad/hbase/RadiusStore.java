package cn.clickwise.clickad.hbase;

import java.util.List;

/**
 * radius 数据存储和查询
 * @author zkyz
 */
public abstract class RadiusStore {

	public  abstract void write(String record);
	
	public  abstract List<String> get(String ip,String time);
	
	public  abstract List<String> get(String ip);
	
}
