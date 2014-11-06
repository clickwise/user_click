package cn.clickwise.clickad.radiusClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.clickwise.lib.string.SSO;
import cn.clickwise.lib.time.TimeOpera;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * 查询用户的特征
 * 
 * @author zkyz
 * 
 */
public class UserQueryHandler extends Handler {

	
	//static Logger logger = LoggerFactory.getLogger(UserQueryHandler.class);

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		//logger.info(TimeOpera.getToday()+" uri:" + uri);
		String uid=SSO.afterStr(uri.toString(), "uid=");
		query(uid,exchange);
	}

	public void query(String uid,HttpExchange exchange)
	{
		
		//Key key=new Key(uid);
		try{
		
		Headers headers=exchange.getResponseHeaders();
		//exchange.getResponseHeaders().add("Content-type", "text/plain; charset=utf-8");
		
		headers.set("Content-type", "text/plain; charset=utf-8");

		String resultstr="";
        String ip=jedis.get(uid);
        
        if(ip==null)
        {
          ip="none";	
        }
        
		resultstr+=(ip.toString()+"\n");
		

		exchange.sendResponseHeaders(200, (long)(resultstr.getBytes().length));
		OutputStream os = exchange.getResponseBody();
		os.write(new String(resultstr).getBytes());

		os.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}



	
}
