package cn.clickwise.clickad.feathouse;

import java.io.File;


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
	
	/**
	 * 获取mysql的配置信息
	 * @return
	 */
	public abstract MysqlConfigure getMysqlConfigure();
	
	/**
	 * 获得不同地区uid(cookie)查询数量统计表名 
	 * @return
	 */
	public abstract Table getQueryTable();
	
	/**
	 * 获取所有dmp的配置信息
	 * @return
	 */
	public abstract Dmp[] getDmps();
	
	public abstract Dmp getDmpById(int id);
	
	public abstract Dmp getDmpByArea(String area);
	
	public abstract Record string2Record(String recordString);
	
	public abstract DataStore getDataStore();
	
	public abstract Connection getConnection();
	
	public abstract Table getInquiryTable();
	
	public abstract File getMissesRootDirectory();
	
	public abstract ArdbConfigure getArdbConfigure();
	
	public abstract File getQueryLogDirectory();
	
    public abstract Context[] getContext();
    
    public abstract Handler[] getHandler();
    
    public abstract CassandraConfigure getCassandraConfigure();
    
    public abstract HBaseConfigure getHBaseConfigure();
    
    public abstract String getTmpIdentify();
    
    public abstract String getStatisticTmpIdentify();
    
    //取回用户特征文件的前缀
    public abstract String getRecordFilePrefix();
    
    public abstract String getUidFilePrefix();
    
    public abstract String getRecordFileDirectory();
	
    public abstract int getQueryType();
    
    public abstract String getDmpRecordFile(int day,Dmp dmp);
    
    
    public abstract String getDmpUidPrefix();
    /**
     * RTB查询的uid，按地区分开存储的文件夹
     * @return
     */
    public abstract String getDmpUidDirectory();
    
    /**
     * RTB查询的uid, 该dmp、该天对应的文件
     * @param day
     * @param dmp
     * @return
     */
    public abstract String getDmpUidFile(int day,Dmp dmp);
    
    public abstract String getEasyDmpUidFile(int day,String areaCode);
    
    public abstract Dmp getDmpByAreaCode(String areaCode);
    
  //  public abstract String getDmpStatisticDirectory();
    
   // public abstract String getDmpStatisticFile(int day,Dmp dmp);
    
    public abstract String getDmpSResultPrefix();
    
    public abstract String getDmpStatisticResultDirectory();
    
    public abstract String getDmpStatisticResultFile(int day,Dmp dmp);
    
    public abstract StatisticStruct string2StatisticResult(String statistic_line);
    
}
