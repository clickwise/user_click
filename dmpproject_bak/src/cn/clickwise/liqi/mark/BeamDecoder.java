package cn.clickwise.liqi.mark;

/**
 * BeamDecoder 的基类
 * @author lq
 *
 */
public abstract class BeamDecoder {

	/**
	 * BeamDecoder 解码
	 * @param words  特征数组
	 * @param init_weights 特征权重数组
	 * @param label_set 候选label集合
	 * @return 解码结果
	 */
	public abstract FEATSET beam_search(WORD[] words,FEAT[] init_weights,LABEL[] label_set,LEARNPARM learn_parms);
	
	
}
