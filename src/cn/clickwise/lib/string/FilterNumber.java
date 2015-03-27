package cn.clickwise.lib.string;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class FilterNumber {

	public static boolean isDebug = true;

	public void filter_number(String separator, int field_num, int sindex,
			boolean isdebug) {
		
		isDebug = isdebug;
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String[] fields = null;
		String line = "";

		String word = "";
		try {
			while ((line = br.readLine()) != null) {
				fields = line.split(separator);
				if (fields.length != field_num) {
					continue;
				}

				word = fields[sindex];
				if (Pattern.matches("[\\d\\.]*", word)) {
					continue;
				}
				pw.println(line);
			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
	
		if (args.length != 4) {
			System.err.println("Usage:<field_num> <sindex> <separator> <is_debug>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    sindex: 过滤的字段索引，从0开始");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    is_debug:是否进行调试");
			
			System.exit(1);
		}
		
		int field_num=0;
		int sindex=0;
		
		// 字段间的分隔符:001 表示 \001
		// :blank 表示\\s+ 即连续空格
		String separator = "";
		String outputSeparator = "";
		
		field_num = Integer.parseInt(args[0]);
		sindex = Integer.parseInt(args[1]);
		
		if (args[2].equals("001")) {
			separator = "\001";
			outputSeparator = "\001";
		} else if (args[2].equals("blank")) {
			separator = "\\s+";
			outputSeparator = "\t";
		} else if (args[2].equals("tab")) {
			separator = "\t";
			outputSeparator = "\t";
		} else {
			separator = args[2].trim();
			outputSeparator = separator.trim();
		}

		boolean isDebug=Boolean.parseBoolean(args[3]);
		
		FilterNumber fn=new FilterNumber();
		fn.filter_number(outputSeparator, field_num, sindex, isDebug);
		
		
		
	}

}
