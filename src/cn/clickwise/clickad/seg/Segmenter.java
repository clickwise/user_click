package cn.clickwise.clickad.seg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * ansj分词
 * @author zkyz
 */
public class Segmenter {

	public void loadAnsjDic(File dict) {
		
		try{			
			
		List<String> dic = FileToArray.fileToArrayList(dict.getAbsolutePath());
		
		for (int i = 0; i < dic.size(); i++) {
			UserDefineLibrary.insertWord(dic.get(i), "", 1000);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}
	
	public String segAnsi(String text) {
		String segs = "";
		List<Term> parse2 = ToAnalysis.parse(text);
		StringBuilder sb = new StringBuilder();
		for (Term term : parse2) {
			sb.append(term.getName());
			sb.append(" ");
		}
		segs = sb.toString();
		segs = segs.trim();
		return segs;
	} 
  
	
	public static void main(String[] args) throws Exception
	{
		//String text="凤凰网 凤凰网是中国领先的综合门户网站，提供含文图音视频的全方位综合新闻资讯、深度访谈、观点评论、财经产品、互动应用、分享社区等服务，同时与凤凰无线、凤凰宽频形成动，为全球主流华人提供互联网、无线通信、电视网三网融合无缝衔接的新媒体优质体验。";
		/*
		Segmenter seg=new Segmenter();
		seg.loadAnsjDic(new File("dict/five_dict_uniq.txt"));
		
		PrintWriter pw=FileWriterUtil.getPW("temp/seg_test/test_seg.txt");
		long start_time=TimeOpera.getCurrentTimeLong();
		
		String[] unsegs=FileToArray.fileToDimArr("temp/seg_test/test.txt");
		for(int i=0;i<unsegs.length;i++)
		{
		  pw.println(seg.segAnsi(unsegs[i]));
		}
        
		long end_time=TimeOpera.getCurrentTimeLong();
		
		System.out.println(unsegs.length+" total doc, use time:"+((double)(end_time-start_time)/(double)1000)+" seconds");
		pw.close();
		*/
		
		if(args.length>4||args.length<2)
		{
			System.err.println("Usage:[dict] <field_num> <seg_field_index> <separator>");
			System.err.println("    dict : 外加词典路径");
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
		
		if(args.length==3)
		{
			fieldNum=Integer.parseInt(args[0]);
			segFieldIndex=Integer.parseInt(args[1]);
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
		}
		else if(args.length==4)
		{
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
		}
		
		
		Segmenter seg=new Segmenter();
		if(args.length==4)
		{
			seg.loadAnsjDic(new File(args[0]));
		}
		
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
		    	pw.print(seg.segAnsi(fields[segFieldIndex]).trim()+outputSeparator);
			}
			else
			{
				pw.print(seg.segAnsi(fields[segFieldIndex]).trim());
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
	

