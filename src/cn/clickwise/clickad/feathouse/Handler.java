package cn.clickwise.clickad.feathouse;

import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler{

	public abstract void setCassandraQuery(CassandraQuery cq);
}
