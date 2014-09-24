package cn.clickwise.rpc;

//列出远程文件信息

@SuppressWarnings("serial")
public class FileStatusCommand extends Command{

	private String name;
	
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
	

}
