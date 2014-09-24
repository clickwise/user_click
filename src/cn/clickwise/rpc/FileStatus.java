package cn.clickwise.rpc;

import java.util.ArrayList;

public class FileStatus extends Result{

	private String name;
	
	private String path;
	
	private boolean isDirectory;
	
	private FileStatus parent;
	
	private ArrayList<FileStatus> children;

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public ArrayList<FileStatus> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<FileStatus> children) {
		this.children = children;
	}

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

	public FileStatus getParent() {
		return parent;
	}

	public void setParent(FileStatus parent) {
		this.parent = parent;
	}
	
	
}
