package cn.clickwise.liqi.mark;

import java.util.ArrayList;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import cn.clickwise.liqi.spark.job.TitleFindSpark.DefFunction;

import scala.Tuple2;

/**
 * 基于spark平台实现的,进行特征搜索的beamdecoder
 * @author lq
 *
 */
public class SparkFeatSearchBD extends BeamDecoder {

	public static JavaSparkContext jsc=null;
	public static int top_num=10000;
	public SparkFeatSearchBD(JavaSparkContext temp_jsc)
	{
		jsc=temp_jsc;
		/*
		jsc = new JavaSparkContext("spark://192.168.110.180:7077", "PerceptronIPM",
				System.getenv("SPARK_HOME"), "/home/hadoop/spark/swa/swa.jar");
		*/
	}
	public static class AddWordFunction extends Function<FEATSET, FEATSET>{
        
		public WORD word=null;
		public  static JavaSparkContext local_jsc=null;
        /**
         * 单词对应各个label的权重
         */
        public WEI[] wlw=null;

		public AddWordFunction(WORD word,WEI[] wlw,JavaSparkContext jsc)
        {
			this.local_jsc=jsc;
        	this.word=word;
        	this.wlw=new WEI[wlw.length];
        	for(int i=0;i<wlw.length;i++)
        	{
        		this.wlw[i]=wlw[i];
        	}
        	
        }
		
		public AddWordFunction() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public FEATSET call(FEATSET fset) throws Exception {
			if(fset==null)
			{
				return null;
			}
			FEATSET nfset=fset;
	        nfset.addWord(word, wlw[fset.label.index-1]);
	        nfset.calScore();
			return nfset;
		}
		
	}
	
	
	public FEATSET beam_search(WORD[] words, FEAT[] init_weights,
			LABEL[] label_set, LEARNPARM learn_parms,JavaSparkContext temp_jsc) {
		
		
		JavaRDD<FEATSET> src=null;
		JavaRDD<FEATSET> tgt=null;
		ArrayList<FEATSET> init_list=new ArrayList<FEATSET>();
		FEATSET ifs=null;//初始feat set
		for(int i=0;i<label_set.length;i++)
		{
			System.out.println("word[0]="+words[0].index);
			ifs=new FEATSET(words[0],label_set[i],new WEI(init_weights[(label_set[0].label_num)*(label_set[0].index-1)].weight));
			ifs.calScore();
			System.out.println("score="+ifs.score);
			init_list.add(ifs);
		}
		System.out.println("words len a:"+words.length);
		System.out.println("words len size:"+init_list.size());
		ArrayList<String> tlst=new ArrayList<String>();
		tlst.add("hello");
		tlst.add("word");
		JavaRDD<String> tgtt=temp_jsc.parallelize(tlst);
		System.out.println("tgtt count:"+tgtt.count());
		tgt=PerceptronIPM.jsc.parallelize(init_list);
		
		System.out.println("tgt.count:"+tgt.count());
		src=tgt;
		WeightsCut wc=new WeightsCut();
		JavaRDD<FEATSET> src_temp=null;
		for(int i=1;i<words.length;i++)
		{
			src_temp=src;
			System.out.println("word["+i+"]="+words[i].index);
			//**words[i] 加到  src特征集合中						
		    tgt=src_temp.map(new AddWordFunction(words[i],wc.weightToWei(init_weights, i+1, label_set),temp_jsc)); 
			src=src.union(tgt);
			
			//**words[i] 形成独立的特征集合加到src中
			init_list=new ArrayList<FEATSET>();
			for(int j=0;j<label_set.length;j++)
			{
				ifs=new FEATSET(words[i],label_set[j],new WEI(init_weights[(label_set[j].label_num)*(label_set[j].index-1)+i].weight));
				ifs.calScore();
				init_list.add(ifs);
			}		
			src=src.union(jsc.parallelize(init_list));
			if(src.collect().size()>top_num)
			{
				src=TopRDD.getTop(top_num, src, temp_jsc);
			}
			//src.takeOrdered(num);排序src				
		}
		
	   FEATSET maxfset=	src.reduce(new Function2<FEATSET, FEATSET, FEATSET>() {
	        @Override
	        public FEATSET call(FEATSET i1,FEATSET i2) {
	        	if(i1.score>i2.score)
	        	{
	        		return i1;
	        	}
	        	else
	        	{
	        		return i2;
	        	}      
	        }
	      });
		
		return maxfset;
	}


	@Override
	public FEATSET beam_search(WORD[] words, FEAT[] init_weights,
			LABEL[] label_set, LEARNPARM learn_parms) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
