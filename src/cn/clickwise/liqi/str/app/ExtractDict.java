package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import cn.clickwise.lib.string.SSO;

public class ExtractDict {

	/**
	 * 从一个文件里提取词典
	 * 
	 * @param input
	 * @param dict
	 */
	public void extract(String input, String dict) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));
			PrintWriter pw = new PrintWriter(new FileWriter(dict));

			String line = "";
			String[] ewords = null;
			HashMap<String, Boolean> ewordsHash = new HashMap<String, Boolean>();

			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}

				ewords = line2dicts(line);
				if (ewords == null) {
					continue;
				}

				for (int j = 0; j < ewords.length; j++) {
					if (!(ewordsHash.containsKey(ewords[j]))) {
						ewordsHash.put(ewords[j], true);
						pw.println(ewords[j]);
					}

				}
			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从一个文件里提取词典
	 * 
	 * @param input
	 * @param dict
	 */
	public void extractMulSource(String[] inputFiles, String dict) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(dict));
			String line = "";
			String[] ewords = null;
			HashMap<String, Boolean> ewordsHash = new HashMap<String, Boolean>();

			for (int i = 0; i < inputFiles.length; i++) {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(inputFiles[i])));
				while ((line = br.readLine()) != null) {
					if (SSO.tioe(line)) {
						continue;
					}

					ewords = line2dicts(line);
					if (ewords == null) {
						continue;
					}

					for (int j = 0; j < ewords.length; j++) {
						if (!(ewordsHash.containsKey(ewords[j]))) {
							ewordsHash.put(ewords[j], true);
							pw.println(ewords[j]);
						}

					}
				}

				br.close();
			}

			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String[] line2dicts(String line) {
		String[] fields = null;
		if (SSO.tioe(line)) {
			return null;
		}
		line = line.trim();

		fields = line.split("\001");
		if (fields.length != 3) {
			return null;
		}

		String words = "";
		words = fields[2];

		if (SSO.tioe(words)) {
			return null;
		}
		// System.out.println("words:"+words);
		words = words.trim();
		String[] tokens = words.split("\\s+");

		String token = "";
		ArrayList<String> ewords = new ArrayList<String>();
		for (int i = 0; i < tokens.length; i++) {
			if (!(isValidWord(tokens[i]))) {
				continue;
			}
			ewords.add(tokens[i]);
		}

		String[] ewordsArr = new String[ewords.size()];
		for (int i = 0; i < ewordsArr.length; i++) {
			ewordsArr[i] = ewords.get(i);
		}

		return ewordsArr;
	}

	public boolean isValidWord(String token) {
		if (SSO.tioe(token)) {
			return false;
		}
		if (token.endsWith("...")) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		String[] inputFiles = {"temp/bat4_res.txt","temp/bat6_res.txt"};
		String dict = "temp/so_dict.txt";

		ExtractDict ed = new ExtractDict();
		ed.extractMulSource(inputFiles, dict);

	}

}
