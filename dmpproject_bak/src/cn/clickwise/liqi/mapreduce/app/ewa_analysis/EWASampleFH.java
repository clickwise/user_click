package cn.clickwise.liqi.mapreduce.app.ewa_analysis;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

public class EWASampleFH {
	
	public String tag_server = "";
	public int tag_port = 0;
	public Hashtable<String,Integer> first_label_hash=null;
	public Hashtable<String,Integer> second_label_hash=null;
	public Hashtable<String,Integer> third_label_hash=null;
	public Hashtable label_str_hash=null;
	public int vec_size=0;
	public Hashtable<String,Integer> label_count_hash=null;
	public Jedis swa_dict_redis;
	public String swa_dict_ip;
	public int swa_dict_port;
	public int swa_dict_db;
	public PrintWriter logWriter=null;
	public Hashtable lll_hash=null;
	public PrintWriter lllWriter=null;
	
	public void load_label_file(String label_file) throws Exception
	{
		FileWriter lllfw=new FileWriter(new File("output/lll.txt"));
		lllWriter=new PrintWriter(lllfw);
		
		FileWriter fhc_fw=new FileWriter(new File("output/fhc.txt"));
		PrintWriter fhc_pw=new PrintWriter(fhc_fw);
		
		FileWriter shc_fw=new FileWriter(new File("output/shc.txt"));
		PrintWriter shc_pw=new PrintWriter(shc_fw);
		
		FileWriter thc_fw=new FileWriter(new File("output/thc.txt"));
		PrintWriter thc_pw=new PrintWriter(thc_fw);
		
		FileReader fr=new FileReader(new File(label_file));
		BufferedReader br=new BufferedReader(fr);
		first_label_hash=new Hashtable<String,Integer>();
		second_label_hash=new Hashtable<String,Integer>();
		third_label_hash=new Hashtable<String,Integer>();
		label_str_hash=new Hashtable();
		label_count_hash=new Hashtable<String,Integer>();
		lll_hash=new Hashtable();
		String line="";
		String[] seg_arr=null;
		String cate="";
		String index="";
		int c=0;
		
		//vec_size=200;
		String[] label_seg=null;
		
		String one_cate="";
		String two_cate="";
		String three_cate="";
		
		int one_index=1;
		int two_index=1;
		int three_index=1;
		
		while((line=br.readLine())!=null)
		{
			line=line.trim();
            if((line==null)||(line.equals("")))
            {
            	continue;
            }
            
            seg_arr=line.split("\\s+");
			if((seg_arr.length)!=2)
			{
				continue;
			}
			
			cate=seg_arr[0].trim();
			index=seg_arr[1].trim();
			
            if((cate==null)||(cate.equals("")))
            {
            	continue;
            }
            if(!(label_str_hash.containsKey(cate)))
            {
            	label_str_hash.put(cate, 1);
            }
            
            label_seg=cate.split("\\|");
            if((label_seg.length)!=3)
            {
            	continue;
            }
            one_cate=label_seg[0].trim();
            two_cate=label_seg[1].trim();
            three_cate=label_seg[2].trim();
            if(!(first_label_hash.containsKey(one_cate)))
            {
            	first_label_hash.put(one_cate, one_index);
            	fhc_pw.println(one_cate+" "+one_index);
            	one_index++;
            }
                   
            if(!(second_label_hash.containsKey(two_cate)))
            {
            	second_label_hash.put(two_cate, two_index);
            	shc_pw.println(two_cate+" "+two_index);
            	two_index++;
            }
            
            if(!(third_label_hash.containsKey(three_cate)))
            {
            	third_label_hash.put(three_cate, three_index);
            	thc_pw.println(three_cate+" "+three_index);
            	three_index++;
            }
            
            
          //  System.out.println(one_cate+"|||"+two_cate+"|||"+three_cate);
           // c++;
			
		}
		//System.out.println("c= "+c);
		
		fhc_fw.close();
		shc_fw.close();
		thc_fw.close();
		fhc_pw.close();
		shc_pw.close();
		thc_pw.close();	
		
	}
	
