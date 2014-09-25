package cn.clickwise.rpc;

//从远程拷贝文件
@SuppressWarnings("serial")
public class FileCopyFromCommand extends Command{

    private String localName;
    
    private String localPath;
    
    private String remoteName;
    
    private String remotePath;

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRemoteName() {
		return remoteName;
	}

	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
    
    
	
	
	
}
