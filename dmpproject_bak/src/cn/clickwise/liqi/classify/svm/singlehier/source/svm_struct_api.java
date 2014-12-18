package cn.clickwise.liqi.classify.svm.singlehier.source;

import org.apache.log4j.Logger;

public class svm_struct_api {

	private static Logger logger = Logger.getLogger(svm_struct_api.class);

	public static void init_struct_model(SAMPLE sample, STRUCTMODEL sm,
			STRUCT_LEARN_PARM sparm, LEARN_PARM lparm, KERNEL_PARM kparm) {
		int i, totwords = 0;
		WORD w;
		WORD[] temp_words;

		sparm.num_classes = 1;
		for (i = 0; i < sample.n; i++)
			/* find highest class label */
			if (sparm.num_classes < ((sample.examples[i].y.class_index) + 0.1))
				sparm.num_classes = (int) (sample.examples[i].y.class_index + 0.1);
		for (i = 0; i < sample.n; i++) /* find highest feature number */
		{
			temp_words = sample.examples[i].x.doc.fvec.words;
			for (int j = 0; j < temp_words.length; j++) {
				w = temp_words[j];
				if (totwords < w.wnum) {
					totwords = w.wnum;
				}
			}

		}

		sparm.num_features = totwords;
		if (svm_struct_common.struct_verbosity >= 0) {
			System.out.println("Training set properties: " + sparm.num_features
					+ " features " + sparm.num_classes + " classes\n");
		}
		sm.sizePsi = sparm.num_features * sparm.num_classes;
		if (svm_struct_common.struct_verbosity >= 2) {
			System.out.println("Size of Phi: " + sm.sizePsi + "\n");
		}
	}

	/**
	 * Initializes the optimization problem. Typically, you do not need to
	 * change this function, since you want to start with an empty set of
	 * constraints. However, if for example you have constraints that certain
	 * weights need to be positive, you might put that in here. The constraints
	 * are represented as lhs[i]*w >= rhs[i]. lhs is an array of feature
	 * vectors, rhs is an array of doubles. m is the number of constraints. The
	 * function returns the initial set of constraints.
	 * 
	 * @param sample
	 * @param sm
	 * @param sparm
	 * @return
	 */
	public static CONSTSET init_struct_constraints(SAMPLE sample,
			STRUCTMODEL sm, STRUCT_LEARN_PARM sparm) {

		CONSTSET c = new CONSTSET();
		int sizePsi = sm.sizePsi;
		int i;
		WORD[] words = new WORD[2];

		if (true) { /* normal case: start with empty set of constraints */
			c.lhs = null;
			c.rhs = null;
			c.m = 0;
		}

		return (c);
	}

	/**
	 * Returns a feature vector describing the match between pattern x and label
	 * y. The feature vector is returned as an SVECTOR (i.e. pairs
	 * <featurenumber:featurevalue>), where the last pair has featurenumber 0 as
	 * a terminator. Featurenumbers start with 1 and end with sizePsi. This
	 * feature vector determines the linear evaluation function that is used to
	 * score labels. There will be one weight in sm.w for each feature. Note
	 * that psi has to match find_most_violated_constraint_???(x, y, sm) and
	 * vice versa. In particular, find_most_violated_constraint_???(x, y, sm)
	 * finds that ybar!=y that maximizes psi(x,ybar,sm)*sm.w (where * is the
	 * inner vector product) and the appropriate function of the loss.
	 * 
	 * @param x
	 * @param y
	 * @param sm
	 * @param sparm
	 * @return
	 */
	public static SVECTOR psi(PATTERN x, LABEL y, STRUCTMODEL sm,
			STRUCT_LEARN_PARM sparm) {
		SVECTOR fvec;
		fvec = svm_common.shift_s(x.doc.fvec, (y.class_index - 1)
				* sparm.num_features);
		fvec.kernel_id = y.class_index;
		return fvec;
	}

	public static boolean finalize_iteration(double ceps,
			int cached_constraint, SAMPLE sample, STRUCTMODEL sm,
			CONSTSET cset, double[] alpha, STRUCT_LEARN_PARM sparm) {
		/*
		 * This function is called just before the end of each cutting plane
		 * iteration. ceps is the amount by which the most violated constraint
		 * found in the current iteration was violated. cached_constraint is
		 * true if the added constraint was constructed from the cache. If the
		 * return value is FALSE, then the algorithm is allowed to terminate. If
		 * it is TRUE, the algorithm will keep iterating even if the desired
		 * precision sparm->epsilon is already reached.
		 */
		return false;
	}

