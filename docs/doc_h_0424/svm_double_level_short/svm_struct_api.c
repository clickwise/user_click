/***********************************************************************/
/*                                                                     */
/*   svm_struct_api.c                                                  */
/*                                                                     */
/*   Definition of API for attaching implementing SVM learning of      */
/*   structures (e.g. parsing, multi-label classification, HMM)        */ 
/*                                                                     */
/*   Author: Thorsten Joachims                                         */
/*   Date: 03.07.04                                                    */
/*                                                                     */
/*   Copyright (c) 2004  Thorsten Joachims - All rights reserved       */
/*                                                                     */
/*   This software is available for non-commercial use only. It must   */
/*   not be modified and distributed without prior permission of the   */
/*   author. The author is not responsible for implications from the   */
/*   use of this software.                                             */
/*                                                                     */
/***********************************************************************/

#include <stdio.h>
#include <string.h>
#include "svm_struct/svm_struct_common.h"
#include "svm_struct_api.h"


char multi_label_arr[1000][50];
int mla_index;
//map<string, string> multi_label_map;
//int mlm_index=1;
void        svm_struct_learn_api_init(int argc, char* argv[])
{
	/* Called in learning part before anything else is done to allow
	   any initializations that might be necessary. */
}

void        svm_struct_learn_api_exit()
{
	/* Called in learning part at the very end to allow any clean-up
	   that might be necessary. */
}

void        svm_struct_classify_api_init(int argc, char* argv[])
{
	/* Called in prediction part before anything else is done to allow
	   any initializations that might be necessary. */
}

void        svm_struct_classify_api_exit()
{
	/* Called in prediction part at the very end to allow any clean-up
	   that might be necessary. */
}


//需要更改读取样本的函数
SAMPLE      read_struct_examples(char *file, STRUCT_LEARN_PARM *sparm)
{
	/* Reads training examples and returns them in sample. The number of
	   examples must be written into sample.n */
	read_all_labels();
	SAMPLE   sample;  /* sample */
	EXAMPLE  *examples;
	long     n;       /* number of examples */
	DOC **docs;       /* examples in original SVM-light format */ 
	LABEL *target;
	long totwords,i,first_num_classes=0,second_num_classes=0;

	/* Using the read_documents function from SVM-light */
//	printf("before read multi documents \n");

	read_multi_documents(file,&docs,&target,&totwords,&n);
//	printf("after read multi documents \n");
	examples=(EXAMPLE *)my_malloc(sizeof(EXAMPLE)*n);
	for(i=0;i<n;i++)     /* find highest class label */
	{
		if(first_num_classes < (target[i].first_class)) 
			first_num_classes=target[i].first_class;

		if(second_num_classes < (target[i].second_class))
			second_num_classes=target[i].second_class;

		// if(third_num_classes < (target[i].third_class))
		//   third_num_classes=target[i].third_class;
	}
	for(i=0;i<n;i++)     /* make sure all class labels are positive */
	{
		if((target[i].first_class<1)||(target[i].second_class<1)) {
			printf("\nERROR: The class label  of example number %ld is not greater than '1'!\n",i+1);
			exit(1);
		}

	}

	//first_num_classes=5;
	//second_num_classes=15;
	//third_num_classes=30;
	for(i=0;i<n;i++) {          /* copy docs over into new datastructure */
		examples[i].x.doc=docs[i];
		examples[i].y.first_class=target[i].first_class;
		examples[i].y.second_class=target[i].second_class;
		//  examples[i].y.third_class=target[i].third_class;
		// examples[i].y.scores=NULL;
		examples[i].y.num_first_classes=first_num_classes;
		examples[i].y.num_second_classes=second_num_classes;
		// examples[i].y.num_third_classes=third_num_classes;
	}
	free(target);
	free(docs);
	sample.n=n;
	sample.examples=examples;

	if(struct_verbosity>=0)
		printf(" (%d examples) ",sample.n);
	return(sample);
}

//设置struct mode 单词的个数，一级、二级、三级类别的个数,和所有特征的个数
void        init_struct_model(SAMPLE sample, STRUCTMODEL *sm, 
		STRUCT_LEARN_PARM *sparm, LEARN_PARM *lparm, 
		KERNEL_PARM *kparm)
{
	/* Initialize structmodel sm. The weight vector w does not need to be
	   initialized, but you need to provide the maximum size of the
	   feature space in sizePsi. This is the maximum number of different
	   weights that can be learned. Later, the weight vector w will
	   contain the learned weights for the model. */
	long i,totwords=0;
	WORD *w;

	sparm->first_num_classes=1;
	sparm->second_num_classes=1;
	// sparm->third_num_classes=1;
	for(i=0;i<sample.n;i++)     /* find highest class label */
	{
		if(sparm->first_num_classes < (sample.examples[i].y.first_class)) 
			sparm->first_num_classes=sample.examples[i].y.first_class;

		if(sparm->second_num_classes < (sample.examples[i].y.second_class))
			sparm->second_num_classes=sample.examples[i].y.second_class;

		// if(sparm->third_num_classes < (sample.examples[i].y.third_class))
		//   sparm->third_num_classes=sample.examples[i].y.third_class;

	}
	// sparm->first_num_classes=5;
	// sparm->second_num_classes=15;
	//sparm->third_num_classes=30;
	for(i=0;i<sample.n;i++)     /* find highest feature number */
		for(w=sample.examples[i].x.doc->fvec->words;w->wnum;w++) 
			if(totwords < w->wnum) 
				totwords=w->wnum;
	sparm->num_features=totwords;
	if(struct_verbosity>=0)
		printf("Training set properties: %d features, %d first_classes %d second_classes \n",
				sparm->num_features,sparm->first_num_classes,sparm->second_num_classes);
	sm->sizePsi=0;
	sm->sizePsi+=sparm->num_features*sparm->first_num_classes;
	sm->sizePsi+=sparm->num_features*sparm->second_num_classes;
	// sm->sizePsi+=sparm->num_features*sparm->third_num_classes;
	//  sm->sizePsi+=sparm->first_num_classes*sparm->second_num_classes;
	//  sm->sizePsi+=sparm->second_num_classes*sparm->third_num_classes;
      //  sparm->top_features=sm->sizePsi;
	if(struct_verbosity>=2)
		printf("Size of Phi: %ld\n",sm->sizePsi);
}

