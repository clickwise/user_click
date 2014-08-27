package cn.clickwise.liqi.file.uitls;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import cn.clickwise.liqi.str.basic.DSCONV;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.sort.SortStrArray;

public class FileReplace {

	public static void replaceSeparator(int sep_index,String sou_sep,String tar_sep,File input_file,File output_file)
	{
		BufferedReader br = FileReaderUtil.getBufRed(input_file);
		String line = "";
		PrintWriter pw = FileWriterUtil.getPWFile(output_file);
		
		String[] tokens = null;

		try {
			while ((line = br.readLine()) != null) {
				
				if (SSO.tioe(line)) {
					continue;
				}
				tokens = line.split(sou_sep);
                for(int j=0;j<=sep_index;j++)
                {
                	if(j<sep_index)
                	{
                	 pw.print(tokens[j]+sou_sep);
                	}
                	else
                	{
                	 pw.print(tokens[j]);
                	}
                }
				pw.print(tar_sep);
				
                for(int j=(sep_index+1);j<tokens.length;j++)
                {
                	if(j<tokens.length)
                	{
                	 pw.print(tokens[j]+sou_sep);
                	}
                	else
                	{
                	 pw.print(tokens[j]);
                	}
                }
                pw.println();
				
			}

			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args)
	{
		/*
		if(args.length!=5)
		{
			System.err.println("Usage: <sep_index> <sou_sep> <tar_sep> <input_file> <output_file>");
			System.exit(1);
		}
		
		
		int sep_index=Integer.parseInt(args[0]);
		String sou_sep=args[1];
		String tar_sep=args[2];
		String input_file=args[3];
		String output_file=args[4];
		*/
		
		int sep_index=0;
		String sou_sep=" ";
		String tar_sep="\001";
		String input_file="temp/seg_test/ec_title_vec.txt";
		String output_file="temp/seg_test/ec_title_vec_n.txt";
		
		FileReplace fr=new FileReplace();
		fr.replaceSeparator(sep_index, sou_sep, tar_sep, new File(input_file), new File(output_file));
		
	}
	
	
}
