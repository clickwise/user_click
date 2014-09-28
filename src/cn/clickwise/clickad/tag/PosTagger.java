package cn.clickwise.clickad.tag;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {

	private MaxentTagger tagger;

	public PosTagger(String model) {

		try {
         tagger = new MaxentTagger(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String tag(String text)
	{	
		return tagger.tagString(text);
	}
	
	public static void main(String[] args) throws Exception
	{
		
		
		/*
		String text="北京 时间 8 月 27 日 晚 ， 2014 赛季 亚 冠 联赛 1 / 4 决赛 第二 回合 展开 争夺 ， 广州 恒 大 [ 微博 ] 主场 2-1 战胜 西悉尼 流浪者 ， 但 因 客场 进球 少 被 淘汰 。 著名 足球 评论员 黄健翔 [ 微博 ] 在 新浪 体育 对 本场 比赛 进行 了解 说 ， 黄健翔 认为 对方 布里奇 制造 的 点球 不 该 判 ， 恒 大 运气 不 好 裁判 手 太 松 。";
	    System.out.println(posTagger.tag(text));
		BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
		String line=null;
		System.out.println("input one sentence:");
		
		try {
			while ((line=reader.readLine())!=null) {
				System.out.println("sentence:"+line);
				System.out.println("tag sentence:"+posTagger.tag(line));
				System.out.println("input one sentence:");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
			
		if(args.length!=3)
		{
			System.err.println("Usage:<field_num> <tag_field_index> <separator>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    tag_field_index: 要进行词性标注的字段编号，从0开始，即0表示第一个字段");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.exit(1);
		}
		
		PosTagger posTagger=new PosTagger("chinese-nodistsim.tagger");
		
		//输入的字段个数用
		int fieldNum=0;
		
		//待分词的字段编号
		int tagFieldIndex=0;
		
		//字段间的分隔符:001 表示 \001
		//             :blank 表示\\s+ 即连续空格
		String separator="";
		String outputSeparator="";
		
		fieldNum=Integer.parseInt(args[0]);
		tagFieldIndex=Integer.parseInt(args[1]);
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
		
		InputStreamReader isr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(isr);
		
		OutputStreamWriter osw=new OutputStreamWriter(System.out);
		PrintWriter pw=new PrintWriter(osw);
		
		//String line="";
		//while((line=br.readLine())!=null)
		//{
		//	pw.println(posTagger.tag(line));
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
			for(int j=0;j<tagFieldIndex;j++)
			{
				pw.print(fields[j]+outputSeparator);
			}
			if(tagFieldIndex<(fieldNum-1))
			{
		    	pw.print(posTagger.tag(fields[tagFieldIndex]).trim()+outputSeparator);
			}
			else
			{
				pw.print(posTagger.tag(fields[tagFieldIndex]).trim());
			}
			
			for(int j=tagFieldIndex+1;j<fieldNum-1;j++)
			{
				pw.println(fields[j]+outputSeparator);
			}
			
			if(tagFieldIndex<(fieldNum-1))
			{
				//pw.print(posTagger.tag(fields[fieldNum-1]));
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