	public static LABEL find_most_violated_constraint_slackrescaling(PATTERN x,
			LABEL y, STRUCTMODEL sm, STRUCT_LEARN_PARM sparm) {
		/*
		 * Finds the label ybar for pattern x that that is responsible for the
		 * most violated constraint for the slack rescaling formulation. It has
		 * to take into account the scoring function in sm, especially the
		 * weights sm.w, as well as the loss function. The weights in sm.w
		 * correspond to the features defined by psi() and range from index 1 to
		 * index sm->sizePsi. Most simple is the case of the zero/one loss
		 * function. For the zero/one loss, this function should return the
		 * highest scoring label ybar, if ybar is unequal y; if it is equal to
		 * the correct label y, then the function shall return the second
		 * highest scoring label. If the function cannot find a label, it shall
		 * return an empty label as recognized by the function empty_label(y).
		 */
		LABEL ybar = new LABEL();
		DOC doc;
		int class_index, bestclass = -1, first = 1;
		double score, score_y, score_ybar, bestscore = -1;

		/*
		 * NOTE: This function could be made much more efficient by not always
		 * computing a new PSI vector.
		 */
		doc = (x.doc);
		doc.fvec = psi(x, y, sm, sparm);
		score_y = svm_common.classify_example(sm.svm_model, doc);

		ybar.scores = null;
		ybar.num_classes = sparm.num_classes;
		for (class_index = 1; class_index <= sparm.num_classes; class_index++) {
			ybar.class_index = class_index;
			doc.fvec = psi(x, ybar, sm, sparm);
			score_ybar = svm_common.classify_example(sm.svm_model, doc);

			score = loss(y, ybar, sparm) * (1.0 - score_y + score_ybar);
			if ((bestscore < score) || (first != 0)) {
				bestscore = score;
				bestclass = class_index;
				first = 0;
			}
		}
		if (bestclass == -1)
			logger.debug("ERROR: Only one class\n");
		ybar.class_index = bestclass;
		if (svm_struct_common.struct_verbosity >= 3)
			logger.debug("[" + bestclass + ":" + bestscore + "] ");
		return (ybar);
	}

public static  LABEL find_most_violated_constraint_marginrescaling(PATTERN x, LABEL y,
			STRUCTMODEL sm, STRUCT_LEARN_PARM sparm) {

		/*
		 * Finds the label ybar for pattern x that that is responsible for the
		 * most violated constraint for the margin rescaling formulation. It has
		 * to take into account the scoring function in sm, especially the
		 * weights sm.w, as well as the loss function. The weights in sm.w
		 * correspond to the features defined by psi() and range from index 1 to
		 * index sm->sizePsi. Most simple is the case of the zero/one loss
		 * function. For the zero/one loss, this function should return the
		 * highest scoring label ybar, if ybar is unequal y; if it is equal to
		 * the correct label y, then the function shall return the second
		 * highest scoring label. If the function cannot find a label, it shall
		 * return an empty label as recognized by the function empty_label(y).
		 */
		LABEL ybar = new LABEL();
		DOC doc;
		int class_index, bestclass = -1, first = 1;
		double score, bestscore = -1;

		/*
		 * NOTE: This function could be made much more efficient by not always
		 * computing a new PSI vector.
		 */
		doc = x.doc;
		ybar.scores = null;
		ybar.num_classes = sparm.num_classes;
		for (class_index = 1; class_index <= sparm.num_classes; class_index++) {
			ybar.class_index = class_index;
			doc.fvec = psi(x, ybar, sm, sparm);
			score = svm_common.classify_example(sm.svm_model, doc);

			score += loss(y, ybar, sparm);
			if ((bestscore < score) || (first != 0)) {
				bestscore = score;
				bestclass = class_index;
				first = 0;
			}
		}
		if (bestclass == -1)
			logger.debug("ERROR: Only one class\n");
		ybar.class_index = bestclass;
		if (svm_struct_common.struct_verbosity >= 3) {
			logger.info("[%" + bestclass + ":" + bestscore + "] ");
		}
		return (ybar);
	}

