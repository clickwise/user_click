package cn.clickwise.liqi.classify.svm.singlehier.source;
/**
 * this defines the y-part (the label) of a training example,
     e.g. the parse tree of the corresponding sentence.
 * @author lq
 *
 */
public class LABEL {

	public int class_index;
	public int num_classes;
	public double[] scores;
	
}
