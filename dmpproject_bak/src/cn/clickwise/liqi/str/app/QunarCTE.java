package cn.clickwise.liqi.str.app;

import cn.clickwise.liqi.str.basic.LineOpera;

public class QunarCTE {
	
	public static void main(String[] args) throws Exception
	{
		String input_file="input/travel/qunar_area_single_crawl2.txt";
		String output_file="temp/travel/qunar_area_cte2.txt";
		String regex="\"userInput\":\"([A-Za-z]*?)\".*?country\":\"(.*?)\".*?\"key\":\"(.*?)\"";
		int N=3;
		LineOpera.fieldFindUniq(input_file, output_file, regex, N);
		String[] add_info={"ctrip"};
		String format_file="temp/travel/qunar_area_cte_format2.txt";
		
		LineOpera.fieldAddTail( output_file,format_file, add_info);
		
	}
	
}
