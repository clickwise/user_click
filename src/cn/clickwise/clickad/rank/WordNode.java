package cn.clickwise.clickad.rank;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词节点
 * @author zkyz
 *
 */
public class WordNode {
	
    private String name;
	private List<WordNode> inNodes;
	private List<WordNode> outNodes;
	private double score;
	
	public WordNode(String name,double score)
	{
		this.name=name;
		this.score=score;
		inNodes=new ArrayList<>();
		outNodes=new ArrayList<>();	
	}
	
	public List<WordNode> getInNodes() {
		return inNodes;
	}
	public void setInNodes(List<WordNode> inNodes) {
		this.inNodes = inNodes;
	}
	public List<WordNode> getOutNodes() {
		return outNodes;
	}
	public void setOutNodes(List<WordNode> outNodes) {
		this.outNodes = outNodes;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	
	
}
