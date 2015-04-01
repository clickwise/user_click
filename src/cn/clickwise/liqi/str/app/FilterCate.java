package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.lib.string.SSO;

public class FilterCate {

	public HashMap<String,String> host_cates=new HashMap<String,String>();
	
	public void loadCate(String cateFile)
	{
		try{
			BufferedReader br=new BufferedReader(new FileReader(cateFile));
			String line="";
			
			String[] fields=null;
			String host="";
			String cate="";
			
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				fields=line.split("\001");
			
				if(fields.length!=2)
				{
					continue;
				}
				
				host=fields[0].trim();
				cate=fields[1].trim();
				host_cates.put(host, cate);
			}
			
			br.close();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void filterCate(String cate,String inputFile,String outputFile)
	{
		try{
			String host="";
			String tcate="";
			
			BufferedReader br=new BufferedReader(new FileReader(inputFile));
			PrintWriter pw=new PrintWriter(new FileWriter(outputFile));
			
			String[] fields=null;
			String line="";
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				fields=line.split("\001");
				if(fields.length!=3)
				{
					continue;
				}
				host=fields[0];
				if(SSO.tioe(host))
				{
					continue;
				}
				
				
				host=host.trim();
				tcate=host_cates.get(host);
				if(SSO.tioe(tcate))
				{
					continue;
				}
				
				if(tcate.equals(cate))
				{
					pw.println(line.replaceAll("\001", "\t"));
				}
			}
			
			
			br.close();
			pw.close();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)
	{
		
		if(args.length!=4)
		{
			System.err.println("Usage:<cate> <host_file> <input> <output>");
			System.err.println("    cate : 选择类别");
			System.err.println("    host_file :已知host分类 文件");
			System.err.println("    input: host 流量文件");
			System.err.println("    output:输出文件");
			System.exit(1);
		}
		
		
		String cate="";
		String host_file="";
		String input="";
		String output="";
		
		cate=args[0];
		host_file=args[1];
		input=args[2];
		output=args[3];
		
		FilterCate fc=new FilterCate();
		fc.loadCate(host_file);
		fc.filterCate(cate, input,output);
		
		
	}
	
}
