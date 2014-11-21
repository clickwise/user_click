package cn.clickwise.clickad.profile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.jmlp.str.app.SetJar;

public class FilePredict {

	private SetJar sd = null;

	private ProfilePredict dpp = null;

	public FilePredict() {
		init();
	}

	public void init() {
		sd = new SetJar();
		sd.init_first();
		sd.init_second();

		dpp = new DictProfilePredict();
		dpp.loadKnowledge();
	}

	public String pline(String line) {

		String seg = "";

		Profile pro = null;
		try {
			seg = sd.double_seg(line);
			System.err.println("seg line:"+seg);
			pro = dpp.predict(new User(seg));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (pro == null) {
			return "";
		}

		return pro.toString();
	}

	public static void main(String[] args) {

		if (args.length != 3) {
			System.err
					.println("Usage:<field_num> <key_field_index> <separator>");
			System.err.println("    field_num : 输入的字段个数");
			System.err
					.println("    key_field_index: 要进行关键词提取的字段编号，从0开始，即0表示第一个字段");
			System.err
					.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格,tab 表示 \t");
			System.exit(1);
		}

		// 输入的字段个数用
		int fieldNum = 0;

		// 待分词的字段编号
		int keyFieldIndex = 0;

		// 字段间的分隔符:001 表示 \001
		// :blank 表示\\s+ 即连续空格
		String separator = "";
		String outputSeparator = "";

		fieldNum = Integer.parseInt(args[0]);
		keyFieldIndex = Integer.parseInt(args[1]);
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
		
		String file="host_content.txt";

		System.err.println("fieldNum:"+fieldNum+" keyFieldIndex:"+keyFieldIndex+" sep:"+args[2]);
		FilePredict fp = new FilePredict();
		System.err.println("finishing init");
		try {
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		String[] fields = null;

		
			while ((line = br.readLine()) != null) {

				try {
					System.err.println("process line :" + line);
					System.err.flush();
					fields = line.split(separator);
					if (fields.length != fieldNum) {
						continue;
					}

					for (int j = 0; j < keyFieldIndex; j++) {
						pw.print(fields[j] + outputSeparator);
					}

					if (keyFieldIndex < (fieldNum - 1)) {
						pw.print(fp.pline(fields[keyFieldIndex]).trim()
								+ outputSeparator);
					} else {
						pw.print(fp.pline(fields[keyFieldIndex]).trim());
					}

					for (int j = keyFieldIndex + 1; j < fieldNum - 1; j++) {
						pw.println(fields[j] + outputSeparator);
					}

					if (keyFieldIndex < (fieldNum - 1)) {
						// pw.print(ke.keyword_extract_noun(fields[fieldNum-1]));
						pw.print(fields[fieldNum - 1]);
					}
					pw.println();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			isr.close();
			osw.close();
			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