	public void load_local_config() throws Exception {

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名
		/*
		 * FileI nputStream fis = new FileInputStream(config_file); Properties
		 * prop = new Properties(); prop.load(fis); seg_server =
		 * prop.getProperty("seg_server"); seg_port =
		 * Integer.parseInt(prop.getProperty("seg_port")); tag_server =
		 * prop.getProperty("tag_server"); tag_port =
		 * Integer.parseInt(prop.getProperty("tag_port"));
		 * 
		 * swa_dict_ip = prop.getProperty("swa_dict_ip"); swa_dict_port =
		 * Integer.parseInt(prop.getProperty("swa_dict_port")); swa_dict_db =
		 * Integer.parseInt(prop.getProperty("swa_dict_db"));
		 * 
		 * swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);//
		 * redis服务器地址 swa_dict_redis.ping(); swa_dict_redis.select(swa_dict_db);
		 * fis.close();
		 */

		FileWriter fw=new FileWriter(new File("output/log.txt"));
		logWriter=new PrintWriter(fw);
		swa_dict_port = 6379;
		swa_dict_db = 15;
		vec_size=500;
		address = address.trim();
		address="adt2";
		if (address.equals("adt2")) {
			swa_dict_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			swa_dict_ip = "192.168.110.181";
		} else if (address.equals("adt6")) {
			swa_dict_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			swa_dict_ip = "192.168.110.188";
		}else if (address.equals("adt0")) {
			swa_dict_ip = "192.168.110.180";
		}
		

		swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
		swa_dict_redis.ping();
		swa_dict_redis.select(swa_dict_db);
		
	}
	
	
	public String line_process(String line) {

		//System.out.println("line:"+line);
		String new_line = "";
		String[] seg_arr = line.split("\001");
		String seg_title="";
		
		//System.out.println("seg_arr.length:"+seg_arr.length);
        if(seg_arr.length!=2)
        {
        	return "";
        }
        double[] line_vec_words=new double[vec_size];
        for(int i=0;i<line_vec_words.length;i++)
        {
        	line_vec_words[i]=0;
        }
           
        String cate="";
        cate=seg_arr[0].trim();
        if((cate==null)||(cate.equals("")))
        {
        	return "";
        }
        if(!(label_str_hash.containsKey(cate)))
        {
        	
        	return "";
        }
        int old_c=0;
        if(!(label_count_hash.containsKey(cate)))
        {
        	label_count_hash.put(cate, 1);
        }
        else
        {
        	old_c=label_count_hash.get(cate);
        	old_c=old_c+1;
        	label_count_hash.remove(cate);
        	label_count_hash.put(cate, old_c);
        	if(old_c>100)
        	{
        		return "";
        	}
        }
        //System.out.println("cate:"+cate);
        
        String one_cate="";
        String two_cate="";
        String three_cate="";
        String[] label_seg=null;
        label_seg=cate.split("\\|");
        if((label_seg.length)!=3)
        {
        	return "";
        }
        String lll_s="";
        one_cate=label_seg[0].trim();
        two_cate=label_seg[1].trim();
        three_cate=label_seg[2].trim();
        int one_index=0;
        int two_index=0;
        int three_index=0;
        
        one_index=first_label_hash.get(one_cate);
        two_index=second_label_hash.get(two_cate);
        three_index=third_label_hash.get(three_cate);
        if((one_index<1)||(two_index<1)||(three_index<1))
        {
        	return "";
        }
        lll_s=one_index+"_"+two_index+"_"+three_index;
        lll_s=lll_s.trim();
        if(!(lll_hash.containsKey(lll_s)))
        {
        	lllWriter.println(lll_s);
        	lll_hash.put(lll_s, 1);
        }
        seg_title=seg_arr[1].trim();
        //System.out.println(cate+" "+seg_title);
        if((seg_title==null)||(seg_title.equals("")))
        {
        	return "";
        }
        
        
        String[] temp_seg=null;
        temp_seg=seg_title.split("\\s+");
        
        String word="";
        //System.out.println();
        if(temp_seg.length<5)
        {
        	return "";
        }
        
       // word=temp_seg[0].trim();
        String temp_rl="";
        for(int i=0;i<temp_seg.length;i++)
        {
           word=temp_seg[i].trim();
           if(isValidWord(word))
           {
        	//   System.out.print(word+" ");
        	 //  print_weghts("add1:",line_vec_words);
        	//   print_weghts("add2:",toLineWeights(swa_dict_redis.get(word)));
        	   temp_rl=swa_dict_redis.get(word);
        	   
        	   if((temp_rl==null)||(temp_rl.trim()).equals(""))
        	   {
        		   continue;
        	   }
        	   temp_rl=temp_rl.trim();
        	   line_vec_words=addTwoWeights(line_vec_words,toLineWeights(temp_rl));
        	  // print_weghts("addafter:", line_vec_words);
           }
        }
      //  System.out.println();
        
       // for(int i=0;i<vec_size;i++)
       // {
        	//System.out.print(line_vec_words[i]+" ");
       // }
        new_line=toLineWords(line_vec_words);  
        new_line=one_index+" "+two_index+" "+three_index+" "+new_line;
        new_line=new_line.trim();
        //System.out.println("new_line:"+new_line);	
		return new_line;
	}
	
