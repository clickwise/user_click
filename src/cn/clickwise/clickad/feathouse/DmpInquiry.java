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
	
	/**
	 * 用户记录从文件写入kv 存储
	 * @param recordFile
	 * @return
	 */
	public abstract State writeRecFile2DataStore(File recordFile);
	
	
	

}
