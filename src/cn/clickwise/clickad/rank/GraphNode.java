package cn.clickwise.clickad.rank;

public class GraphNode {
	private String name;
	
	//单词在句子中的起始和终止index，包前不包后
	private int startIndex;
	private int endIndex;
	public GraphNode(String name,int startIndex,int endIndex)
	{
		this.setName(name);
		this.setStartIndex(startIndex);
		this.setEndIndex(endIndex);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	
}
