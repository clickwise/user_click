package cn.clickwise.clickad.tag;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {

	private MaxentTagger tagger;

	public PosTagger(String model) {

		try {
         tagger = new MaxentTagger(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String tag(String text)
	{	
		return tagger.tagString(text);
	}
	
	public static void main(String[] args) throws Exception
	{
		PosTagger posTagger=new PosTagger("models/chinese-distsim.tagger");
		String text="北京 时间 8 月 27 日 晚 ， 2014 赛季 亚 冠 联赛 1 / 4 决赛 第二 回合 展开 争夺 ， 广州 恒 大 [ 微博 ] 主场 2-1 战胜 西悉尼 流浪者 ， 但 因 客场 进球 少 被 淘汰 。 著名 足球 评论员 黄健翔 [ 微博 ] 在 新浪 体育 对 本场 比赛 进行 了解 说 ， 黄健翔 认为 对方 布里奇 制造 的 点球 不 该 判 ， 恒 大 运气 不 好 裁判 手 太 松 。";
	    System.out.println(posTagger.tag(text));
		BufferedReader reader= new BufferedReader(new InputStreamReader(System.in));
		String line=null;
		System.out.println("input one sentence:");
		
		try {
			while ((line=reader.readLine())!=null) {
				System.out.println("sentence:"+line);
				System.out.println("tag sentence:"+posTagger.tag(line));
				System.out.println("input one sentence:");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
