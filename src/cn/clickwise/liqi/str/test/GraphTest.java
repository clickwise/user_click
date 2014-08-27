package cn.clickwise.liqi.str.test;

import org.ictclas4j.segment.AtomSeg;
import org.ictclas4j.segment.GraphGenerate;
import org.ictclas4j.segment.SegGraph;

public class GraphTest {

	public static void main(String[] args)
	{
		String src="2012秋装新款夏装新品韩版女装海魂衫纯棉条纹长袖T恤打底衫上衣";
		AtomSeg atomSeg=new AtomSeg(src);
		
		//SegGraph segGraph=GraphGenerate.generate(atomSeg.getAtoms(), dict);
		
	}
	
}
