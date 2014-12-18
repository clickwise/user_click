package cn.clickwise.liqi.mark;

public class PrintUtil {

	public static void printNoZero(double[] arr)
	{
		String pstr="";
		
		for(int i=0;i<arr.length;i++)
		{
		  if(arr[i]!=0)
		  {
			  pstr+=(i+":"+arr[i]+" ");
		  }
		}
		System.out.println(pstr);
	}
}