	public static double loss(LABEL y, LABEL ybar, STRUCT_LEARN_PARM sparm) {
		/*
		 * loss for correct label y and predicted label ybar. The loss for
		 * y==ybar has to be zero. sparm->loss_function is set with the -l
		 * option.
		 */
		if (sparm.loss_function == 0) { /* type 0 loss: 0/1 loss */
			if (y.class_index == ybar.class_index) /*
													 * return 0, if y==ybar.
													 * return 100 else
													 */
				return (0);
			else
				return (100);
		}
		if (sparm.loss_function == 1) { /* type 1 loss: squared difference */
			return ((y.class_index - ybar.class_index) * (y.class_index - ybar.class_index));
		} else {
			/*
			 * Put your code for different loss functions here. But then
			 * find_most_violated_constraint_???(x, y, sm) has to return the
			 * highest scoring label with the largest loss.
			 */
			logger.debug("Unkown loss function\n");
			System.exit(1);
		}

		return 10000;
	}
	
	/**
	  Returns true, if y is an empty label. An empty label might be
		     returned by find_most_violated_constraint_???(x, y, sm) if there
		     is no incorrect label that can be found for x, or if it is unable
		     to label x at all 
	 * @param y
	 * @return
	 */
	public static   boolean empty_label(LABEL y)
	{
		  return(y.class_index<0.9);
	}
	

    public static void realloc(CONSTSET cset)
    {
    	DOC[] olhs=cset.lhs;
    	cset.lhs=new DOC[cset.m];
    	for(int i=0;i<(cset.m-1);i++)
    	{
    		cset.lhs[i]=olhs[i];
    	}
    	cset.lhs[cset.m-1]=new DOC();
    	
    }
    
    public static void realsmallloc_lhs(CONSTSET cset)
    {
    	DOC[] olhs=cset.lhs;
    	cset.lhs=new DOC[cset.m];
    	for(int i=0;i<(cset.m);i++)
    	{
    		cset.lhs[i]=olhs[i];
    	} 	
    }
    
    public static void realsmallloc_rhs(CONSTSET cset)
    {
    	double[] orhs=cset.rhs;
    	cset.rhs=new double[cset.m];
    	for(int i=0;i<(cset.m);i++)
    	{
    		cset.rhs[i]=orhs[i];
    	} 	
    }

    public static void realloc_rhs(CONSTSET cset)
    {
    	double[] orhs=cset.rhs;
    	cset.rhs=new double[cset.m];
    	for(int i=0;i<(cset.m-1);i++)
    	{
    		cset.rhs[i]=orhs[i];
    	}
    	cset.rhs[cset.m-1]=0; 	
    }
    
    public static double[] realloc_alpha(double[] alpha,int m)
    {
    	double[] oalpha=alpha;
    	alpha=new double[m];
    	for(int i=0;i<(m-1);i++)
    	{
    		alpha[i]=oalpha[i];
    	}
    	alpha[m-1]=0;
    	
    	return alpha;
    } 
    
    public static int[] realloc_alpha_list(int[] alpha_list,int m)
    {
    	int[] oalpha_list=alpha_list;
    	alpha_list=new int[m];
    	for(int i=0;i<(m-1);i++)
    	{
    		alpha_list[i]=oalpha_list[i];
    	}
    	alpha_list[m-1]=0;
    	
    	return alpha_list;
    } 
    
    public static void   print_struct_learning_stats(SAMPLE sample, STRUCTMODEL sm,
			CONSTSET cset, double[] alpha, 
			STRUCT_LEARN_PARM sparm)
   {
       /* This function is called after training and allows final touches to
       the model sm. But primarly it allows computing and printing any
       kind of statistic (e.g. training error) you might want. */

       /* Replace SV with single weight vector */
       MODEL model=sm.svm_model;
       if(model.kernel_parm.kernel_type == svm_common.LINEAR) {
          if(svm_struct_common.struct_verbosity>=1) {
            logger.info("Compacting linear model..."); 
          }
          
          sm.svm_model=svm_common.compact_linear_model(model);
          sm.w=sm.svm_model.lin_weights; /* short cut to weight vector */

          if(svm_struct_common.struct_verbosity>=1) {
        	  logger.info("done\n"); 
         }
}  
}
    

}