CONSTSET    init_struct_constraints(SAMPLE sample, STRUCTMODEL *sm, 
		STRUCT_LEARN_PARM *sparm)
{
	/* Initializes the optimization problem. Typically, you do not need
	   to change this function, since you want to start with an empty
	   set of constraints. However, if for example you have constraints
	   that certain weights need to be positive, you might put that in
	   here. The constraints are represented as lhs[i]*w >= rhs[i]. lhs
	   is an array of feature vectors, rhs is an array of doubles. m is
	   the number of constraints. The function returns the initial
	   set of constraints. */
	CONSTSET c;
	long     sizePsi=sm->sizePsi;
	long     i;
	WORD     words[2];

	if(1) { /* normal case: start with empty set of constraints */
		c.lhs=NULL;
		c.rhs=NULL;
		c.m=0;
	}
	else { /* add constraints so that all learned weights are
		  positive. WARNING: Currently, they are positive only up to
		  precision epsilon set by -e. */
		c.lhs=my_malloc(sizeof(DOC *)*sizePsi);
		c.rhs=my_malloc(sizeof(double)*sizePsi);
		for(i=0; i<sizePsi; i++) {
			words[0].wnum=i+1;
			words[0].weight=1.0;
			words[1].wnum=0;
			/* the following slackid is a hack. we will run into problems,
			   if we have move than 1000000 slack sets (ie examples) */
			c.lhs[i]=create_example(i,0,1000000+i,1,create_svector(words,NULL,1.0));
			c.rhs[i]=0.0;
		}
	}
	return(c);
}

//训练出权重w后，对未标记的样本进行类别预测,训练时不需要
LABEL       classify_struct_example(PATTERN x, STRUCTMODEL *sm, 
		STRUCT_LEARN_PARM *sparm)
{
	/* Finds the label yhat for pattern x that scores the highest
	   according to the linear evaluation function in sm, especially the
	   weights sm.w. The returned label is taken as the prediction of sm
	   for the pattern x. The weights correspond to the features defined
	   by psi() and range from index 1 to index sm->sizePsi. If the
	   function cannot find a label, it shall return an empty label as
	   recognized by the function empty_label(y). */
	LABEL y;
	DOC doc;
	int first_class,best_first_class=-1,first=1,j;
	int second_class,best_second_class=-1;
	//  int third_class,best_third_class=-1;

	double score,bestscore=-1;
	WORD *words;

	doc=*(x.doc);
	//  y.scores=(double *)my_malloc(sizeof(double)*(sparm->num_classes+1));
	y.num_first_classes=sparm->first_num_classes;
	y.num_second_classes=sparm->second_num_classes;
	// y.num_third_classes=sparm->third_num_classes;

	words=doc.fvec->words;
       
        /*
        WORD *ai;
        ai=doc.fvec->words;
        //printf("the words classify struct example:\n");
        while(ai->wnum){
         ai++;
       //  printf("%d:%f ",ai->wnum,ai->weight);
        }
        printf("\n");
          
       // int temp_top=(int)(sparm->top_features);
        */
      
      //long temp_top=sparm->top_features;
      // printf("temp_top is %ld \n",temp_top);
	//for(j=0;(words[j]).wnum != 0;j++) {       /* Check if feature numbers   */
	  // if((words[j]).wnum>temp_top) /* are not larger than in     */
	//		(words[j]).wnum=0;                    /* model. Remove feature if   */
//	}    

	// for(first_class=1;first_class<=sparm->first_num_classes;first_class++) {
	// for(second_class=1;second_class<=sparm->second_num_classes;second_class++)
	// {
	//  for(third_class=1;third_class<=sparm->second_num_classes;second_class++)
	//  {

	char ll_ss[512];
	int temp_c1;
	int temp_c2;
	// int temp_c3;
	int li=0; 
       long temp_top=sparm->top_features;        
	for(li=0;li<mla_index;li++)
	{
		//  y.first_class=first_class;
		//  y.second_class=second_class;
		//  y.third_class=third_class;
		strcpy(ll_ss,multi_label_arr[li]);
		sscanf(ll_ss,"%d_%d",&temp_c1,&temp_c2);
		// printf("temp_c1=%d temp_c2=%d \n",temp_c1,temp_c2);

		y.first_class=temp_c1;
		y.second_class=temp_c2;
		// y.third_class=temp_c3;

		doc.fvec=psi(x,y,sm,sparm);
             
              for(j=0;(doc.fvec->words[j]).wnum != 0;j++) {       /* Check if feature numbers   */
                 if((doc.fvec->words[j]).wnum>(temp_top-1)) /* are not larger than in     */
                   {
                       (doc.fvec->words[j]).wnum=0;                    /* model. Remove feature if   */
                       (doc.fvec->words[j]).weight=0; 
                   }
               }
               /*
               WORD *ai;
               ai=doc.fvec->words;         
               printf("the words classify struct example:\n");
               while(ai->wnum){
               ai++;
               printf("%d:%f ",ai->wnum,ai->weight);
               }
              printf("\n");
              */
		score=classify_example(sm->svm_model,&doc);
		free_svector(doc.fvec);
		if((bestscore<score)  || (first)) {
			bestscore=score;
			best_first_class=temp_c1;
			best_second_class=temp_c2;
			//  best_third_class=temp_c3;
			first=0;
		}

	}
	// }//first_class loop
	// }//second_class loop
	// }//first_class loop

	y.first_class=best_first_class;
	y.second_class=best_second_class;
	y.score=bestscore;
	// y.third_class=best_third_class;

	return(y);
}

