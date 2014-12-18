package cn.clickwise.liqi.mark;

import java.io.Serializable;

/**
 * 标记的封装
 * @author lq
 *
 */
public class LABEL implements Serializable{
	
   /**
    * label 的编号	，从1开始的整数
    */
   public int index;
   
   /**
    * 所有类别的数目
    */
   public int label_num;
   
   public LABEL(int index,int label_num)
   {
	   this.index=index;
	   this.label_num=label_num;
   }
	
}
