package cn.clickwise.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.clickwise.lib.string.SSO;

public class FieldCrawl {

	public static boolean isDebug = true;
	
	public void fieldCrawl(String separator, int field_num,int crawl_index, boolean isdebug) {
		
		isDebug=isdebug;
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);
		
		String line = "";
		String[] fields = null;
		
		String key = "";
		String pline="";
		
		
		try {
			while ((line = br.readLine()) != null) {

				try {
					fields = line.split(separator);
					if (fields.length < field_num) {
						continue;
					}

					key = fields[crawl_index];
					if (SSO.tioe(key)) {
						continue;
					}
					key = key.trim();

					if (isDebug == true) {
						System.err.println("fields.length:" + fields.length
								+ " key:" + key);
					}
					
					Document doc=null;
					try {

						//////String content=fetcher.getSourceEasyProxy(url,getProxy());
						//////doc=Jsoup.parse(content);
						if(isDebug==true)
						{
							System.err.println("crawling the url "+key);
						}
						doc = Jsoup.connect(key).get();
						if(doc==null)
						{
							continue;
						}
					} catch (Exception e) {
						//e.printStackTrace();
					}

					pline=line+separator+doc.text().replaceAll("\\n", " ");
					pw.println(pline);

				} catch (Exception e) {
					//e.printStackTrace();
				}
			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
	
	public static void main(String[] args)
	{
		if (args.length != 4) {
			System.err.println("Usage:<field_num> <crawl_index> <separator> <is_debug>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    crawl_index: 抓取的字段索引，从0开始");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    is_debug:是否进行调试");
			
			System.exit(1);
		}
		
		int field_num=0;
		int crawl_index=0;
		// 字段间的分隔符:001 表示 \001
		// :blank 表示\\s+ 即连续空格
		String separator = "";
		String outputSeparator = "";
		
		field_num = Integer.parseInt(args[0]);
		crawl_index = Integer.parseInt(args[1]);

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
		FieldCrawl fc = new FieldCrawl();
		fc.fieldCrawl(outputSeparator, field_num, crawl_index,isDebug);
		
	}
	
	
	
}
