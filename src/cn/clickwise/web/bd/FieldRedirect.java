package cn.clickwise.web.bd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

public class FieldRedirect {
	
	public static boolean isDebug = true;
	
	public void redirectBat(String separator, int field_num, int rindex,boolean isdebug) {
		
		isDebug=isdebug;
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);
		String line = "";
		String[] fields = null;
		
		String url="";
		String rurl="";
		try {
			
			String nline="";
			while((line=br.readLine())!=null)
			{
				fields=line.split(separator);
				if(fields.length!=field_num)
				{
					continue;
				}
				
				url=fields[rindex];
				rurl=Redirect.getRedirect(url);
				if(SSO.tioe(rurl))
				{
					continue;
				}
				
				nline="";
				for(int j=0;j<rindex;j++)
				{
					nline+=(fields[j]+separator);
				}
				
				nline+=(rurl+separator);
				
				for(int j=rindex+1;j<field_num;j++)
				{
					nline+=(fields[j]+separator);
				}
				nline=nline.trim();
				pw.println(nline);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)
	{
		if (args.length != 4) {
			System.err.println("Usage:<field_num> <red_index> <separator> <is_debug>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    red_index: 重定向的字段索引，从0开始");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    is_debug:是否进行调试");
			
			System.exit(1);
		}
		
		// 字段间的分隔符:001 表示 \001
				// :blank 表示\\s+ 即连续空格
				String separator = "";
				String outputSeparator = "";

				int field_num = 0;
				int red_index = 0;

				field_num = Integer.parseInt(args[0]);
				red_index = Integer.parseInt(args[1]);

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
				FieldRedirect fr=new FieldRedirect();
				fr.redirectBat(outputSeparator, field_num, red_index,  isDebug);
	}
}
