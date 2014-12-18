package cn.clickwise.liqi.mark;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.util.Vector;

import scala.Tuple2;

/**
 * 更新权重
 * @author lq
 *
 */
public class WeightsUpdate {
	
	public static class DivFunction extends Function<Double,Double>{
        public double part_num;
		public DivFunction(double part_num)
        {
        	this.part_num=part_num;
        }
		public DivFunction() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public Double call(Double t) throws Exception {
	
			return t/this.part_num;
		}
		
	}

	public  double[] update_weight(double[] weights,WORD[] words,LABEL y,LABEL yt)
	{
	   double[] nweights=new double[weights.length];
	   for(int i=0;i<weights.length;i++)
	   {
		   nweights[i]=weights[i];
	   }
	   
	   WORD w=null;
	   for(int i=0;i<words.length;i++)
	   {
		   w=words[i];
		   nweights[FeatureIndex.featIndexMap(w, yt)]+=w.count;
		   nweights[FeatureIndex.featIndexMap(w, y)]-=w.count;
	   }
	   
		return nweights;
	}
	
	
	
	/**
	 * 加和两数组，普通方法
	 * @param w1
	 * @param w2
	 * @return
	 */
	public  double[] plain_sum_weight(double[] w1,double[] w2)
	{
  
		double[] sumw=new double[w1.length];
		for(int i=0;i<w1.length;i++)
		{
			sumw[i]=w1[i]+w2[i];
		}
        
        return sumw;	
	}
	
	/**
	 * 数组元素都除以pnum,普通方法
	 * @param w1
	 * @param pnum
	 * @return
	 */
	public  double[] plain_div_weight(double[] w1,double pnum)
	{
  
		double[] avgw=new double[w1.length];
		for(int i=0;i<w1.length;i++)
		{
			avgw[i]=w1[i]/pnum;
		}

        return avgw;	
	}
	
	
	/**
	 * 加和两数组，向量方法
	 * @param w1
	 * @param w2
	 * @return
	 */
	public static double[] vec_sum_weight(double[] w1,double[] w2)
	{
        Vector v1=new Vector(w1);
        v1.addInPlace(new Vector(w2));
        return v1.elements();	
	}
	
	/**
	 * 数组元素都除以pnum,向量方法
	 * @param w1
	 * @param pnum
	 * @return
	 */
	public static double[] vec_div_weight(double[] w1,double pnum)
	{
		 Vector v1=new Vector(w1);
		 Vector v2=v1.divide(pnum);
         return v2.elements();	
	}
	
	public static double[] sum_weight(double[] w1,double[] w2,JavaSparkContext jsc)
	{
  
        ArrayList<Tuple2<Double,Double>> w1l=new ArrayList<Tuple2<Double,Double>>(); 
        for(int i=0;i<w1.length;i++)
        {
        	w1l.add(new Tuple2(w1[i],w2[i]));
        }
        
        JavaRDD<Tuple2<Double,Double>> wrdd=jsc.parallelize(w1l);
        
        JavaRDD<Double> sumrdd=wrdd.map(new Function<Tuple2<Double,Double>,Double>() {
					public Double call(Tuple2<Double,Double> t)
							throws Exception {	         
						return t._1+t._2;
					}
				});
        
        List<Double> sumlist=sumrdd.collect();
        double[] nw=new double[w1.length];
        for(int i=0;i<nw.length;i++)
        {
        	nw[i]=sumlist.get(i);
        }
        
        return nw;	
	}
	
	
	
	
	public static double[] div_weight(double[] w1,double pnum,JavaSparkContext jsc)
	{
  
        ArrayList<Double> w1l=new ArrayList<Double>(); 
        for(int i=0;i<w1.length;i++)
        {
        	w1l.add(w1[i]);
        }
        
        JavaRDD<Double> wrdd=jsc.parallelize(w1l);
        
        JavaRDD<Double> sumrdd=wrdd.map(new DivFunction(pnum));
        
        List<Double> sumlist=sumrdd.collect();
        double[] nw=new double[w1.length];
        for(int i=0;i<nw.length;i++)
        {
        	nw[i]=sumlist.get(i);
        }
        
        return nw;	
	}
	
	public static double[] regular_weight(double[] w)
	{
		double sum=0;
		double[] nw=new double[w.length];
		for(int i=0;i<w.length;i++)
		{
			sum+=w[i];
		}
		if(sum==0)
		{
			sum=1000000;
		}
		for(int i=0;i<w.length;i++)
		{
			nw[i]=w[i]/sum;
		}
		return nw;
	}
	
	public static double[] regular_weight_abs(double[] w)
	{
		double sum=0;
		double[] nw=new double[w.length];
		for(int i=0;i<w.length;i++)
		{
			sum+=Math.abs(w[i]);
		}
		if(sum==0)
		{
			sum=1000000;
		}
		for(int i=0;i<w.length;i++)
		{
			nw[i]=(w[i]*2)/sum;
		}
		return nw;
	}
	
}
