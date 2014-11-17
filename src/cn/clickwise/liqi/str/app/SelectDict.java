package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.FileToArray;
import cn.clickwise.lib.string.SSO;

/**
 * 
 * @author zkyz
 */
public class SelectDict {

	private HashMap<String, String> wordHash = new HashMap<String, String>();

	public void selectLine(String line) {
		String[] fields = line.split("\001");
		if (fields.length != 2) {
			return;
		}

		String word = fields[0];
		String text = fields[1];

		text = SSO.midstrs(text, "[", "]");
		if ((SSO.tioe(text)) || (SSO.tioe(word))) {
			return;
		}

		if (!(wordHash.containsKey(word))) {
			wordHash.put(word, "1");
		}

		String[] tokens = text.split("\\s+");
		if ((tokens == null) || (tokens.length < 1)) {
			return;
		}

		String token = "";
		for (int i = 0; i < tokens.length; i++) {
			token = tokens[i];
			if (SSO.tioe(token)) {
				continue;
			}
			if (!(wordHash.containsKey(token))) {
				wordHash.put(token, "1");
			}
		}

	}

	public void selectFile(String input) {
		try {
			String[] samples = FileToArray.fileToDimArr(input);
			for(int i=0;i<samples.length;i++)
			{
				selectLine(samples[i]);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void selectStdin() {
		try {
            String line="";
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            while((line=br.readLine())!=null)
            {
            	if(SSO.tioe(line))
            	{
            		continue;
            	}
            	
            	selectLine(line);
            }
            
            br.close();
            
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printResult(String output)
	{
		try{
			PrintWriter pw=new PrintWriter(new FileWriter(output));
			for(Map.Entry<String, String> m:wordHash.entrySet())
			{
				pw.println(m.getKey());
			}
			pw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void printStdout()
	{
		try{
			PrintWriter pw=new PrintWriter(new OutputStreamWriter(System.out));
			for(Map.Entry<String, String> m:wordHash.entrySet())
			{
				pw.println(m.getKey());
			}
			pw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		SelectDict sd=new SelectDict();
		sd.selectStdin();
		sd.printStdout();
	}
	
	
	
}
