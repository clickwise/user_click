package cn.clickwise.liqi.mark;

import java.io.Serializable;

/**
 * 特征的封装 
 * @author lq
 *
 */
public class FEAT implements Serializable{

	/**
	 * 特征的编号
	 */
	public int index;
	
	/**
	 * 特征的权重
	 */
	public double weight;
	
	public FEAT(int index,double weight)
	{
		this.index=index;
		this.weight=weight;
	}
	
}
