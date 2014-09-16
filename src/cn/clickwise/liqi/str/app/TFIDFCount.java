package cn.clickwise.liqi.str.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import love.cq.util.MapCount;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.sort.SortStrArray;

public class TFIDFCount {

	/***
	 * 
	 * @param tfidfFile
	 */
	public void tfidf_count(File tfidfFile,File wordtfidfFile) {

		try {
			String[] texts = FileToArray.fileToDimArr(tfidfFile);
			String text = "";
			String[] tokens = null;
			String token = "";
			String word = "";
			String tfidf = "";
			HashMap<String, Double> wordMap = new HashMap<String, Double>();
			MapCount<String> wordCount = new MapCount<String>();
			double otfidf = 0;

			for (int i = 0; i < texts.length; i++) {
				text = texts[i];
				if (SSO.tioe(text)) {
					continue;
				}

				tokens = text.split("\\s+");
				for (int j = 0; j < tokens.length; j++) {
					token = tokens[j];
					word = SSO.beforeStr(token, ":");
					tfidf = SSO.afterStr(token, ":");
					if (SSO.tioe(word)) {
						continue;
					}
					if ((SSO.tioe(tfidf))
							|| !(Pattern.matches("[\\.\\dE\\-]*", tfidf))) {
						continue;
					}

					wordCount.add(word);
					if (!(wordMap.containsKey(word))) {
						wordMap.put(word, Double.parseDouble(tfidf));
					} else {
						otfidf = wordMap.get(word);
						otfidf += Double.parseDouble(tfidf);
						wordMap.remove(word);
						wordMap.put(word, otfidf);
					}
				}
			}
			
			int count=0;
			double avg_tfidf=0;
			
			ArrayList<String> wordList=new ArrayList<String>();
			for(Map.Entry<String, Double> e:wordMap.entrySet())
			{
				if(!(wordCount.get().containsKey(e.getKey())))
				{
					continue;
				}
				
				count=wordCount.get().get(e.getKey());
				avg_tfidf=e.getValue()/((double)count);
				wordList.add(e.getKey()+"\001"+avg_tfidf);		
			}
			
			String[] sortWordList=SortStrArray.sort_List(wordList, 1, "dou", 2, "\001");		
			PrintWriter pw=FileWriterUtil.getPWFile(wordtfidfFile);
			for(int i=0;i<sortWordList.length;i++)
			{
				pw.println(sortWordList[i]);
			}
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		TFIDFCount tfidfCount=new TFIDFCount();
		tfidfCount.tfidf_count(new File("D:/projects/user_click_win_workplace/user_click/temp/seg_test/tfidf.txt"), new File("D:/projects/user_click_win_workplace/user_click/temp/seg_test/wordtfidf.txt"));
		
	}
	

}
