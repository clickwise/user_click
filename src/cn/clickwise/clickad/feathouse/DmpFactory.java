package cn.clickwise.clickad.feathouse;

/**
 * 获取某dmp的配置信息
 * @author zkyz
 *
 */
public abstract class DmpFactory {

	/**
	 * 根据地域名获取对应dmp配置
	 * @param area
	 * @return
	 */
	public abstract Dmp getDmpByAreaName(String area);
	
	/**
	 * 获取所有的dmp配置
	 * @return
	 */
	public abstract Dmp[] getAllDmps();
}
