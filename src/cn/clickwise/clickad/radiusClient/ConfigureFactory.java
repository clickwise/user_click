package cn.clickwise.clickad.radiusClient;



public abstract class ConfigureFactory {

	/**
	 * 返回pcap文件存放的目录
	 * @return
	 */
	public abstract String getPcapDirectory();
	
	/**
	 * 返回某天生成的pcap文件文件名
	 * @param day
	 * @return
	 */
	public abstract String getPcapFileDay(int day);
	
	/**
	 * 返回pcap文件名
	 * @return
	 */
	public abstract String getPcapFile();
	
	/**
	 * 设置重新连接前的等待时间
	 * @return
	 */
	public abstract long getResetConnectionSuspend();
	
	public abstract RedisCenter getRedisCenter();
	
	public abstract OnlineDatabase getOnlineDatabase();
	
	
    public abstract Context[] getContext();
    
    public abstract Handler[] getHandler();
    
    public abstract int getServerPort();
    
    public abstract int getRedisPort();
    
    public abstract int getRedisDB();
    
    public abstract String getRedisIp();
}
