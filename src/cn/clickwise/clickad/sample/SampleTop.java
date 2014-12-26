package cn.clickwise.clickad.sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

public class SampleTop {

	public void randomTop(ArrayList<String> docs,int field_num,int label_field_index, String separator,int top,String outputFile)
	{
		HashMap<String,ArrayList<String>> docsHash=new HashMap<String,ArrayList<String>>();
		String doc="";
		String[] fields=null;
		String label="";
		String text="";
		
		for(int i=0;i<docs.size();i++)
		{
			doc=docs.get(i);
			if(SSO.tioe(doc))
			{
				continue;
			}
			doc=doc.trim();
			fields=doc.split(separator);
			if((fields==null)||(fields.length<field_num))
			{
				continue;
			}
			
			label=fields[label_field_index];

			if(SSO.tioe(label))
			{
				continue;
			}
			label=label.trim();
			if(!(docsHash.containsKey(label)))
			{
				docsHash.put(label, new ArrayList<String>());
				docsHash.get(label).add(doc);
			}
			else
			{
				docsHash.get(label).add(doc);
			}	
		}
		
		try{
			PrintWriter pw=new PrintWriter(new FileWriter(outputFile));
			double threshold=0;
			double random=0;
			for(Map.Entry<String, ArrayList<String>> doce:docsHash.entrySet())
			{
				threshold=(double)top/(double)(doce.getValue().size());	
				for(int j=0;j<doce.getValue().size();j++)
				{
					random=Math.random();
					if(random<threshold)
					{
						pw.println(doce.getValue().get(j));
					}
				}
			}
			pw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		if (args.length != 5) {
			System.err.println("Usage:<field_num>  <label_field_index> <top>  <separator> <output> ");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    label_field_index: 标记所在的字段，从0开始，即0表示第一个字段");
			System.err.println("    top: 每个标记保留的最多样本数");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    output:结果写入的文件");
			System.exit(1);
		}

		int field_num = 0;
		int label_field_index = 0;
		int top;
		String separator = "";
		String outputSeparator = "";
		String outputFile="";

		field_num = Integer.parseInt(args[0]);
		label_field_index = Integer.parseInt(args[1]);
		top= Integer.parseInt(args[2]);
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

		outputFile=args[4];
		try {
			//InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(new FileReader("temp/tb/tb_goods_short_mod_rearch1224.txt"));
			String line = "";
			String[] fields = null;
			ArrayList<String> docs = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}
				line = line.trim();
				docs.add(line);
			}

			//isr.close();
			br.close();
			SampleTop st=new SampleTop();
			st.randomTop(docs, field_num, label_field_index, outputSeparator, top, outputFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
