package cn.clickwise.liqi.str.app;

import java.net.*;   
import java.io.*;   
import java.net.HttpURLConnection;   
import java.net.URLEncoder;   
class UrlClient {   
       
    public static final HttpURLConnection openConnection(URL url, boolean in, boolean out,String requestMethode) throws IOException{   
        HttpURLConnection con = (HttpURLConnection) url.openConnection ();   
        con.setDoInput(in);   
        con.setDoOutput (out);   
        if(requestMethode == null){   
            requestMethode = "GET";   
        }   
        con.setRequestMethod(requestMethode);   
        con.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");   
        return con;   
    }   
       
    public static final String getCookies(HttpURLConnection con){ // method for getting the cookies   
        try {   
            con.connect();   
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
        String headerName=null; String cookie=null;    
        for (int i=1; (headerName = con.getHeaderFieldKey(i))!=null; i++) {   
            if (headerName.equals("Set-Cookie"))                  
             cookie = con.getHeaderField(i);               
        }   
        cookie = cookie.substring(0, cookie.indexOf(";"));   
        String cookieName = cookie.substring(0, cookie.indexOf("="));   
        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());   
        System.out.println(cookieName);   
        System.out.println(cookieValue);   
           
        return cookieName+"="+cookieValue;   
    }   
    public static void POST_Req(HttpURLConnection conn, String cookieHeader)   
    {   
        try {   
                // Encoding Login Data   
               
                String data=URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("Ulm.Mule", "UTF-8") +   
                "&" + URLEncoder.encode("password", "UTF-8") + URLEncoder.encode("123456", "UTF-8");   
                   
                // Posting the login data to the server   
                                   
                conn.setRequestProperty("cookie", cookieHeader);                   
                OutputStreamWriter writer=new OutputStreamWriter(conn.getOutputStream());   
                writer.write(data);   
                writer.flush();        
                   
                //Reading response of the server                   
                   
                BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));   
                String response;   
                while ((response=reader.readLine())!=null) {   
                    System.out.println(response);   
                }   
                writer.close();   
                reader.close();   
            } catch (IOException e) {   
                   
                e.printStackTrace();   
            }   
               
    }   
       
    public static void main(String[] args) throws IOException   
    {   
        // connection for getting the cookies   
        URL con=new URL("http://192.168.110.186:9009/r/q.jpg?add");   
        HttpURLConnection test=openConnection(con, true, true, "GET");   
        System.out.println(); 
        try {   
            test.connect();   
            System.out.println(test.getContent()+"");
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
        
        
        
        //Another connection for POST Request   
        /*
        URL con2=new URL("http://address/do_login");   
        HttpURLConnection test2=openConnection(con2, true, true, "POST");   
        POST_Req(test2, getCookies(test2));
        */   
    }   
       
       
}
