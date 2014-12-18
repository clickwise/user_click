package cn.clickwise.liqi.nlp.classify.perceptron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import redis.clients.jedis.Jedis;

/**
 * Perceptron  Algorithm 用于分类 
 * @author lq
 *
 */

public class PerceptronAlgorithm {
	
	public int fln=0;//第一级标记最大编号
	public int sln=0;//第二级标记最大编号
	public int tln=0;//第三级标记最大编号
	public int fdim=0;
	
	public LABEL[] label_set=null;
	
	public String redis_para_ip = "";
	public int redis_para_port = 6379;
	public int redis_para_db = 0;
	public Jedis para_redis;
	
	public Hashtable<String,Integer> fli_hash=null;
	public Hashtable<String,Integer> sli_hash=null;
	public Hashtable<String,Integer> tli_hash=null;
	
	public int[][] f_s_map;
	public int[][] s_t_map;
	
	public LABEL[] read_label_set(String label_file) throws Exception
	{
		LABEL[] lset=null;
		Vector lvec=new Vector();
		FileReader fr=new FileReader(new File(label_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		
		String[] seg_arr=null;
		int temp_fln=0;
		int temp_sln=0;
		int temp_tln=0;
		fln=0;
		sln=0;
		tln=0;
		
		Hashtable<String,Integer> lhash=new Hashtable<String,Integer>();
		Hashtable<Integer,Vector> f_s_hash=new Hashtable<Integer,Vector>();
		Hashtable<Integer,Vector> s_t_hash=new Hashtable<Integer,Vector>();
		
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			
			
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("_");
			if((seg_arr.length)!=3)
			{
				continue;
			}
			
			if(!(lhash.containsKey(line)))
			{
				lhash.put(line, 1);
				lvec.add(line);
			}
			temp_fln=Integer.parseInt(seg_arr[0]);
			temp_sln=Integer.parseInt(seg_arr[1]);
			temp_tln=Integer.parseInt(seg_arr[2]);
			if(!(f_s_hash.containsKey(temp_fln)))
			{
				Vector temp_vec=new Vector();
				temp_vec.add(temp_sln);
				f_s_hash.put(temp_fln, temp_vec);
			}
			else
			{
				Vector old_temp_vec=f_s_hash.get(temp_fln);
				boolean isNew=true;
				int old_sln=0;
				for(int k=0;k<old_temp_vec.size();k++)
				{
					old_sln=Integer.parseInt(old_temp_vec.get(k)+"");
					if(old_sln==temp_sln)
					{
						isNew=false;
					}
				}
				if(isNew==true)
				{
					old_temp_vec.add(temp_sln);
					f_s_hash.remove(temp_fln);
					f_s_hash.put(temp_fln, old_temp_vec);
				}	
			}
			
			
			if(!(s_t_hash.containsKey(temp_sln)))
			{
				Vector temp_vec=new Vector();
				temp_vec.add(temp_tln);
				s_t_hash.put(temp_sln, temp_vec);
			}
			else
			{
				Vector old_temp_vec=s_t_hash.get(temp_sln);
				boolean isNew=true;
				int old_tln=0;
				for(int k=0;k<old_temp_vec.size();k++)
				{
					old_tln=Integer.parseInt(old_temp_vec.get(k)+"");
					if(old_tln==temp_tln)
					{
						isNew=false;
					}
				}
				
				if(isNew==true)
				{
					old_temp_vec.add(temp_tln);
					s_t_hash.remove(temp_sln);
					s_t_hash.put(temp_sln, old_temp_vec);
				}	
			}
			
				
			if(temp_fln>fln)
			{
				fln=temp_fln;
			}
			if(temp_sln>sln)
			{
				sln=temp_sln;
			}
			if(temp_tln>tln)
			{
				tln=temp_tln;
			}					
		}
		
		//System.out.println();
		f_s_map=new int[fln][];
		Vector tt_vec=null;
		for(int i=0;i<f_s_map.length;i++)
		{
		   	tt_vec=f_s_hash.get((i+1));
		   	f_s_map[i]=new int[tt_vec.size()];
			//System.out.print("f:"+(i+1)+": ");
		   	for(int j=0;j<tt_vec.size();j++)
		   	{
		   	
		   		f_s_map[i][j]=Integer.parseInt(tt_vec.get(j)+"");
		   		//System.out.print(f_s_map[i][j]+" ");
		   	}
			//System.out.println();
		}

		s_t_map=new int[sln][];
		for(int i=0;i<s_t_map.length;i++)
		{		   	
		   	tt_vec=s_t_hash.get((i+1));
		   	s_t_map[i]=new int[tt_vec.size()];
			//System.out.print("s:"+(i+1)+": ");
		   	for(int j=0;j<tt_vec.size();j++)
		   	{
		   		s_t_map[i][j]=Integer.parseInt(tt_vec.get(j)+"");
		  		//System.out.print(s_t_map[i][j]+" ");
		   	}	
		   	//System.out.println();
		}
		
		
		lset=new LABEL[lvec.size()];
		
		String lstr="";
		
        LABEL temp_label=null;	
		int temp_fl=0;
		int temp_sl=0;
		int temp_tl=0;
		for(int i=0;i<lvec.size();i++)
		{
			lstr=lvec.get(i)+"";
			lstr=lstr.trim();
            seg_arr=lstr.split("_");
			temp_fl=Integer.parseInt(seg_arr[0]);
			temp_sl=Integer.parseInt(seg_arr[1]);
			temp_tl=Integer.parseInt(seg_arr[2]);
			
			temp_label=new LABEL();
			temp_label.index=(i+1);
			temp_label.fl=temp_fl;
			temp_label.sl=temp_sl;
			temp_label.tl=temp_tl;
			temp_label.fln=fln;
			temp_label.sln=sln;
			temp_label.tln=tln;
			
			lset[i]=temp_label;
		}
		
		fdim=fln+sln+tln;
		label_set=lset;
		
		return lset;
	}
	
	public void read_label_index(String findex_file,String sindex_file,String tindex_file) throws Exception
	{		
		fli_hash=new Hashtable<String,Integer>();
		sli_hash=new Hashtable<String,Integer>();
		tli_hash=new Hashtable<String,Integer>();
		FileReader fr_first = new FileReader(new File(findex_file));
		BufferedReader br_first = new BufferedReader(fr_first);

		String line_first = "";
		String[] pair_first = null;
		String name_first = "";
	    int index_first = 0;
		while ((line_first = br_first.readLine()) != null) {
			line_first = line_first.trim();
			pair_first = line_first.split("\\s+");
			if ((pair_first.length) != 2) {
				continue;
			}
			name_first = pair_first[0].trim();
			index_first = Integer.parseInt(pair_first[1]);
			if (!(fli_hash.containsKey(index_first))) {
				fli_hash.put(name_first,index_first);
			}
		}
		br_first.close();
		fr_first.close();

		FileReader fr_second = new FileReader(new File(sindex_file));
		BufferedReader br_second = new BufferedReader(fr_second);

		String line_second = "";
		String[] pair_second = null;
		String name_second = "";
		int index_second = 0;
		while ((line_second = br_second.readLine()) != null) {
			line_second = line_second.trim();
			pair_second = line_second.split("\\s+");
			if ((pair_second.length) != 2) {
				continue;
			}
			name_second = pair_second[0].trim();
			index_second = Integer.parseInt(pair_second[1]);
			if (!(sli_hash.containsKey(index_second))) {
				sli_hash.put(name_second,index_second);
			}
		}
		br_second.close();
		fr_second.close();

		FileReader fr_third = new FileReader(new File(tindex_file));
		BufferedReader br_third = new BufferedReader(fr_third);

		String line_third = "";
		String[] pair_third = null;
		String name_third = "";
		int index_third = 0;
		
		while ((line_third = br_third.readLine()) != null) {
			line_third = line_third.trim();
			//System.out.println("line_third:"+line_third);
			pair_third = line_third.split("\\s+");
			if ((pair_third.length) != 2) {
				continue;
			}
			name_third = pair_third[0].trim();
			index_third = Integer.parseInt(pair_third[1]);
			if (!(tli_hash.containsKey(index_third))) {
				tli_hash.put(name_third,index_third);
			}
		}
		br_third.close();
		fr_third.close();
	}
	
	public class LABEL{
		public int index;
		public int fl;
		public int sl;
		public int tl;
		public int fln;
		public int sln;
		public int tln;
	}
	
	public class WORD{
		public String word;
		public int count;
	}
		
	public int scan_one_line(String line)
	{
		int res=0;
		String[] seg_arr=null;
		line=line.trim();
		if((line==null)||(line.equals("")))
		{
			return res;
		}
		
		String lstr="";
		String word_info="";
		seg_arr=line.split("\001");
		if((seg_arr.length)!=2)
		{
			return res;
		}
		
		lstr=seg_arr[0].trim();
		word_info=seg_arr[1].trim();
		
		LABEL ybar=str_to_label(lstr);
		WORD[] words=str_to_words(word_info);		
		//LABEL ypre=classify_example(words);
		LABEL ypre=classify_example_tree(words);
		update_words_para(words,ybar,ypre);
		
		return 1;
	}
	
	
	public double[] psi(String word,LABEL y)
	{
		double[] fvec=null;
		fvec=new double[fdim];
		for(int i=0;i<fdim;i++)
		{
			fvec[i]=0;
		}
		
		int findex=0;
		int sindex=0;
		int tindex=0;
		findex=y.fl-1;
		sindex=y.fln+y.sl-1;
		tindex=y.fln+y.sln+y.tl-1;
		fvec[findex]=1.0;
		fvec[sindex]=1.0;
		fvec[tindex]=1.0;
		
		return fvec;		
	}
	
	
	
	
	public LABEL classify_example(WORD[] words)
	{
		LABEL ymax=label_set[0];
		double max_score=score_label(words,label_set[0]);
		double temp_score=0.0;
		for(int i=1;i<label_set.length;i++)
		{
			temp_score=score_label(words,label_set[i]);
			if(temp_score>max_score)
			{
				ymax=label_set[i];
				max_score=temp_score;
			}
		}

		return ymax;
	}
	
	public LABEL classify_example_tree(WORD[] words)
	{
		int fmax=1;//最大的第一层标签
		int svir=1;//虚假的第二层标签
		int tvir=1;//虚假的第三层标签
		
		LABEL temp_label=new LABEL();
		temp_label.fl=fmax;
		temp_label.sl=svir;
		temp_label.tl=tvir;
		double max_score=score_label(words,temp_label);
		double temp_score=0.0;
		
		for(int i=2;i<fln;i++)
		{
			temp_label=new LABEL();
			temp_label.fl=i;
			temp_label.sl=svir;
			temp_label.tl=tvir;
			temp_score=score_label(words,temp_label);
			if(temp_score>max_score)
			{
				max_score=temp_score;
				fmax=i;
			}
		}
		
		int fvir=fmax;
		int smax=f_s_map[fmax-1][0];
		tvir=1;
		temp_label=new LABEL();
		temp_label.fl=fvir;
		temp_label.sl=smax;
		temp_label.tl=tvir;
		max_score=score_label(words,temp_label);
		temp_score=0.0;
		
		for(int i=1;i<f_s_map[fmax-1].length;i++)
		{
			temp_label=new LABEL();
			temp_label.fl=fvir;
			temp_label.sl=f_s_map[fmax-1][i];
			temp_label.tl=tvir;
			temp_score=score_label(words,temp_label);
			if(temp_score>max_score)
			{
				max_score=temp_score;
				smax=i;
			}			
		}
		
		
		fvir=fmax;
		svir=smax;
		int tmax=s_t_map[smax][0];
		temp_label=new LABEL();
		temp_label.fl=fvir;
		temp_label.sl=svir;
		temp_label.tl=tmax;
		max_score=score_label(words,temp_label);
		temp_score=0.0;
		
		for(int i=1;i<s_t_map[smax-1].length;i++)
		{
			temp_label=new LABEL();
			temp_label.fl=fvir;
			temp_label.sl=svir;
			temp_label.tl=s_t_map[smax-1][i];
			temp_score=score_label(words,temp_label);
			if(temp_score>max_score)
			{
				max_score=temp_score;
				tmax=i;
			}			
		}
		
		
		LABEL ymax=new LABEL();
        ymax.fl=fmax;
        ymax.sl=smax;
        ymax.tl=tmax;
        ymax.fln=fln;
        ymax.sln=sln;
        ymax.tln=tln;
        
		return ymax;
	}
	
	public double score_label(WORD[] words,LABEL y)
	{
		double score=0;
		
		String word="";
		double count=0.0;
		
		double[] wfvec=null;
		double[] wpara=null;
		for(int i=0;i<words.length;i++)
		{
			word=words[i].word;
			word=word.trim();
			count=words[i].count;
			wpara=get_word_para(word);
			wfvec=psi(word,y);
            score=score+inner_product(wfvec,wpara);
		}
		
		return score;
	}
	
	
	
	public void init_para_table() throws Exception
	{
		Properties prop = new Properties();
		// URL is = this.getClass().getResource("conf/config.properties");
		//InputStream model_is = this.getClass().getResourceAsStream(
		//		"input/per_config.properties");
		InputStream model_is = new FileInputStream(new File("input/per_config.properties"));
		prop.load(model_is);
		redis_para_ip = prop.getProperty("redis_para_ip");
		redis_para_port =Integer.parseInt(prop.getProperty("redis_para_port"));
		redis_para_db = Integer.parseInt(prop.getProperty("redis_para_db"));
		para_redis = new Jedis(redis_para_ip, redis_para_port, 100000);
		para_redis.ping();
		para_redis.select(redis_para_db);		
	}
	
	public double[] get_word_para(String word)
	{
		double[] para=null;
		para=new double[fdim];
		for(int i=0;i<fdim;i++)
		{
			para[i]=0.0;
		}
		
		String para_line="";
		para_line=para_redis.get(word);
		String[] seg_arr=null;
		if(para_line!=null)
		{
			para_line=para_line.trim();
			if(para_line==null)
			{
				return para;
			}
			seg_arr=para_line.split("\\s+");
			if((seg_arr.length)!=fdim)
			{
				return para;
			}
			for(int j=0;j<seg_arr.length;j++)
			{
				para[j]=Double.parseDouble(seg_arr[j]);
			}						
		}		
		return para;
	}
	
	public void set_word_para(String word,double[] para)
	{
		String para_line="";
		for(int i=0;i<para.length;i++)
		{
			para_line=para_line+para[i]+" ";
		}
		para_line=para_line.trim();
		para_redis.set(word,para_line);		
	}
	
	public double inner_product(double[] arr1,double[] arr2)
	{
		double in_pro=0;
		for(int i=0;i<arr1.length;i++)
		{
			in_pro=in_pro+arr1[i]*arr2[i];
		}				
		return in_pro;
	}
	
	public WORD[] str_to_words(String words_str)
	{
		WORD[] warr=null;
		Hashtable<String,Integer> w_hash=new Hashtable<String,Integer>();
		String word="";	
		words_str=words_str.trim();
		if((words_str==null)||(words_str.equals("")))
		{
			return null;
		}
		String[] seg_arr=null;
		seg_arr=words_str.split("\\s+");
		int temp_count=0;
		for(int i=0;i<seg_arr.length;i++)
		{
			word=seg_arr[i].trim();
			if((word==null)||(word.equals("")))
			{
				continue;
			}
			if(!(w_hash.containsKey(word)))
			{
				w_hash.put(word, 1);				
			}
			else
			{
				temp_count=w_hash.get(word);
				temp_count=temp_count+1;
				w_hash.remove(word);
				w_hash.put(word, temp_count);
			}
		}
		
		Enumeration w_enum=w_hash.keys();
		WORD tword=null;
		warr=new WORD[w_hash.size()];
		int index=0;
		while(w_enum.hasMoreElements())
		{
			word=w_enum.nextElement()+"";
			word=word.trim();
			
			//if((word==null)||(word.equals("")))
			//{
			//	continue;
			//}
			temp_count=w_hash.get(word);
			tword=new WORD();
			tword.word=word;
			tword.count=temp_count;
			warr[index++]=tword;
		}
		
		return warr;
	}
	public LABEL str_to_label(String lstr)
	{
		LABEL y=null;
		lstr=lstr.trim();
		if((lstr==null)||(lstr.equals("")))
		{
			return null;
		}
		String[] seg_arr=lstr.split("\\|");
		
		String fc="";
		String sc="";
		String tc="";
		if((seg_arr.length)!=3)
		{
			return null;
		}
		
		fc=seg_arr[0].trim();
		sc=seg_arr[1].trim();
		tc=seg_arr[2].trim();
		int fl=0;
		int sl=0;
		int tl=0;
		fl=fli_hash.get(fc);
		sl=sli_hash.get(sc);
		tl=tli_hash.get(tc);
		if((fl>0)&&(sl>0)&&(tl>0))
		{
			y=new LABEL();
			y.fl=fl;
			y.sl=sl;
			y.tl=tl;
			y.fln=fln;
			y.sln=sln;
			y.tln=tln;
		}		
		return y;
	}
	
	public void update_words_para(WORD[] words,LABEL ybar,LABEL ypre)
	{
		WORD one_word=null;
		for(int i=0;i<words.length;i++)
		{
			one_word=words[i];
			update_one_word_para(one_word,ybar,ypre);
		}	
	}
	
	public void update_one_word_para(WORD word,LABEL ybar,LABEL ypre)
	{
		double[] para=null;
		para=get_word_para(word.word);
		double[] fvec_ybar=new double[fdim];
		double[] fvec_ypre=new double[fdim];
		fvec_ybar=psi(word.word,ybar);
		fvec_ypre=psi(word.word,ypre);
		
		for(int i=0;i<fdim;i++)
		{
			para[i]=para[i]+fvec_ybar[i]-fvec_ypre[i];
		}
		set_word_para(word.word,para);		
	}
	
		
	public static void main(String[] args) throws Exception
	{
		PerceptronAlgorithm per_alg = new PerceptronAlgorithm();
		per_alg.init_para_table();
		String label_set_file="input/lll.txt";
		per_alg.read_label_set(label_set_file);
		String findex_file="input/fhc.txt";
		String sindex_file="input/shc.txt";
		String tindex_file="input/thc.txt";
        per_alg.read_label_index(findex_file, sindex_file, tindex_file);
        
		//InputStreamReader isr = new InputStreamReader(System.in);
		FileReader fr=new FileReader(new File("input/sge_ngram_test.txt"));
		//BufferedReader br = new BufferedReader(isr);
		BufferedReader br = new BufferedReader(fr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		int res=0;
		int lnum=0;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			lnum++;
			System.out.println("lnum:"+lnum);
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			res = per_alg.scan_one_line(line);

		}
		

		br.close();
		fr.close();
		osw.close();
		pw.close();
		
		
	}
	
}

