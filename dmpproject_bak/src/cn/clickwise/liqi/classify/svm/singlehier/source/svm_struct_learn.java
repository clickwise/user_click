package cn.clickwise.liqi.classify.svm.singlehier.source;
import org.apache.log4j.Logger; 

/**
 * Basic algorithm for learning structured outputs (e.g. parses,
 * sequences, multi-label classification) with a Support Vector
 * Machine.
 * @author lq
 *
 */

public class svm_struct_learn {

	public static final short SLACK_RESCALING  =  1;
	public static final short MARGIN_RESCALING  = 2;
    public static final short NSLACK_ALG =  0;
	public static final short NSLACK_SHRINK_ALG =   1;
	public static final short ONESLACK_PRIMAL_ALG =  2;
	public static final short ONESLACK_DUAL_ALG =   3;
	public static final short ONESLACK_DUAL_CACHE_ALG=  4;
	
	
	
   private static Logger logger = Logger.getLogger(svm_struct_learn.class);   

	public void svm_learn_struct(SAMPLE sample,STRUCT_LEARN_PARM sparm,LEARN_PARM lparm,KERNEL_PARM kparm,STRUCTMODEL sm,int alg_type)
	{
		int i,j;
		int numIt=0;
		
		int argmax_count=0;
		int  newconstraints=0, totconstraints=0, activenum=0; 
		int opti_round;
		int[] opti;
		int fullround, use_shrinking;
		int old_totconstraints=0;
		double  epsilon,svmCnorm;
		int    tolerance,new_precision=1,dont_stop=0;
		double      lossval,factor,dist;
		double      margin=0;
		double      slack;
		double[] slacks; 
		double slacksum, ceps=0;
		double      dualitygap,modellength,alphasum;
		int       sizePsi=0;
		double[]   alpha=null;
		int[] alphahist=null;
		int optcount=0,lastoptcount=0;
		CONSTSET    cset;
		SVECTOR     diff=null;
		SVECTOR     fy, fybar, f;
		SVECTOR[]   fycache=null;
		SVECTOR     slackvec;
		WORD[]      slackv=new WORD[2];
		MODEL       svmModel=null;
		KERNEL_CACHE kcache=null;
		LABEL       ybar;
		DOC         doc;
		
		int n=sample.n;
		EXAMPLE[]  ex=sample.examples;
		double   rt_total=0, rt_opt=0, rt_init=0, rt_psi=0, rt_viol=0;
		double      rt1,rt2;
		rt1=svm_common.get_runtime();
		
		svm_struct_api.init_struct_model(sample, sm, sparm, lparm, kparm);
		sizePsi=sizePsi+1;
		logger.info("the sizePsi2 is "+sizePsi);
		if(alg_type==svm_struct_common.NSLACK_SHRINK_ALG)
		{
			use_shrinking=1;
		}
		else
		{
			use_shrinking=0;
		}
		
		opti=new int[n];
		for(i=0;i<n;i++)
		{
			opti[i]=0;
		}
		
		 opti_round=0;
		 svmCnorm=sparm.C/n;
		 
		 if(sparm.slack_norm==1)
		 {
			 lparm.svm_c=svmCnorm;
			 lparm.sharedslack=1;
		 }
		 else if(sparm.slack_norm == 2) {
			    lparm.svm_c=999999999999999.0; /* upper bound C must never be reached */
			    lparm.sharedslack=0;
			    if(kparm.kernel_type != svm_common.LINEAR) {
			      logger.error("ERROR: Kernels are not implemented for L2 slack norm!");      
			      System.exit(0); 
			    }
			  }
			  else {
			    logger.error("ERROR: Slack norm must be L1 or L2!"); 
			  }
		 
		 epsilon=100.0;       
		 tolerance=Math.min(n/3,Math.max(n/100,5));
		 lparm.biased_hyperplane=0;
		
		 cset=svm_struct_api.init_struct_constraints(sample, sm, sparm);
		
		 if(cset.m > 0) {
			    alpha=new double[cset.m];
			    alphahist=new int[cset.m];
			    for(i=0; i<cset.m; i++) {
			      alpha[i]=0;
			      alphahist[i]=-1; /* -1 makes sure these constraints are never removed */
			    }
		 }
		 
		 /* set initial model and slack variables*/
		  svmModel=new MODEL();
		  lparm.epsilon_crit=epsilon;
		  if(kparm.kernel_type != svm_common.LINEAR)
		  {
			    kcache=svm_learn.kernel_cache_init(Math.max(cset.m,1),lparm.kernel_cache_size); 
		  }
		  svm_learn sl=new svm_learn();
		  sl.svm_learn_optimization(cset.lhs,cset.rhs,cset.m,sizePsi+n,
					 lparm,kparm,kcache,svmModel,alpha); 
	      svm_common.add_weight_vector_to_linear_model(svmModel);
		  sm.svm_model=svmModel;
		  sm.w=svmModel.lin_weights;
		  
		  if(svm_common.USE_FYCACHE!=0)
		  {
			  fycache=new SVECTOR[n];
			  for(i=0;i<n;i++){
				  fy=svm_struct_api.psi(ex[i].x,ex[i].y,sm,sparm);//temp point
				  if(kparm.kernel_type==svm_common.LINEAR){
					  diff=svm_common.add_list_ss(fy);
					  fy=diff;
				  }
				  
			  }
			  
		  }
		  
		  rt_init+=Math.max(svm_common.get_runtime()-rt1, 0);
		  rt_total+=Math.max(svm_common.get_runtime()-rt1, 0);
		
		    /*****************/
		   /*** main loop ***/
		  /*****************/
		  
		  do { /* iteratively increase precision */
			  
			  epsilon=Math.max(epsilon*0.49999999999,sparm.epsilon);
			  new_precision=1;
			  
			  if(epsilon == sparm.epsilon)   /* for final precision, find all SV */
			  {
			        tolerance=0; 
			  }
			  
			  lparm.epsilon_crit=epsilon/2;  /* svm precision must be higher than eps */
			  if(svm_struct_common.struct_verbosity>=1)
			  {
			        logger.info("Setting current working precision to "+epsilon);
			  }
			  
			  do{/* iteration until (approx) all SV are found for current
		            precision and tolerance */
				  
			      opti_round++;
			      activenum=n;
			      dont_stop=0;
			      old_totconstraints=totconstraints;
				  
			      do { /* with shrinking turned on, go through examples that keep
				      producing new constraints */
				
				      if(svm_struct_common.struct_verbosity>=1)
				      {
					    logger.info("Iter "+(++numIt)+" ("+activenum+" active):");
				      }
				  
				      ceps=0;
				      if(activenum==n)
				      {
				        fullround=1;
				      }
				      else
				      {
					    fullround=0; 
				      }
				      
				      for(i=0; i<n; i++) { /*** example loop ***/
				    	
				    	rt1=svm_common.get_runtime();
				    	  
				  	  if((use_shrinking==0) || (opti[i] != opti_round)) {
                          /* if the example is not shrunk
                          away, then see if it is necessary to 
			              add a new constraint */
                         rt2=svm_common.get_runtime();
                         argmax_count++;
                        if(sparm.loss_type == SLACK_RESCALING) 
                           ybar=svm_struct_api.find_most_violated_constraint_slackrescaling(ex[i].x,ex[i].y,sm,sparm);
                        else
                          ybar=svm_struct_api.find_most_violated_constraint_marginrescaling(ex[i].x,ex[i].y,sm,sparm);

                        rt_viol+=Math.max(svm_common.get_runtime()-rt2,0);

                       if(svm_struct_api.empty_label(ybar)) {
                         if(opti[i] != opti_round) {
                           activenum--;
                           opti[i]=opti_round; 
                         }
                        if(svm_struct_common.struct_verbosity>=2)
                           logger.info("no-incorrect-found("+i+") ");
                           continue;
                         }

                        /**** get psi(y)-psi(ybar) ****/
                        rt2=svm_common.get_runtime();
                       if(fycache!=null) 
                       {
                          fy=svm_common.copy_svector(fycache[i]);
                       }
                       else
                       {
                          fy=svm_struct_api.psi(ex[i].x,ex[i].y,sm,sparm);
                       }
                       
                       
                      fybar=svm_struct_api.psi(ex[i].x,ybar,sm,sparm);
                      rt_psi+=Math.max(svm_common.get_runtime()-rt2,0);

                     /**** scale feature vector and margin by loss ****/
                     lossval=svm_struct_api.loss(ex[i].y,ybar,sparm);
                     if(sparm.slack_norm == 2)
                        lossval=Math.sqrt(lossval);
                        if(sparm.loss_type == SLACK_RESCALING)
                            factor=lossval;
                        else           /* do not rescale vector for */
                            factor=1.0;      /* margin rescaling loss type */
                    for(f=fy;f!=null;f=f.next)
                    {
                       f.factor*=factor;
                    }
                    
                    for(f=fybar;f!=null;f=f.next)
                      f.factor*=-factor;
                      margin=lossval;

                    /**** create constraint for current ybar ****/
                    svm_common.append_svector_list(fy,fybar);/* append the two vector lists */
                    doc=svm_common.create_example(cset.m,0,i+1,1,fy);

                    /**** compute slack for this example ****/
                    slack=0;
                    for(j=0;j<cset.m;j++) 
                       if(cset.lhs[j].slackid == i+1) {
                         if(sparm.slack_norm == 2) /* works only for linear kernel */
                           slack=Math.max(slack,cset.rhs[j]-(svm_common.classify_example(svmModel,cset.lhs[j])-sm.w[sizePsi+i]/(Math.sqrt(2*svmCnorm))));
                         else
                           slack=Math.max(slack,cset.rhs[j]-svm_common.classify_example(svmModel,cset.lhs[j]));
                       }

                      /**** if `error' add constraint and recompute ****/
                      dist=svm_common.classify_example(svmModel,doc);
                      ceps=Math.max(ceps,margin-dist-slack);
                      if(slack > (margin-dist+0.0001)) {
                        logger.debug("\nWARNING: Slack of most violated constraint is smaller than slack of working\n");
                        logger.debug("         set! There is probably a bug in 'find_most_violated_constraint_*'.\n");
                        logger.debug("Ex "+i+": slack="+slack+", newslack="+(margin-dist)+"\n");
                       /* exit(1); */
                     }
                     if((dist+slack)<(margin-epsilon)) { 
                        if(svm_struct_common.struct_verbosity>=2)
                           {
                        	logger.info("("+i+",eps="+(margin-dist-slack)+") "); 
                           }
                           if(svm_struct_common.struct_verbosity==1)
                           {
                        	   logger.info("."); 
                           }

                           /**** resize constraint matrix and add new constraint ****/
                           cset.m++;
                           svm_struct_api.realloc(cset);
                           
                           if(kparm.kernel_type == svm_common.LINEAR) {
                              diff=svm_common.add_list_ss(fy); /* store difference vector directly */
                            if(sparm.slack_norm == 1) 
                              cset.lhs[cset.m-1]=svm_common.create_example(cset.m-1,0,i+1,1,svm_common.copy_svector(diff));
                            else if(sparm.slack_norm == 2) {
                              /**** add squared slack variable to feature vector ****/
                              slackv[0].wnum=sizePsi+i;
                              slackv[0].weight=1/(Math.sqrt(2*svmCnorm));
                              slackv[1].wnum=0; /*terminator*/
                              slackvec=svm_common.create_svector(slackv,null,1.0);
                              cset.lhs[cset.m-1]=svm_common.create_example(cset.m-1,0,i+1,1,svm_common.add_ss(diff,slackvec));
                             }
                            }
                           else { /* kernel is used */
                             if(sparm.slack_norm == 1) 
                               cset.lhs[cset.m-1]=svm_common.create_example(cset.m-1,0,i+1,1,svm_common.copy_svector(fy));
                              else if(sparm.slack_norm == 2)
                                System.exit(1);
                         }
                         svm_struct_api.realloc_rhs(cset);
                         cset.rhs[cset.m-1]=margin;
                         
                         alpha=svm_struct_api.realloc_alpha(alpha,cset.m);
                         alpha[cset.m-1]=0;
                         alphahist=svm_struct_api.realloc_alpha_list(alphahist,cset.m);
                         alphahist[cset.m-1]=optcount;
                         newconstraints++;
                         totconstraints++;
                        }
                        else {
                         logger.info("+");  
                         if(opti[i] != opti_round) {
                           activenum--;
                           opti[i]=opti_round; 
                         }
                        } 
                      }//if use shrinking
				    	
					  /**** get new QP solution ****/
					  if((newconstraints >= sparm.newconstretrain) 
					     || ((newconstraints > 0) && (i == n-1))
					     || ((new_precision!=0) && (i == n-1))) {
					    if(svm_struct_common.struct_verbosity>=1) {
					      logger.info("*");
					    }
					    rt2=svm_common.get_runtime();
					 
					    svmModel=new MODEL();
					    /* Always get a new kernel cache. It is not possible to use the
					       same cache for two different training runs */
					    
					    if(kparm.kernel_type != svm_common.LINEAR)
					      kcache=sl.kernel_cache_init(Math.max(cset.m,1),lparm.kernel_cache_size);
					    /* Run the QP solver on cset. */
					    sl.svm_learn_optimization(cset.lhs,cset.rhs,cset.m,sizePsi+n,
								   lparm,kparm,kcache,svmModel,alpha);
					    /* Always add weight vector, in case part of the kernel is
					       linear. If not, ignore the weight vector since its
					       content is bogus. */
					    svm_common.add_weight_vector_to_linear_model(svmModel);
					    sm.svm_model=svmModel;
					    sm.w=svmModel.lin_weights; /* short cut to weight vector */
					    optcount++;
					    /* keep track of when each constraint was last
					       active. constraints marked with -1 are not updated */
					    for(j=0;j<cset.m;j++) 
					      if((alphahist[j]>-1) && (alpha[j] != 0))  
						alphahist[j]=optcount;
					    rt_opt+=Math.max(svm_common.get_runtime()-rt2,0);
					    
					    if((new_precision!=0) && (epsilon <= sparm.epsilon))  
					      dont_stop=1; /* make sure we take one final pass */
					    new_precision=0;
					    newconstraints=0;
					  }
				  	  
					  rt_total+=Math.max(svm_common.get_runtime()-rt1,0);				    	  
				     }//exmample loop
				      
				     rt1=svm_common.get_runtime(); 
				      
				 	if(svm_struct_common.struct_verbosity>=1)
				 		  logger.info("(NumConst="+cset.m+", SV="+(svmModel.sv_num-1)+", CEps="+ceps+", QPEps="+svmModel.maxdiff+")\n");
				     
					if(svm_struct_common.struct_verbosity>=2)
						  logger.info("Reducing working set..."); 
				 	
					remove_inactive_constraints(cset,alpha,optcount,alphahist,Math.max(50,optcount-lastoptcount));
				 	
					lastoptcount=optcount;
					if(svm_struct_common.struct_verbosity>=2)
					  logger.info("done. (NumConst="+cset.m+")\n");
					
					rt_total+=Math.max(svm_common.get_runtime()-rt1,0);
				 					    				      
			      }while((use_shrinking!=0) && (activenum > 0)); /* when using shrinking, 
				    repeat until all examples 
				    produced no constraint at
				    least once */								  
			  }while(((totconstraints - old_totconstraints) > tolerance) || (dont_stop!=0));			  
		  }while((epsilon > sparm.epsilon) 
				  || svm_struct_api.finalize_iteration(ceps,0,sample,sm,cset,alpha,sparm));  //main_loop
		
		
		  if(svm_struct_common.struct_verbosity>=1) {
			    /**** compute sum of slacks ****/
			    /**** WARNING: If positivity constraints are used, then the
				  maximum slack id is larger than what is allocated
				  below ****/
			    slacks=new double[n+1];
			    for(i=0; i<=n; i++) { 
			      slacks[i]=0;
			    }
			    
			    if(sparm.slack_norm == 1) {
			      for(j=0;j<cset.m;j++) 
				    slacks[cset.lhs[j].slackid]=Math.max(slacks[cset.lhs[j].slackid],cset.rhs[j]-svm_common.classify_example(svmModel,cset.lhs[j]));
			      }
			    else if(sparm.slack_norm == 2) {
			      for(j=0;j<cset.m;j++) 
				slacks[cset.lhs[j].slackid]=Math.max(slacks[cset.lhs[j].slackid],cset.rhs[j]-(svm_common.classify_example(svmModel,cset.lhs[j])
					   -sm.w[sizePsi+cset.lhs[j].slackid-1]/(Math.sqrt(2*svmCnorm))));
			    }
			    slacksum=0;
			    for(i=1; i<=n; i++)  
			      slacksum+=slacks[i];
			  
			    alphasum=0;
			    for(i=0; i<cset.m; i++)  
			      alphasum+=alpha[i]*cset.rhs[i];
			      modellength=svm_common.model_length_s(svmModel);
			      dualitygap=(0.5*modellength*modellength+svmCnorm*(slacksum+n*ceps))
			               -(alphasum-0.5*modellength*modellength);
			    
			      logger.info("Final epsilon on KKT-Conditions: "+Math.max(svmModel.maxdiff,epsilon)+"\n");
			      logger.info("Upper bound on duality gap: "+dualitygap+"\n");
			      logger.info("Dual objective value: dval="+(alphasum-0.5*modellength*modellength)+"\n");
			      logger.info("Total number of constraints in final working set: "+(int)cset.m+" (of "+(int)totconstraints+")\n");
			      logger.info("Number of iterations:"+numIt+"\n");
			      logger.info("Number of calls to 'find_most_violated_constraint': "+argmax_count+"\n");
			      if(sparm.slack_norm == 1) {
			    	  logger.info("Number of SV: "+(svmModel.sv_num-1)+" \n");
			    	  logger.info("Number of non-zero slack variables: "+svmModel.at_upper_bound+" (out of "+n+")\n");
			    	  logger.info("Norm of weight vector: |w|="+modellength+"\n");
			      }	      			      
			     else if(sparm.slack_norm == 2){ 
			    	 logger.info("Number of SV: "+(svmModel.sv_num-1)+" (including "+svmModel.at_upper_bound+" at upper bound)\n");
			    	 logger.info("Norm of weight vector (including L2-loss): |w|="+modellength+"\n");
			     }
			      
			     logger.info("Norm. sum of slack variables (on working set): sum(xi_i)/n="+slacksum/n+"\n");
			     logger.info("Norm of longest difference vector: ||Psi(x,y)-Psi(x,ybar)||="+sl.length_of_longest_document_vector(cset.lhs,cset.m,kparm)+"\n");
			     logger.info("Runtime in cpu-seconds: "+ rt_total/100.0+" ("+(100.0*rt_opt)/rt_total+" for QP, "+(100.0*rt_viol)/rt_total+" for Argmax, "+(100.0*rt_psi)/rt_total+" for Psi, "+(100.0*rt_init)/rt_total+" for init)\n");
			  }
		  
		  if(svm_struct_common.struct_verbosity>=4)
			  logger.info(svm_struct_common.printW(sm.w,sizePsi,n,lparm.svm_c));

			  if(svmModel!=null) {
			    sm.svm_model=svm_common.copy_model(svmModel);
			    sm.w=sm.svm_model.lin_weights; /* short cut to weight vector */
			  }
		  
	}
	
	
public	void remove_inactive_constraints(CONSTSET cset, double[] alpha, int currentiter, int[] alphahist, int mininactive)
/* removes the constraints from cset (and alpha) for which
alphahist indicates that they have not been active for at
least mininactive iterations */

{  
   int i,m;

   m=0;
   for(i=0;i<cset.m;i++) {
     if((alphahist[i]<0) || ((currentiter-alphahist[i]) < mininactive)) {
      /* keep constraints that are marked as -1 or which have recently been active */
      cset.lhs[m]=cset.lhs[i];      
      cset.lhs[m].docnum=m;
      cset.rhs[m]=cset.rhs[i];
      alpha[m]=alpha[i];
      alphahist[m]=alphahist[i];
      m++;
     }
    else {
    }
   }
   
   if(cset.m != m) {
     cset.m=m;
     svm_struct_api.realsmallloc_lhs(cset);
     svm_struct_api.realsmallloc_rhs(cset);
     /* alpha=realloc(alpha,sizeof(double)*cset->m); */
     /* alphahist=realloc(alphahist,sizeof(long)*cset->m); */
   }
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
