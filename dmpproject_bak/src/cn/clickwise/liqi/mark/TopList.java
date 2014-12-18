package cn.clickwise.liqi.mark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 从array list 选择出得分最高的k个元素
 * @author lq
 *
 */
public class TopList {

	public static  ArrayList<FEATSET> getTopFromList(int num, ArrayList<FEATSET> all_list)
	{
	
        Collections.sort(all_list, new Comparator<FEATSET>() {
              @Override
	               public int compare(FEATSET t1, FEATSET t2) {
	                    if(t1.score < t2.score) {
	                         return 1;
	                    } else if(t1.score > t2.score) {
	                         return -1;
	                    }
	                    return 0;
	               }
	          });
        
        ArrayList<FEATSET> small_list=new ArrayList<FEATSET>();
        for(int i=0;i<num;i++)
        {
        	if(i>(all_list.size()-1))
        	{
        		break;
        	}
        	small_list.add(all_list.get(i));
        }
        
      
		return small_list;
	}
	
	/**
	 * 获得得分最高的元素
	 * @param all_list
	 * @return
	 */
	public FEATSET getBestFromList(ArrayList<FEATSET> all_list)
	{
	
        Collections.sort(all_list, new Comparator<FEATSET>() {
              @Override
	               public int compare(FEATSET t1, FEATSET t2) {
	                    if(t1.score < t2.score) {
	                         return 1;
	                    } else if(t1.score > t2.score) {
	                         return -1;
	                    }
	                    return 0;
	               }
	          });
        
    /*
		for(int i=0;i<all_list.size();i++)
		{
			if(all_list.get(i).score!=0)
			{
			 System.out.println("ns:"+all_list.get(i).toString()+"   score["+i+"]:"+all_list.get(i).score);
			}
		}	
     */    
		return all_list.get(0);
	}
	
	
}
