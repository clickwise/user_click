package cn.clickwise.liqi.file.uitls;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import love.cq.util.MapCount;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 限制输出
 * 
 * @author zkyz
 * 
 */
public class FileLimit {

	public static void limit_cate(File input_file, File output_file, int key_index,
			int field_num,String seprator,int limit) {
		BufferedReader br = FileReaderUtil.getBufRed(input_file);
		PrintWriter pw=FileWriterUtil.getPWFile(output_file);
		
		MapCount<String> mc=new MapCount<String>();
		String line = "";
		String[] fields = null;
		String key="";
		try {
			while ((line = br.readLine()) != null) {
                if(SSO.tioe(line))
                {
                	continue;
                }
				
                fields=line.split(seprator);
				if(fields.length!=field_num)
				{
					continue;
				}
                
				key=fields[key_index];
				mc.add(key);
				if(mc.get().get(key)>limit)
				{
					continue;
				}
				pw.println(fields[2].replaceAll("\\s+", ""));
			}
			
			br.close();
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public static void main(String[] args)
	{
		File input_file=new File("temp/seg_test/seg_ec_title.txt"); 
		File output_file=new File("temp/seg_test/seg_ec_title_limit.txt");
		int key_index=0;
		int field_num=3;
		String seprator="\001";
		int limit=500;
		
		FileLimit.limit_cate(input_file, output_file, key_index, field_num, seprator, limit);
	}

}
