package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

/**
 * 输入分词后的文件，输出TFIDF统计结果
 * 
 * @author zkyz
 * 
 */
public class TFIDFStatistic {

	private ArrayList<HashMap<String,Word>> docs=new ArrayList<HashMap<String,Word>>();
	
	private Map<String, Integer> idfs = new HashMap<String, Integer>();

	private class Word {

		private String word;

		private double count;

		public Word(String word) {
			this.word = word;
			this.count = 0;
		}

		public Word(String word, double count) {
			this.word = word;
			this.count = count;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public double getCount() {
			return count;
		}

		public void setCount(double count) {
			this.count = count;
		}

	}

	public HashMap<String,Word> line2words(String line)
	{
		if(SSO.tioe(line))
		{
			return null;
		}
		
		HashMap<String,Word> added=new HashMap<String,Word>();
		String[] tokens=line.split("\\s+");
		String t="";
		for(int i=0;i<tokens.length;i++)
		{
			t=tokens[i];
			if(SSO.tioe(t))
			{
				continue;
			}
			if(!(added.containsKey(t)))
			{
				added.put(t, new Word(t,1));
			}
			else
			{
				(added.get(t)).setCount((added.get(t).getCount())+1);
			}
		}
		
		return added;
	}
	/**
	 * 统计单词的IDF值
	 */
	public void IDFStatistic(String segments) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(segments));
			String line = "";
			HashMap<String,Word> whs=null;
			
			while ((line = br.readLine()) != null) {
              whs=line2words(line);
              if(whs==null)
              {
            	  continue;
              }
              docs.add(whs);
			}
			
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public ArrayList<HashMap<String,Word>>  getDocs() {
		return docs;
	}
	public void setDocs(ArrayList<HashMap<String,Word>>  docs) {
		this.docs = docs;
	}
	public Map<String, Integer> getIdfs() {
		return idfs;
	}
	public void setIdfs(Map<String, Integer> idfs) {
		this.idfs = idfs;
	}

}
