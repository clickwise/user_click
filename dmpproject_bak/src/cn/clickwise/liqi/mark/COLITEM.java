package cn.clickwise.liqi.mark;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * collect item
 * @author lq
 *
 */
public class COLITEM implements Serializable{

	public String docid;
	public ArrayList<WORD> words;
	
	public COLITEM(String docid,ArrayList<WORD> words)
	{
		this.docid=docid;
		this.words=words;
	}
}