LABEL       find_most_violated_constraint_slackrescaling(PATTERN x, LABEL y, 
		STRUCTMODEL *sm, 
		STRUCT_LEARN_PARM *sparm)
{
	/* Finds the label ybar for pattern x that that is responsible for
	   the most violated constraint for the slack rescaling
	   formulation. It has to take into account the scoring function in
	   sm, especially the weights sm.w, as well as the loss
	   function. The weights in sm.w correspond to the features defined
	   by psi() and range from index 1 to index sm->sizePsi. Most simple
	   is the case of the zero/one loss function. For the zero/one loss,
	   this function should return the highest scoring label ybar, if
	   ybar is unequal y; if it is equal to the correct label y, then
	   the function shall return the second highest scoring label. If
	   the function cannot find a label, it shall return an empty label
	   as recognized by the function empty_label(y). */
	LABEL ybar;
	DOC doc;
	int first_class,best_first_class=-1,first=1;
	int second_class,best_second_class=-1;
	int third_class,best_third_class=-1;  

	double score,score_y,score_ybar,bestscore=-1;

	/* NOTE: This function could be made much more efficient by not
	   always computing a new PSI vector. */
	doc=*(x.doc);
	doc.fvec=psi(x,y,sm,sparm);
	score_y=classify_example(sm->svm_model,&doc);
	free_svector(doc.fvec);

	//  ybar.scores=NULL;
	ybar.num_first_classes=sparm->first_num_classes;
	ybar.num_second_classes=sparm->second_num_classes;
	//ybar.num_third_classes=sparm->third_num_classes;

	for(first_class=1;first_class<=sparm->first_num_classes;first_class++) {
		for(second_class=1;second_class<=sparm->second_num_classes;second_class++)
		{
			//   for(third_class=1;third_class<=sparm->second_num_classes;second_class++)
			//   {     
			ybar.first_class=first_class;
			ybar.second_class=second_class;
			//    ybar.third_class=third_class;

			// if(isVaildLable(y,ybar))
			//{
			doc.fvec=psi(x,ybar,sm,sparm);
			score_ybar=classify_example(sm->svm_model,&doc);
			free_svector(doc.fvec);
			score=loss(y,ybar,sparm)*(1.0-score_y+score_ybar);
			if((bestscore<score)  || (first)) {
				bestscore=score;
				best_first_class=first_class;
				best_second_class=second_class;
				//  best_third_class=third_class;
				first=0;
			}
			//} 
		}//first_class loop
		}//second_class loop
		// }//first_class loop

	if((best_first_class == -1)||(best_second_class==-1)) 
		printf("ERROR: Only one class\n");

	ybar.first_class=best_first_class;
	ybar.second_class=best_second_class;
	// ybar.third_class=best_third_class;

	if(struct_verbosity>=3)
		printf(" [%d %d :%.2f] ",best_first_class,best_second_class,bestscore);
	return(ybar);
}

LABEL       find_most_violated_constraint_marginrescaling(PATTERN x, LABEL y, 
		STRUCTMODEL *sm, 
		STRUCT_LEARN_PARM *sparm)
{
	/* Finds the label ybar for pattern x that that is responsible for
	   the most violated constraint for the margin rescaling
	   formulation. It has to take into account the scoring function in
	   sm, especially the weights sm.w, as well as the loss
	   function. The weights in sm.w correspond to the features defined
	   by psi() and range from index 1 to index sm->sizePsi. Most simple
	   is the case of the zero/one loss function. For the zero/one loss,
	   this function should return the highest scoring label ybar, if
	   ybar is unequal y; if it is equal to the correct label y, then
	   the function shall return the second highest scoring label. If
	   the function cannot find a label, it shall return an empty label
	   as recognized by the function empty_label(y). */
	LABEL ybar;
	DOC doc;
	int first_class,best_first_class=-1,first=1;
	int second_class,best_second_class=-1;
	// int third_class,best_third_class=-1;
	double score,bestscore=-1;
	// printf("in find_most_violated_constraint_marginrescaling \n");
	/* NOTE: This function could be made much more efficient by not
	   always computing a new PSI vector. */
	doc=*(x.doc);
	// ybar.scores=NULL;
	ybar.num_first_classes=sparm->first_num_classes;
	ybar.num_second_classes=sparm->second_num_classes;
	// ybar.num_third_classes=sparm->third_num_classes;

	char ll_ss[512];
	int temp_c1;
	int temp_c2;
	//    int temp_c3;
	//double rt_t1=get_runtime();  
	//  for(first_class=1;first_class<=sparm->first_num_classes;first_class++) {
	//    for(second_class=1;second_class<=sparm->second_num_classes;second_class++)
	//    {
	//      for(third_class=1;third_class<=sparm->third_num_classes;third_class++)
	//    {

	int li=0;
	// printf("mla_index is %d \n",mla_index);
	// exit(0);
	for(li=0;li<mla_index;li++)
	{

		// char ll_ss[512];
		// int temp_c1;
		// int temp_c2;
		// int temp_c3;
		strcpy(ll_ss,multi_label_arr[li]);
		sscanf(ll_ss,"%d_%d",&temp_c1,&temp_c2);
		// printf("temp_c1=%d temp_c2=%d temp_c3 =%d \n",temp_c1,temp_c2,temp_c3);

		//  exit(0); 
		ybar.first_class=temp_c1;
		ybar.second_class=temp_c2;
		// ybar.third_class=temp_c3;
		// ybar.first_class=first_class;
		//  ybar.second_class=second_class;
		//  ybar.third_class=third_class;
		//   printf("first_class is %d second_class is %d third_class is %d isValid is %d \n",first_class,second_class,third_class,isVaildLable(y,ybar));      
		// if(isVaildLable(y,ybar))
		// {
		doc.fvec=psi(x,ybar,sm,sparm);   
		//   printf("I am here \n"); 

		score=classify_example(sm->svm_model,&doc);
		//  exit(0);
		free_svector(doc.fvec);
		//   printf("I am here 1 \n");

		score+=loss(y,ybar,sparm);
		if((bestscore<score)  || (first)) {
			bestscore=score;
			/*
			   best_first_class=first_class;
			   best_second_class=second_class;
			   best_third_class=third_class;
			 */
			best_first_class=temp_c1;
			best_second_class=temp_c2;
			// best_third_class=temp_c3;
			first=0;
		}
		//}
	}//the new
	// }//third_class loop
	// }//second_class loop
	// }// first_class loop

	// double rt_t2=get_runtime();
	//printf("the time is %3.15lf \n",rt_t2-rt_t1);
	//  printf("b1=%d b2=%d b3=%d \n",best_first_class,best_second_class,best_third_class);
	// exit(0);
	if((best_first_class == -1)||(best_second_class==-1))
		printf("ERROR: Only one class\n");

	ybar.first_class=best_first_class;
	ybar.second_class=best_second_class;
	// ybar.third_class=best_third_class;

	if(struct_verbosity>=3)
		printf(" [%d %d:%.2f] ",best_first_class,best_second_class,bestscore);

	return(ybar);
}

