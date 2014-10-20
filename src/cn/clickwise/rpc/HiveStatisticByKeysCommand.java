package cn.clickwise.rpc;

@SuppressWarnings("serial")
public class HiveStatisticByKeysCommand extends Command{

    private String keyPath;
    
    private int day;
    
    //keys写入的远程文件的名称
    private String remoteTmpName;
    
    //keys写入的远程文件的路径
    private String remoteTmpPath;
    
    //取回记录写入的本地文件的名称
    private String resultName;

    //取回记录写入的本地文件的路径
    private String resultPath;
    
    //匹配记录写入的远程文件夹的名称
    private String resultRemoteName;
    
    //匹配记录写入的远程文件夹的路径
    private String resultRemotePath;
    
    private String tmpIdentify;
    
	public static String writeObject(HiveStatisticByKeysCommand hskc)
	{
		String hskcs="";

		
		return hskcs;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
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
}
