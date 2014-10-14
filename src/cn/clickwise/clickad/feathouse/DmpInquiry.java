package cn.clickwise.clickad.feathouse;

import java.io.File;


/**
 * dmp离线查询
 * @author zkyz
 */
public abstract class DmpInquiry {
	
	private int day;
	
	public abstract void init();
	
	
	/**
	 * 从某地区dmp获取keyFile里所有用户的记录
	 * @param keyFile
	 * @return
	 */
	public abstract State fetchFromDmp(File keyFile,File recordFile,Dmp dmp);
	
	/**
	 * 从所有地区dmp获取所有用户的记录
	 * @param timeRange
	 * @return
	 */
	public abstract State fetchFromAllDmps(int day);
	
	
	/**
	 * 用户记录从文件写入kv存储
	 * @param recordFile
	 * @return
	 */
	public abstract State writeRecFile2DataStore(File recordFile,Connection con,Dmp dmp,int day);
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	
	/**
	 * 重置从不同地区查询用户数量
	 * @param key
	 * @return
	 */
	public abstract State resetStatistics(InquiryReceipt inquiryReceipt);
	

}
