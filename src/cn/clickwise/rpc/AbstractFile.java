package cn.clickwise.rpc;

import java.io.Serializable;
import java.util.Date;

public class AbstractFile implements Serializable {
	
	private static final long serialVersionUID = 3193791462643928074L;

	private String path;
	
	private String name;
	
    private Date modifyTime;
    
    private Date createTime;
	
	public String getPath() {
		
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
