package cn.clickwise.rpc;

import java.io.Serializable;

public class File extends AbstractFile{
    
	private static final long serialVersionUID = -7019180830108626305L;

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	

}
