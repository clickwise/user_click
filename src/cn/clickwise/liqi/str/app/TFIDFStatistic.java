package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 输入分词后的文件，输出TFIDF统计结果
 * 
 * @author zkyz
 * 
 */
public class TFIDFStatistic {

	private ArrayList<ArrayList<Word>> docs=new ArrayList<ArrayList<Word>>();
	
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

	public ArrayList<Word> line2words(String line)
	{
		HashMap<String,Word> added=new HashMap<String,Word>();
		
		
		return null;
	}
	/**
	 * 统计单词的IDF值
	 */
	public void IDFStatistic(String segments) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(segments));
			String word = "";
			while ((word = br.readLine()) != null) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public ArrayList<ArrayList<Word>> getDocs() {
		return docs;
	}
	public void setDocs(ArrayList<ArrayList<Word>> docs) {
		this.docs = docs;
	}
	public Map<String, Integer> getIdfs() {
		return idfs;
	}
	public void setIdfs(Map<String, Integer> idfs) {
		this.idfs = idfs;
	}

}
