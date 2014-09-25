package cn.clickwise.lib.linux;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

//触发linux系统命令
public class COMMAND {

	/**
	 * 执行linux 命令
	 * @param cmd
	 * @return
	 */
	public static String exec(String cmd)	{		
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
	
	
}
