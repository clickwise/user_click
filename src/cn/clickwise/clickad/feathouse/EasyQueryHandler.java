package cn.clickwise.clickad.feathouse;

import java.io.IOException;
import java.net.URI;

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

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		URI uri = exchange.getRequestURI();
		System.out.println("uri:" + uri);
		String uid = SSO.afterStr(uri.toString(), "uid=");
	}
	
	

}
