package cn.clickwise.clickad.cluster;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.str.basic.DS2STR;
import cn.clickwise.liqi.str.basic.SSO;


public class CateWords {

	public HashMap<String,HashMap<String,String>> cate_words=new HashMap<String,HashMap<String,String>>();
	public HashMap<String,double[]> wordMap=new HashMap<String,double[]>();
	public int vecsize=200;
	
	public void readCateWords(String file)
	{
		String[] samples = null;
		String line = "";
		String[] seg_arr = null;
		String cate = "";
		String words = "";
		String word="";
		String md5word = "";
		String[] word_arr=null;
		try {
			samples = FileToArray.fileToDimArr(file);
			for (int i = 0; i < samples.length; i++) {
				line = samples[i];
				if (SSO.tioe(line)) {
					continue;
				}

				seg_arr = line.split("\001");
				if (seg_arr.length != 2) {
					continue;
				}
				cate = seg_arr[0].trim();
				words = seg_arr[1].trim();
				//System.out.println("cate:"+cate+" words:"+words);
                word_arr=words.split("\\s+");
                HashMap<String,String> tempMap=null;
                if(!(cate_words.containsKey(cate)))
                {
                	//cate_words.put(cate, new HashMap<String,String>());
                	tempMap=new HashMap<String,String>();
                	cate_words.put(cate,tempMap);
                }
                else
                {
                	tempMap=cate_words.get(cate);
                }
                for(int j=0;j<word_arr.length;j++)
                {
                	word=word_arr[j].trim();
                	if(word.equals("NA"))
                	{
                		continue;
                	}
                	if(!(tempMap.containsKey(word)))
                	{
                		tempMap.put(word, "1");
                	}
                }
                
                cate_words.remove(cate);
                cate_words.put(cate, tempMap);
			}
		} catch (Exception e) {

		}
	}
	
	public void readModel(String file)
	{
		String[] samples = null;
		String line = "";
		String[] seg_arr = null;
		String word = "";
		String vstr = "";
		double[] v=null;

		try {
			samples = FileToArray.fileToDimArr(file);
			for (int i = 0; i < samples.length; i++) {
				line = samples[i];
				if (SSO.tioe(line)) {
					continue;
				}

				seg_arr = line.split("\001");
				if (seg_arr.length != 2) {
					continue;
				}
				word = seg_arr[0];
				vstr = seg_arr[1];
				//System.out.println("word:"+word+"  vstr:"+vstr);
				/////v = STR2DS.str2douarr(vstr);
				if (v.length != vecsize) {
					continue;
				}
				wordMap.put(word, v);		
			}
		} catch (Exception e) {

		}
	}
	
	public void write_words(String dir)
	{
		
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			String cate="";
			for(Entry<String,HashMap<String,String>> words:cate_words.entrySet())
			{
				cate=words.getKey();
				//System.out.println("cate:"+cate);
				fw = new FileWriter(new File(dir+"/"+cate+".txt"));
				pw = new PrintWriter(fw);
				double[] temp_arr=null;
				for(Entry<String,String> word:words.getValue().entrySet())
				{
					//System.out.println(+cate);
					temp_arr=wordMap.get(word.getKey());
					if(temp_arr==null)
					{
						continue;
					}
					/////pw.println(word.getKey()+"\001"+DS2STR.doublearr2str(temp_arr));
				}
				
				pw.close();
				fw.close();
			}
		
		} catch (Exception e) {

		}
	}
	
	public static void main(String[] args)
	{
		if(args.length<3)
		{
			System.err.println("Usage:<trainfile> <modelfile> <outputdir>");
			System.exit(1);
		}
		
		String trainfile=args[0];
		String modelfile=args[1];
		String outputdir=args[2];
		
		CateWords cw=new CateWords();
		cw.readCateWords(trainfile);
		cw.readModel(modelfile);
		cw.write_words(outputdir);
	}
}
