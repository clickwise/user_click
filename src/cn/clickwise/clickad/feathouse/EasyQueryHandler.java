package cn.clickwise.clickad.feathouse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.rpc.FileCopyToCommand;

import com.sun.net.httpserver.HttpExchange;

/**
 * 查询用户的特征
 * 
 * @author zkyz
 * 
 */
public class EasyQueryHandler extends Handler {

	private CassandraQuery cq;

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		String uid = SSO.afterStr(uri.toString(), "uid=");
		query(uid,exchange);
	}

	@Override
	public void setCassandraQuery(CassandraQuery cq) {
		this.cq = cq;
	}

	public void query(String uid,HttpExchange exchange)
	{
		System.out.println("in query:"+uid);
		Key key=new Key(uid);
		List<Record> result = cq.queryUid(key);
		System.out.println("after query:"+uid);
		try{
		exchange.sendResponseHeaders(200, 0);
		OutputStream os = exchange.getResponseBody();
		
		String encode="";
		for(int j=0;j<result.size();j++)
		{
			os.write(new String((result.get(j).toString())+"\n").getBytes());
		}
		
		os.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
}
