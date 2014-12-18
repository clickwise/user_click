package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;


public class LabelFlatToHierarchy {

	public void flatToHier(String source_file,String dest_file) throws Exception
	{
		FileReader fr=new FileReader(new File(source_file));
		BufferedReader br=new BufferedReader(fr);
		
		String line="";
		Hashtable first_hash=new Hashtable();
		
		
		FileWriter fw=new FileWriter(new File(dest_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String[] seg_arr=null;
		String first_cate="";
		String second_cate="";
		String old_scs="";
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("@");
			if((seg_arr==null)||(seg_arr.length!=2))
			{
				continue;
			}
			first_cate=seg_arr[0].trim();
			second_cate=seg_arr[1].trim();
			if(!first_hash.containsKey(first_cate))
			{
				first_hash.put(first_cate, second_cate+" ");
			}
			else
			{
				old_scs=first_hash.get(first_cate)+"";
				old_scs=old_scs+second_cate+" ";
				first_hash.remove(first_cate);
				first_hash.put(first_cate,old_scs );
			}
		}
		
		
		Enumeration cate_enum=first_hash.keys();
		while(cate_enum.hasMoreElements())
		{
			first_cate=cate_enum.nextElement()+"";
			old_scs=first_hash.get(first_cate)+"";
			old_scs=old_scs.trim();
			pw.println(first_cate+":"+old_scs);
		}
		
		fr.close();
		br.close();
		fw.close();
		pw.close();
		
	}
	
	public static void main(String[] args) throws Exception
	{
		LabelFlatToHierarchy lfth=new LabelFlatToHierarchy();
		String source_file="dict/label_nart_name.txt";
		String dest_file="output/label_nart_hier.txt";
		lfth.flatToHier(source_file, dest_file);
		
	}
}
