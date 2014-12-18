package cn.clickwise.liqi.mark;

import java.io.Serializable;

/**
 * 参数的封装
 * @author lq
 *
 */
public class LEARNPARM implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * BeamDecoder 的类型
	 * 
	 */
	public String bd_type;
	
	/**
	 * label 的数目
	 */
	public int label_num;
	
	/**
	 * beam search 的best 数目
	 */
	public int top_num;
	
	public String docid;
	
	public LEARNPARM()
	{
		
	}
	public LEARNPARM(String bd_type,int label_num)
	{
		this.bd_type=bd_type;
		this.label_num=label_num;
	}
	
	
	
}
