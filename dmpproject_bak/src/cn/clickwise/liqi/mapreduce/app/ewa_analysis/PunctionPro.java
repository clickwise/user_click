package cn.clickwise.liqi.mapreduce.app.ewa_analysis;

public class PunctionPro {

	
	public static void main(String[] args)
	{
		String str=",+ 噪音 4动画=======【天天特价】夏装lee男士牛仔短裤双皇冠信誉！！钱币纸币人民币收藏353#";
		str = str.replaceAll("[\\pP‘’“”]", "");
		str = str.replaceAll("[\\pS‘’“”]", "");
        System.out.println("str:"+str);
		
	}
}
