package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;


public class ErrorStatis {

	public static void main(String[] args) throws Exception
	{
		FileReader fr=new FileReader(new File("input/evl-performance.dat"));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File("output/evl_error_statis_str.txt"));
		PrintWriter pw=new PrintWriter(fw);
		
		FileReader fl_fr=new FileReader(new File("dict/first_nart_names.txt"));
		BufferedReader fl_br=new BufferedReader(fl_fr);
		
		FileReader sl_fr=new FileReader(new File("dict/second_nart_names.txt"));
		BufferedReader sl_br=new BufferedReader(sl_fr);
		
		Hashtable<String,String> fl_hash=new Hashtable<String,String>();
		Hashtable<String,String> sl_hash=new Hashtable<String,String>();
		
		String temp_line="";
		String[] temp_seg=null;
		String temp_name="";
		int temp_index=0;
		while((temp_line=fl_br.readLine())!=null)
		{
			temp_seg=temp_line.split("\\s+");
			if(temp_seg.length<2)
			{
				continue;
			}
			temp_name=temp_seg[0].trim();
			temp_index=Integer.parseInt(temp_seg[1]);
			if(!fl_hash.containsKey(temp_index))
			{
				fl_hash.put(temp_index+"", temp_name);
			}
		}
		
		while((temp_line=sl_br.readLine())!=null)
		{
			temp_seg=temp_line.split("\\s+");
			if(temp_seg.length<2)
			{
				continue;
			}
			temp_name=temp_seg[0].trim();
			temp_index=Integer.parseInt(temp_seg[1]);
			if(!sl_hash.containsKey(temp_index))
			{
				sl_hash.put(temp_index+"", temp_name);
			}
		}
		
	
		String line="";
		int count=0;
		String[] seg_arr=null;
		String first_cate="";
		String second_cate="";
		Hashtable<String,String> line_hash=new Hashtable<String,String>();
		String label_str="";
		String old_lines="";
		while((line=br.readLine())!=null)
		{
			count++;
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			if(count>3)
			{
			   seg_arr=line.split("\\s+");	
			   if(seg_arr.length!=4)
			   {
				   continue;
			   }
			   first_cate=seg_arr[2].trim();
			   second_cate=seg_arr[3].trim();
			   label_str=first_cate+"_"+second_cate;
			   if(!line_hash.containsKey(label_str))
			   {
				   line_hash.put(label_str, line+"\001");
			   }
			   else
			   {
				   old_lines=line_hash.get(label_str);
				   line_hash.remove(label_str);
				   old_lines=old_lines+line+"\001";
				   line_hash.put(label_str, old_lines);
			   }	
			}			
		}
		
		fr.close();
		br.close();
		
		Enumeration str_enum=line_hash.keys();
		String temp_key="";
		String lines_one="";
		String fp="";
		String sp="";
		String fg="";
		String sg="";
		
		String[] tt_seg=null;
		while(str_enum.hasMoreElements())
		{
			temp_key=str_enum.nextElement()+"";
			lines_one=line_hash.get(temp_key);
			lines_one=lines_one.trim();
			if((lines_one==null)||(lines_one.equals("")))
			{
		    	continue;	
			}
			
			seg_arr=lines_one.split("\001");
			for(int j=0;j<seg_arr.length;j++)
			{
				line=seg_arr[j];
				line=line.trim();
				if((line==null)||(line.equals("")))
				{
			    	continue;	
				}
				
				tt_seg=line.split("\\s+");
				if(tt_seg.length!=4)
				{
					continue;
				}
				
				fp=tt_seg[0].trim();
				sp=tt_seg[1].trim();
				fg=tt_seg[2].trim();
				sg=tt_seg[3].trim();
				if((!fp.equals(fg))||(!sp.equals(sg)))
				{
				  pw.println("gnd:"+fl_hash.get(fg)+" "+sl_hash.get(sg)+" pred:"+fl_hash.get(fp)+" "+sl_hash.get(sp));
				}
			}
		}
		
		fw.close();
		pw.close();
		
		
	}
	
	
}
