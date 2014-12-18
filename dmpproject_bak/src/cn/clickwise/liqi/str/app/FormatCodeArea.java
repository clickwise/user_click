package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import cn.clickwise.liqi.str.basic.SSO;

public class FormatCodeArea {

	public void formatChina(String input_file,String output_file,String host) throws Exception
	{
		FileReader fr=new FileReader(new File(input_file));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String line="";
		String code="";
		String area="";
		String country="";
		
		String[] seg_arr=null;
		country="中国";
		while((line=br.readLine())!=null)
		{
			if(!(SSO.tnoe(line)))
			{
				continue;
			}
			
			line=line.trim();
			seg_arr=line.split("\\s+");
			if(seg_arr.length!=2)
			{
				continue;
			}
			
			area=seg_arr[0].trim();
			code=seg_arr[1].trim();
			pw.println(code+"\001"+area+"\001"+country+"\001"+host);		
		}
		
		pw.close();
		fw.close();
		fr.close();
		br.close();		
	}
	
	
	public void formatInternational(String input_file,String output_file,String host) throws Exception
	{
		FileReader fr=new FileReader(new File(input_file));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String line="";
		String code="";
		String area="";
		String country="";
		
		String[] seg_arr=null;
		country="中国";
		while((line=br.readLine())!=null)
		{
			if(!(SSO.tnoe(line)))
			{
				continue;
			}
			
			line=line.trim();
			seg_arr=line.split("\\s+");
			if(seg_arr.length<2)
			{
				continue;
			}
			
			area=seg_arr[0].trim();
			code=seg_arr[seg_arr.length-1].trim();
			
			country="";
			
			for(int j=1;j<(seg_arr.length-1);j++)
			{
				country=country+seg_arr[j]+" ";
			}
			country=country.trim();
			
			pw.println(code+"\001"+area+"\001"+country+"\001"+host);		
		}
		
		pw.close();
		fw.close();
		fr.close();
		br.close();		
	}
	
	
	public void formatQunar(String input_file,String output_file,String host) throws Exception
	{
		FileReader fr=new FileReader(new File(input_file));
		BufferedReader br=new BufferedReader(fr);
		
		FileWriter fw=new FileWriter(new File(output_file));
		PrintWriter pw=new PrintWriter(fw);
		
		String line="";
		String code="";
		String area="";
		String country="";
		
		String[] seg_arr=null;
		country="中国";
		while((line=br.readLine())!=null)
		{
			if(!(SSO.tnoe(line)))
			{
				continue;
			}
			
			line=line.trim();
			seg_arr=line.split("\\s+");
			if(seg_arr.length<1)
			{
				continue;
			}
			
			area=seg_arr[0].trim();
			//code=seg_arr[seg_arr.length-1].trim();
			code=area;
			country="";
			
			for(int j=1;j<(seg_arr.length);j++)
			{
				country=country+seg_arr[j]+" ";
			}
			country=country.trim();
			if(SSO.tnoe(country))
			{
			pw.println(code+"\001"+area+"\001"+country+"\001"+host);
			}
			else
			{
				pw.println(code+"\001"+area+"\001unknown\001"+host);
			}
		}
		
		pw.close();
		fw.close();
		fr.close();
		br.close();		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		String host="qunar";
		
		/*
		String input_file="input/travel/china_ac.txt";
		String output_file="temp/travel/china_ac_standard.txt";
		FormatCodeArea fca=new FormatCodeArea();
		fca.formatChina(input_file, output_file,host);
		
		input_file="input/travel/inter_ac.txt";
		output_file="temp/travel/inter_ac_standard.txt";
		fca.formatInternational(input_file, output_file,host);
		*/
		/*
		String input_file="input/travel/qunar_area_decode.txt";
		String output_file="input/travel/qunar_ac.txt";
		FormatCodeArea fca=new FormatCodeArea();
		fca.formatInternational(input_file, output_file,host);
		*/
		String input_file="input/travel/part-r-00000";
		String output_file="input/travel/qunar_ac_2.txt";
		FormatCodeArea fca=new FormatCodeArea();
		fca.formatQunar(input_file, output_file,host);
	}
	
	
}
