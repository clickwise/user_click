package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;


public class EWACleanWord {

	public void clean_words(String input_file,String output_file) throws Exception
	{
		FileReader fr = new FileReader(new File(input_file));
		BufferedReader br = new BufferedReader(fr);

		FileWriter fw = new FileWriter(output_file);
		PrintWriter pw = new PrintWriter(fw);
		
		String line="";
		String[] seg_arr=null;
		int c=0;
		String temp_word="";
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("\\s+");
			
			if((seg_arr==null)||(seg_arr.length<1))
			{
				continue;
			}
	
			temp_word=clean_one_word(line);
			temp_word=temp_word.trim();
			if((temp_word==null)||(temp_word.length()<1))
			{
				continue;
			}
			
			if(isNumbers(temp_word))
			{
				continue;
			}
			/*
			if(isWeigts(temp_word))
			{
				continue;
			}
			*/
			pw.println(line);
			
		}
		
		fr.close();
		br.close();
		fw.close();
		pw.close();
		
	}
	
	public String clean_one_word(String word)
	{
		String cword="";
		cword = word.replaceAll("[\\pP‘’“”]", "");
		cword = cword.replaceAll("[\\pS‘’“”]", "");	
		return cword;
	}
	
	public boolean isNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	
	public boolean isWeigts(String s) {
		boolean iaw = false;
		String pat = "[0-9\\.\\-\\+]*[gG]";
		if (Pattern.matches(pat, s)) {
			iaw = true;
		}
		
		String pat2 = "[0-9\\.\\-\\+]*KG";
		if (Pattern.matches(pat2, s)) {
			iaw = true;
		}
		String pat3 = "[0-9\\.\\-\\+]*kg";
		if (Pattern.matches(pat3, s)) {
			iaw = true;
		}
		
		return iaw;
	}
	
	public static void main(String[] args) throws Exception {
		EWACleanWord esw = new EWACleanWord();
		String input_file = "input/sw_file_n.txt";
		String output_file = "output/sw_file_c.txt";
		esw.clean_words(input_file, output_file);

	}
}
