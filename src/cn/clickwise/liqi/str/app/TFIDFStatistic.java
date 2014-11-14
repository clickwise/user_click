package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
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

	private ArrayList<HashMap<String, Word>> docs = new ArrayList<HashMap<String, Word>>();

	private Map<String, Word> idfs = new HashMap<String, Word>();

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
		
		public String toString()
		{
			String str="";	
			str=word+":"+count;
			return str;
		}

	}

	public HashMap<String, Word> line2words(String line) {
		if (SSO.tioe(line)) {
			return null;
		}

		HashMap<String, Word> added = new HashMap<String, Word>();
		String[] tokens = line.split("\\s+");
		String t = "";
		for (int i = 0; i < tokens.length; i++) {
			t = tokens[i];
			if (SSO.tioe(t)) {
				continue;
			}
			if (!(added.containsKey(t))) {
				added.put(t, new Word(t, 1));
			} else {
				(added.get(t)).setCount((added.get(t).getCount()) + 1);
			}
		}

		return added;
	}

	public void readDocument(String segments) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(segments));
			String line = "";
			HashMap<String, Word> whs = null;

			while ((line = br.readLine()) != null) {

				whs = line2words(line);
				if (whs == null) {
					continue;
				}
				docs.add(whs);
			}

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 统计单词的IDF值
	 */
	public void IDFStatistic() {

		HashMap<String, Word> whs = null;

		for (int i = 0; i < docs.size(); i++) {
			whs = docs.get(i);
			for (Map.Entry<String, Word> w : whs.entrySet()) {
				if (!(idfs.containsKey(w.getKey()))) {
					idfs.put(w.getKey(), new Word(w.getKey()));
				} else {
					idfs.get(w.getKey()).setCount(
							idfs.get(w.getKey()).getCount() + 1);
				}
			}
		}

	}

	public void TFIDFStatistic() {
		HashMap<String, Word> whs = null;
		for (int i = 0; i < docs.size(); i++) {
			whs = docs.get(i);
			for (Map.Entry<String, Word> w : whs.entrySet()) {
				w.getValue().setCount(
						(w.getValue().getCount())
								/ (idfs.get(w.getKey()).getCount()));
			}
		}
	}

	public void printTFIDF(String outFile) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outFile));
			HashMap<String, Word> whs = null;
			for (int i = 0; i < docs.size(); i++) {
				whs = docs.get(i);
				for (Map.Entry<String, Word> w : whs.entrySet()) {
                    pw.println(w.getValue().toString()+" ");
				}
				pw.println();
			}
			pw.close();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<HashMap<String, Word>> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<HashMap<String, Word>> docs) {
		this.docs = docs;
	}

	public Map<String, Word> getIdfs() {
		return idfs;
	}

	public void setIdfs(Map<String, Word> idfs) {
		this.idfs = idfs;
	}
	
	public static void main(String[] args)
	{
		if(args.length!=2)
		{
			System.err.println("Usage:<input> <output>");
			System.exit(1);
		}
		
		String input=args[0];
		String output=args[1];
		
		TFIDFStatistic tfidf=new TFIDFStatistic();
		tfidf.readDocument(input);
		tfidf.IDFStatistic();
		tfidf.TFIDFStatistic();
		tfidf.printTFIDF(output);
		
	}

}
