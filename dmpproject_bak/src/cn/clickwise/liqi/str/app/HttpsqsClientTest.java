package cn.clickwise.liqi.str.app;

import cn.clickwise.liqi.datastructure.sqs.Httpsqs_client;
import cn.clickwise.liqi.file.utils.FileToArray;

import com.daguu.lib.httpsqs4j.Httpsqs4j;
import com.daguu.lib.httpsqs4j.HttpsqsClient;
import com.daguu.lib.httpsqs4j.HttpsqsException;
import com.daguu.lib.httpsqs4j.HttpsqsStatus;

public class HttpsqsClientTest {
     public static void connect() throws Exception
     {
    	 
 		String sqs_ip = "192.168.110.186";
 		int sqs_port = 1218;
 		HttpsqsClient sqsclient = null;
 		HttpsqsStatus sqsstatus = null;
 		Httpsqs4j.setConnectionInfo(sqs_ip, sqs_port, "UTF-8");
 		sqsclient = Httpsqs4j.createNewClient();
 		sqsstatus = sqsclient.getStatus("bd_sw");
 		sqsclient.putString("bd_sw", "test word");
 		sqsclient.putString("bd_sw", "test word1");
 		sqsclient.putString("bd_sw", "test word2");
 		sqsclient.putString("bd_sw", "test word3");
 		//System.out.println(sqsclient.getString("bd_sw"));
 		
 		Httpsqs_client hc=new Httpsqs_client("192.168.110.186","11011","utf-8");
 		hc.put("rstat1", "test");
 		String[] msg_vals=FileToArray.fileToDimArr("input/rest_server/20140411_13.txt");
 		String line="";
 		
 		String[] seg_arr=null;
 		String cookie="";
 		String type="";
 		String info="";
 		String info_type="";
 		for(int i=0;i<msg_vals.length;i++)
 		{
 		  line=msg_vals[i];
 		  seg_arr=line.split("\t");
 		  if(seg_arr.length<2)
 		  {
 			  continue;
 		  }
 		  cookie=seg_arr[0].trim();
 		  type=seg_arr[1].trim();
 		  info_type=getType(type);
 		  info="";
 		  for(int j=2;j<seg_arr.length;j++)
 		  {
 			  info+=seg_arr[j].trim()+"\t";
 		  }
 		  info=info.trim();
 		  sqsclient.putString("tbbd_msg", info_type+line);
 		  //if(i%100==0)
 		 // {
 		  //  System.out.println(line);
 		  //}
 		}
 	 
     }
     
     public static String getType(String type)
     {
    	 if(type.equals("BAIDU"))
    	 {
    		 return "add_bd?s=";
    	 }
    	 else if(type.equals("TBSEARCH"))
    	 {
    		 return "add_tbs?s=";
    	 }
    	 
         return "";
     }
     public static void main(String[] args) throws Exception
     {
    	 connect();
     }
}
