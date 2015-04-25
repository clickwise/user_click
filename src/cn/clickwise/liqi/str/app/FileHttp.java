package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class FileHttp {
	public static void main(String[] args) throws Exception
	{
	  FileReader fr=new FileReader(new File("D:/projects/spread_data/sifu/data0417/sum_sf_http.txt"));
	  BufferedReader br=new BufferedReader(fr);
	  String line="";
	  FileWriter fw=new FileWriter(new File("D:/projects/spread_data/sifu/data0417/sum_sf_sel.txt"));
	  PrintWriter pw=new PrintWriter(fw);

	  String[] seg_arr=null;
	  String host="";
	  String pv="";
	  while((line=br.readLine())!=null)
	  {
		  line=line.trim();
		  if((line==null)||(line.equals("")))
		  {
			  continue;
		  }
         
		  seg_arr=line.split("\001");
	      if(seg_arr.length!=1)
	      {
	    	  continue;
	      }
	      host=seg_arr[0].trim();
	
	      host=host.replaceFirst("http://", "");
	      pw.println(host);
	  }
	  fw.close();
	  pw.close();
	  br.close();
	  fr.close();
	}
}
