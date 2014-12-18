package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class WordStatis {

	public static void main(String[] args) throws Exception
	{
		  InputStreamReader isr = new InputStreamReader(System.in);
		  BufferedReader br = new BufferedReader(isr);
		  String line = "";
		  
		  OutputStreamWriter osw = new OutputStreamWriter(System.out);
		  PrintWriter pw = new PrintWriter(osw);
		  
		  String new_line = ""; 
		  int c=0;
		  while ((line = br.readLine()) != null) { 
		     line =line.trim();
		     if ((line == null) || (line.equals(""))) 
		     { 
			   continue; 
		     }	  
             if(line.length()<6)
             {
		      pw.println(new_line);
		      c++;
             }		  
		  }
		  System.out.println("c:"+c);
		  br.close(); 
		  isr.close();
		  osw.close(); 
		  pw.close();
	}
	
	
	
}
