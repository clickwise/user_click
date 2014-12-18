package cn.clickwise.liqi.external.bash;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 调用linux系统命令
 * @author lq
 *
 */
public class BashCmd {

	/**
	 * 执行linux 命令
	 * @param cmd
	 * @return
	 */
	public static String execmd(String cmd)	{		
        try {  

            String[] cmdA = { "/bin/sh", "-c", cmd };  
            System.out.println(cmd);
            Process process = Runtime.getRuntime().exec(cmdA);
            
            LineNumberReader br = new LineNumberReader(new InputStreamReader(process.getInputStream()));  
            StringBuffer sb = new StringBuffer();  

            String line;  
            while ((line = br.readLine()) != null) {  
                 System.out.println(line);  
                 sb.append(line).append("\n");  
             }  
             return sb.toString();  
         } catch (Exception e) {  
             e.printStackTrace();  
         }  
         return null;  	
	}
	
	public static void main(String[] args)
	{
	      String train_cmd="";
	      String c="5000";
	      String train_format_file="/home/hadoop/lq/ModelData/svm_singhier/train_format.txt";
	      String model_file="/home/hadoop/lq/ModelData/svm_singhier/model.txt";
	      train_cmd="/home/hadoop/lq/SWA/cplus/singhier_svm/svm_multiclass_learn -c "+c+" "+train_format_file+" "+model_file+" >logll.txt 2>&1 &";
	      System.out.println("train_cmd:"+train_cmd);
	      try{
	      execmd(train_cmd);
	      }
	      catch(Exception e)
	      {
	                System.out.println(e.getMessage());
	      }
	}
	
	
}
