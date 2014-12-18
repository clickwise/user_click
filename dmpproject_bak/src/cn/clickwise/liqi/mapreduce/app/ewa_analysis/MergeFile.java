package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;


public class MergeFile {

	public void merge_file(String first_file,String second_file,String mfile) throws Exception
	{
		FileReader first_fr=new FileReader(new File(first_file));
		BufferedReader first_br=new BufferedReader(first_fr);
		
		FileReader second_fr=new FileReader(new File(second_file));
		BufferedReader second_br=new BufferedReader(second_fr);
		
		FileWriter fw=new FileWriter(new File(mfile));
		PrintWriter pw=new PrintWriter(fw);
		
		Hashtable word_hash=new Hashtable();
		String line="";
		
		while((line=first_br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			if(!(word_hash.containsKey(line)))
			{
				word_hash.put(line, 1);
				pw.println(line);
			}
			
		}
		
		first_br.close();
		first_fr.close();
		
		while((line=second_br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			if(!(word_hash.containsKey(line)))
			{
				word_hash.put(line, 1);
				pw.println(line);
			}
			
		}
		
		second_br.close();
		second_fr.close();
		fw.close();
		pw.close();
		
		
	}
	
	public static void main(String[] args) throws Exception
	{
		String first_file="output/sw_file_c.txt";
		String second_file="output/undict_words.txt";
		String mfile="output/sw_file_mer.txt";
		
		
		MergeFile mf=new MergeFile();
		mf.merge_file(first_file, second_file, mfile);
		
		
	}
	
	
}