int         empty_label(LABEL y)
{
	/* Returns true, if y is an empty label. An empty label might be
	   returned by find_most_violated_constraint_???(x, y, sm) if there
	   is no incorrect label that can be found for x, or if it is unable
	   to label x at all */
	return((y.first_class<0.9)|| (y.second_class<0.9 ));
}

//返回样本(x,y)对应的特征
/*特征格式：假设单词是x={(w1,f1),(w2,f2),...(wn,fn)},其中wi 是单词词典索引，fi是对应的频率
 *                    y={c1,c2,c3},c1是第一级类别的索引(从1开始，连续)，c2是第二级类别的索引，c3是第三级类别的索引
 *
 *  那么返回的特征是:
 *  第一级类别特征: {(w1+(c1-1)|V|,f1),(w2+(c1-1)|V|,f2),...(wn+(c1-1)|V|,fn)}
 *  第二级类别特征: {(w1+|C_1||V|+(c2-1)|V|,f1),(w2+|C_1||V|+(c2-1)|V|,f2),...(wn+|C_1||V|+(c2-1)|V|,fn)}
 ***  第三级类别特征: {(w1+|C_1||V|+|C_2||V|+(c3-1)|V|,f1),(w2+|C_1||V|+|C_2||V|+(c3-1)|V|,f2),...(wn+|C_1||V|+|C_2||V|+(c3-1)|V|,fn)}
 *** 一二级类别联合特征：{(|C_1||V|+|C_2||V|+|C_3||V|+(c1-1)|C_2|+c2,1)}
 ***  二三级类别联合特征：{(|C_1||V|+|C_2||V|+|C_3||V|+|C_1||C_2|+(c2-1)|C_3|+c3,1)} 
 */
SVECTOR     *psi(PATTERN x, LABEL y, STRUCTMODEL *sm,
		STRUCT_LEARN_PARM *sparm)
{
	/* Returns a feature vector describing the match between pattern x and
	   label y. The feature vector is returned as an SVECTOR
	   (i.e. pairs <featurenumber:featurevalue>), where the last pair has
	   featurenumber 0 as a terminator. Featurenumbers start with 1 and end with
	   sizePsi. This feature vector determines the linear evaluation
	   function that is used to score labels. There will be one weight in
	   sm.w for each feature. Note that psi has to match
	   find_most_violated_constraint_???(x, y, sm) and vice versa. In
	   particular, find_most_violated_constraint_???(x, y, sm) finds that
	   ybar!=y that maximizes psi(x,ybar,sm)*sm.w (where * is the inner
	   vector product) and the appropriate function of the loss.  */
	SVECTOR *fvec;
	register WORD *sum,*sumi;
	register WORD *ai;
	long veclength;
	long wveclength;
	long c1,c2;
	/*
	   long C_1=5;
	   long C_2=15;
	   long C_3=30;
	 */

	long C_1=25;
	long C_2=136;
	//  long C_3=870;

	c1=y.first_class;
	c2=y.second_class;
	//  c3=y.third_class;

	ai=x.doc->fvec->words;
	veclength=0;
        //printf("\n");
      
	while(ai->wnum){
               
		veclength++;
		ai++;
      //    printf("%d:%f ",ai->wnum,ai->weight);
	} 

    //  printf("\n");
    //   printf("y_first:%d y_second:%d \n",y.first_class,y.second_class);
	veclength++;
	// printf("veclength is %ld \n",veclength);
	// printf("C_1 is %ld \n",C_1);
	// printf("C_2 is %ld \n",C_2);
	// printf("C_3 is %ld \n",C_3);

	//exit(0);
	wveclength=veclength*(C_1+C_2);
	wveclength++;

	sum=(WORD *)my_malloc(sizeof(WORD)*wveclength);
	sumi=sum;

     //  printf("fvec: ");
	//第一级类别特征
	ai=x.doc->fvec->words;
	while(ai->wnum){
		(*sumi)=(*ai);
		sumi->wnum+=(c1-1)*sparm->num_features;
              //  printf("%d:%f ",sumi->wnum,sumi->weight);
		ai++;
		sumi++;
	}     

	//第二级类别特征
	ai=x.doc->fvec->words;
	while(ai->wnum){
		(*sumi)=(*ai);
		sumi->wnum=sumi->wnum+C_1*sparm->num_features+(c2-1)*sparm->num_features;
                // printf("%d:%f ",sumi->wnum,sumi->weight);
		ai++;
		sumi++;
	} 

// printf("\n");
	//第三级类别特征
	// ai=x.doc->fvec->words;
	// while(ai->wnum){
	//    (*sumi)=(*ai);
	//    sumi->wnum=sumi->wnum+C_1*sparm->num_features+C_2*sparm->num_features+(c3-1)*sparm->num_features;
	//    ai++;
	//    sumi++;
	// }


	//一二级类别联合特征
	/*
	   register WORD *fs_class;
	   fs_class->wnum=C_1*sparm->num_features+C_2*sparm->num_features+C_3*sparm->num_features+(c1-1)*C_2+c2;
	   fs_class->weight=5.0;
	   (*sumi)=(*fs_class);
	   sumi++;

	//二三级类别联合特征
	register WORD *st_class;
	st_class->wnum=C_1*sparm->num_features+C_2*sparm->num_features+C_3*sparm->num_features+C_1*C_2+(c2-1)*C_3+c3;
	st_class->weight=5.0;
	(*sumi)=(*st_class);
	sumi++;
	 */
	sumi->wnum=0;

	char *userdefined=NULL;

	if(x.doc->fvec->userdefined)
	{
		userdefined=(char *)my_malloc(sizeof(char)*(strlen(x.doc->fvec->userdefined)+1));
		strcpy(userdefined,x.doc->fvec->userdefined);
	}

	fvec=create_svector_shallow(sum,userdefined,x.doc->fvec->factor);

	/* shift the feature numbers to the position of weight vector of class y */
	//  fvec=shift_s(x.doc->fvec,(y.class-1)*sparm->num_features);

	/* The following makes sure that the weight vectors for each class
	   are treated separately when kernels are used . */

	fvec->kernel_id=(c1-1)*C_2+c2;

	return(fvec);
}

