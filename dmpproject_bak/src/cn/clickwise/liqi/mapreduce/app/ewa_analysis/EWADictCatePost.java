package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
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


public class EWADictCatePost {

	public Hashtable first_level_hash=null;
	public Hashtable second_level_hash=null;
	public Hashtable third_level_hash=null;
	public Hashtable word_id_hash=null;
	public ArrayList<SortElement> al ;
	public Hashtable<String,String> cate_words_hash=null;
	
	public class SortElement implements Comparable{
		
		public String key;
		public double val;
		public String cate;
		public int docid;
		
		public SortElement(String key,double val,String cate,int docid)
		{
		   this.key=key;
		   this.val=val;
		   this.cate=cate;
		   this.docid=docid;
		}
		public int compareTo(Object o) {
			SortElement s = (SortElement)o;
			return val < s.val ? 1 : (val == s.val ? 0 : -1);
		};
		
		public String toString(){
		    return  "key = " + this.key+ ",cate = " + this.cate+",val = " + this.val +",docid = " + this.docid ;
		}
		
	}
	
	public void read_level_names(String first_level_path,
			String second_level_path, String third_level_path) throws Exception {
		first_level_hash = new Hashtable();
		second_level_hash = new Hashtable();
		third_level_hash = new Hashtable();

		FileReader fr_first = new FileReader(new File(first_level_path));
		BufferedReader br_first = new BufferedReader(fr_first);

		String line_first = "";
		String[] pair_first = null;
		String name_first = "";
		String index_first = "";
		while ((line_first = br_first.readLine()) != null) {
			line_first = line_first.trim();
			pair_first = line_first.split("\\s+");
			if ((pair_first.length) != 2) {
				continue;
			}
			name_first = pair_first[0];
			index_first = pair_first[1];
			if (!(first_level_hash.containsKey(index_first))) {
				first_level_hash.put(index_first, name_first);
			}
		}
		br_first.close();
		fr_first.close();

		FileReader fr_second = new FileReader(new File(second_level_path));
		BufferedReader br_second = new BufferedReader(fr_second);

		String line_second = "";
		String[] pair_second = null;
		String name_second = "";
		String index_second = "";
		while ((line_second = br_second.readLine()) != null) {
			line_second = line_second.trim();
			pair_second = line_second.split("\\s+");
			if ((pair_second.length) != 2) {
				continue;
			}
			name_second = pair_second[0];
			index_second = pair_second[1];
			if (!(second_level_hash.containsKey(index_second))) {
				second_level_hash.put(index_second, name_second);
			}
		}
		br_second.close();
		fr_second.close();

		FileReader fr_third = new FileReader(new File(third_level_path));
		BufferedReader br_third = new BufferedReader(fr_third);

		String line_third = "";
		String[] pair_third = null;
		String name_third = "";
		String index_third = "";
		
		while ((line_third = br_third.readLine()) != null) {
			line_third = line_third.trim();
			//System.out.println("line_third:"+line_third);
			pair_third = line_third.split("\\s+");
			if ((pair_third.length) != 2) {
				continue;
			}
			name_third = pair_third[0];
			index_third = pair_third[1];
			if (!(third_level_hash.containsKey(index_third))) {
				third_level_hash.put(index_third, name_third);
			}
		}
		br_third.close();
		fr_third.close();

	}
	
	
	
