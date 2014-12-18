package cn.clickwise.liqi.file.utils;

import java.io.PrintWriter;

import cn.clickwise.liqi.mark.ShuffleArray;
import cn.clickwise.liqi.math.random.RandomGen;

/**
 * 大文件分割成许多小文件
 * @author lq
 *
 */
public class FileSplit {

	/**
	 * 生成训练集和测试集
	 * @param trp
	 * @param input_file
	 * @param trfile
	 * @param tefile
	 * @throws Exception
	 */
	public static void splitRandom(double trp,String input_file,String trfile,String tefile) throws Exception
	{
		String[] input_arr=FileToArray.fileToDimArr(input_file);
		double rand=0;
		ShuffleArray.shuffleStrs(input_arr);
		PrintWriter trpw=FileWriterUtil.getPW(trfile);
		PrintWriter tepw=FileWriterUtil.getPW(tefile);
		
		for(int i=0;i<input_arr.length;i++)
		{
			rand=Math.random();
			if(rand<trp)
			{
				 trpw.println(RandomGen.RandomString(50)+"\001"+input_arr[i].trim());
			}
			else
			{
				tepw.println(RandomGen.RandomString(50)+"\001"+input_arr[i].trim());
			}
		}
		
		trpw.close();
		tepw.close();	
	}
	
	
	

    /**
     * 文件分割成指定数目小文件
     * @param input_file
     * @param out_dir
     * @param split_num
     * @throws Exception
     */
	public static void splitParts(String input_file,String out_dir,int split_num) throws Exception
	{
		String[] input_arr=FileToArray.fileToDimArr(input_file);
		double rand=0;
		ShuffleArray.shuffleStrs(input_arr);
		PrintWriter[] pws=new PrintWriter[split_num];
		for(int i=0;i<pws.length;i++)
		{
			pws[i]=FileWriterUtil.getPW(out_dir+"/part_"+i+".txt");
		}
		
		int perfile_num=(int)(((double)(input_arr.length))/((double)split_num));
		for(int i=0;i<input_arr.length;i++)
		{
           for(int j=1;j<=split_num;j++)
           {
        	   if(i<(j*perfile_num))
        	   {
        		   pws[j-1].println(input_arr[i]);
        		   break;
        	   }
           }		
		}
		
		for(int i=0;i<pws.length;i++)
		{
			pws[i].close();
		}
	}
	
	
	public static void main(String[] args) throws Exception
	{
		String input_file="input/mark/train_format.txt";
		String trfile="temp/mark/host/tr_format.txt";
		String tefile="temp/mark/host/te_format.txt";
		double trp=0.7;
		splitRandom(trp,input_file,trfile,tefile);
		
		String trsplit_dir="temp/mark/host/tr_splits";
		String tesplit_dir="temp/mark/host/te_splits";
		splitParts(trfile,trsplit_dir,30);
		splitParts(tefile,tesplit_dir,20);
		
	}
	
}
