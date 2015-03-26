package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

public class TFIDFStatisticN {

	private ArrayList<HashMap<String, Word>> docs = new ArrayList<HashMap<String, Word>>();

	private Map<String, Word> idfs = new HashMap<String, Word>();
	
	private int field_num=0;
	
	private int text_index=0;

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
            String[] fields=null;
			String text="";
			
			while ((line = br.readLine()) != null) {
                fields=line.split("\001");
                if(fields.length!=field_num)
                {
                	continue;
                }
                
                text=fields[text_index];
				whs = line2words(text);
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
	
	public int getField_num() {
		return field_num;
	}

	public void setField_num(int field_num) {
		this.field_num = field_num;
	}

	public int getText_index() {
		return text_index;
	}

	public void setText_index(int text_index) {
		this.text_index = text_index;
	}
	
	public static void main(String[] args)
	{
		if(args.length!=4)
		{
			System.err.println("Usage:<field_num> <text_index> <input> <output>");
			System.exit(1);
		}
		
		int field_num=Integer.parseInt(args[0]);
		int text_index=Integer.parseInt(args[1]);
		String input=args[2];
		String output=args[3];
		
		TFIDFStatisticN tfidf=new TFIDFStatisticN();
		tfidf.setField_num(field_num);
		tfidf.setText_index(text_index);
		tfidf.readDocument(input);
		tfidf.IDFStatistic();
		tfidf.TFIDFStatistic();
		tfidf.printTFIDF(output);
		
	}


	
}
