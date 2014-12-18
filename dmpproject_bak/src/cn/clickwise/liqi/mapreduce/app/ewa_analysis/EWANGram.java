package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.regex.Pattern;


public class EWANGram {

	
	public String line_process(String line)
	{
		line=line.trim();
		if((line==null)||(line.equals("")))
		{
			return "";
		}
		String pline="";
		String cate="";
		String words="";
		
		String[] seg_arr=null;
		seg_arr=line.split("\001");
		if((seg_arr.length)!=2)
		{
			return "";
		}
		
		cate=seg_arr[0].trim();
		words=seg_arr[1].trim();
		if((words==null)||(words.equals("")))
		{
			return "";
		}
		
		String[] word_arr=null;
		
		word_arr=words.split("\\s+");
		if((word_arr.length)<1)
		{
			return "";
		}
		
		Vector all_w=new Vector();
		String one_word="";
		Vector temp_vec=null;
		for(int i=0;i<word_arr.length;i++)
		{
			one_word=word_arr[i].trim();
			if((one_word==null)||(one_word.equals("")))
			{
				return "";
			}
			all_w.add(one_word);
			temp_vec=getNWords(one_word);
			for(int k=0;k<temp_vec.size();k++)
			{
				all_w.add(temp_vec.get(k));
			}		
		}
		
		pline=pline+cate+"\001";
		for(int i=0;i<all_w.size();i++)
		{
			one_word=all_w.get(i)+"";
			one_word=one_word.trim();
			
			if((one_word==null)||(one_word.equals("")))
			{
				return "";
			}
			if(isNumbers(one_word))
			{
				continue;
			}
			pline=pline+one_word+" ";
		}
		
		pline=pline.trim();		
		return pline;
		
	}
	
	public Vector getNWords(String word)
	{
		String[] nws=null;
		String one_gram="";
		Vector nv=new Vector();
		for(int len=2;len<(word.length());len++)
		{
			for(int j=0;j<(word.length()-len+1);j++)
			{
				one_gram=word.substring(j, j+len);
				if(!(isEnglish(one_gram)))
				{
				  nv.add(one_gram);
				}
			}
		}
		
		//for(int i=0;i<nv.size();i++)
		//{
			//System.out.println("nv:"+nv.get(i));
		//}
		
		return nv;
	}
	
	public boolean isNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	
	public boolean isEnglish(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+a-zA-Z]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		EWANGram ewang=new EWANGram();
		//String word="";
		//ewang.getNWords(word);
		//String old_line="女装男装|其他女装|毛呢外套\0012013秋冬装新款 ShuLier 可拆卸 貉子毛领 毛呢大衣";
		//String new_line="";
		//new_line=ewang.line_process(old_line);
		//System.out.println("new_line:"+new_line);
	
		InputStreamReader isr = new InputStreamReader(System.in);
		//FileReader fr=new FileReader(new File("input/test3.txt"));
		BufferedReader br = new BufferedReader(isr);
		//BufferedReader br = new BufferedReader(fr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			new_line = ewang.line_process(line);

			if ((new_line == null) || (new_line.equals(""))) {
				continue;
			}
			pw.println(new_line);

		}
		

		br.close();
		isr.close();
		osw.close();
		pw.close();
		
	}
	
	
	
}
