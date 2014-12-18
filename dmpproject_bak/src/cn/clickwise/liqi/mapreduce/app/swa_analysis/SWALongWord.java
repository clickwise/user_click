package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;


public class SWALongWord {

	public void longExtShort(String input_dir,String output_dir) throws Exception
	{
		
		File input_dir_file=new File(input_dir);
		File output_dir_file=new File(output_dir);
		if(!(output_dir_file.exists()))
		{
			output_dir_file.mkdir();
		}
		
		File[] cate_files=input_dir_file.listFiles();
		String fn="";
		for(int i=0;i<cate_files.length;i++)
		{
			fn=cate_files[i].getName();
			if((fn.indexOf("txt"))==-1)
			{
				continue;
			}
			FileReader fr=new FileReader(cate_files[i]);
			BufferedReader br=new BufferedReader(fr);
			
			FileWriter fw=new FileWriter(new File(output_dir+"/"+fn));
			PrintWriter pw=new PrintWriter(fw);
			
			String line="";
			String[] seg_arr=null;
			String sw="";
	 		String cate="";
			String hn="";
			
			Vector words_vec=new Vector();
			Vector line_vec=new Vector();
			while((line=br.readLine())!=null)
			{
				if((line==null)||(line.equals("")))
				{
					continue;
				}
				seg_arr=line.split("\\s+");
				if(seg_arr.length!=3)
				{
					continue;
				}
				sw=seg_arr[0].trim();
				cate=seg_arr[1].trim();
				hn=seg_arr[2].trim();
				if((sw==null)||(sw.equals("")))
				{
					continue;
				}
				words_vec.add(sw);	
				line_vec.add(line);
			}
			
			String[] words_arr=new String[words_vec.size()];
			
			for(int j=0;j<words_vec.size();j++)
			{
				words_arr[j]=words_vec.get(j)+"";
			}
			
			String small_words="";
			String pword="";
			String sword="";
			
			for(int j=0;j<words_arr.length;j++)
			{
				small_words="";
				pword=words_arr[j].trim();			
				if((pword==null)||(pword.equals("")))
				{
					continue;
				}
				for(int k=0;k<words_arr.length;k++)
				{
				  sword=words_arr[k].trim();
				  if((sword==null)||(sword.equals("")))
				  {
				    continue;
				  }		
				
				  if(((pword.indexOf(sword))!=-1)&&(k!=j))
				  {
					  small_words=small_words+sword+" ";
				  }					
				}
				if(pword.length()>4)
				{
				small_words=small_words.trim();
				pw.println(line_vec.get(j)+"\001"+small_words);
				}
				else
				{
					pw.println(line_vec.get(j)+"\001");
				}
				
			}
			
			fr.close();
			br.close();
			fw.close();
			pw.close();
				
		}
		
		
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		String input_dir="output_copy";
		String output_dir="output_secondpart_small2";
		
		SWALongWord swlw=new SWALongWord();
		swlw.longExtShort(input_dir, output_dir);
		
		
		
		
	}
	
	
	
}