//定义损失函数
double      loss(LABEL y, LABEL ybar, STRUCT_LEARN_PARM *sparm)
{
	/* loss for correct label y and predicted label ybar. The loss for
	   y==ybar has to be zero. sparm->loss_function is set with the -l option. */

	if(sparm->loss_function == 0) { /* type 0 loss: 0/1 loss */

		//    if(y.class == ybar.class)     /* return 0, if y==ybar. return 100 else */
		//     return(0);
		//   else
		//     return(100);
		if((y.first_class==ybar.first_class)&&(y.second_class==ybar.second_class))
		{
			return(0);
		}
		else
		{
			return(100);
		}
		/*
		   else if((y.first_class==ybar.first_class)&&(y.second_class==ybar.second_class)&&(y.second_class!=ybar.second_class)) 
		   {
		   return(100);
		   }
		   else if((y.first_class==ybar.first_class)&&(y.second_class!=ybar.second_class)&&(y.second_class!=ybar.second_class))
		   {
		   return(200);
		   }
		   else if((y.first_class!=ybar.first_class)&&(y.second_class!=ybar.second_class)&&(y.second_class!=ybar.second_class))
		   {
		   return(1000);
		   }
		   else//the label is not plausible
		   {
		   return(2000);
		   }
		 */
	}
	if(sparm->loss_function == 1) { /* type 1 loss: squared difference */

		if((y.first_class==ybar.first_class)&&(y.second_class==ybar.second_class))
		{
			return(0);
		}
		else
		{
			return(100);
		}
		/*
		   else if((y.first_class==ybar.first_class)&&(y.second_class==ybar.second_class)&&(y.second_class!=ybar.second_class))
		   {
		   return(100);
		   }
		   else if((y.first_class==ybar.first_class)&&(y.second_class!=ybar.second_class)&&(y.second_class!=ybar.second_class))
		   {
		   return(200);
		   }
		   else if((y.first_class!=ybar.first_class)&&(y.second_class!=ybar.second_class)&&(y.second_class!=ybar.second_class))
		   {
		   return(1000);
		   }
		   else//the label is not plausible
		   {
		   return(2000);
		   }
		 */

		// return((y.class-ybar.class)*(y.class-ybar.class));
	}
	else {
		/* Put your code for different loss functions here. But then
		   find_most_violated_constraint_???(x, y, sm) has to return the
		   highest scoring label with the largest loss. */
		printf("Unkown loss function\n");
		exit(1);
	}
}

int         finalize_iteration(double ceps, int cached_constraint,
		SAMPLE sample, STRUCTMODEL *sm,
		CONSTSET cset, double *alpha, 
		STRUCT_LEARN_PARM *sparm)
{
	/* This function is called just before the end of each cutting plane iteration. ceps is the amount by which the most violated constraint found in the current iteration was violated. cached_constraint is true if the added constraint was constructed from the cache. If the return value is FALSE, then the algorithm is allowed to terminate. If it is TRUE, the algorithm will keep iterating even if the desired precision sparm->epsilon is already reached. */
	return(0);
}

void        print_struct_learning_stats(SAMPLE sample, STRUCTMODEL *sm,
		CONSTSET cset, double *alpha, 
		STRUCT_LEARN_PARM *sparm)
{
	/* This function is called after training and allows final touches to
	   the model sm. But primarly it allows computing and printing any
	   kind of statistic (e.g. training error) you might want. */

	/* Replace SV with single weight vector */
	MODEL *model=sm->svm_model;
	if(model->kernel_parm.kernel_type == LINEAR) {
		if(struct_verbosity>=1) {
			printf("Compacting linear model..."); fflush(stdout);
		}
		sm->svm_model=compact_linear_model(model);
		sm->w=sm->svm_model->lin_weights; /* short cut to weight vector */
		free_model(model,1);
		if(struct_verbosity>=1) {
			printf("done\n"); fflush(stdout);
		}
	}  
}

void        write_struct_model(char *file, STRUCTMODEL *sm, 
		STRUCT_LEARN_PARM *sparm)
{
	/* Writes structural model sm to file file. */
	FILE *modelfl;
	long j,i,sv_num;
	MODEL *model=sm->svm_model;
	SVECTOR *v;

	if ((modelfl = fopen (file, "w")) == NULL)
	{ perror (file); exit (1); }
	fprintf(modelfl,"SVM-multiclass Version %s\n",INST_VERSION);
	fprintf(modelfl,"%d # number of first classes\n",
			sparm->first_num_classes);
	fprintf(modelfl,"%d # number of second classes\n",
			sparm->second_num_classes);
	// fprintf(modelfl,"%d # number of third classes\n",
	//          sparm->third_num_classes);
	fprintf(modelfl,"%d # number of base features\n",
			sparm->num_features);
	fprintf(modelfl,"%d # loss function\n",
			sparm->loss_function);
	fprintf(modelfl,"%ld # kernel type\n",
			model->kernel_parm.kernel_type);
	fprintf(modelfl,"%ld # kernel parameter -d \n",
			model->kernel_parm.poly_degree);
	fprintf(modelfl,"%.8g # kernel parameter -g \n",
			model->kernel_parm.rbf_gamma);
	fprintf(modelfl,"%.8g # kernel parameter -s \n",
			model->kernel_parm.coef_lin);
	fprintf(modelfl,"%.8g # kernel parameter -r \n",
			model->kernel_parm.coef_const);
	fprintf(modelfl,"%s# kernel parameter -u \n",model->kernel_parm.custom);
	fprintf(modelfl,"%ld # highest feature index \n",model->totwords);
	fprintf(modelfl,"%ld # number of training documents \n",model->totdoc);

	sv_num=1;
	for(i=1;i<model->sv_num;i++) {
		for(v=model->supvec[i]->fvec;v;v=v->next) 
			sv_num++;
	}
	fprintf(modelfl,"%ld # number of support vectors plus 1 \n",sv_num);
	fprintf(modelfl,"%.8g # threshold b, each following line is a SV (starting with alpha*y)\n",model->b);

	for(i=1;i<model->sv_num;i++) {
		for(v=model->supvec[i]->fvec;v;v=v->next) {
			fprintf(modelfl,"%.32g ",model->alpha[i]*v->factor);
			fprintf(modelfl,"qid:%ld ",v->kernel_id);
			for (j=0; (v->words[j]).wnum; j++) {
				fprintf(modelfl,"%ld:%.8g ",
						(long)(v->words[j]).wnum,
						(double)(v->words[j]).weight);
			}
			if(v->userdefined)
				fprintf(modelfl,"#%s\n",v->userdefined);
			else
				fprintf(modelfl,"#\n");
			/* NOTE: this could be made more efficient by summing the
			   alpha's of identical vectors before writing them to the
			   file. */
		}
	}
	fclose(modelfl);
}

