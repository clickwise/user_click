package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;


public class CateGroup {

   public void group_cate(String input_file,String output_file) throws Exception
   {
	   FileReader fr=new FileReader(new File(input_file));
	   BufferedReader br=new BufferedReader(fr);
	   
	   String line="";
	   
	   FileWriter fw=new FileWriter(new File(output_file));
	   PrintWriter pw=new PrintWriter(fw);
	   
	   String[] seg_arr=null;
	   String old_cate="";
	   Hashtable sam_hash=new Hashtable();
	   String old_ls="";
	   while((line=br.readLine())!=null)
	   {
		   line=line.trim();
		 //  System.out.println(line);
		   if(line==null)
		   {
			   continue;
		   }
		   seg_arr=line.split("\t");
		 //  System.out.println("line.length:"+seg_arr.length);
		   if(seg_arr.length<3)
		   {
			   continue;
		   }
		
		   old_cate=seg_arr[1].trim();
		   if((old_cate==null)||(old_cate.equals("")))
		   {
			   old_cate="NNAA";
		   }
		   if(!(sam_hash.containsKey(old_cate)))
		   {
			   sam_hash.put(old_cate, line+"\001");
		   }
		   else
		   {
			   old_ls=sam_hash.get(old_cate)+"";
			   old_ls=old_ls+line+"\001";
			   sam_hash.remove(old_cate);
			   sam_hash.put(old_cate, old_ls);
		   }		   
	   }
	   
	   fr.close();
	   br.close();
	   Enumeration key_enum=sam_hash.keys();
	   while(key_enum.hasMoreElements())
	   {
		   old_cate=key_enum.nextElement()+"";
		   old_cate=old_cate.trim();
		   if((old_cate==null)||(old_cate.equals("")))
		   {
			   continue;
		   }
		   old_ls=sam_hash.get(old_cate)+"";
		   old_ls=old_ls.trim();
		   seg_arr=old_ls.split("\001");
		   for(int i=0;i<seg_arr.length;i++)
		   {
			   line=seg_arr[i];
			   pw.println(line);
		   }
	   }
	   
	   fw.close();
	   pw.close();
	   
	   
	   
   }
	
	
	
   public static void main(String[] args) throws Exception
   {
	   CateGroup cg=new CateGroup();
	   String input_file="input/test_output_10000.txt";
	   String output_file="output/test_output_10000_gc.txt";
	   cg.group_cate(input_file, output_file);
	   
   }
}
