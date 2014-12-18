package cn.clickwise.liqi.mark;

/**
 * 找出权重数组里相关某单词的项，形成新数组返回
 * @author lq
 *
 */
public class WeightsCut {

	/**
	 * 选择出某单词不同标记的权重
	 * @param sub_weights
	 * @param index
	 * @param label_set
	 * @return
	 */
	public  WEI[] weightToWei(FEAT[] sub_weights,int index,LABEL[] label_set)
	{
		WEI[] word_weights=new WEI[label_set.length];
		//System.out.println("label_set.length:"+label_set.length);
		//System.out.println("init_weights.length:"+sub_weights.length);
		for(int i=0;i<label_set.length;i++)
		{
			//System.out.println("wtw:"+i+" "+label_set[i].index);
			//rw[i]=new WEI(init_weights[index+(label_set[i].label_num)*(label_set[i].index-1)].weight);
			word_weights[i]=new WEI(sub_weights[FeatureIndex.featIndexNoWordMap(index, label_set[i])].weight);
		}
		return word_weights;
	}
	
	public  FEAT[] all2part(double[] init_weights,WORD[] words,LABEL[] label_set)
	{
		
		//System.out.println("init_weights len"+init_weights.length+" words.length:"+words.length+" label set len:"+label_set.length);
		FEAT[] small_weights=new FEAT[(words.length)*(label_set.length)];
		
		WORD w=null;
		LABEL l=null;
		
		for(int i=0;i<words.length;i++)
		{
			w=words[i];
			//System.out.println("w"+i+" "+w.index);
		
			for(int j=0;j<label_set.length;j++)
			{	
				l=label_set[j];
				/*
				System.out.println("i="+i+" j="+j+" l.index="+l.index);
				System.out.println("label_set[0].label_num:"+label_set[0].label_num+" w.index:"+w.index);
				System.out.println("i+j*(label_set[0].label_num):"+(i+j*(label_set[0].label_num)));
				System.out.println("(w.index-1)+label_set[0].label_num*(l.index-1):"+(w.index-1)+label_set[0].label_num*(l.index-1));
				System.out.println("(w.index-1)+label_set[0].label_num*(l.index-1):"+(w.index-1)+label_set[0].label_num*(l.index-1));
		        */
				//small_weights[i+j*(label_set[0].label_num)]=new FEAT((w.index-1)+label_set[0].label_num*(l.index-1),init_weights[(w.index-1)+label_set[0].label_num*(l.index-1)]);		
				small_weights[j+i*(label_set[0].label_num)]=new FEAT(FeatureIndex.featIndexMap(w, l),init_weights[FeatureIndex.featIndexMap(w, l)]);
			}
		}
		
		return small_weights;
	}
	
	
}
