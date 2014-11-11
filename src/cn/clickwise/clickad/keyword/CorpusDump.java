package cn.clickwise.clickad.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import love.cq.util.MapCount;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.DSCONV;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.sort.SortStrArray;

/**
 * 从语料库中分析单词的统计特征
 * 
 * @author zkyz 
 */
public class CorpusDump {

	public void tf_idf(File input_file, File output_file) {
		
		MapCount<String> idfs = new MapCount<String>();
		BufferedReader br = FileReaderUtil.getBufRed(input_file);
		String line = "";

		String[] tokens = null;
		String[] uniq_tokens = null;
		Map<String,Integer> wc=null;
		try {
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				tokens = line.split("\\s+");
				uniq_tokens = DSCONV.deduplicate(tokens);

				for (String token : uniq_tokens) {
					idfs.add(token);
				}
			}

			br.close();
			br = FileReaderUtil.getBufRed(input_file);			
			PrintWriter pw = FileWriterUtil.getPWFile(output_file);
			
			ArrayList<String> words_list=new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				tokens = line.split("\\s+");
				wc = DSCONV.wordCount(tokens);
				
				words_list=new ArrayList<String>();
				for (Map.Entry<String,Integer> word:wc.entrySet()) {
					words_list.add(word.getKey()+":"+(double)word.getValue()/(double)(idfs.get().get(word.getKey()))+" ");
				}
					
				String[] words_sort=SortStrArray.sort_List(words_list, 1, "dou", 2,":");
				for(int i=0;i<words_sort.length;i++)
				{
					pw.print(words_sort[i]+" ");		
				}
				pw.println();
			}
				
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args)
	{
		CorpusDump corpusDump=new CorpusDump();
		corpusDump.tf_idf(new File("temp/seg_test/tb_test_bat.txt"), new File("temp/seg_test/tfidf.txt"));
		
	}
	

}
