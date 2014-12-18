package cn.clickwise.liqi.mark;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.storage.StorageLevel;

import cn.clickwise.liqi.math.random.RandomGen;
import cn.clickwise.liqi.spark.job.TitleFindSpark.DefFunction;
import cn.clickwise.liqi.str.basic.SSO;

import scala.Tuple2;

/**
 * perceptron iter param mix
 * 
 * @author lq
 * 
 */
public class PerceptronIPM {

	public static JavaSparkContext jsc;
	public static double[] weights;
	public static LABEL[] label_set;
	public static LEARNPARM learn_parm = null;
	public static double part_num;
	public static int loop_num = 1000;
	public static int top_num = 10000;
	public static ArrayList<COLITEM> collect_features = new ArrayList<COLITEM>();
	public static int cloop = 0;
	public static JavaRDD<FEATSET> init_src = null;
	public static Broadcast<JavaRDD<FEATSET>> bc = null;

	public static class EpochPerceptron extends
			PairFlatMapFunction<Iterator<String>, String, double[]> {
		public boolean col_feat = false;
		public LEARNPARM local_learn_parm = learn_parm;
		public LABEL[] local_label_set;
		public double[] part_weights = null;

		// public JavaRDD<FEATSET> init_epoch_src;
		public EpochPerceptron(boolean col_feat) {
			this.col_feat = col_feat;
			this.local_learn_parm = learn_parm;

			System.out.println("init src 3 size:" + init_src.count());
			FEATSET ifs = null;
			ArrayList<String> init_list = new ArrayList<String>();
			for (int i = 0; i < label_set.length; i++) {
				ifs = new FEATSET();
				ifs.label = label_set[i];
				init_list.add("test" + i);
			}

			// System.out.println("init_epoch_src 3 size:"+init_epoch_src.count());
			this.local_label_set = new LABEL[label_set.length];
			for (int i = 0; i < label_set.length; i++) {
				this.local_label_set[i] = label_set[i];
			}
			part_weights=new double[weights.length];
			for(int i=0;i<weights.length;i++)
			{
			  part_weights[i] = 0;
			}
		}

		public static class CopyFunction extends Function<FEATSET, Boolean> {

			public CopyFunction() {
				// TODO Auto-generated constructor stub
			}

			@Override
			public Boolean call(FEATSET t) throws Exception {

				return true;
			}

		}

		@Override
		public Iterable<Tuple2<String, double[]>> call(Iterator<String> part_ds)
				throws Exception {
			System.out.println("part_weights.length:"+part_weights.length);
			double[] local_part_weights=new double[part_weights.length];
			for(int i=0;i<local_part_weights.length;i++)
			{
				local_part_weights[i] = 0;
			}
			// System.out.println("in call");
			if (part_ds == null) {
				// System.out.println("part_ds is null");
				return null;
			} else {
				// Iterator<String> part_ds_bak=part_ds;
				// System.out.println("part_ds.size:"+part_ds_bak.next());
				// System.out.println("part_ds is not null");
			}

			String lds = "";// 一行样本
			String lst = "";// 标记
			String lwo = "";// 单词
			String did = "";// docid
			String rankey="";
			String[] seg_arr = null;
			//WORD[] words = null;
			//FEATSET best_fset = null;
			//LABEL yt = null;
			
			WeightsCut wc=new WeightsCut();
			WeightsUpdate wu=new WeightsUpdate();
			int part_record=0;
			int correct_num=0;
			int incorrect_num=0;
			StrToClass sc=new StrToClass();
			
			LEARNPARM learn_parms = new LEARNPARM();
			learn_parms.bd_type = "plnbdns";
			learn_parms.label_num = 28;
			learn_parms.top_num = 1000;
			double[] weights = new double[39504 * (learn_parms.label_num)];
			BeamDecoder bd = BeamDecoderFactory.create(learn_parms);
			////////////////////////////
			
			while (part_ds.hasNext()) {
				part_record++;
				 lds=part_ds.next();
				 lds=lds.trim();
	             seg_arr=lds.split("\001");
	            if((seg_arr==null)||(seg_arr.length!=4))
	            {
	            	continue;
	            }
				did = seg_arr[2];
				String label = seg_arr[1];
				//String lwo = "13:1 133:3 277:3 624:3 659:3 938:3 1393:58 1395:3 1405:3 1461:3 1684:3 1839:3 2228:3 2941:3 3248:3 8260:3 26658:3";
	            lwo=seg_arr[3].trim();
				WORD[] words = sc.str2words(lwo);


				//for (int l = 1; l < 10; l++) {
				
					FEATSET best_fset = beam_search(words,
							wc.all2part(weights, words, local_label_set),
							local_label_set, learn_parms);

					LABEL yt = sc.str2label(label, learn_parms);
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
			////////////////////////
			/*
			while (part_ds.hasNext()) {
				part_record++;
				lds = part_ds.next();
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
				yt = sc.str2label(lst, local_learn_parm);
				System.out.println("in mp docid:"+did+" yt:"+yt.index);
				words = sc.str2words(lwo);
				if (words == null) {
					continue;
				}
				
				//ShuffleArray.shuffle(words);
				best_fset = bd.beam_search(words, wc.all2part(
						local_part_weights, words, local_label_set), local_label_set,
						local_learn_parm);
				

				
				if (did.indexOf("3") > -1) {
					String word_str = "";
					for (int j = 0; j < best_fset.words.size(); j++) {
						word_str += ((best_fset.words.get(j)).index + ":"
								+ (best_fset.words.get(j)).count + " ");
					}
					word_str = word_str.trim();

					System.out.println("sel word:" + did + " " + word_str);
				}
				if (yt.index == best_fset.label.index) {
                    correct_num++;
				} else {
					local_part_weights = wu.update_weight(local_part_weights,
							words, best_fset.label, yt);
					incorrect_num++;
					//part_weights=WeightsUpdate.regular_weight_abs(part_weights);
				}

			}
          */
          
			double acc=((double)(correct_num))/((double)(correct_num+incorrect_num));
			System.out.println("correct_num="+correct_num);
			System.out.println("incorrect_num="+incorrect_num);
			System.out.println("acc="+acc);
			System.out.println("part_record="+part_record);
			// TODO Auto-generated method stub
			ArrayList<Tuple2<String, double[]>> res = new ArrayList<Tuple2<String, double[]>>();
			res.add(new Tuple2(RandomGen.RandomString(50), local_part_weights));
			return res;
		}
		
		public FEATSET beam_search(WORD[] words, FEAT[] sub_weights,
				LABEL[] label_set, LEARNPARM learn_parms) {
			ArrayList<FEATSET> src = null;
			src = new ArrayList<FEATSET>();
			TopList tl=new TopList();
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
			return tl.getBestFromList(src);
		}

	}

	public static JavaRDD<FEATSET> getTop(int num, JavaRDD<FEATSET> rdd) {
		List<FEATSET> output = rdd.collect();
		Collections.sort(output, new Comparator<FEATSET>() {
			@Override
			public int compare(FEATSET t1, FEATSET t2) {
				if (t1.score < t2.score) {
					return 1;
				} else if (t1.score > t2.score) {
					return -1;
				}
				return 0;
			}
		});

		ArrayList<FEATSET> small_list = new ArrayList<FEATSET>();
		for (int i = 0; i < num; i++) {
			if (i > (output.size() - 1)) {
				break;
			}
			small_list.add(output.get(i));
		}

		JavaRDD<FEATSET> nrdd = jsc.parallelize(small_list);
		return nrdd;
	}

	public static class FieldFunction extends
			Function<Tuple2<String, double[]>, double[]> {

		public FieldFunction() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public double[] call(Tuple2<String, double[]> t) throws Exception {
			return t._2;
		}

	}

	public static void main(String[] args) {
		if (args.length < 5) {
			System.err
					.println("Usage: PerceptronIPM <master> [hdfsTrainFile] [hdfsDictFile] [hdfsLabelFile] [hdfsTestFile]");
			System.exit(1);
		}

		jsc = new JavaSparkContext(args[0], "PerceptronIPM",
				System.getenv("SPARK_HOME"), "/home/hadoop/spark/swa/swa.jar");

		learn_parm = new LEARNPARM();
		learn_parm.bd_type = "plnbdns";

		JavaRDD<String> dataSet = jsc.textFile(args[1]);
		JavaRDD<String> dictSet = jsc.textFile(args[2]);
		JavaRDD<String> labelSet = jsc.textFile(args[3]);
		JavaRDD<String> testSet = jsc.textFile(args[4]);
		List<String> testList=testSet.collect();
		StrToClass sc=new StrToClass();
		List<String> lst = labelSet.collect();
		ArrayList<FEATSET> init_list = new ArrayList<FEATSET>();
		FEATSET ifs = null;
		label_set = new LABEL[lst.size()];
		for (int i = 0; i < label_set.length; i++) {
			label_set[i] = sc.str2label(lst.get(i), learn_parm);
			System.out.println(label_set[i].index);
			ifs = new FEATSET();
			ifs.label = label_set[i];
			init_list.add(ifs);
		}
		init_src = jsc.parallelize(init_list);
		init_src.persist(new StorageLevel());
		bc = jsc.broadcast(init_src);

		System.out.println("init_src count1:" + init_src.count());
		learn_parm.label_num = label_set.length;
		learn_parm.top_num = 1000;
		weights = new double[((int) dictSet.count()) * (int) (labelSet.count())];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}

		for (int i = 1; i < loop_num; i++) {
			cloop = i;
			System.out.println("loop :" + i);

			JavaPairRDD<String, double[]> part_weights = dataSet
					.mapPartitions(new EpochPerceptron(false));
			part_num = part_weights.count();
			System.out.println("loop " + i + " part_num:" + part_num);
			List<Tuple2<String, double[]>> pw_res = part_weights.collect();
			Tuple2<String, double[]> pw_t = null;
			for (int k = 0; k < pw_res.size(); k++) {
				pw_t = pw_res.get(k);
				System.out.println("k=" + k + "  " + pw_t._1);
				for (int l = 0; l < 100; l++) {
					System.out.print(l + ":" + pw_t._2[l] + " ");
				}
				System.out.println();
			}
			JavaRDD<double[]> part_weigths_no_key = part_weights
					.map(new FieldFunction());
			double[] sum_weights_no_key = part_weigths_no_key
					.reduce(new Function2<double[], double[], double[]>() {
						@Override
						public double[] call(double[] i1, double[] i2) {
							String tk = RandomGen.RandomString(50);
							WeightsUpdate wu=new WeightsUpdate();
							return wu.plain_sum_weight(i1, i2);
							
							/*
							return WeightsUpdate.vec_sum_weight(i1, i2);
							*/
						}
					});
			WeightsUpdate wu=new WeightsUpdate();
			weights = wu.plain_div_weight(sum_weights_no_key,
					part_num);
			//weights=WeightsUpdate.regular_weight(weights);
			if(i%5==0)
			{
			  double loop_acc=PerceptronPredict.predict_accurate(weights, testList, label_set, learn_parm,i);
			  System.out.println(" accuracy is :"+loop_acc);
			  System.out.println("test size is "+testSet.count());
			}
			
			/*
			weights = WeightsUpdate.vec_div_weight(sum_weights_no_key,
					part_num);
					*/
			/*
			 * Tuple2<String, double[]> sum_weights = part_weights .reduce(new
			 * Function2<Tuple2<String, double[]>, Tuple2<String, double[]>,
			 * Tuple2<String, double[]>>() {
			 * 
			 * @Override public Tuple2<String, double[]> call( Tuple2<String,
			 * double[]> i1, Tuple2<String, double[]> i2) { String
			 * tk=RandomGen.RandomString(50); return new
			 * Tuple2(tk,WeightsUpdate.sum_weight(i1._2, i2._2, jsc)); } });
			 * weights = WeightsUpdate.div_weight(sum_weights._2, part_num,
			 * jsc);
			 */
			/*
			 * JavaRDD<double[]> part_weights = dataSet .map(new
			 * SingleEpochPerceptron(false)); part_num = part_weights.count();
			 * double[] sum_weights = part_weights .reduce(new
			 * Function2<double[], double[], double[]>() {
			 * 
			 * @Override public double[] call( double[] i1,double[] i2) { return
			 * WeightsUpdate.sum_weight(i1, i2, jsc); } }); weights =
			 * WeightsUpdate.div_weight(sum_weights, part_num, jsc);
			 */

		}

		
		double acc=PerceptronPredict.predict_accurate(weights, testList, label_set, learn_parm,10000);
		System.out.println(" accuracy is :"+acc);
		System.out.println("test size is "+testSet.count());
		
		JavaPairRDD<String, double[]> part_weights = dataSet
				.mapPartitions(new EpochPerceptron(true));

		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(new File("/tmp/col_feat.txt"));
			pw = new PrintWriter(fw);
			COLITEM colit = null;
			String word_str = "";
			for (int i = 0; i < collect_features.size(); i++) {
				colit = collect_features.get(i);
				word_str = "";
				for (int j = 0; j < colit.words.size(); j++) {
					word_str += colit.words.get(j).index + " ";
				}
				word_str = word_str.trim();
				pw.println(colit.docid + " " + word_str);
     
			}
			pw.println("accuracy is :"+acc);
			pw.println("test size is "+testSet.count());
			pw.close();
			fw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static class AddWordFunction extends Function<FEATSET, FEATSET> {

		public WORD word = null;
		/**
		 * 单词对应各个label的权重
		 */
		public WEI[] wlw = null;

		public AddWordFunction(WORD word, WEI[] wlw) {

			this.word = word;
			this.wlw = new WEI[wlw.length];
			for (int i = 0; i < wlw.length; i++) {
				this.wlw[i] = wlw[i];
			}

		}

		public AddWordFunction() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public FEATSET call(FEATSET fset) throws Exception {
			if (fset == null) {
				return null;
			}
			FEATSET nfset = fset;
			System.out.println("fset index:" + fset.label.index);
			nfset.addWord(word, wlw[fset.label.index - 1]);
			nfset.calScore();
			return nfset;
		}

	}

	public static FEATSET beam_search(WORD[] words, FEAT[] sub_weights,
			LABEL[] label_set, LEARNPARM learn_parms) {

		JavaRDD<FEATSET> src = null;
		JavaRDD<FEATSET> tgt = null;
		WeightsCut wc=new WeightsCut();
		 System.out.println("words len ab:"+words.length);
		// System.out.println(init_src);
		 System.out.println("init_src count2:"+bc.value().count());
		// System.out.println("here a:");
		src = init_src;
		JavaRDD<FEATSET> src_temp = null;
		JavaRDD<FEATSET> tgt_temp = null;
		for (int i = 0; i < words.length; i++) {
			src_temp = src;
			// System.out.println("word["+i+"]="+words[i].index);
			// System.out.println("src temp size="+src_temp.count());
			// **words[i] 加到 src特征集合中
			tgt = src_temp.map(new AddWordFunction(words[i], wc.weightToWei(sub_weights, (i+1), label_set)));
			src = src.union(tgt);

			// **words[i] 形成独立的特征集合加到src中
			tgt_temp = init_src.map(new AddWordFunction(words[i], wc.weightToWei(sub_weights, i+1, label_set)));
			src = src.union(tgt_temp);
			if (src.collect().size() > top_num) {
				src = getTop(top_num, src);
			}
			// src.takeOrdered(num);排序src
		}

		FEATSET maxfset = src
				.reduce(new Function2<FEATSET, FEATSET, FEATSET>() {
					@Override
					public FEATSET call(FEATSET i1, FEATSET i2) {
						if (i1.score > i2.score) {
							return i1;
						} else {
							return i2;
						}
					}
				});

		return maxfset;
	}

}
