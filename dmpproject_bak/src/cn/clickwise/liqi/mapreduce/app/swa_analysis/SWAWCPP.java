package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;


public class SWAWCPP {

	public Hashtable<String,Integer> word_ips_hash;
	
	public class SortElement implements Comparable{
		
		public String key;
		public double val;
		public String cate;
		public int ips;
		
		public SortElement(String key,double val,String cate,int ips)
		{
		   this.key=key;
		   this.val=val;
		   this.cate=cate;
		   this.ips=ips;
		}
		public int compareTo(Object o) {
			SortElement s = (SortElement)o;
			return ips < s.ips ? 1 : (ips == s.ips ? 0 : -1);
		};
		
		public String toString(){
		    return  "key = " + this.key+ ",cate = " + this.cate+",val = " + this.val +",ips = " + this.ips  ;
		}
		
	}
	
	public void read_word_ips(String word_ips_file) throws Exception
	{
		word_ips_hash=new Hashtable<String,Integer>();
		FileReader fr=new FileReader(new File(word_ips_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		
		String[] seg_arr=null;
		String word="";
		String ips="";
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("\001");
			if(seg_arr.length<2)
			{
				continue;
			}
			ips=seg_arr[seg_arr.length-1].trim();
			if((ips==null)||(ips.equals("")))
			{
				continue;
			}
			
			for(int j=0;j<(seg_arr.length-1);j++)
			{
				word=seg_arr[j].trim();
				if((word==null)||(word.equals("")))
				{
					continue;
				}
				if(!(word_ips_hash.containsKey(word)))
				{
					word_ips_hash.put(word, Integer.parseInt(ips));
				}	
			}

		}
		fr.close();
		br.close();
		
		
	}
	
	public void wcpp(String input_file,String output_file) throws Exception
	{
		FileReader fr=new FileReader(new File(input_file));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File(output_file));
        PrintWriter pw=new PrintWriter(fw);		
		
		String line="";
		String[] seg_arr=null;
		
		String word="";
		Hashtable<String,Integer> cate_hash=new Hashtable<String,Integer>();
		String[] temp_seg=null;
		String cate="";
		int cate_num=0;
		String ccn="";
		
		String maxstr="";
		
		ArrayList<SortElement> al = new ArrayList<SortElement>();
		String[] cn_seg=null;
		String maxk="";
		double maxv=0;
		Hashtable cname_hash=new Hashtable();
		
		int wstatis=0;
		while((line=br.readLine())!=null)
		{
			cate_hash=new Hashtable<String,Integer>();
			seg_arr=line.split("\001");
		    if(seg_arr.length<2)
		    {
		    	continue;
		    }
		    word=seg_arr[0].trim();
		    if((word==null)||(word.equals("")))
		    {
		    	continue;
		    }
		    if(isNumbers(word))
		    {
		    	continue;
		    }
		    for(int i=1;i<seg_arr.length;i++)
		    {
		    	ccn=seg_arr[i].trim();
		    	temp_seg=ccn.split("\\s+");
		    	if((temp_seg.length)!=2)
		    	{
		    		continue;
		    	}
		    	cate=temp_seg[0].trim();
		    	if((temp_seg[1].trim())==null)
		    	{
		    		continue;
		    	}
		    	cate_num=Integer.parseInt(temp_seg[1].trim());
		    	if(!(cate_hash.containsKey(cate)))
		    	{
		    		cate_hash.put(cate, cate_num);
		    	}
		    }
		    
		    maxstr=maxCate(word,cate_hash);
            if(((maxstr.trim())!=null)&&(!((maxstr.trim()).equals(""))))
            {
            	//pw.println(word+"\001"+maxstr);
            }
            
            cn_seg=maxstr.split("\\s+");
            int ips;
            if((cn_seg.length==2)&&((cn_seg[1].trim())!=null))
            {
            	maxk=cn_seg[0].trim();
            	if((maxk!=null)&&(!(maxk.equals(""))))
            	{
            		if(!(cname_hash.containsKey(maxk)))
            		{
            			cname_hash.put(maxk, 1);
            		}
            	}
            	maxv=Double.parseDouble(cn_seg[1].trim());
            	ips=word_ips_hash.get(word);
            	if(ips<1)
            	{
            		continue;
            	}
            	SortElement sortele=new SortElement(word,maxv,maxk,ips);
            	wstatis++;
            	al.add(sortele);
            }         
		}
		
		Collections.sort(al);
		
		String[] name_arr=new String[cname_hash.size()];
		Enumeration name_enum=cname_hash.keys();
		String name="";
		int index=0;
		
		File[] name_files=new File[cname_hash.size()];
		FileWriter[] name_fw=new FileWriter[cname_hash.size()];
		PrintWriter[] name_pw=new PrintWriter[cname_hash.size()];
		
		
		while(name_enum.hasMoreElements())
		{
			name=name_enum.nextElement()+"";
			name=name.trim();
			if((name==null)||(name.equals("")))
			{
				continue;
			} 
			name_arr[index++]=name;
		}
		
		for(int i=0;i<name_arr.length;i++)
		{
			name_files[i]=new File("output/"+cte(name_arr[i])+".txt");
			name_fw[i]=new FileWriter(name_files[i]);
			name_pw[i]=new PrintWriter(name_fw[i]);
		}
		
		Iterator it = al.iterator();
		SortElement tst=null;
		while(it.hasNext()){
			tst=(SortElement)it.next();
			for(int j=0;j<name_arr.length;j++)
			{
				if((tst.cate.trim()).equals(name_arr[j].trim()))
				{
					name_pw[j].println(tst.key+"  "+tst.cate+"  "+tst.val);
				}
			}
		}
		
		fr.close();
		br.close();
		fw.close();
		pw.close();
		
		for(int i=0;i<name_arr.length;i++)
		{
			name_fw[i].close();
			name_pw[i].close();			
		}
		
		System.out.println("word number:"+wstatis);
			
	}
	
