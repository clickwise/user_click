package cn.clickwise.clickad.classify;

import org.jmlp.classify.svm_struct.source.svm_struct_main;

public class ClassifierModel {
	
  public static void main(String[] args)
  {
	  svm_struct_main svm_struct=new svm_struct_main();
	  if(args.length!=1)
	  {
		  System.out.println("Usage:ClassifierModel <model>");
		  System.exit(1);
	  }
	  
	  double c=5000.0;
	  svm_struct.train_from_stream(c, args[0]);
	  
  }
}
