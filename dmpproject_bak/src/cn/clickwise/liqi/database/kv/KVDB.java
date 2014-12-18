package cn.clickwise.liqi.database.kv;

import java.util.ArrayList;
import java.util.Properties;

public abstract class KVDB {

	/**
	 * 读取配置文件
	 * @param config_file
	 */
	public abstract void load_config(Properties prop) throws Exception;
	

    /**
     * 写入键值
     * @param key
     * @param value
     * @throws Exception
     */
	public abstract void set(String key,String value) throws Exception;
	
	
    /**
     * 读取键值
     * @param key
     * @throws Exception
     */
	public abstract String get(String key) throws Exception;
	
	
	/**
	 * 检测键值是否存在
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public abstract boolean exist(String key) throws Exception;
	
	public abstract int size() throws Exception;
	

    /**
     * 批量更新 key val
     * @param kv_list
     * @return
     * @throws Exception
     */
	public abstract void mulset(ArrayList<String> kv_list) throws Exception;
	
	
	/**
	 * 批量更新 key val
	 * 从文件读取更新源
	 * @param file_name
	 * @throws Exception
	 */
	public abstract void mulsetfromfile(String file_name) throws Exception;
	
}
