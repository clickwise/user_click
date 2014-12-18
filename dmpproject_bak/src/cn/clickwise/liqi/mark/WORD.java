package cn.clickwise.liqi.mark;

import java.io.Serializable;

/**
 * 单词的封装
 * @author lq
 *
 */
public class WORD implements Serializable{
	
	/**
	 *单词的编号 ,从 1开始的整数
	 */
	public int index;
	
	/**
	 * 单词次数统计
	 */
	//public int count;
	public double count;
	
	/*
	public WORD(int index,int count)
	{
	  this.index=index;
	  this.count=count;
	}
	*/
	public WORD(int index,double count)
	{
	  this.index=index;
	  this.count=count;
	}
}
