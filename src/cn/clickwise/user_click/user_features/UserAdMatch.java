package cn.clickwise.user_click.user_features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.net.http.admatchtest.MatchAdTest;

/**
 * 将前一天的用户进行匹配，输出匹配结果
 * @author zkyz
 *
 */
public class UserAdMatch {
	public   MatchAdTest mat=new MatchAdTest();
	public void traverse_log(File log,File output_file) {
		FileReader fr = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
         
		BufferedReader br = null;
		String record = "";
		String json_record = "";
		
		FileWriter fw=null;
		PrintWriter pw=null;
		try {
			fw=new FileWriter(output_file);
			pw=new PrintWriter(fw);
			// fr=new FileReader(input_file);
			fis = new FileInputStream(log.getAbsolutePath());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((record = br.readLine()) != null) {
               if(SSO.tioe(record))
               {
            	   continue;
               }
              record=record.trim();	
        	  String cookie=record;
        	  pw.println(cookie+"\001"+mat.testMatchAd(cookie));
        	  
			}
			br.close();
			fw.close();
			pw.close();
		} catch (Exception e) {
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage:<UserAdMatch> <log_file> <output_file>");
			System.exit(1);
		}

		UserAdMatch uam = new UserAdMatch();
		uam.traverse_log(new File(args[0]),new File(args[1]));
	}
	
	
	  
}
