package cn.clickwise.liqi.mark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class TopRDD {

	public static JavaRDD<FEATSET> getTop(int num,JavaRDD<FEATSET> rdd,JavaSparkContext jsc)
	{
		List<FEATSET> output = rdd.collect();
        Collections.sort(output, new Comparator<FEATSET>() {
              @Override
	               public int compare(FEATSET t1, FEATSET t2) {
	                    if(t1.score < t2.score) {
	                         return 1;
	                    } else if(t1.score > t2.score) {
	                         return -1;
	                    }
	                    return 0;
	               }
	          });
        
        ArrayList<FEATSET> small_list=new ArrayList<FEATSET>();
        for(int i=0;i<num;i++)
        {
        	if(i>(output.size()-1))
        	{
        		break;
        	}
        	small_list.add(output.get(i));
        }
        
        JavaRDD<FEATSET> nrdd=jsc.parallelize(small_list);
		return nrdd;
	}
	
	
}
