package cn.clickwise.liqi.mark;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 使用训练的参数进行预测
 * @author lq
 *
 */
public class PerceptronPredict {

	public static PrintWriter pw = null;
	public static int log_level=0;
	/**
	 * 在测试集测试预测准确度
	 * @param weights
	 * @param test_sample
	 * @param learn_parms
	 * @return
	 */
	public static double predict_accurate(double[] weights,List<String> test_sample,LABEL[] label_set,LEARNPARM learn_parms,int loop_i)
	{
		
		
		double acc=0;
		try {
			if(log_level>3)
			{
			 FileWriter fw = null;
			 fw = new FileWriter(new File("/tmp/perceptron_predic.txt"),true);
			 pw = new PrintWriter(fw);
			}
	

		String lds = "";// 一行样本
		String lst = "";// 标记
		String lwo = "";// 单词
		String did = "";// docid
		String rankey="";
		String[] seg_arr = null;
		WORD[] words = null;
		FEATSET best_fset = null;
		LABEL yt = null;
		int correct_num=0;
		int incorrect_num=0;
		BeamDecoder bd = BeamDecoderFactory.create(learn_parms);
		WeightsCut wc=new WeightsCut();
		StrToClass sc=new StrToClass();
		for(int i=0;i<test_sample.size();i++){
			lds = test_sample.get(i);
			// System.out.println("lds:"+lds);
			if (SSO.tioe(lds)) {
				continue;
			}
			seg_arr = lds.split("\001");
			if (seg_arr.length != 4) {
				continue;
			}
			rankey=seg_arr[0].trim();
			lst = seg_arr[1].trim();
			lwo = seg_arr[3].trim();
			did = seg_arr[2].trim();
			yt = sc.str2label(lst, learn_parms);
			
			words =sc.str2words(lwo);
			if (words == null) {
				continue;
			}
			
			ShuffleArray.shuffle(words);
			best_fset = bd.beam_search(words, wc.all2part(
					weights, words, label_set), label_set,
					learn_parms);
			if(log_level>3)
			{
			  pw.println("label="+yt.index+" predict="+best_fset.label.index+" score="+best_fset.score);
			}
			/*
			best_fset = beam_search(words, WeightsCut.all2part(
					part_weights, words, local_label_set), local_label_set,
					local_learn_parm);
					*/


			// }
			/** 预测标记和样本相同 ***/
			if (yt.index == best_fset.label.index) {
				correct_num++;
			} 
			else
			{
				incorrect_num++;
			}

		}
		
		acc=((double)correct_num)/((double)(correct_num+incorrect_num));
		if(log_level>3)
		{
		  pw.println("looop:"+loop_i);
		  pw.println("correct_num:"+correct_num);
		  pw.println("incorrect_num:"+incorrect_num);
		  pw.println("acc:"+acc);
		  pw.close();
		
		}
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}
		return acc;
		
	}
	
}
