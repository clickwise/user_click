package cn.clickwise.liqi.mark;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import cn.clickwise.liqi.file.utils.FileReaderUtil;
import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.math.random.RandomGen;

public class TestUnit {

	public static PrintWriter pw = null;
	public static int log_level = 5;
	public static void init(String log_file)
	{
		if (log_level > 3) {
			try {
				FileWriter fw = new FileWriter(new File(log_file));
				pw=new PrintWriter(fw);
			} catch (Exception e) {
               System.out.println(e.getMessage());
			}
		}
	}
	/**
	 * 测试plain beam decoder
	 */
	public static void testPBD(String input_file,String test_file,String log_file,String bd_type,String train_file,String dict_file,String label_file) {
		init(log_file);
		
		String[] sample = null;
		int correct_num=0;
		int incorrect_num=0;
		HashMap<String,String> dict_map=FileReaderUtil.getIndexLabelFromPlainFile(dict_file);
		String[][] trains=null;
		HashMap<String,String> train_map=new HashMap<String,String>();
		HashMap<String,String> label_map=FileReaderUtil.getIndexLabelFromPlainFile(label_file);
		try{
		trains=FileToArray.fileToDoubleDimArr(train_file, "\001");
		for(int i=0;i<trains.length;i++)
		{
			//System.out.println(trains[i][1]+":"+trains[i][2]);
			if(!(train_map.containsKey(trains[i][1].trim())))
			{
				
				train_map.put(trains[i][1].trim(), trains[i][2]);
			}
		}
		
		sample = FileToArray.fileToDimArr(input_file);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		LEARNPARM learn_parms = new LEARNPARM();
		learn_parms.bd_type = bd_type;
		learn_parms.label_num = 28;
		learn_parms.top_num = 1000;
		double[] weights = new double[39504 * (learn_parms.label_num)];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}
		BeamDecoder bd = BeamDecoderFactory.create(learn_parms);
		StrToClass sc=new StrToClass();
		LABEL[] local_label_set = new LABEL[learn_parms.label_num];
		for (int i = 1; i <= learn_parms.label_num; i++) {
			local_label_set[i - 1] =sc.str2label(i + "",
					learn_parms);
		}
		WeightsCut wc=new WeightsCut();
		WeightsUpdate wu=new WeightsUpdate();
		
		for(int loop=0;loop<100;loop++)
		{
			incorrect_num=0;
			correct_num=0;
		for (int ll = 0; ll < sample.length; ll++) {
            String[] seg_arr=sample[ll].split("\001");
            if((seg_arr==null)||(seg_arr.length!=4))
            {
            	continue;
            }
			String did = seg_arr[2];
			String label = seg_arr[1];
			//String lwo = "13:1 133:3 277:3 624:3 659:3 938:3 1393:58 1395:3 1405:3 1461:3 1684:3 1839:3 2228:3 2941:3 3248:3 8260:3 26658:3";
            String lwo=seg_arr[3].trim();
			WORD[] words = sc.str2words(lwo);
            ShuffleArray.shuffle(words);

			//for (int l = 1; l < 10; l++) {
			    learn_parms.docid=did;
				FEATSET best_fset = bd.beam_search(words,
						wc.all2part(weights, words, local_label_set),
						local_label_set, learn_parms);
                
				LABEL yt = sc.str2label(label, learn_parms);
				did=did.trim();
				pw.println("----------------------------");
				pw.println("did="+did+" text=["+train_map.get(did)+"]");
				pw.println("y=" + best_fset.label.index+"["+label_map.get(best_fset.label.index+"") + "] yt="
						+ yt.index+"["+label_map.get(yt.index+"")+"]");
				pw.println("whole words=["+WordsToStr.wtsa(dict_map, words)+"]");
				pw.println("left words=["+WordsToStr.wts(dict_map, best_fset.words)+"]");
				pw.println("----------------------------");
				pw.flush();
				if (yt.index == best_fset.label.index) {
					correct_num++;
				} else {
					// weights=WeightsUpdate.regular_weight(weights);
					//PrintUtil.printNoZero(weights);
					weights = wu.update_weight(weights, words,
							best_fset.label, yt);
					incorrect_num++;
					//weights = WeightsUpdate.regular_weight_abs(weights);
					//PrintUtil.printNoZero(weights);

				}
			//}
		}
		
		double acc=((double)(correct_num))/((double)(correct_num+incorrect_num));
		pw.println("loop="+loop);
		pw.println("correct_num="+correct_num);
		pw.println("incorrect_num="+incorrect_num);
		pw.println("acc="+acc);
		System.out.println("loop="+loop);
		System.out.println("correct_num="+correct_num);
		System.out.println("incorrect_num="+incorrect_num);
		System.out.println("acc="+acc);
		pw.flush();
		try{
		 double test_acc=PerceptronPredict.predict_accurate(weights, FileToArray.fileToArrayList(test_file), local_label_set, learn_parms, 1000);
		 pw.println("test_acc="+test_acc);
		 System.out.println("test_acc="+test_acc);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		}

		pw.close();

	}

	public static void testSort() {
		ArrayList<FEATSET> all_list = new ArrayList<FEATSET>();
		FEATSET fis = new FEATSET();
		fis.score = 3;
		all_list.add(fis);

		fis = new FEATSET();
		fis.score = 5;
		all_list.add(fis);

		fis = new FEATSET();
		fis.score = 10;
		all_list.add(fis);

		ArrayList<FEATSET> small_list = TopList.getTopFromList(5, all_list);
		for (int i = 0; i < small_list.size(); i++) {
			System.out.println("sl[" + i + "]" + small_list.get(i).score);
		}
	}

	public static void testShuffle() {
		WORD[] words = new WORD[5];
		words[0] = new WORD(1, 1);
		words[1] = new WORD(2, 5);
		words[2] = new WORD(3, 2);
		words[3] = new WORD(4, 1);
		words[4] = new WORD(5, 3);
		System.out.println("orgin_words:");
		for (int i = 0; i < words.length; i++) {
			System.out.print(words[i].index + ":" + words[i].count + " ");
		}
		System.out.println();
		for (int l = 0; l < 10; l++) {
			ShuffleArray.shuffle(words);
			System.out.println("shuffle_words: " + l);
			for (int i = 0; i < words.length; i++) {
				System.out.print(words[i].index + ":" + words[i].count + " ");
			}
			System.out.println();
		}

	}

	public static void testStrShuffle() {
		String[] words = new String[5];
		words[0] = "str1";
		words[1] = "str2";
		words[2] = "str3";
		words[3] = "str4";
		words[4] = "str5";
		System.out.println("orgin_words:");
		for (int i = 0; i < words.length; i++) {
			System.out.print(words[i] + " ");
		}
		System.out.println();
		for (int l = 0; l < 10; l++) {
			ShuffleArray.shuffleStrs(words);
			System.out.println("shuffle_words: " + l);
			for (int i = 0; i < words.length; i++) {
				System.out.print(words[i] + " ");
			}
			System.out.println();
		}

	}

	public static void main(String[] args) {
		String input_file="temp/mark/host/tr_format.txt";
		String test_file="temp/mark/host/te_format.txt";
		String log_file="temp/mark/plainbeamdecoderx.txt";
		String bd_type="plnbdns";
		String train_file="temp/mark/train.txt";
		String dict_file="temp/mark/dict.txt";
		String label_file="temp/mark/label.txt";
		
		if(args.length!=3)
		{
			System.out.println("Usage: <input_file> <test_file> <log_file> <bd_type> <train_file> <dict_file> <label_file>");
		}
		/*
		input_file=args[0];
		test_file=args[1];
		log_file=args[2];
		bd_type=args[3];
		train_file=args[4];
		dict_file=args[5];
		label_file=args[6];
		*/
		testPBD(input_file,test_file,log_file,bd_type,train_file,dict_file,label_file);
		// testSort();
		// testShuffle();
		// testStrShuffle();
	}

}
