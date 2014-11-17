package cn.clickwise.clickad.profile;

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

	private HashMap<String, WORD> wordHash = new HashMap<String, WORD>();
	
	private class WORD{
		
		private String w;
		
		private int count;

		public WORD(String w,int count)
		{
			this.w=w;
			this.count=count;
		}
		public String getW() {
			return w;
		}

		public void setW(String w) {
			this.w = w;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
		
		public String toString()
		{
			String str="";
			str=w+" "+count;
			return str;
		}
		
	}

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
			wordHash.put(word, new WORD(word,1));
		}
		else
		{
			wordHash.get(word).setCount((wordHash.get(word).getCount())+1);
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
				wordHash.put(token, new WORD(token,1));
			}
			else
			{
				wordHash.get(token).setCount((wordHash.get(token).getCount())+1);
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
			for(Map.Entry<String, WORD> m:wordHash.entrySet())
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
			for(Map.Entry<String, WORD> m:wordHash.entrySet())
			{
				pw.println(m.getValue().toString());
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
