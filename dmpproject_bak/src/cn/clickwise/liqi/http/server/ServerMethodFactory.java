package cn.clickwise.liqi.http.server;

public class ServerMethodFactory {

	/**
	 * 根据name获得相应的处理方法
	 * @param name
	 * @return
	 */
	public static ServerMethod getServerMethod(String name)
	{
		
		ServerMethod sermet=null;
		if(name.equals("hive"))
		{
			sermet=new HiveServerMethod();
		}
		else if(name.equals("host_find"))
		{
			
		}
		return sermet;
	}
	
}
