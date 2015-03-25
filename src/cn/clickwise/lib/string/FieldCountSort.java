package cn.clickwise.lib.string;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.sort.SortStrArray;

/**
 * 
 * 综合每行的关键词，加和相同关键词的pv、ip，按pv排序
 * 
 * @author zkyz
 */
public class FieldCountSort {

	public static boolean isDebug = false;

	public void sumAndSort(String separator, int field_num, int sindex) {

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		String[] fields = null;
		HashMap<String, Integer>[] ws = new HashMap[field_num - 1];
		for (int i = 0; i < ws.length; i++) {
			ws[i] = new HashMap<String, Integer>();
		}

		int j = 0;

		String key = "";
		int num = 0;

		try {
			while ((line = br.readLine()) != null) {

				try {
					fields = line.split(separator);
					if (fields.length < field_num) {
						continue;
					}

					key = fields[0];
					if (SSO.tioe(key)) {
						continue;
					}
					key = key.trim();

					if (isDebug == true) {
						System.err.println("fields.length:" + fields.length
								+ " key:" + key);
					}

					for (j = 1; j < field_num; j++) {
						num = Integer.parseInt(fields[j]);
						if (!(ws[j - 1].containsKey(key))) {
							ws[j - 1].put(key, num);
						} else {
							ws[j - 1].put(key, ws[j - 1].get(key) + num);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (isDebug == true) {
				for (j = 1; j < field_num; j++) {
					System.err.println("ws[" + j + "]:" + ws[j - 1].size());
				}
			}

			ArrayList<String> wlist = new ArrayList<String>();

			String pline = "";
			String[] tokens = null;

			for (Map.Entry<String, Integer> e : ws[0].entrySet()) {
				pline = e.getKey() + "\001" + e.getValue() + "\001";
				for (j = 2; j < field_num; j++) {
					if (ws[j - 1].containsKey(e.getKey())) {
						pline = pline + ws[j - 1].get(e.getKey());
					}
				}

				pline = pline.trim();
				tokens = pline.split("\001");
				if (tokens.length != field_num) {
					continue;
				}
				wlist.add(pline);
			}

			if (isDebug == true) {
				System.err.println("wlist.size:" + wlist.size());
			}

			String[] sort_arr = null;
			sort_arr = SortStrArray.sort_List(wlist, sindex, "int", field_num,
					"\001");
			for (j = 0; j < sort_arr.length; j++) {
				pw.println(sort_arr[j]);
			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Usage:<field_num> <sort_index> <separator>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    sort_index: 排序的字段索引，从0开始");
			System.err
					.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.exit(1);
		}

		// 字段间的分隔符:001 表示 \001
		// :blank 表示\\s+ 即连续空格
		String separator = "";
		String outputSeparator = "";

		int field_num = 0;
		int sort_index = 0;

		field_num = Integer.parseInt(args[0]);
		sort_index = Integer.parseInt(args[1]);

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

		FieldCountSort fcs = new FieldCountSort();
		fcs.sumAndSort(separator, field_num, sort_index);

	}

}
