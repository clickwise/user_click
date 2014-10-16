package cn.clickwise.clickad.radiusClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class StreamLook {
	
	public void head(String input,String output,int n)
	{
		try{
			FileInputStream fis=new FileInputStream(new File(input));
			FileOutputStream fos=new FileOutputStream(new File(output));
			OutputStreamWriter osw=new OutputStreamWriter(fos);
			
			byte[] buffer=new byte[1024];
			
			byte b;
			
			int m=0;
			
			while(m<n)
			{
				m+=1024;
				fis.read(buffer);
				for(int j=0;j<1024;j++)
				{
			      b=buffer[j];
			      int v = b & 0xFF;  
			      String hv = Integer.toHexString(v);
			      if (hv.length() == 1) {
			    	  hv = '0' + hv;
			      }

			    osw.write(hv+" ");

				}
					
			}

			fis.close();
			fos.close();
			osw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
	/** 
	 * Convert hex string to byte[] 
	 * @param hexString the hex string 
	 * @return byte[] 
	 */  
	public  byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	} 
	
	 private byte charToByte(char c) {  
		    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
	 
	public static void main(String[] args)
	{
		if(args.length!=3)
		{
			System.err.println("Usage:<StreamHead> <input> <output> <n>");
			System.exit(1);
		}
		
		String input=args[0];
		String output=args[1];
		int n=Integer.parseInt(args[2]);
		
		StreamLook sh=new StreamLook();
		sh.head(input, output, n);
		
		
	}
}
