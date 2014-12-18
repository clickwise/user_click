package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


public class EWADictToSample {

	public int vec_size=200;
	
	public void dictToSam(String input_file,String dict_sample_file,String docid_file) throws Exception
	{
		FileReader fr=new FileReader(new File(input_file));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter dict_sam_fw=new FileWriter(new File(dict_sample_file));
		PrintWriter dict_sam_pw=new PrintWriter(dict_sam_fw);
		
		FileWriter docid_fw=new FileWriter(new File(docid_file));
		PrintWriter docid_pw=new PrintWriter(docid_fw);
		
		String line="";
		String word="";
		int docid=1;
		String[] seg_arr=null;
		
		String sample_line="";
		
		double[] ww=null;
		String wl="";
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("\\s+");
			if(seg_arr.length!=(vec_size+1))
			{
				continue;
			}
			word=seg_arr[0].trim();
			
			if((word==null)||(word.equals("")))
			{
				continue;
			}
			
			sample_line="";
			
			for(int j=1;j<seg_arr.length;j++)
			{
				sample_line=sample_line+(seg_arr[j].trim())+" ";
			}
			
			sample_line=sample_line.trim();
			ww=toLineWeights(sample_line);
			wl=toLineWords(ww);
			wl=wl.trim();
			
			if((wl==null)||(wl.equals("")))
			{
				continue;
			}
			dict_sam_pw.println(docid+" 1 1 1 "+wl);
			docid_pw.println(word+"\001"+docid);
			docid++;
		}
		
		fr.close();
		br.close();
		dict_sam_fw.close();
		dict_sam_pw.close();
		docid_fw.close();
		docid_pw.close();	
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

	public static void main(String[] args) throws Exception
	{
		if(args.length!=3)
		{
			System.out.println("用法:java -cp 'ewa.jar' EWADictToSample <input_file> <dict_sample_file> <docid_file>");
			System.out.println("<input_file>:输入的单词向量文件 ");
			System.out.println("<dict_sample_file> 输出的样本文件");
			System.out.println("<docid_file> 输出的文档编号文件");
			System.exit(1);
		}
		
        String input_file=args[0];
        String dict_sample_file=args[1];
        String docid_file=args[2];
        System.out.println("input_file:"+input_file);
        System.out.println("dict_sample_file:"+dict_sample_file);
        System.out.println("docid_file:"+docid_file);
        
        EWADictToSample ewa_dts=new EWADictToSample();
        ewa_dts.dictToSam(input_file, dict_sample_file, docid_file);
        
		
	}
	
}
