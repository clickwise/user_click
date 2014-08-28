package cn.clickwise.clickad.rank;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.sort.SortStrArray;

/**
 * TextRank 算法
 * 
 * @author zkyz
 * 
 */
public class TextRank {

	private Map<String, WordNode> v_set=new HashMap<String,WordNode>();

	private double d=0.85;
	
	public void initGraph(File dict_file) {
		try {
			String[] dicts = FileToArray.fileToDimArr(dict_file);
			for(int i=0;i<dicts.length;i++)
			{
				if(!v_set.containsKey(dicts[i]))
				{
					v_set.put(dicts[i], new WordNode(dicts[i],Math.random()));
				}
				
			}
			
		} catch (Exception e) {
            
		}
	}

	public void graphFromFile(File input_file) {
		BufferedReader br = FileReaderUtil.getBufRed(input_file);
		String line = "";

		SentenceGraph sg = new SentenceGraph(new File("temp/seg_test/ecw.txt"));
		List<List<GraphNode>> startNodes = null;
		List<List<GraphNode>> endNodes = null;

    	List<GraphNode> sns=null;
    	List<GraphNode> ens=null;
    	
    	String preWord;
    	String nextWord;
    	
    	WordNode preWordNode;
        WordNode nextWordNode;
		try {

			while ((line = br.readLine()) != null) {
				sg.sen2graph(line);
				startNodes = sg.getStartNodes();
				endNodes = sg.getEndNodes();
                for(int i=1;i<line.length()-1;i++)
                {
                	sns=startNodes.get(i);
                	ens=startNodes.get(i);
                	for(int j=0;j<ens.size();j++)
                	{
                		preWord=ens.get(j).getName();
                		preWordNode=v_set.get(preWord);
                		if(preWordNode==null)
                		{
                			continue;
                		}
                		
                		for(int k=0;k<sns.size();k++)
                		{
                			nextWord=sns.get(k).getName();
                			nextWordNode=v_set.get(nextWord);
                    		if(nextWordNode==null)
                    		{
                    			continue;
                    		}
                			preWordNode.getOutNodes().add(nextWordNode);
                			nextWordNode.getInNodes().add(preWordNode);                			
                		}
                	}              		
                }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shrinkGraph()
	{
		
		Set<String> keys=v_set.keySet();
		Iterator<String> it=keys.iterator();
		WordNode wn=null;
		String key="";
		List<String> key_list=new ArrayList<String>();
	    System.out.println("before shrink:"+v_set.size());
		while(it.hasNext())
		{
			key=it.next();
			key_list.add(key);
		}
		
		for(int i=0;i<key_list.size();i++)
		{
			key=key_list.get(i);
			wn=v_set.get(key);
			if((wn.getInNodes().size()<1)||(wn.getOutNodes().size()<1))
			{
				v_set.remove(key);
			}
		}
		
		System.out.println("after shrink:"+v_set.size());
	}
	
	public void updateRank()
	{
		double score=0;
		double inscore=0;
		WordNode inNode;
		for(Map.Entry<String, WordNode> wn:v_set.entrySet())
		{
           List<WordNode> inNodes=wn.getValue().getInNodes();
           score=(1-d);
           inscore=0;
		   for(int i=0;i<inNodes.size();i++)
		   {
			   inNode=inNodes.get(i);
			   inscore+=inNode.getScore()/(double)(inNode.getOutNodes().size());
		   }
          
          score+=d*inscore; 
          wn.getValue().setScore(score);    
		}
	}
	
	public void updateToConvergence()
	{
		
		for(int i=0;i<100;i++)
		{
			System.out.println("loop "+i);
			updateRank();
		}
	}
	
	
	public void printRank(File output_file)
	{
		PrintWriter pw=FileWriterUtil.getPWFile(output_file);
		ArrayList<String> textRanks=new ArrayList<String>();
		
		for(Map.Entry<String, WordNode> wn:v_set.entrySet())
		{
          //pw.println(wn.getKey()+" "+wn.getValue().getScore());
			textRanks.add(wn.getKey()+"\001"+wn.getValue().getScore());
		}
		
		String[] sortTextRanks=SortStrArray.sort_List(textRanks, 1, "dou", 2, "\001");
		
		for(int i=0;i<sortTextRanks.length;i++)
		{
			pw.println(sortTextRanks[i]);
		}
		
		pw.println();
		
	}
	
	
	public static void main(String[] args) throws Exception
	{
		if(args.length!=2)
		{
			System.err.println("Usage:<se_words> <rank_file>");
			System.exit(1);
		}
		
		String se_words=args[0];
		String rank_file=args[1];
		File se_dir=new File(args[0]);
		File[] se_files=se_dir.listFiles();
		for(int i=0;i<se_files.length;i++)
		{
		   if(se_files[i].getName().indexOf("00")>-1)
		   {
			   se_words=se_files[i].getAbsolutePath();
		   }
		}
		
		TextRank textRank=new TextRank();
		textRank.initGraph(new File(se_words));
		textRank.graphFromFile(new File(se_words));
		textRank.shrinkGraph();
		textRank.updateToConvergence();
		textRank.printRank(new File(rank_file));
		
	}
	
	
}
