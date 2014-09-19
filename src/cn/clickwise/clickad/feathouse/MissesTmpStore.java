package cn.clickwise.clickad.feathouse;

import java.io.File;

/**
 * 查询未命中uid的临时存储
 * @author zkyz
 */
public abstract class MissesTmpStore {


	public abstract File getFileByName(String name);
	
	
}