	public void read_pred_and_sort(String predict_file) throws Exception
	{
		FileReader fr=new FileReader(new File(predict_file));
		BufferedReader br=new BufferedReader(fr);
        String line="";
        String[] seg_arr=null;
        String docid="";
        String first_cate="";
        String second_cate="";
        String third_cate="";
        String cate_str="";
        String score="";
           
        while((line=br.readLine())!=null)
        {
        	line=line.trim();
        	if((line==null)||(line.equals("")))
        	{
        		continue;
        	}
        	seg_arr=line.split("\\s+");
        	if(seg_arr.length!=5)
        	{
        		continue;
        	}
        	docid=seg_arr[0].trim();
        	first_cate=seg_arr[1].trim();
        	second_cate=seg_arr[2].trim();
        	third_cate=seg_arr[3].trim();
        	cate_str=first_cate+"|"+second_cate+"|"+third_cate;
        	score=seg_arr[4].trim();
        	SortElement sortele=new SortElement("",Double.parseDouble(score),cate_str,Integer.parseInt(docid));
        	al.add(sortele);
        }
        Collections.sort(al);
        Iterator it=al.iterator();
        SortElement stl=null;
        
        String word="";
        String first_cate_name;
        String second_cate_name;
        String third_cate_name;
        FileWriter fw=new FileWriter(new File("output/ec_ckws_num.txt"));
        PrintWriter pw=new PrintWriter(fw);
        cate_words_hash=new Hashtable<String,String>();
        String cn_str="";
        String old_str="";
        while(it.hasNext())
        {
        	stl=(SortElement)it.next();
        	word=word_id_hash.get(stl.docid+"")+"";
            cate_str=stl.cate;
            seg_arr=cate_str.split("\\|");
            if(seg_arr.length!=3)
            {
            	continue;
            }
            first_cate=seg_arr[0].trim();
            second_cate=seg_arr[1].trim();
            third_cate=seg_arr[2].trim();
            first_cate_name=first_level_hash.get(first_cate)+"";
            second_cate_name=second_level_hash.get(second_cate)+"";
            third_cate_name=third_level_hash.get(third_cate)+"";
            if((first_cate_name==null)||(second_cate_name==null)||(third_cate_name==null))
            {
            	continue;
            }
            cn_str=first_cate_name+"|"+second_cate_name+"|"+third_cate_name;
            if(!(cate_words_hash.containsKey(cn_str)))
            {
            	cate_words_hash.put(cn_str, word+"|"+stl.val+"\001");
            }
            else
            {
            	old_str=cate_words_hash.get(cn_str);
            	old_str=old_str+ word+"|"+stl.val+"\001";
            	cate_words_hash.remove(cn_str);
            	cate_words_hash.put(cn_str, old_str);
            }
        	//pw.println(word+"\001"+first_cate_name+"|"+second_cate_name+"|"+third_cate_name+"\001"+stl.val);
        }
        //pw.close();
        
        Enumeration cwh_enum=cate_words_hash.keys();
        
        while(cwh_enum.hasMoreElements())
        {
           cn_str=cwh_enum.nextElement()+"";
           old_str=cate_words_hash.get(cn_str);
           seg_arr=old_str.split("\001");
           pw.println(cn_str+"\001"+seg_arr.length+"\001"+old_str);
        }
        pw.close();
        
        
	}
	
	public void read_word_id_file(String wif_file) throws Exception
	{
		word_id_hash=new Hashtable();
		FileReader fr=new FileReader(new File(wif_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String[] seg_arr=null;
		String word="";
		String index="";
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("\001");
			if(seg_arr.length!=2)
			{
				continue;
			}
			word=seg_arr[0].trim();
			index=seg_arr[1].trim();
			if(!(word_id_hash.containsKey(index)))
			{
				word_id_hash.put(index, word);
			}
		}
		
		fr.close();
		br.close();
		
		
		
	}
	
	public void load_and_init()
	{
		al = new ArrayList<SortElement>();
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		EWADictCatePost edc=new EWADictCatePost();
		edc.load_and_init();
		String first_level_path="model_dir/fhc.txt";	
		String second_level_path="model_dir/shc.txt";
		String third_level_path="model_dir/thc.txt";
		edc.read_level_names(first_level_path, second_level_path, third_level_path);
		String word_id_file="input/dict_docid.txt";
		edc.read_word_id_file(word_id_file);
		String predict_file="input/predictions";
		edc.read_pred_and_sort(predict_file);
	}
	
	
	
	
}
