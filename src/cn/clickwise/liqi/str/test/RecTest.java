package cn.clickwise.liqi.str.test;

public class RecTest {

	public void m1()
	{
		Rec rec=new Rec();
		System.out.println("rec1:"+rec.toString());
		setRec(rec);
		System.out.println("rec2:"+rec.toString());
		
		String tstr="08 06 0a 20 ef 1a 01 22 42 7a 70 37 51 45 6b 71 44 65 43 6a 53 50 4c 69 53 54 49 34 48 32 4c 30 30 32 49 4c 68 58 44 79 28 06 00 00 00 02";
	    System.out.println("tstr.length:"+tstr.length());
	    int ipStart=tstr.indexOf("08 06");
	    int ipEnd=ipStart+17;
	    System.out.println("ipStart="+ipStart);
	    System.out.println("ipEnd="+ipEnd); 
	    String ip=tstr.substring(ipStart, ipEnd);
	    System.out.println("ip="+ip);
	    
	    int statusStart=tstr.indexOf("28 06");
	    int statusEnd=statusStart+17;
	    System.out.println("statusStart="+statusStart);
	    System.out.println("statusEnd="+statusEnd);
	    
	    String status=tstr.substring(statusStart, statusEnd);
	    System.out.println("status="+status);
	    
        
	    String ustr=tstr.replaceFirst(ip, "").replaceFirst(status, "");
	    System.out.println("ustr:"+ustr.trim());
	    

	    
	}
	
	public void setRec(Rec rec)
	{
		rec.setIp("192.168.110.182");
		rec.setPort(9000);
	}
	
	public static void main(String[] args)
	{
	  RecTest rt=new RecTest();
	  rt.m1();
	}
	
}
