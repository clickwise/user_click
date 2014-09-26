package cn.clickwise.rpc;

//获取远程hive表匹配key集合的记录
@SuppressWarnings("serial")
public class HiveFetchByKeysCommand extends Command{

    private String keyName;
    
    private String keyPath;
    
    private String tableName;
    
    private String keyFieldName;
    
    //key存储的hive表名
    private String keyTableName;
    
    private int day;
    
    //keys写入的远程文件的名称
    private String remoteTmpName;
    
    //keys写入的远程文件的路径
    private String remoteTmpPath;
     
    //keys写入的hdfs文件的名称
    private String hdfTmpName;
    
    //keys写入的hdfs文件的名称
    private String hdfTmpPath;
    
    //取回记录写入的本地文件的名称
    private String resultName;

    //取回记录写入的本地文件的路径
    private String resultPath;
    
    //匹配记录写入的远程文件夹的名称
    private String resultRemoteName;
    
    //匹配记录写入的远程文件夹的路径
    private String resultRemotePath;
    
    private String tmpIdentify;
    
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

	public String getHdfTmpName() {
		return hdfTmpName;
	}

	public void setHdfTmpName(String hdfTmpName) {
		this.hdfTmpName = hdfTmpName;
	}

	public String getHdfTmpPath() {
		return hdfTmpPath;
	}

	public void setHdfTmpPath(String hdfTmpPath) {
		this.hdfTmpPath = hdfTmpPath;
	}

	public String getResultRemoteName() {
		return resultRemoteName;
	}

	public void setResultRemoteName(String resultRemoteName) {
		this.resultRemoteName = resultRemoteName;
	}

	public String getResultRemotePath() {
		return resultRemotePath;
	}

	public void setResultRemotePath(String resultRemotePath) {
		this.resultRemotePath = resultRemotePath;
	}

	public String getKeyTableName() {
		return keyTableName;
	}

	public void setKeyTableName(String keyTableName) {
		this.keyTableName = keyTableName;
	}
	
	public static String writeObject(HiveFetchByKeysCommand hfkc)
	{
		String hfkcs="";
		hfkcs+=(hfkc.getKeyName()+";"+hfkc.getKeyPath()+";"+hfkc.getTableName()+";"+hfkc.getKeyFieldName()+";"+hfkc.getKeyTableName()
				+";"+hfkc.getDay()+";"+hfkc.getRemoteTmpName()+";"+hfkc.getRemoteTmpPath()+";"+hfkc.getTableName()+";"+hfkc.getHdfTmpPath()
				+";"+hfkc.getResultName()+";"+hfkc.getResultPath()+";"+hfkc.getResultRemoteName()+";"+hfkc.getResultRemotePath()+";"+hfkc.getTmpIdentify());
		return hfkcs;
	}
	
	public static HiveFetchByKeysCommand readObject(String hfkcs)
	{
		HiveFetchByKeysCommand hfkc=new HiveFetchByKeysCommand();
	  
        String[] tokens=hfkcs.split(";");
        if(tokens.length!=15)
        {
		   return null;
        }
        else
        {
        	 hfkc.setKeyName(tokens[0]);
        	 hfkc.setKeyPath(tokens[1]);
        	 hfkc.setTableName(tokens[2]);
        	 hfkc.setKeyFieldName(tokens[3]);
        	 hfkc.setKeyTableName(tokens[4]);
        	 hfkc.setDay(Integer.parseInt(tokens[5]));
        	 hfkc.setRemoteTmpName(tokens[6]);
        	 hfkc.setRemoteTmpPath(tokens[7]);
        	 hfkc.setHdfTmpName(tokens[8]);
        	 hfkc.setHdfTmpPath(tokens[9]);
        	 hfkc.setResultName(tokens[10]);
        	 hfkc.setResultPath(tokens[11]);
        	 hfkc.setResultRemoteName(tokens[12]);
        	 hfkc.setResultRemotePath(tokens[13]);
        	 hfkc.setTmpIdentify(tokens[14]);
        	return  hfkc;
        }
	}

	public String getTmpIdentify() {
		return tmpIdentify;
	}

	public void setTmpIdentify(String tmpIdentify) {
		this.tmpIdentify = tmpIdentify;
	}


}
