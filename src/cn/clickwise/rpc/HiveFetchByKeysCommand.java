package cn.clickwise.rpc;

//获取远程hive表匹配key集合的记录
@SuppressWarnings("serial")
public class HiveFetchByKeysCommand extends Command{

    private String keyName;
    
    private String keyPath;
    
    private String tableName;
    
    private String keyFieldName;
    
    private int day;
    
    private String remoteTmpName;
    
    private String remoteTmpPath;
    
    //取回记录写入的文件名
    private String resultName;

    //取回记录写入的文件路径
    private String resultPath;
    
	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getKeyFieldName() {
		return keyFieldName;
	}

	public void setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}


	public static String writeObject(HiveFetchByKeysCommand hfkc)
	{
		
		return "";
	}
	
	public static HiveFetchByKeysCommand readObject(String hfkcs)
	{
		
		return null;
	}

	public String getResultName() {
		return resultName;
	}

	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public String getRemoteTmpName() {
		return remoteTmpName;
	}

	public void setRemoteTmpName(String remoteTmpName) {
		this.remoteTmpName = remoteTmpName;
	}

	public String getRemoteTmpPath() {
		return remoteTmpPath;
	}

	public void setRemoteTmpPath(String remoteTmpPath) {
		this.remoteTmpPath = remoteTmpPath;
	}
	
}
