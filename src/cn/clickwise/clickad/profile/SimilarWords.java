package cn.clickwise.clickad.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.ansj.vec.domain.WordEntry;

import cn.clickwise.clickad.profile.com.ansj.vec.Word2VEC;
import cn.clickwise.lib.string.SSO;

public class SimilarWords {

	private Word2VEC vec;

	public SimilarWords() {
		init();
	}

	public SimilarWords(String model) {
		init(model);
	}

	public void init() {

	}

	public void init(String model) {
		vec = new Word2VEC();
		try {
			vec.loadGoogleModel(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Set<WordEntry> similarWords(String word)
	{
		if(SSO.tioe(word))
		{
			return null;
		}
		Set<WordEntry> sws=vec.distance(word);
		
		return sws;
	}
	
	public String setWords(Set<WordEntry> sws)
	{
		String str="";
		WordEntry we=null;
		Iterator<WordEntry> it=sws.iterator();
		ArrayList<String> list=new ArrayList<String>();
		while(it.hasNext())
		{
			we=it.next();
			list.add(we.name);
		}
		
		
		return "["+SSO.implode(list, " ")+"]";
	}
	
	/**
	 * 读取输入流，每一行计算出相关词，结果写到输出
	 */
	public void similarInOut()
	{
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			PrintWriter pw=new PrintWriter(new OutputStreamWriter(System.out));
			String line="";
			Set<WordEntry> sws=null;
			
			while((line=br.readLine())!=null)
			{
			    sws=similarWords(line);
			    if(sws==null)
			    {
			    	continue;
			    }
				pw.println(line+"\001"+setWords(sws));
			}
			
			br.close();
			pw.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Word2VEC getVec() {
		return vec;
	}

	public void setVec(Word2VEC vec) {
		this.vec = vec;
	}
	
	public static void main(String[] args)
	{
		if (args.length != 1) {
			System.err.println("Usage:<model>");
			System.exit(1);
		}
		String model = args[0];
		SimilarWords SW=new SimilarWords(model);
		SW.similarInOut();
	}
}
