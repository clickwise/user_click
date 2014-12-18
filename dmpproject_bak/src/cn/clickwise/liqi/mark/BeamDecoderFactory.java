package cn.clickwise.liqi.mark;

import java.util.Properties;

import org.apache.spark.api.java.JavaSparkContext;

import cn.clickwise.liqi.nlp.classify.basic.ModelClassify;
import cn.clickwise.liqi.nlp.classify.medlda.singlehier.api.SingleMedldaClassify;
import cn.clickwise.liqi.nlp.classify.svm.singlehier.api.SingleHierSVMClassify;

/**
 * 根据输入的参数返回合适的BeamDecoder解码器
 * @author lq
 *
 */
public class BeamDecoderFactory {

	public static BeamDecoder create(LEARNPARM learn_parms)
	{
	   BeamDecoder bd=null;
	   if(learn_parms.bd_type.equals("sfsbd"))
	   {
		  // System.out.println("creating SparkFeatSearchBD");
		  // bd=new SparkFeatSearchBD();
	   }
	   else if(learn_parms.bd_type.equals("plnbd"))
	   {
		   //System.out.println("creating PlainBeamDecoder");
		   bd=new PlainBeamDecoder();
	   }
	   else if(learn_parms.bd_type.equals("plnbdns"))
	   {
		   //System.out.println("creating PlainBeamDecoder");
		   bd=new PlainBeamDecoderNoSearch();
	   }
	   return bd;
	}
	public static BeamDecoder createSFSBDBAK(LEARNPARM learn_parms,JavaSparkContext jsc)
	{
		//SparkFeatSearchBD bd=null;
		// bd=new SparkFeatSearchBD(jsc);
	   
	    return null;
	}
}