void        print_struct_testing_stats(SAMPLE sample, STRUCTMODEL *sm,
		STRUCT_LEARN_PARM *sparm, 
		STRUCT_TEST_STATS *teststats)
{
	/* This function is called after making all test predictions in
	   svm_struct_classify and allows computing and printing any kind of
	   evaluation (e.g. precision/recall) you might want. You can use
	   the function eval_prediction to accumulate the necessary
	   statistics for each prediction. */
}

void        eval_prediction(long exnum, EXAMPLE ex, LABEL ypred, 
		STRUCTMODEL *sm, STRUCT_LEARN_PARM *sparm, 
		STRUCT_TEST_STATS *teststats)
{
	/* This function allows you to accumlate statistic for how well the
	   predicition matches the labeled example. It is called from
	   svm_struct_classify. See also the function
	   print_struct_testing_stats. */
	if(exnum == 0) { /* this is the first time the function is
			    called. So initialize the teststats */
	}
}

STRUCTMODEL read_struct_model(char *file, STRUCT_LEARN_PARM *sparm)
{
	/* Reads structural model sm from file file. This function is used
	   only in the prediction module, not in the learning module. */
	FILE *modelfl;
	STRUCTMODEL sm;
	long i,queryid,slackid;
	double costfactor;
	long max_sv,max_words,ll,wpos;
	char *line,*comment;
	WORD *words;
	char version_buffer[100];
	MODEL *model;

	nol_ll(file,&max_sv,&max_words,&ll); /* scan size of model file */
	max_words+=2;
	ll+=2;

	words = (WORD *)my_malloc(sizeof(WORD)*(max_words+10));
	line = (char *)my_malloc(sizeof(char)*ll);
	model = (MODEL *)my_malloc(sizeof(MODEL));

	if ((modelfl = fopen (file, "r")) == NULL)
	{ perror (file); exit (1); }

	fscanf(modelfl,"SVM-multiclass Version %s\n",version_buffer);
	if(strcmp(version_buffer,INST_VERSION)) {
		perror ("Version of model-file does not match version of svm_struct_classify!"); 
		exit (1); 
	}
	fscanf(modelfl,"%d%*[^\n]\n", &sparm->first_num_classes);
	fscanf(modelfl,"%d%*[^\n]\n", &sparm->second_num_classes);
	//  fscanf(modelfl,"%d%*[^\n]\n", &sparm->third_num_classes);  
	fscanf(modelfl,"%d%*[^\n]\n", &sparm->num_features);  
	fscanf(modelfl,"%d%*[^\n]\n", &sparm->loss_function);  
	fscanf(modelfl,"%ld%*[^\n]\n", &model->kernel_parm.kernel_type);  
	fscanf(modelfl,"%ld%*[^\n]\n", &model->kernel_parm.poly_degree);
	fscanf(modelfl,"%lf%*[^\n]\n", &model->kernel_parm.rbf_gamma);
	fscanf(modelfl,"%lf%*[^\n]\n", &model->kernel_parm.coef_lin);
	fscanf(modelfl,"%lf%*[^\n]\n", &model->kernel_parm.coef_const);
	fscanf(modelfl,"%[^#]%*[^\n]\n", model->kernel_parm.custom);

	fscanf(modelfl,"%ld%*[^\n]\n", &model->totwords);
        sparm->top_features=model->totwords;
	fscanf(modelfl,"%ld%*[^\n]\n", &model->totdoc);
	fscanf(modelfl,"%ld%*[^\n]\n", &model->sv_num);
	fscanf(modelfl,"%lf%*[^\n]\n", &model->b);

	model->supvec = (DOC **)my_malloc(sizeof(DOC *)*model->sv_num);
	model->alpha = (double *)my_malloc(sizeof(double)*model->sv_num);
	model->index=NULL;
	model->lin_weights=NULL;

	for(i=1;i<model->sv_num;i++) {
		fgets(line,(int)ll,modelfl);
		if(!parse_document(line,words,&(model->alpha[i]),&queryid,&slackid,
					&costfactor,&wpos,max_words,&comment)) {
			printf("\nParsing error while reading model file in SV %ld!\n%s",
					i,line);
			exit(1);
		}
		model->supvec[i] = create_example(-1,0,0,0.0,
				create_svector(words,comment,1.0));
		model->supvec[i]->fvec->kernel_id=queryid;
	}
	fclose(modelfl);
	free(line);
	free(words);
	if(verbosity>=1) {
		fprintf(stdout, " (%d support vectors read) ",(int)(model->sv_num-1));
	}
	sm.svm_model=model;
	sm.sizePsi=model->totwords;
	sm.w=NULL;
	return(sm);
}

void        write_label(FILE *fp, LABEL y)
{
	/* Writes label y to file handle fp. */

	int i;
	fprintf(fp,"%d %d %lf ",y.first_class,y.second_class,y.score);
	/*
	   if(y.scores) 
	   for(i=1;i<=y.num_classes;i++)
	   fprintf(fp," %f",y.scores[i]);
	 */
	fprintf(fp,"\n");

} 

void        free_pattern(PATTERN x) {
	/* Frees the memory of x. */
	free_example(x.doc,1);
}