	public String cte(String name)
	{
		String ename="";

		if(name.equals("财经"))
		{
			ename="caijing";
		}
		else if(name.equals("彩票"))
		{
			ename="caipiao";
			
		}
		else if(name.equals("导航"))
		{
			ename="daohang";
			
		}
		else if(name.equals("店商"))
		{
			ename="dianshang";
			
		}
		else if(name.equals("动画"))
		{
			ename="donghua";
			
		}
		else if(name.equals("房产"))
		{
			ename="fangchan";
		}
		else if(name.equals("黄色网站"))
		{
			ename="huangsewangzhan";
		}
		else if(name.equals("健康"))
		{
			ename="jiankang";
		}
		else if(name.equals("交友"))
		{
			ename="jiaoyou";
		}
		else if(name.equals("教育"))
		{
			ename="jiaoyu";
		}
		else if(name.equals("旅游"))
		{
			ename="lvyou";
		}
		else if(name.equals("母婴"))
		{
			ename="muying";
		}
		else if(name.equals("女性时尚"))
		{
			ename="nvxingshishang";
		}
		else if(name.equals("汽车"))
		{
			ename="qiche";
		}
		else if(name.equals("社区"))
		{
			ename="shequ";
		}
		else if(name.equals("视频"))
		{
			ename="shipin";
		}
		else if(name.equals("数码"))
		{
			ename="shuma";
		}
		else if(name.equals("体育"))
		{
			ename="tiyu";
		}
		else if(name.equals("天气"))
		{
			ename="tianqi";
		}
		else if(name.equals("团购"))
		{
			ename="tuangou";
		}
		else if(name.equals("文娱"))
		{
			ename="wenyu";
		}
		else if(name.equals("小说"))
		{
			ename="xiaoshuo";
		}
		else if(name.equals("笑话"))
		{
			ename="xiaohua";
		}
		else if(name.equals("新闻资讯"))
		{
			ename="xinwenzixun";
		}
		else if(name.equals("音乐"))
		{
			ename="yinyue";
		}
		else if(name.equals("游戏"))
		{
			ename="youxi";
		}
		else if(name.equals("噪音"))
		{
			ename="zaoyin";
		}
		else if(name.equals("招聘"))
		{
			ename="zhaopin";
		}
		
		return ename;
	}
	
	public String maxCate(String word,Hashtable<String,Integer> c_hash)
	{
		String maxc="";
		int maxn=0;
		Enumeration c_enum=c_hash.keys();
		String tempc="";
		int tempn=0;
		while(c_enum.hasMoreElements())
		{
			tempc=c_enum.nextElement()+"";
			tempn=c_hash.get(tempc);
			if(tempn>maxn)
			{
				maxc=tempc;
				maxn=tempn;
			}
			
		}
		
		String second_maxc="";
		int second_maxn=0;
		Enumeration second_c_enum=c_hash.keys();
		while(second_c_enum.hasMoreElements())
		{
			tempc=second_c_enum.nextElement()+"";
			tempn=c_hash.get(tempc);
			if((tempn>second_maxn)&&(!(tempc.equals(maxc))))
			{
				second_maxc=tempc;
				second_maxn=tempn;
			}			
		}
		
	//	System.out.println(word+" "+maxc+" "+maxn+"||||"+second_maxc+" "+second_maxn);
		String rs="";
		if(maxn>second_maxn)
		{
		   rs=maxc+" "+maxn;
		}
		else
		{
			rs="NA";
		}
		return rs;
		
	}
	
	public boolean isNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}
	
	public static void main(String[] args) throws Exception
	{
		String input_file="input/part_secondpart_cate.txt";
		String output_file="output/word_secondpart_catemax.txt";
		String wi_file="input/bkw_ten_day.txt";
		SWAWCPP swa=new SWAWCPP();
		swa.read_word_ips(wi_file);
		swa.wcpp(input_file, output_file);
		
		
		
	}
	
}
