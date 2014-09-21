package cn.clickwise.clickad.feathouse;

import java.io.File;

/**
 * 查询未命中uid的临时存储
 * @author zkyz
 */
public abstract class MissesTmpStore {


	public abstract File getFileByName(String name);
	
	/**
	 * 按照area 和 timeRange 查找文件，
	 * iscreate:
	 *    false:未找到不创建新文件
	 *    true:未找到创建新文件
	 * @param area
	 * @param timeRange
	 * @param iscreate
	 * @return
	 */
	public abstract File findFileByAreaTimeRange(Area area,TimeRange timeRange,boolean iscreate);
	
	
}
