package cn.clickwise.lib.des;

import java.io.File;

public class DesTest {

	private static final int BUFSIZE=1024*50;
	private deshead DesHead;
	
	private boolean[][][] SubKey=new boolean[2][16][48];
	
	private boolean Is3DES;
	
	private char[] deskey=new char[17];//密钥缓冲区
	
	private char[] databuf=new char[BUFSIZE];
	
	
	
	public boolean Encrypt(File fh_out,File fh_in,String KeyStr)
	{
		int len,k=0,TBlock;
		
		
		
		return true;
	}
	
	private boolean SetSubKey(String KeyStr)
	{
		int len;
		if(KeyStr==null||KeyStr.length()>16||KeyStr.length()==0)
		{
			System.out.println("设置DES密钥出错：空密钥，或密钥太长!");
		}
		
		for(int i=0;i<deskey.length;i++)
		{
			deskey[i]=0;
		}
		
		for(int i=0;i<KeyStr.length();i++)
		{
			deskey[i]=KeyStr.charAt(i);
		}
		
		
		
		
		
		return true;
	}
	
	
	private void SetSubKey(PSubKey pSubKey,char[] Key)
	{
		boolean[] K=new boolean[64];
		boolean[] KL=new boolean[28];
		boolean[] KR=new boolean[28];
		
		
	}
	
}
