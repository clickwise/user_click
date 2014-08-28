package cn.clickwise.liqi.str.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;

public class DictNormalize {

	public void normalize(File input_file, File output_file) {
		Map<String, Integer> dict = new HashMap<String, Integer>();
		try {
			PrintWriter pw = FileWriterUtil.getPWFile(output_file);

			String[] tokens = FileToArray.fileToDimArr(input_file);
			String[] words = null;
			for (int i = 0; i < tokens.length; i++) {
				words = tokens[i].split("\\s+");
				for (int j = 0; j < words.length; j++) {
					if ((words[j].length() < 15)&&(words[j].length() > 1)
							&& !(dict.containsKey(words[j]))) {
						dict.put(words[j], 1);
					}
				}

			}

			for (Map.Entry<String, Integer> word : dict.entrySet()) {
				pw.println(word.getKey());
			}
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		DictNormalize dn=new DictNormalize();
		dn.normalize(new File("temp/seg_test/ecw.txt"), new File("temp/seg_test/ecwn.txt"));
	}

}
