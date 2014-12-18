package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class FileLinHttp {

	public static void main(String[] args) throws Exception
	{
	  FileReader fr=new FileReader(new File("D:/youxi_20140211/youxi_sel_20140211.txt"));
	  BufferedReader br=new BufferedReader(fr);
	  String line="";
	  FileWriter fw=new FileWriter(new File("D:/youxi_20140211/youxi_sel_20140211_http.txt"));
	  PrintWriter pw=new PrintWriter(fw);
	  StringTokenizer st=null;
	  String host="";
	  String score="";
	  String prefix="http://";
	  
	
	  String thost="";
	  boolean isInH=false;
	  Hashtable url_hash=new Hashtable();
	  String trunk_host="";
	  while((line=br.readLine())!=null)
	  {
		  line=line.trim();
          line=prefix+line;
          line=line.trim();
          pw.println(line);
	  }
	  
	  fw.close();
	  pw.close();
	  br.close();
	  fr.close();
	}
}
