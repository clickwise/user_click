package cn.clickwise.liqi.mark;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

import cn.clickwise.liqi.mark.SparkFeatSearchBD.AddWordFunction;
import cn.clickwise.liqi.math.random.RandomGen;

/**
 * 基于普通实现的beamdecoder,进行特征搜索
 * 
 * @author lq
 * 
 */
public class PlainBeamDecoder extends BeamDecoder {
	public PrintWriter pw = null;
	public static int log_level = 0;

	public PlainBeamDecoder() {
		
		if (log_level > 3) {
			try {
				FileWriter fw = new FileWriter(new File("temp/mark/plainbeamdecoder.txt"));
				pw=new PrintWriter(fw);
			} catch (Exception e) {
               System.out.println(e.getMessage());
			}
		}
		
	}

	public ArrayList<FEATSET> addWord(ArrayList<FEATSET> old_list, WORD w,
			WEI[] word_weights) {
		ArrayList<FEATSET> new_list = new ArrayList<FEATSET>();

		FEATSET fis = null;
		if(log_level>3)
		{
		  pw.println("old_list.size:"+old_list.size());
		}
		for (int i = 0; i < old_list.size(); i++) {
			fis = old_list.get(i);
			if(fis.words.size()>10)
			{
				continue;
			}
			if(log_level>3)
			{
			  pw.println("adding word: [index="+w.index+",count="+w.count+"]");
			  pw.println("old fis:"+fis.toString());
			}
			fis.addWord(w, word_weights[fis.label.index - 1]);
			if(log_level>3)
			{
			  pw.println("new fis:"+fis.toString());
			}
			//System.out.println(fis.toString());

			new_list.add(fis);
		}

		return new_list;
	}

	public ArrayList<FEATSET> merge_list(ArrayList<FEATSET> fir_list,
			ArrayList<FEATSET> sec_list) {
		ArrayList<FEATSET> m_list = new ArrayList<FEATSET>();

		for (int i = 0; i < fir_list.size(); i++) {
			m_list.add(fir_list.get(i));
		}

		for (int i = 0; i < sec_list.size(); i++) {
			m_list.add(sec_list.get(i));
		}

		return m_list;
	}

	@Override
	public FEATSET beam_search(WORD[] words, FEAT[] sub_weights,
			LABEL[] label_set, LEARNPARM learn_parms) {
		//System.out.println("in plain beam search");
		ArrayList<FEATSET> src = null;
		ArrayList<FEATSET> tgt = null;
		WeightsCut wc=new WeightsCut();
		ArrayList<FEATSET> init_list = new ArrayList<FEATSET>();
		src = new ArrayList<FEATSET>();
		FEATSET ifs = null;// 初始feat set
		if(log_level>3)
		{
		 pw.println("process word 0");
		}
		for (int i = 0; i < label_set.length; i++) {
			// System.out.println("word[0]="+words[0].index);
			ifs = new FEATSET(words[0], label_set[i], new WEI(sub_weights[FeatureIndex.featIndexNoWordMap(1, label_set[i])].weight));
			ifs.calScore();
			// System.out.println("score="+ifs.score);
			src.add(ifs);
		}

		ArrayList<FEATSET> src_temp = null;
		for (int i = 1; i < words.length; i++) {
			if(log_level>3)
			{
			  pw.println("process word "+i);
			}
			src_temp = new ArrayList<FEATSET>();
			for (int j = 0; j < src.size(); j++) {
				src_temp.add(FEATSET.copy(src.get(j)));
			}
			
			// System.out.println("word["+i+"]="+words[i].index);
			// **words[i] 加到 src特征集合中
			if(log_level>3)
			{
			  pw.println("process word "+i+" call add");
			}
			 tgt = addWord(src, words[i],
					wc.weightToWei(sub_weights, (i+1), label_set));
			if(log_level>3)
			{
			 pw.println("process word "+i+" merge src tgt");
			}
			src = merge_list(src_temp, tgt);
			if(log_level>3)
			{
			 pw.println("process word "+i+" standalone add");
			}
			// **words[i] 形成独立的特征集合加到src中
			init_list = new ArrayList<FEATSET>();
			for (int j = 0; j < label_set.length; j++) {
				// ifs=new FEATSET(words[i],label_set[j],new
				// WEI(init_weights[(label_set[j].label_num)*(label_set[j].index-1)+i].weight));
				ifs = new FEATSET(words[i], label_set[j], new WEI(
						sub_weights[FeatureIndex.featIndexNoWordMap((i+1), label_set[j])].weight));
				ifs.calScore();
				init_list.add(ifs);
			}
			if(log_level>3)
			{
			 pw.println("process word "+i+" merge src init_list");
			}
			src = merge_list(src, init_list);
			if (src.size() > (learn_parms.top_num)) {
				src = TopList.getTopFromList(learn_parms.top_num, src);
			}
			
		}
       /*
		for(int i=0;i<src.size();i++)
		{
			if(src.get(i).score!=0)
			{
			 System.out.println(src.get(i).toString()+"   score["+i+"]:"+src.get(i).score);
			}
		}
		*/
		TopList tl=new TopList();
		return tl.getBestFromList(src);
	}

}
