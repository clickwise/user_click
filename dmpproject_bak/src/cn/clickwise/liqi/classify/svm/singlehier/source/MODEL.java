package cn.clickwise.liqi.classify.svm.singlehier.source;



public class MODEL {
	  public int sv_num;
	  public int at_upper_bound;
	  public double b;
	  public DOC[] supvec;
	  public double[] alpha;
	  public int[] index;
	  public int totwords;
	  public int totdoc;
	  public KERNEL_PARM kernel_parm;
	  
	  public double loo_error,loo_recall,loo_precision;
	  public double xa_error,xa_recall,xa_precision;
	  public double[] lin_weights;
	  public double maxdiff;
}
