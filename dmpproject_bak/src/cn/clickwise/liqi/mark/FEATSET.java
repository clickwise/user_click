package cn.clickwise.liqi.mark;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 候选feat 集合
 * 
 * @author lq
 * 
 */
public class FEATSET implements Serializable {

	public ArrayList<WORD> words = null;

	/**
	 * words对应的权重
	 */
	public ArrayList<WEI> weights = null;

	public LABEL label;

	/**
	 * 此 feat set 的得分
	 */
	public double score;

	public FEATSET() {
		words = new ArrayList<WORD>();
		weights = new ArrayList<WEI>();
	}

	public FEATSET(WORD word, LABEL label, WEI weight) {
		words = new ArrayList();
		words.add(word);
		weights = new ArrayList<WEI>();
		weights.add(weight);
		this.label = label;
	}

	public void addWord(WORD word, WEI weight) {
		words.add(word);
		weights.add(weight);
		
	}

	public void calScore() {
		WORD w;
		WEI we;
		double sum = 0;
		for (int i = 0; i < words.size(); i++) {
			w = words.get(i);
			we = weights.get(i);
			sum += w.count * we.val;
		}
		score = sum;
	}

	public String toString() {
		String str = "label=" + this.label.index + " word=[";
		String word_str = "";
		for (int j = 0; j < this.words.size(); j++) {
			word_str += ((this.words.get(j)).index + ":"
					+ (this.words.get(j)).count + " ");
		}
		word_str = word_str.trim();
        
		str += word_str;
        str+="]";
		
		return str;
	}
	
	/**
	 * 复制FEATSET 对象,值传递
	 * @param src
	 * @return
	 */
	public static FEATSET copy(FEATSET src)
	{
		FEATSET nfset=new FEATSET();
		nfset.label=new LABEL(src.label.index,src.label.label_num);
		nfset.words=new ArrayList<WORD>();
		WORD w=null;
		for(int i=0;i<src.words.size();i++)
		{
			w=new WORD(src.words.get(i).index,src.words.get(i).count);
			nfset.words.add(w);
		}
		
		nfset.weights=new ArrayList<WEI>();
		WEI we=null;
		for(int i=0;i<src.weights.size();i++)
		{
			we=new WEI(src.weights.get(i).val);
			nfset.weights.add(we);
		}
		
		return nfset;
	}
	
	

}
