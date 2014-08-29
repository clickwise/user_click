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
		
		if(args.length>1)
		{
			System.err.println("Usage:[dict]");
			System.exit(1);
		}
		
		Segmenter seg=new Segmenter();
		if(args.length==1)
		{
			seg.loadAnsjDic(new File(args[0]));
		}
		
		InputStreamReader isr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(isr);
		
		OutputStreamWriter osw=new OutputStreamWriter(System.out);
		PrintWriter pw=new PrintWriter(osw);
		
		String line="";
		while((line=br.readLine())!=null)
		{
			pw.println(seg.segAnsi(line));
		}
		
		isr.close();
		osw.close();
		br.close();
		pw.close();
		
	}
	
}
	

