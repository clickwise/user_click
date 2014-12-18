package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;


public class SelectTopSample {

	public static void main(String[] args) throws Exception
	{
		FileReader fr=new FileReader(new File("input/mmtrainSample.txt"));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File("output/top_mmtrainSample.txt"));
		PrintWriter pw=new PrintWriter(fw);
		
		String line="";
		Hashtable<String,Integer> label_count_hash=new Hashtable<String,Integer>();
		String[] seg_arr=null;
		String first_cate="";
		String second_cate="";
		String cate_str="";
		int old_count=0;
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("\\s+");
			if(seg_arr.length<3)
			{
				continue;
			}
			first_cate=seg_arr[1].trim();
			second_cate=seg_arr[2].trim();
			cate_str=first_cate+"_"+second_cate;
			if(!label_count_hash.containsKey(cate_str))
			{
				pw.println(line);
				label_count_hash.put(cate_str, 1);
			}
			else
			{
				old_count=label_count_hash.get(cate_str);
				old_count++;
				label_count_hash.remove(cate_str);
				label_count_hash.put(cate_str, old_count);
				if(old_count<100)
				{
					pw.println(line);
				}
			}		
		}
		
		fr.close();
		br.close();
		fw.close();
		pw.close();
		
		
		
		
	}
	
	
}
