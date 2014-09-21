package cn.clickwise.clickad.feathouse;

import java.io.File;

/**
 * 用户记录的临时存储
 * @author zkyz
 */
public abstract class UserRecordTmpStore {
	

	public abstract File getFileByName(String name);
	
	
}
