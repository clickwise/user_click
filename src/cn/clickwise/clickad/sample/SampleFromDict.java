package cn.clickwise.clickad.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import cn.clickwise.lib.string.FileToArray;
import cn.clickwise.lib.string.SSO;

public class SampleFromDict {

	/**
	 * 词典索引
	 */
	private HashMap<String,Integer> dicts=new HashMap<String,Integer>();
	
	public void loadDict(String dict)
	{
		try{
		  String[] lines=FileToArray.fileToDimArr(dict);
		  String line="";
		  String[] fields=null;
		  String word="";
		  int index=0;
		  
		  for(int i=0;i<lines.length;i++)
		  {
			  line=lines[i];
			  if(SSO.tioe(line))
			  {
				  continue;
			  }
			  line=line.trim();
			  fields=line.split("\\s+");
			  if(fields.length!=2)
			  {
				  continue;
			  }
			  word=fields[0];
			  if(SSO.tioe(fields[1]))
			  {
				  continue;
			  }
			  
			  index=Integer.parseInt(fields[1]);
			  if(!(dicts.containsKey(word)))
			  {
				  dicts.put(word, index);
			  } 
		  }  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getSample(String words)
	{
		String sample=get_word_id(words);
		if(SSO.tioe(sample))
		{
			return "";
		}
		sample=sample.trim();
		
		return sample;
	}
	
	public String get_word_id(String s) {
		String words[] = s.split("\\s+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				// //ids = jedis.get(words[i]);
				ids = dicts.get(words[i]) + "";
				// System.out.println("ids:"+ids);
			} catch (Exception re) {
				re.printStackTrace();
			}
			if (ids == null) {
				continue;
			}
			if (SSO.tioe(ids)) {
				continue;
			}
			if (!(Pattern.matches("[\\d]*", ids))) {
				continue;
			}
			Long id = Long.parseLong(ids);
			if (id != null) {
				Integer cnt = cnts.get(id);
				if (cnt == null)
					cnts.put(id, 1);
				else
					cnts.put(id, cnt + 1);
			}
		}
		List<Long> keys = new ArrayList<Long>(cnts.keySet());
		Collections.sort(keys, new Comparator<Long>() {
			public int compare(Long l1, Long l2) {
				if (l1 > l2)
					return 1;
				else if (l1 < l2)
					return -1;
				return 0;
			}
		});

		for (int i = 0; i < keys.size(); i++) {
			Long l = keys.get(i);
			if (i == 0)
				res += l + ":" + cnts.get(l);
			else
				res += " " + l + ":" + cnts.get(l);
		}
		return res;
	}
	
	public static void main(String[] args) throws Exception
	{
		if(args.length!=4)
		{
			System.err.println("Usage:<dict> <field_num> <seg_field_index> <separator>");
			System.err.println("    dict : 词典路径");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    seg_field_index: 要分词的字段编号，从0开始，即0表示第一个字段");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格,tab标识\t");
			System.exit(1);
		}
		
		//外加词典的路径
		String dict="";
		
		//输入的字段个数用
		int fieldNum=0;
		
		//待分词的字段编号
		int segFieldIndex=0;
		
		//字段间的分隔符:001 表示 \001
		//             :blank 表示\\s+ 即连续空格
		String separator="";
		String outputSeparator="";
		
		dict=args[0];
		fieldNum=Integer.parseInt(args[1]);
		segFieldIndex=Integer.parseInt(args[2]);
		if(args[3].equals("001"))
		{
			separator="\001";
			outputSeparator="\001";
		}
		else if(args[3].equals("blank"))
		{
			separator="\\s+";
			outputSeparator="\t";
		}
		else if(args[3].equals("tab"))
		{
			separator="\t";
			outputSeparator="\t";
		}
		else
		{
			separator=args[3];
			outputSeparator=separator.trim();
		}
		
		SampleFromDict sfd=new SampleFromDict();
		sfd.loadDict(dict);
		
		InputStreamReader isr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(isr);
		
		OutputStreamWriter osw=new OutputStreamWriter(System.out);
		PrintWriter pw=new PrintWriter(osw);
		
		String line="";
		String[] fields=null;
		while((line=br.readLine())!=null)
		{
			fields=line.split(separator);
			if(fields.length!=fieldNum)
			{
				continue;
			}
			for(int j=0;j<segFieldIndex;j++)
			{
				pw.print(fields[j]+outputSeparator);
			}
			if(segFieldIndex<(fieldNum-1))
			{
		    	pw.print(sfd.getSample(fields[segFieldIndex]).trim()+outputSeparator);
			}
			else
			{
				pw.print(sfd.getSample(fields[segFieldIndex]).trim());
			}
			
			for(int j=segFieldIndex+1;j<fieldNum-1;j++)
			{
				pw.println(fields[j]+outputSeparator);
			}
			
			if(segFieldIndex<(fieldNum-1))
			{
				//pw.print(seg.segAnsi(fields[fieldNum-1]));
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
