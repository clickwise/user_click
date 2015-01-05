package cn.clickwise.clickad.keyword;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.regex.Pattern;

import cn.clickwise.lib.string.SSO;

/**
 * 根据pos提取关键词
 * @author lq
 */
public class KeyExtractPos {

	public String keywordPos(String text,String posstring)
	{
		String k_s = "";
		String[] seg_arr = text.split("\\s+");
		Vector new_word_arr = new Vector();
		String[] history_word_arr = new String[7];
		for (int i = 0; i < history_word_arr.length; i++) {
			history_word_arr[i] = "";
		}

		String key_word = "";
		String subkey1 = "", subkey2 = "", subkey4 = "", subkey5 = "", subkey6 = "", subkey7 = "", subkey8 = "";

		for (int i = 0; i < seg_arr.length; i++) {
		
				key_word = seg_arr[i];
				key_word = key_word.trim();
				if ((key_word.length() > 1)&&(isInPos(key_word,posstring))) {
					new_word_arr.add(key_word);
				}
		}

		String temp_CC = "";
		for (int i = 0; i < new_word_arr.size(); i++) {
			temp_CC = new_word_arr.get(i) + "";
			if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_]*", temp_CC))) {
				k_s = k_s + temp_CC + " ";
			}
		}

		return k_s;
	
	}
	
	public boolean isInPos(String tagword,String posstring)
	{
		String tag=SSO.afterStr(tagword, "#");
		if(SSO.tioe(tag))
		{
			return false;
		}
		
		if(posstring.indexOf(tag)>-1)
		{
			return true;
		}
		
		return false;
	}
	
	public static void main(String[] args) throws Exception
	{

		if(args.length!=4)
		{
			System.err.println("Usage:<field_num> <key_field_index> <separator> <posstring>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    key_field_index: 要进行关键词提取的字段编号，从0开始，即0表示第一个字段");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格,tab 表示 \t");
			System.err.println("    posstring:要提取的pos tag 中间用'_'隔开,例如NN_NR");
			System.exit(1);
		}
		
		//输入的字段个数用
		int fieldNum=0;
		
		//待分词的字段编号
		int keyFieldIndex=0;
		
		//字段间的分隔符:001 表示 \001
		//             :blank 表示\\s+ 即连续空格
		String separator="";
		String outputSeparator="";
		
		fieldNum=Integer.parseInt(args[0]);
		keyFieldIndex=Integer.parseInt(args[1]);
		
		
		if(args[2].equals("001"))
		{
			separator="\001";
			outputSeparator="\001";
		}
		else if(args[2].equals("blank"))
		{
			separator="\\s+";
			outputSeparator="\t";
		}
		else if(args[2].equals("tab"))
		{
			separator="\t";
			outputSeparator="\t";
		}
		else
		{
			separator=args[2].trim();
			outputSeparator=separator.trim();
		}	
		
		String posstring=args[3];
		
		KeyExtractPos kep = new KeyExtractPos();

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		//String line = "";
		//while ((line = br.readLine()) != null) {
		//	pw.println(ke.keyword_extract(line));
		//}
		String line="";
		String[] fields=null;
		while((line=br.readLine())!=null)
		{
			fields=line.split(separator);
			if(fields.length!=fieldNum)
			{
				continue;
			}
			for(int j=0;j<keyFieldIndex;j++)
			{
				pw.print(fields[j]+outputSeparator);
			}
			if(keyFieldIndex<(fieldNum-1))
			{
	
		    	  pw.print(kep.keywordPos(fields[keyFieldIndex],posstring).trim()+outputSeparator);
	
			}
			else
			{
				  pw.print(kep.keywordPos(fields[keyFieldIndex],posstring).trim());
				
		
			}
			
			for(int j=keyFieldIndex+1;j<fieldNum-1;j++)
			{
				pw.println(fields[j]+outputSeparator);
			}
			
			if(keyFieldIndex<(fieldNum-1))
			{
				//pw.print(ke.keyword_extract_noun(fields[fieldNum-1]));
				pw.print(fields[fieldNum-1]);
			}	
			pw.println();
		}
		
		
		isr.close();
		osw.close();
		br.close();
		pw.close();
	}
	
}