void        free_label(LABEL y) {
	/* Frees the memory of y. */
	//if(y.scores) free(y.scores);
}

void        free_struct_model(STRUCTMODEL sm) 
{
	/* Frees the memory of model. */
	/* if(sm.w) free(sm.w); */ /* this is free'd in free_model */
	if(sm.svm_model) free_model(sm.svm_model,1);
	/* add free calls for user defined data here */
}

void        free_struct_sample(SAMPLE s)
{
	/* Frees the memory of sample s. */
	int i;
	for(i=0;i<s.n;i++) { 
		free_pattern(s.examples[i].x);
		free_label(s.examples[i].y);
	}
	free(s.examples);
}

void        print_struct_help()
{
	/* Prints a help text that is appended to the common help text of
	   svm_struct_learn. */

	printf("          none\n\n");
	printf("Based on multi-class SVM formulation described in:\n");
	printf("          K. Crammer and Y. Singer. On the Algorithmic Implementation of\n");
	printf("          Multi-class SVMs, JMLR, 2001.\n");
}

void         parse_struct_parameters(STRUCT_LEARN_PARM *sparm)
{
	/* Parses the command line parameters that start with -- */
	int i;

	for(i=0;(i<sparm->custom_argc) && ((sparm->custom_argv[i])[0] == '-');i++) {
		switch ((sparm->custom_argv[i])[2]) 
		{ 
			case 'a': i++; /* strcpy(learn_parm->alphafile,argv[i]); */ break;
			case 'e': i++; /* sparm->epsilon=atof(sparm->custom_argv[i]); */ break;
			case 'k': i++; /* sparm->newconstretrain=atol(sparm->custom_argv[i]); */ break;
		}
	}
}

void        print_struct_help_classify()
{
	/* Prints a help text that is appended to the common help text of
	   svm_struct_classify. */
}

void        parse_struct_parameters_classify(STRUCT_LEARN_PARM *sparm)
{
	/* Parses the command line parameters that start with -- for the
	   classification module */
	int i;

	for(i=0;(i<sparm->custom_argc) && ((sparm->custom_argv[i])[0] == '-');i++) {
		switch ((sparm->custom_argv[i])[2]) 
		{ 
			/* case 'x': i++; strcpy(xvalue,sparm->custom_argv[i]); break; */
			default: printf("\nUnrecognized option %s!\n\n",sparm->custom_argv[i]);
				 exit(0);
		}
	}
}

int        parse_multi_document(char *line,WORD *words, LABEL *label, long *queryid, long *slackid, double *costfactor, long int *numwords, long int max_words_doc, char **comment)
{

	//  printf("in the parse_multi_document \n");
	register long wpos,pos;
	long wnum;
	double weight;
	char featurepair[1000],junk[1000];

	(*queryid)=0;
	(*slackid)=0;
	(*costfactor)=1;

	pos=0;
	(*comment)=NULL;
	while(line[pos]){ /*cut off comments */
		if((line[pos]=='#') &&(!(*comment))){
			line[pos]=0;
			(*comment)=&(line[pos+1]);
		}
		if(line[pos]=='\n'){/*strip the CR */
			line[pos]=0;             
		}
		pos++;
	}

	if(!(*comment)) (*comment)=&(line[pos]);
	/* printf("Comment: '%s'\n",(*comment));*/

	wpos=0;
	/*check,that line starts with target value or zero ,but not with feature pair */ 
	if(sscanf(line,"%s",featurepair) == EOF) return(0);
	pos=0;
	while((featurepair[pos] != ':')&& featurepair[pos]) pos++;
	if(featurepair[pos]==':'){
		perror("Line must start with label or 0 !!!\n");
		printf("LINE:%s",line);
		exit(1);
	}

	// printf("here 1 \n");
	// printf("line is::%s \n",line);
	/* read the first target value */
	//int temp_class;
	char temp_s_c[1000];
	pos=0;
	while(space_or_null((int)line[pos])) pos++;
	// printf("pos3 is %ld \n",pos);

	// if(sscanf(line+pos,"%d",label->first_class) == EOF) return(0);
	if(sscanf(line+pos,"%s",temp_s_c) == EOF) return(0);
	// printf("temp_class is %s \n",temp_s_c);
	label->first_class=atoi(temp_s_c);
	// printf("label->first_class is %d \n",label->first_class);

	pos=0;
	while(space_or_null((int)line[pos])) pos++;
	// printf("pos1 is %ld \n",pos);
	while((!space_or_null((int)line[pos]))&&line[pos]) pos++;
	// printf("pos2 is %ld \n",pos);

	/* read the second target value */
	if(sscanf(line+pos,"%s",temp_s_c) == EOF) return(0);
	label->second_class=atoi(temp_s_c);
	//  printf("label->second_class is %d \n",label->second_class);

	while(space_or_null((int)line[pos])) pos++;
	while((!space_or_null((int)line[pos])) && line[pos]) pos++;
	// printf("pos5 is %ld \n",pos);

	/*read the third target value */
	// if(sscanf(line+pos,"%s",temp_s_c) == EOF) return(0);
	// label->third_class=atoi(temp_s_c);
	// printf("label->third_class is %d \n",label->third_class);

	// while(space_or_null((int)line[pos])) pos++;
	// while((!space_or_null((int)line[pos])) && line[pos]) pos++;
	// printf("pos6 is %ld \n",pos);



	while((pos+=read_word(line+pos,featurepair))&&
			(featurepair[0])&&
			(wpos<max_words_doc)){
		// printf("%s\n",featurepair);
		if(sscanf(featurepair,"qid:%ld%s",&wnum,junk)==1){
			/*it is the query id */
			(*queryid)=(long)wnum;
		} 
		else if(sscanf(featurepair,"sid:%ld%s",&wnum,junk)==1){
			/* it is the slack id */
			if(wnum>0)
				(*slackid)=(long)wnum;
			else {
				perror("Slack-id must be  greater or equal to 1!!!\n");
				printf("LINE: %s\n",line);
				exit(1);
			}
		}
		else if(sscanf(featurepair,"cost:%lf%s",&weight,junk)==1){
			/*it is the example-dependent cost factor */
			(*costfactor)=(double)weight;
		}
		else if(sscanf(featurepair,"%ld:%lf%s",&wnum,&weight,junk)==2){
			/*it is a regular feature */
			if(wnum<=0){
				perror("Feature numbers must be larger or equal to 1 !!!\n");
				printf("LINE:%s\n",line);
				exit(1);
			}
			if((wpos>0)&&(words[wpos-1].wnum>wnum)){
				perror("Features must be in increasing order!!!\n");
				printf("LINE:%s\n",line);
				exit(1); 
			}
			(words[wpos]).wnum=wnum;
			(words[wpos]).weight=(FVAL)weight;
                      //  printf("wpos %d wnum:%d weight:%f \n",wpos,(words[wpos]).wnum, (words[wpos]).weight);
			wpos++;
		}
		else{
			perror("Cannot parse feature/value pair!!!\n");
			printf("'%s' in LINE: %s",featurepair,line);
			exit(1);
		}
	}
	(words[wpos]).wnum=0;
	(*numwords)=wpos+1;
	return (1);

}

