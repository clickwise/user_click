package cn.clickwise.liqi.classify.svm.singlehier.source;

public class svm_struct_common {

	public static int struct_verbosity=1;
	public static final int   NSLACK_ALG     =    0;
    public static final int   NSLACK_SHRINK_ALG    =    1;
    public static final int   ONESLACK_PRIMAL_ALG   =   2;
    public static final int   ONESLACK_DUAL_ALG      =  3;
    public static final int   ONESLACK_DUAL_CACHE_ALG  =4;
    
    public static String printW(double[] w, int sizePhi, int n,double C)
    {
    	 String log_info="";
    	  int i;
    	  log_info="---- w ----\n";
    	  for(i=0;i<sizePhi;i++)
    	    {
    	      log_info=log_info+w[i]+" ";
    	    }
    	  log_info=log_info+"\n----- xi ----\n";
    	  for(;i<sizePhi+2*n;i++)
    	    {
    	      log_info=log_info+1/Math.sqrt(2*C)*w[i]+" ";
    	    }
    	  log_info=log_info+"\n";
    	  
    	  return log_info;
    }
    
}
