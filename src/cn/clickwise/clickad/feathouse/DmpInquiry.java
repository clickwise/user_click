package cn.clickwise.clickad.feathouse;

import java.io.File;

/**
 * dmp离线查询
 * @author zkyz
 */
public abstract class DmpInquiry {
	
	/**
	 * 从dmp获取keyFile里所有用户的记录
	 * @param keyFile
	 * @return
	 */
	public abstract State fetchFromDmp(File keyFile,File recordFile);
	
	
	
	
	
	

}
