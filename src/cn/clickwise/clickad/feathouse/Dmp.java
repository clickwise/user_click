package cn.clickwise.clickad.feathouse;

public class Dmp {
    
	//dmp的名字
	private String name;
	
	//dmp所属的区域
	private String area;
	
	//dmp所属的区域编码
	private int areaCode;
	
	//用户特征服务器的host
	private String host;
	
	//用户特征服务器的rpc调用端口
	private int rpcPort;
	
	//hive fetch by keys method
	private String dmpInquiryMethod;
	
	private String userFeatureTableName;
	
	private String uidFieldName;
	
	//dmp取数据临时文件名称的部分标识，用来给临时文件命名
	private String tmpIdentify;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(int areaCode) {
		this.areaCode = areaCode;
	}
	public int getRpcPort() {
		return rpcPort;
	}
	public void setRpcPort(int rpcPort) {
		this.rpcPort = rpcPort;
	}
	public String getDmpInquiryMethod() {
		return dmpInquiryMethod;
	}
	public void setDmpInquiryMethod(String dmpInquiryMethod) {
		this.dmpInquiryMethod = dmpInquiryMethod;
	}
	public String getUserFeatureTableName() {
		return userFeatureTableName;
	}
	public void setUserFeatureTableName(String userFeatureTableName) {
		this.userFeatureTableName = userFeatureTableName;
	}
	public String getUidFieldName() {
		return uidFieldName;
	}
	public void setUidFieldName(String uidFieldName) {
		this.uidFieldName = uidFieldName;
	}
	public String getTmpIdentify() {
		return tmpIdentify;
	}
	public void setTmpIdentify(String tmpIdentify) {
		this.tmpIdentify = tmpIdentify;
	}
	
}
