package cn.clickwise.liqi.mark;

import java.util.HashMap;
import java.util.List;

public class WordsToStr {

	public static String wts(HashMap<String,String> dict_map,List<WORD> words)
	{
		String wstr="";
		for(int i=0;i<words.size();i++)
		{
			wstr+=(dict_map.get((words.get(i).index+""))+" ");
		}
		wstr=wstr.trim();
		
		return wstr;
	}
	
	public static String wtsa(HashMap<String,String> dict_map,WORD[] words)
	{
		String wstr="";
		for(int i=0;i<words.length;i++)
		{
			wstr+=(dict_map.get((words[i].index+""))+" ");
		}
		wstr=wstr.trim();
		
		return wstr;
	}
}
