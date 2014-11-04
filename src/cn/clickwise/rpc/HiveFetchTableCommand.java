package cn.clickwise.rpc;

//获取远程hive表信息
@SuppressWarnings("serial")
public class HiveFetchTableCommand extends Command{

	//table 名称
	private String tableName;
	
	//查询选项，
	//0 只输出key field
	//1 输出 all fields
	private int queryType;
	
	private int day;
	
	private String keyFieldName;
	
    //取回记录写入的本地文件的名称
    private String resultName;

    //取回记录写入的本地文件的路径
    private String resultPath;
    
    //匹配记录写入的远程文件夹的名称
    private String resultRemoteName;
    
    //匹配记录写入的远程文件夹的路径
    private String resultRemotePath;
    
    private String tmpIdentify;

    private String areaCode;


	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getKeyFieldName() {
		return keyFieldName;
	}

	public void setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
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

	public String getTmpIdentify() {
		return tmpIdentify;
	}

	public void setTmpIdentify(String tmpIdentify) {
		this.tmpIdentify = tmpIdentify;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String toString()
	{
		String str="";
		str=str+tableName+";"+queryType+";"+day+";"+keyFieldName+";"+resultName+";"+resultPath+";"+resultRemoteName+";"+resultRemotePath+";"+tmpIdentify+";"+areaCode;
		return str;
	}
	
}