void read_multi_documents(char *docfile, DOC ***docs,LABEL **label, long int *totwords,long int *totdoc)
{
	char *line,*comment;
	WORD *words;
	long dnum=0,wpos,dpos=0,dneg=0,dunlab=0,queryid,slackid,max_docs;
	long max_words_doc,ll;
	LABEL doc_label;
	double  costfactor;
	FILE *docfl;

	//  if(verbosity>=1){
	printf("Scanning examples...");
	fflush(stdout);
	//  }  

	nol_ll(docfile,&max_docs,&max_words_doc,&ll);/*scan size of input file */ 
        //max_words_doc=2000;
        //ll=2000;
	max_words_doc+=2;
	ll+=2;
	// max_docs+=2;
	//  if(verbosity>=1){
	printf("done\n");
	fflush(stdout);
	//  }

	printf("ll is %ld max_docs is %ld  max_words_doc is %ld \n",ll,max_docs,max_words_doc);
	(*docs)=(DOC **)my_malloc(sizeof(DOC *)*max_docs);/*feature vectors*/
	(*label)=(LABEL *)my_malloc(sizeof(LABEL)*max_docs);/*target values*/  
	// (*doc_label)=(LABEL *)my_malloc(sizeof(LABEL));

	line=(char *)my_malloc(sizeof(char)*ll);

	if((docfl=fopen(docfile,"r"))==NULL) 
	{
		perror (docfile);
		exit(1);
	}

	words=(WORD *)my_malloc(sizeof(WORD)*(max_words_doc+10));
	if(verbosity>=1){
		printf("Reading examples into memory ...");
		fflush(stdout);
	}
	dnum=0;
	(*totwords)=0;
	while((!feof(docfl))&&fgets(line,(int)ll,docfl)){
		if(line[0]=='#') continue; /*line contains comments*/
                //printf("line is %s \n:",line);
		if(!parse_multi_document(line,words,&doc_label,&queryid,&slackid,&costfactor,&wpos,max_words_doc,&comment))
		{
			printf("\nParsing error in line %ld !\n%s",dnum,line);
			exit(1);
		}
		//  char label_key[512];
		//  sprintf(label_key,"%d_%d_%d",doc_label->first_class,doc_label->second_class,doc_label->third_class);
		//  if(multi_label_map.find(label_key)==multi_label_map.end())
		//  {
		//                   multi_label_map[label_key]=mlm_index;
		//                   mlm_index++;
		//  }
		(*label)[dnum]=doc_label;
		if((wpos>1)&&((words[wpos-2]).wnum>(*totwords)))
			(*totwords)=(words[wpos-2]).wnum;
		if((*totwords)>MAXFEATNUM){
			printf("\nMaximum feature number exceeds limit defined in MAXFEATNUM!\n");
			printf("LINE:%s\n",line);
			exit(1);
		}
                /*
                WORD *ai;
                ai=words;
                printf("the words:\n");
                while(ai->wnum){
                 ai++;
                 printf("%d:%f ",ai->wnum,ai->weight);
                }
                printf("\n");       
                */
		(*docs)[dnum]=create_example(dnum,queryid,slackid,costfactor,create_svector(words,comment,1.0));
		/*printf("\nNorm=%f\n",((*docs)[dnum]->fvec)->twonorm_sq;*/
                /*
                WORD *ai;
                ai=(*docs)[dnum]->fvec->words;
                printf("the words:\n");
                while(ai->wnum){
                 ai++;
                 printf("%d:%f ",ai->wnum,ai->weight);
                }
                printf("\n");
                */
		dnum++;
		if(verbosity>=1){
			if((dnum%100)==0){
				printf("%ld..",dnum);
				fflush(stdout);
			}
		}
	} 
        /*
        WORD *ai; 
        ai=words;
        printf("the words:\n");
        while(ai->wnum){
                ai++;
          printf("%d:%f ",ai->wnum,ai->weight);
        }
        printf("\n");
        */          

	fclose(docfl);
	free(line);
	free(words);
	if(verbosity>=1) {
		fprintf(stdout, "OK. (%ld examples read)\n", dnum);
	}
	(*totdoc)=dnum;

}

int isVaildLable(LABEL y,LABEL ybar)
{

	if((y.first_class==ybar.first_class)&&(y.second_class==ybar.second_class))
	{
		return(1);
	}
	else if((y.first_class==ybar.first_class)&&(y.second_class!=ybar.second_class))
	{
		return(1);
	}
	else if((y.first_class!=ybar.first_class)&&(y.second_class!=ybar.second_class))
	{
		return(1);
	}
	else//the label is not plausible
	{
		return(0);
	}



}

int read_all_labels()
{
	char *label_file="example4/lll.txt";
	FILE *label_fl;
	int label_ll=50;
	char *line= line=(char *)my_malloc(sizeof(char)*label_ll);
	if((label_fl=fopen(label_file,"r"))==NULL)
	{
		perror (label_file);
		exit(1);
	}
	mla_index=0;
	while((!feof(label_fl))&&fgets(line,label_ll,label_fl)){
		strcpy(multi_label_arr[mla_index],line);
		printf("multi lable is: %s \n",line);
		mla_index++;
	}
	fclose(label_fl);
	return 0;
}
