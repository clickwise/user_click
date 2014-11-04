package cn.clickwise.lib.string;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;



public class FilterHost {

	public void filter(int field_num,int field_index, String separator, String input,
			String host, String output) {
	
		try {
			String[] hosts = FileToArray.fileToDimArr(host);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(input)));
			PrintWriter pw=new PrintWriter(new FileWriter(output));
			
			String line="";
			String[] tokens=null;
			String key="";
		
			
			while((line=br.readLine())!=null)
			{
				tokens=line.split(separator);
				if(tokens.length!=field_num)
				{
					continue;
				}
				key=tokens[field_index];
				//System.out.println("key:"+key);
				if(SSO.tioe(key))
				{
					continue;
				}
				
				boolean isIn=false;
				for(int j=0;j<hosts.length;j++)
				{
					if((key.indexOf(hosts[j].trim()))>-1)
					{
						isIn=true;
						break;
					}
				}
	
				if(isIn==true)
				{
					//System.out.println("sel line:"+line);
					pw.println(line);
				}
				
			}
			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length != 6) {
			System.err
					.println("Usage:<filed_num> <field index> <separator > <input_file> <host_file> <output_file>");
			System.exit(1);
		}
        int field_num=0;
		int field_index = 0;
		String sepInput = "";
		String separator = "\001";
		String input = "";
		String host = "";
		String output = "";
		field_num = Integer.parseInt(args[0]);
		
		field_index = Integer.parseInt(args[1]);
		sepInput = args[2].trim();
		if (sepInput.equals("001")) {
			separator = "\001";
		} else if (sepInput.equals("tab")) {
			separator = "\t";
		} else {
			separator = sepInput;
		}

		input = args[3];
		host = args[4];
		output = args[5];

		FilterHost fh = new FilterHost();
		fh.filter(field_num,field_index, separator, input, host, output);
	}

}