	public String simple_line_process(String line) {

		//System.out.println("line:"+line);
		String new_line = "";
		String seg_title="";
		
        double[] line_vec_words=new double[vec_size];
        for(int i=0;i<line_vec_words.length;i++)
        {
        	line_vec_words[i]=0;
        }
           

        seg_title=line.trim();
        //System.out.println(cate+" "+seg_title);
        if((seg_title==null)||(seg_title.equals("")))
        {
        	return "";
        }
        
        
        String[] temp_seg=null;
        temp_seg=seg_title.split("\\s+");
        
        String word="";
        //System.out.println();
        if(temp_seg.length<1)
        {
        	return "";
        }
        
       // word=temp_seg[0].trim();
        String temp_rl="";
        for(int i=0;i<temp_seg.length;i++)
        {
           word=temp_seg[i].trim();
           if(isValidWord(word))
           {
        	//   System.out.print(word+" ");
        	  // print_weghts("add1:",line_vec_words);
        	   //print_weghts("add2:",toLineWeights(swa_dict_redis.get(word)));
        	   temp_rl=swa_dict_redis.get(word);
        	   
        	   if((temp_rl==null)||(temp_rl.trim()).equals(""))
        	   {
        		   continue;
        	   }
        	   temp_rl=temp_rl.trim();
        	   line_vec_words=addTwoWeights(line_vec_words,toLineWeights(temp_rl));
        	   print_weghts("addafter:", line_vec_words);
           }
        }

        new_line=toLineWords(line_vec_words);  
        new_line=new_line.trim();
 
		return new_line;
	}
	
	public void print_weghts(String add_info,double[] weights)
	{
		
		logWriter.println(add_info+"  ");
		for(int i=0;i<weights.length;i++)
		{
			logWriter.print(weights[i]+" ");
		}		
		logWriter.println();
	}
	
	public double[] toLineWeights(String line)
	{
		double[] lw=new double[vec_size];
		line=line.trim();
		String[] seg_arr=line.split("\\s+");
		if(seg_arr.length!=vec_size)
		{
			for(int j=0;j<lw.length;j++)
			{
				lw[j]=0;
			}
		}
		else
		{
			for(int j=0;j<vec_size;j++)
			{
				lw[j]=Double.parseDouble(seg_arr[j]);
			}
		}
		
		return lw;
	}
	
	public double[] addTwoWeights(double[] weights_1,double[] weights_2)
	{
		double[] nw=new double[vec_size];
		for(int i=0;i<vec_size;i++)
		{
		  nw[i]=weights_1[i]+weights_2[i];	
		}	
		return nw;
	}
	
	public String toLineWords(double[] weights)
	{
		String wl="";
		String word_token="";
		for(int i=0;i<weights.length;i++)
		{
			word_token=(i+1)+":"+weights[i];
			wl=wl+word_token+" ";
		}
		wl=wl.trim();
		
		return wl;
	}
	
	public boolean isValidWord(String word)
	{
		boolean ivl=true;
		if(word==null)
		{
			return false;
		}
		if(word.equals(""))
		{
			ivl=false;
		}
		
		if(Pattern.matches("\\\\/", word))
		{
			ivl=false;
		}	
		return ivl;
	}
	
	public void close_bat()
	{
		lllWriter.close();
	}
	public static void main(String[] args) throws Exception {

		String label_file="input/tb_cates_ju.txt";
		EWASampleFH ewa = new EWASampleFH();
		ewa.load_local_config();
        ewa.load_label_file(label_file);
		InputStreamReader isr = new InputStreamReader(System.in);
		//FileReader fr=new FileReader(new File("input/test3.txt"));
		BufferedReader br = new BufferedReader(isr);
		//BufferedReader br = new BufferedReader(fr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			new_line = ewa.line_process(line);

			if ((new_line == null) || (new_line.equals(""))) {
				continue;
			}
			pw.println(new_line);

		}
		
		ewa.close_bat();

		br.close();
		isr.close();
		osw.close();
		pw.close();

	}
}
