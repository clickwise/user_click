package cn.clickwise.liqi.mark;

import java.util.Random;

/**
 * 打乱数组的顺序
 * @author lq
 *
 */
public class ShuffleArray {

	/**
	 *  打乱数组的顺序
	 * @param words
	 */
	public static void shuffle(WORD[] words)
	{
	    int index;
	    WORD temp;
	    Random random = new Random();
	    for (int i =  words.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        temp =  words[index];
	        words[index] =  words[i];
	        words[i] = temp;
	    }
	}
	
	/**
	 *  打乱数组的顺序
	 * @param words
	 */
	public static void shuffleStrs(String[] words)
	{
	    int index;
	    String temp;
	    Random random = new Random();
	    for (int i =  words.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        temp =  words[index];
	        words[index] =  words[i];
	        words[i] = temp;
	    }
	}
	
	
}
