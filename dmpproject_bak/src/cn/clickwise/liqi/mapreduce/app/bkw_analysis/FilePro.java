package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class FilePro {

	public static void main(String[] args) throws Exception
	{
	  FileReader fr=new FileReader(new File("D:/project/spread_data/youxi/youxi_20140217/qq.txt"));
	  BufferedReader br=new BufferedReader(fr);
	  String line="";
	  FileWriter fw=new FileWriter(new File("D:/project/spread_data/youxi/youxi_20140217/youxi_20140217.txt"));
	  PrintWriter pw=new PrintWriter(fw);
	  StringTokenizer st=null;
	  String host="";
	  String score="";
	  String prefix="http://";
	  
	  String[] host_arr={"www.qidian.com","www.ranwen.net","quanshu.cc","www.xxsy.net","www.xsjie.com","about:blank","www.59to.com","www.9see.com","www.biquge.com","www.yankanshu.com","www.epzw.com"};
	  String thost="";
	  boolean isInH=false;
	  Hashtable url_hash=new Hashtable();
	  String trunk_host="";
	  while((line=br.readLine())!=null)
	  {
		  line=line.trim();
		  if((line==null)||(line.equals("")))
		  {
			  continue;
		  }
		  isInH=false;
		for(int i=0;i<host_arr.length;i++)
		{
			thost=host_arr[i].trim();
			if((line.indexOf(thost))!=-1)
			{
				 isInH=true;
			}
		}
		  
		if(isInH==false)
		{
			if(!(url_hash.containsKey(line)))
			{
				trunk_host=line.replaceFirst("http:\\/\\/", "");
				
			  pw.println(trunk_host);
			  url_hash.put(line, 1);
			}
		}
	  }
	  fw.close();
	  pw.close();
	  br.close();
	  fr.close();
	}
	
	
}
