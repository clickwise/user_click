package cn.clickwise.clickad.rank;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.file.uitls.FileToArray;

public class SentenceGraph {

	private Map<String, String> dict;
	private List<List<GraphNode>> startNodes;
	private List<List<GraphNode>> endNodes;

	public SentenceGraph(File dict_file) {
		dict = new HashMap<>();
		loadDict(dict_file);
	}

	public void loadDict(File dict_file) {
		try {
			dict = FileReaderUtil.file2HashSin(dict_file.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<List<GraphNode>> sen2graph(String sentence) {

		String ngram;
       
		List<List<GraphNode>> graphNodes = new ArrayList<List<GraphNode>>();

		for (int i = 0; i < sentence.length(); i++) {
			graphNodes.add(i, new ArrayList<GraphNode>());
			for (int j = i; j < sentence.length(); j++) {
				ngram = sentence.substring(i, j);
				if (dict.containsKey(ngram)&&(j-i)>1&&(!Pattern.matches("[0-9a-zA-Z]*", ngram))) {
					graphNodes.get(i).add(new GraphNode(ngram, i, j));
				}
			}
		}
		setStartNodes(graphNodes);
		
		List<List<GraphNode>> endNodes = new ArrayList<List<GraphNode>>();
		endNodes.add(0, new ArrayList<GraphNode>());
		for (int i = 1; i <=sentence.length(); i++) {
			endNodes.add(i, new ArrayList<GraphNode>());
			for (int j = 0; j < i; j++) {
				ngram = sentence.substring(j, i);
				if (dict.containsKey(ngram)&&(i-j)>1&&(!Pattern.matches("[0-9a-zA-Z]*", ngram))) {
					endNodes.get(i).add(new GraphNode(ngram, j, i));
				}
			}
		}
		
		setEndNodes(endNodes);
		
		return graphNodes;
	}

	public static void main(String[] args) throws Exception
	{
		
		SentenceGraph sg=new SentenceGraph(new File("temp/seg_test/ecw.txt"));	
		String sentence="2012秋装新款夏装新品韩版女装海魂衫纯棉条纹长袖T恤打底衫上衣";
		
		List<List<GraphNode>> graphNodes=sg.sen2graph(sentence);
		String words="";
		for(int i=0;i<graphNodes.size();i++)
		{
			List<GraphNode> sns=graphNodes.get(i);
			words="";
			for(int j=0;j<sns.size();j++)
			{
				words+=sns.get(j).getName()+" ";
			}
			System.out.println(i+" ["+words+"]");
		}
		
		List<List<GraphNode>> endNodes=sg.getEndNodes();
		
		for(int i=0;i<endNodes.size();i++)
		{
			List<GraphNode> sns=endNodes.get(i);
			words="";
			for(int j=0;j<sns.size();j++)
			{
				words+=sns.get(j).getName()+" ";
			}
			System.out.println(i+" ["+words+"]");
		}
		
	}

	public List<List<GraphNode>> getStartNodes() {
		return startNodes;
	}

	public void setStartNodes(List<List<GraphNode>> startNodes) {
		this.startNodes = startNodes;
	}

	public List<List<GraphNode>> getEndNodes() {
		return endNodes;
	}

	public void setEndNodes(List<List<GraphNode>> endNodes) {
		this.endNodes = endNodes;
	}
	
	
}
