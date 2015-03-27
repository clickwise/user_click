package cn.clickwise.lib.string;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class FieldSum {
	
	public static boolean isDebug = true;
	
	public void sumAndSort(String separator, int field_num, int first_index,int second_index,boolean isdebug) {

		isDebug=isdebug;
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		String[] fields = null;


		int j = 0;

		int num1 = 0;
		int num2 = 0;
		
		int sum1=0;
		int sum2=0;
		try {
			while ((line = br.readLine()) != null) {

				try {
					fields = line.split(separator);
					if (fields.length < field_num) {
						continue;
					}
					
					num1=Integer.parseInt(fields[first_index]);
					num2=Integer.parseInt(fields[second_index]);
					
					sum1+=num1;
					sum2+=num2;

				} catch (Exception e) {

					//e.printStackTrace();
				}
			}


            pw.println("sum1:"+sum1);
            pw.println("sum2:"+sum2);
			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		if (args.length != 5) {
			System.err.println("Usage:<field_num> <first_index> <second_index> <separator> <is_debug>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    first_index: 第一个加和的字段索引，从0开始");
			System.err.println("    second_index: 第二个加和的字段索引，从0开始");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    is_debug:是否进行调试");
			
			System.exit(1);
		}
		
		// 字段间的分隔符:001 表示 \001
		// :blank 表示\\s+ 即连续空格
		String separator = "";
		String outputSeparator = "";
		int field_num = 0;
		int first_index = 0;
        int second_index=0;
        
		field_num = Integer.parseInt(args[0]);
		first_index = Integer.parseInt(args[1]);
		second_index=Integer.parseInt(args[2]);

		if (args[3].equals("001")) {
			separator = "\001";
			outputSeparator = "\001";
		} else if (args[3].equals("blank")) {
			separator = "\\s+";
			outputSeparator = "\t";
		} else if (args[3].equals("tab")) {
			separator = "\t";
			outputSeparator = "\t";
		} else {
			separator = args[3].trim();
			outputSeparator = separator.trim();
		}

		boolean isDebug=Boolean.parseBoolean(args[4]);
		
		FieldSum fs=new FieldSum();
		fs.sumAndSort(outputSeparator, field_num, first_index, second_index, isDebug);
		
	}
}
