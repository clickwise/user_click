package cn.clickwise.clickad.classify;

import org.jmlp.classify.svm_struct.source.svm_struct_api_factory;
import org.jmlp.classify.svm_struct.source.svm_struct_main;

public class ClassifierModel {

	public static void main(String[] args) {
		
		svm_struct_main svm_struct = new svm_struct_main();
		
		if (args.length < 2) {
			System.out
					.println("Usage:ClassifierModel [<api_type>] <model> \n"
							+ " api_type: svm struct api type for example:multiclass, \n"
							+ " model: model save path \n");
			System.exit(1);
		}

		if (args.length == 1) {//default: multiclass
			double c = 5000.0;
			svm_struct_api_factory ssaf=new svm_struct_api_factory(0);
			svm_struct.train_from_stream(c, args[0]);	
		}
		else if(args.length==2)
		{
			double c = 5000.0;
			svm_struct_api_factory ssaf=new svm_struct_api_factory(Integer.parseInt(args[0]));
			svm_struct.train_from_stream(c, args[1]);
		}

	}
	
	

}
