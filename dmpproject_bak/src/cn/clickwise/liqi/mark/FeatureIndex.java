package cn.clickwise.liqi.mark;

/**
 * 根据word 和  label 返回特征编号
 * @author lq
 *
 */
public class FeatureIndex {

	/**
	 * 根据word 和  label 返回特征编号
	 * @param word
	 * @param label
	 * @return feat index
	 */
	public static int featIndexMap(WORD word,LABEL label)
	{
		int index;
		//index=(word.index-1)+(label.index-1)*label.label_num;
		index=(label.index-1)+(word.index-1)*(label.label_num);
		return index;
	}
	
	/**
	 * 根据word 和  label 返回特征编号
	 * @param word
	 * @param label
	 * @return feat index
	 */
	public static int featIndexNoWordMap(int index,LABEL label)
	{
		int index_map;
		//index=(word.index-1)+(label.index-1)*label.label_num;
		index_map=(label.index-1)+(index-1)*(label.label_num);
		return index_map;
	}
}
