package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;


public class WordSel {

	public void sel_words(String input_dir,String output_file) throws Exception
	{
		File bkw_dir=new File(input_dir);
		File[] bkw_files=bkw_dir.listFiles();
		Hashtable<String,Integer> word_hash=new Hashtable<String,Integer>();

		
		for(int i=0;i<bkw_files.length;i++)
		{
			FileReader fr=new FileReader(bkw_files[i]);
			BufferedReader br=new BufferedReader(fr);
			String line="";
			String[] seg_arr=null;
			String word="";
			int ips=0;
			int old_ips=0;
			String w="";
			while((line=br.readLine())!=null)
			{
				line=line.trim();
				if((line==null)||(line.equals("")))
				{
					continue;
				}
				seg_arr=line.split("\\s+");
				if((seg_arr.length)<2)
				{
					continue;
				}
				word="";
				ips=Integer.parseInt(seg_arr[seg_arr.length-1]);
				for(int k=0;k<(seg_arr.length-1);k++)
				{
				   w=seg_arr[k].trim();
				   if((w==null)||(w.equals("")))
				   {
					  continue;
				   }				   
				 // word=word+w+" ";
				    word=clean_one_word(w);
					word=word.trim();
					if(isNumbers(word))
					{
						continue;
					}
					if((word==null)||(word.equals(""))||(ips<1)||(word.length()>7))
					{
						continue;
					}
					if(!(word_hash.containsKey(word)))
					{
						word_hash.put(word, ips);
					}
					else
					{
						old_ips=word_hash.get(word);
						word_hash.remove(word);
						old_ips=old_ips+ips;
						word_hash.put(word, old_ips);
					} 
				}
			
			}
			fr.close();
			br.close();			
		}
		
		
		FileWriter fw=new FileWriter(new File(output_file));
        PrintWriter pw=new PrintWriter(fw);
        
        Enumeration word_enum=word_hash.keys();
        String temp_word="";
        int temp_ips=0;
        while(word_enum.hasMoreElements())
        {
        	temp_word=word_enum.nextElement()+"";
        	temp_ips=word_hash.get(temp_word);
        	pw.println(temp_word+"\001"+temp_ips);        	
        }
		
		
	}
	
	public String clean_one_word(String word) {
		String new_word = word;
		// new_word=new_word.replaceAll("``", "");
		// new_word=new_word.replaceAll("''", "");
		new_word = new_word.replaceAll("&nbsp;", "");
		new_word = new_word.replaceAll("&nbsp", "");
		new_word = new_word.replaceAll("&ldquo;", "");
		new_word = new_word.replaceAll("&ldquo", "");
		new_word = new_word.replaceAll("&rdquo;", "");
		new_word = new_word.replaceAll("&rdquo", "");
		// new_word=new_word.replaceAll(";", "");
		new_word = new_word.replaceAll("&", "");
		new_word = new_word.replaceAll("VS", "");
		new_word = new_word.replaceAll("-RRB-", "");
		new_word = new_word.replaceAll("-LRB-", "");
		new_word = new_word.replaceAll("_", "");
		// new_word=new_word.replaceAll("[\\.]*", "");
		new_word = new_word.replaceAll("\\\\\\#", "");
		/*
		 * if (Pattern.matches("[a-zA-Z\\,\\.\\?0-9\\!\\-\\s]*", new_word))
		 * { return ""; } if (!(Pattern.matches("[\\u4e00-\\u9fa5]+",
		 * new_word))) { return ""; }
		 */
		new_word = new_word.replaceAll("★", "");
		new_word = new_word.replaceAll("__", "");
		new_word = new_word.replaceAll("ˇ", "");
		new_word = new_word.replaceAll("®", "");
		new_word = new_word.replaceAll("♣", "");
		new_word = new_word.replaceAll("\\丨", "");
		new_word = new_word.trim();

		return new_word;
	}
	public boolean isNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	public static void main(String[] args) throws Exception
	{
		String input_dir="bkw_sort";
		String output_file="bkw_ten_day.txt";
		WordSel ws=new WordSel();
		ws.sel_words(input_dir, output_file);
		
		
	}
	
	
}
