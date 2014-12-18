package cn.clickwise.liqi.str.app;

import cn.clickwise.liqi.str.basic.LineOpera;

public class QunarAreaDecode {
   
	public static void main(String[] args) throws Exception
	{
		String input_file="input/travel/qunar_area_code_crawl.txt";
		String output_file="temp/travel/qunar_area_decode.txt";
		String regex="\"\\s*display\\s*\"\\s*:\\s*\"(.*?)\"";
		int N=1;
		LineOpera.fieldFind(input_file, output_file, regex, N);
	}
	
	
}
