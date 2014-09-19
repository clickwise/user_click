package cn.clickwise.clickad.feathouse;

public abstract class ConfigureFactory {

	/**
	 * 获取查询未命中用户的存储
	 * @return
	 */
	public abstract MissesStore getMissesStore();
	
	/**
	 * 获取查询未命中用户的临时存储
	 * @return
	 */
	public abstract MissesTmpStore getMissesTmpStore();
	
	/**
	 * 获取用户记录的临时存储
	 * @return
	 */
	public abstract UserRecordTmpStore getUserRecordTmpStore();
	
}
