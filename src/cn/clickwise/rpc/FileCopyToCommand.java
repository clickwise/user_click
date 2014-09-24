package cn.clickwise.rpc;

//本地向远程写文件
@SuppressWarnings("serial")
public class FileCopyToCommand extends Command{
	
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
    
	public static String writeObject(FileCopyToCommand fcc)
	{
		String fccs="";
		fccs+=fcc.getLocalName()+";";
		fccs+=fcc.getLocalPath()+";";
		fccs+=fcc.getRemoteName()+";";
		fccs+=fcc.getRemotePath();
		return fccs;
	}
	
	public static FileCopyToCommand readObject(String fccs)
	{
		FileCopyToCommand fcc=new FileCopyToCommand();
		String[] tokens=fccs.split(";");
		if(tokens.length!=4)
		{
			return null;
		}
		
		fcc.setLocalName(tokens[0]);
		fcc.setLocalPath(tokens[1]);
		fcc.setRemoteName(tokens[2]);
		fcc.setRemotePath(tokens[3]);
		
		return fcc;
	}
    
}
