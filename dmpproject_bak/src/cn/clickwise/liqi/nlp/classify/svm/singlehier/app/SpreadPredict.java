package cn.clickwise.liqi.nlp.classify.svm.singlehier.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import cn.clickwise.liqi.crawler.basic.FilterContent;
import cn.clickwise.liqi.nlp.classify.basic.ModelClassify;
import cn.clickwise.liqi.nlp.classify.basic.ModelClassifyFactory;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;

public class SpreadPredict {
	
	
	 public void predict_spread(String input_file,String output_file,String config_file) throws Exception
	 {
		   Properties prop=ConfigFileReader.getPropertiesFromFile(config_file);
			
	       ModelClassify mc=ModelClassifyFactory.create(prop);
	        
	 	   FileWriter fw=new FileWriter(new File(output_file));
		   PrintWriter pw=new PrintWriter(fw);
		   
	       FileReader fr=new FileReader(new File(input_file));
	 	   BufferedReader br=new BufferedReader(fr);
	 	   
	 	   String line="";
	 	   String[] seg_arr=null;
	 	   String url="";
	 	   String text="";
	 	   String label="";
	 	   while((line=br.readLine())!=null)
	 	   {
	 		   line=line.trim();
	 				   
	 		   seg_arr=line.split("\\s+");
	 		   if(seg_arr==null)
	 		   {
	 			   continue;
	 		   }
	 		   if(seg_arr.length<2)
	 		   {
	 			   continue;
	 		   }
	 		   url=seg_arr[0].trim();
	 		   text="";
	 		   for(int j=1;j<seg_arr.length;j++)
	 		   {
	 			 text=text+seg_arr[j]+" ";   
	 		   }
	 		   text=text.trim();
	 		   if(!(SSO.tnoe(url)))
	 		   {
	 			   continue;
	 		   }
	 		   if(!(SSO.tnoe(text)))
	 		   {
	 			   continue;
	 		   }


	 		   text=text.replaceAll("\001", "");
	 		   text=FilterContent.getFilterContent(text);
	 		   label=mc.predictFromPlainText(text);
	           pw.println(url+"\001"+label);
	 	   }
	 	   
	 	   fr.close();
	 	   br.close();
	 	   fw.close();
	 	   pw.close();
		   
		   
		   
	 }
	   
	   
	public static void main(String[] args) throws Exception
	{
		SpreadPredict sp=new SpreadPredict();
		String config_file="";
		String input_file="";
		String output_file="";
		if (args.length != 3) {
			System.out.println("用法 :EWAECServer <configure file>");
			System.exit(1);
		}
	
		config_file=args[0];
		input_file=args[1];
		output_file=args[2];
		sp.predict_spread(input_file, output_file, config_file);
	
	}
	
	
}
