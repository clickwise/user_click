package cn.clickwise.clickad.feathouse;

import java.io.File;
import java.util.Date;

/**
 * 查询未命中的用户存储
 * @author zkyz
 */
public abstract class MissesStore {

	/**
	 * 根据地域名和时间获取查询未命中uid，将结果合并为一个文件
	 * @param area
	 * @param timeRange
	 * @return
	 */
	public abstract File getMissesByAreaName(Area area,TimeRange timeRange);
	
	/**
	 * 根据名字返回文件
	 * @param name
	 * @return
	 */
	public abstract File getFileByName(String name);
	
	public abstract File findFileByAreaDate(Area area,Date date);
	
}
