package cn.clickwise.liqi.str.test;

import java.io.File;
import java.util.ArrayList;

import org.ictclas4j.bean.Dictionary;
import org.ictclas4j.bean.SegNode;
import org.ictclas4j.bean.WordItem;
import org.ictclas4j.bean.WordTable;
import org.ictclas4j.segment.AtomSeg;
import org.ictclas4j.segment.GraphGenerate;
import org.ictclas4j.segment.SegGraph;

import cn.clickwise.liqi.file.uitls.FileToArray;

public class GraphTest {

	public static void main(String[] args) throws Exception
	{
		String src="2012秋装新款夏装新品韩版女装海魂衫纯棉条纹长袖T恤打底衫上衣";
		AtomSeg atomSeg=new AtomSeg(src);
		Dictionary dict=new Dictionary();
		WordTable wordTable=new WordTable();
		wordTable.setWords(new ArrayList<WordItem>());
		String[] tokens=FileToArray.fileToDimArr(new File("dict/all_words.txt"));
		for(int i=0;i<tokens.length;i++)
		{
	    	wordTable.getWords().add(new WordItem(tokens[i],tokens[i].length(),0,1));
		}
		
		dict.wts.add(wordTable);	
		SegGraph segGraph=GraphGenerate.generate(atomSeg.getAtoms(), dict);
		ArrayList<SegNode> segNodes=segGraph.getSnList();
		
		for(int i=0;i<segNodes.size();i++)
		{
		  System.out.println(segNodes.get(i).getWord());	
		  ArrayList<SegNode>  snodes=segGraph.getNextElements(i);
		  for(int j=0;j<segNodes.size();j++)
		  {
			  System.out.print(segNodes.get(j).getWord()+" ");	  
		  }
		  System.out.println();
		  
		}
		
		
	}
	
}
