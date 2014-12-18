package cn.clickwise.liqi.datastructure.sqs;

/*
 * 不包括keep-alive的部分，get方法只能获得数据，无法获得pos
 * 作者：李博 lb13810398408@gmail.com
 */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import cn.clickwise.liqi.str.edcode.Base64Code;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class Httpsqs_client {
        private String server, port, charset;

        public Httpsqs_client(String server, String port, String charset) {
                this.server = server;
                this.port = port;
                this.charset = charset;
        }
       
        private String doprocess(String urlstr) {
                URL url = null;
                try {
                        url = new URL(urlstr);
                } catch (MalformedURLException e) {
                        return "The httpsqs server must be error";
                }
                try {
                        BufferedReader instream = new BufferedReader(new InputStreamReader(url.openStream()));
                        String s = null;
                        StringBuffer result = new StringBuffer("");
                       
                        while((s = instream.readLine()) != null)
                        {
                                result.append(s);
                        }
                        instream.close();
                        return result.toString();
                } catch (IOException e) {
                        return "Get data error";
                }
        }
       
        public String maxqueue(String queue_name, String num) {
                String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + queue_name + "&opt=maxqueue&num=" + num;
                String result = null;
               
                result = this.doprocess(urlstr);
                return result;
        }
       
        public String reset(String queue_name) {
                String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + queue_name + "&opt=reset";
                String result = null;
               
                result = this.doprocess(urlstr);
                return result;
        }
       
        public String view(String queue_name, String pos) {
                String urlstr = "http://" + this.server + ":" + this.port + "/?charset=" + this.charset + "&name=" + queue_name + "&opt=view&pos=" + pos;
                String result = null;
               
                result = this.doprocess(urlstr);
                return result;
        }
       
        public String status(String queue_name) {
                String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + queue_name + "&opt=status";
                String result = null;
               
                result = this.doprocess(urlstr);
                return result;
        }
       
        public String get(String queue_name) {
                String urlstr = "http://" + this.server + ":" + this.port + "/?charset=" + this.charset + "&name=" + queue_name + "&opt=get";
                String result = null;
               
                result = this.doprocess(urlstr);
                return result;
        }

        public String put(String queue_name, String data) {
                String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + queue_name + "&opt=put";
                URL url = null;
                try {
                        url = new URL(urlstr);
                } catch (MalformedURLException e) {
                        return "The httpsqs server must be error";
                }

                URLConnection conn = null;

                try {
                        conn = url.openConnection();
                        conn.setDoOutput(true);
                        OutputStreamWriter out = null;
                        out = new OutputStreamWriter(conn.getOutputStream());
                        out.write(data);
                        out.flush();
                        out.close();
                } catch (IOException e) {
                        return "Put data error";
                }

                BufferedReader reader = null;
               
                try {
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                                return line;
                        }
                } catch (IOException e) {
                        return "Get return data error";
                }
                return null;
        }
        
        public static void main(String[] args)
        {
        	Httpsqs_client sqs_fetch=new Httpsqs_client("42.62.29.24","1218","utf-8");
        	Httpsqs_client sqs_put=new Httpsqs_client("192.168.110.186","1218","utf-8");
        	String one_item="";
        	int index=0;
        	while(index<10)
        	{
        		//one_item=sqs_fetch.get("rstat1");
        		one_item=sqs_fetch.get("tbbd_msg");
    	    
    	        if((one_item.indexOf("HTTPSQS_GET_END")==-1))
    	        {
    	            System.out.println("one_item:"+one_item);	
    	            index++;
    	            sqs_put.put("tbbd_msg",one_item);
    	        }
                //System.out.println("decode_str:"+new String(Base64.decodeBase64(one_item.getBytes())));
        		//if(one_item.indexOf("add_bd")==-1)
        		//{
        		  
        		 // System.out.println("one_item:"+one_item); 
        		 // System.out.println(new String(Base64.decodeBase64(one_item)));
        	     // sqs_put.put("tbbd_msg",one_item);
        		//}
        	}
        	
        //	sqs_put.put("tbbd_msg","add_tbs?s=NmU4YzIyYTRhZGJkYjdkMzE0NzkxOTUyOTg1MmY4MTUJVEJTRUFSQ0gJMjAxNC0wMy0xOCAyMTo0MDowMwnpkavpm4XmqbHmn5wJ5a625bGF5bu65p2QfOS6lOmHkeeUteW3pXzmi4nmiYsJ5qmx5p cfA=");
        	
        	//sqs_put.put("tbbd_msg","add_tbs?s=ZTRkMWNmYjgyYjhlNjE1OGViMjFmN2UxODIyOTE1MTMJVEJTRUFSQ0gJMjAxNC0wMy0xOCAyMTo0MDoxNgnlj4zogqnog4zljIUJTkEJ5Y M6IKp6IOM5YyFfA= ");		
            //sqs_put.put("tbbd_msg","add_tbs?s=NmU4YzIyYTRhZGJkYjdkMzE0NzkxOTUyOTg1MmY4MTUJVEJTRUFSQ0gJMjAxNC0wMy0xOCAyMTo0MDowMwnpkavpm4XmqbHmn5wJ5a625bGF5bu65p2QfOS6lOmHkeeUteW3pXzmi4nmiYsJ5qmx5p cfA= ");		
            //sqs_put.put("tbbd_msg","add_tbs?s=NTcwMDE2MmQ5MzJmNGRmNjVlMDY2YzFiZGNiNzY5M2EJVEJTRUFSQ0gJMjAxNC0wMy0xOCAyMTo0MDowNQnolrDooaPojYnlubLoirHnp5LmnYDku7fljIXpgq4JTkEJ5bmy6IqxfOiWsOiho iNiQ= ");		
        	 
        	// sqs_put.put("tbbd_msg","add_tbs?s=YWEzOGU0NTYxMGFjZTZmZmQ1MTA1MjEzZGY1NmJlOWYJVEJTRUFSQ0gJMjAxNC0wMy0xOCAyMTo0MDowOAnnmq7luKbnlLfnnJ/nmq4r6Zi/546b5bC8CeWGheiho mFjemlsHzmnI3oo4XphY3ppbB85aWz5byP6IWw5bimL eUt W8j earuW4pgnnmq7luKZ855yf55quIOmYv eOm WwvA=");

            /*
        	String dec_str=new String(dec,Charset.forName("utf-8"));   	
        	System.out.println("dec_str:"+dec_str);
        	
        	 dec_str=UrlCode.getDecodeUrl(dec_str,"gb2312");
        	 System.out.println("dec_str2:"+dec_str);
        	 */
        }
}
