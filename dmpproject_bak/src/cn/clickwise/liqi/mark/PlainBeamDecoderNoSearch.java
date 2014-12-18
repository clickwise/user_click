package cn.clickwise.liqi.mark;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PlainBeamDecoderNoSearch extends BeamDecoder{

	public PrintWriter pw = null;
	public static int log_level = 0;
	public PlainBeamDecoderNoSearch()
	{
		if (log_level > 3) {
			try {
				FileWriter fw = new FileWriter(new File("temp/mark/plainbeamdecoder.txt"));
				pw=new PrintWriter(fw);
			} catch (Exception e) {
               System.out.println(e.getMessage());
			}
		}
	}
	@Override
	public FEATSET beam_search(WORD[] words, FEAT[] sub_weights,
			LABEL[] label_set, LEARNPARM learn_parms) {
		ArrayList<FEATSET> src = null;
		src = new ArrayList<FEATSET>();
		FEATSET ifs = null;// 初始feat set
		//pw.println("process word 0");
		for (int i = 0; i < label_set.length; i++) {
			// System.out.println("word[0]="+words[0].index);
			ifs = new FEATSET(words[0], label_set[i], new WEI(sub_weights[FeatureIndex.featIndexNoWordMap(1, label_set[i])].weight));
			for(int j=1;j<words.length;j++)
			{
				ifs.addWord(words[j], new WEI(sub_weights[FeatureIndex.featIndexNoWordMap(j+1, ifs.label)].weight));
			}	
			
			// System.out.println("score="+ifs.score);
			ifs.calScore();
			//pw.println(ifs.toString()+"  score="+ifs.score);
			src.add(ifs);
		}

		// TODO Auto-generated method stub
		TopList tl=new TopList();
		return tl.getBestFromList(src);
	}

}
