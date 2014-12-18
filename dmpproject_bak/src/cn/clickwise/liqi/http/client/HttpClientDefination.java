package cn.clickwise.liqi.http.client;

/**
 * httpclient的基类 
 * @author lq
 *
 */
public abstract class HttpClientDefination {

	public String server_ip;
	public int server_port;
	public String method_name;
	public abstract String getResponse(String request);
	public  void set_server_ip(String ip)
	{
	  this.server_ip=ip;	
	}
	public  void set_server_port(int port)
	{
		this.server_port=port;
	}
	public  void set_method_name(String method_name)
	{
		this.method_name=method_name;
	}
}
