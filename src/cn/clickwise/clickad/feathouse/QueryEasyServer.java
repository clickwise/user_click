package cn.clickwise.clickad.feathouse;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

/**
 * 接收RTB传过来的用户数据查询请求，
 * 查询cassandra数据库，返回结果
 * @author zkyz
 */
public class QueryEasyServer extends Server{

	private Configuration conf;
	
	private CassandraQuery cq;
	
	private ConfigureFactory confFactory;
	
	public void init()
	{
		confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
		
		CassandraConfigure cassConf=confFactory.getCassandraConfigure();
		cq = new CassandraQuery();
		Connection con = new Connection();
		con.setHost(cassConf.getHost());
		con.setPort(cassConf.getPort());
		con.setCfName(cassConf.getCfName());
		con.setKeySpace(cassConf.getKeySpace());
		con.setColumnName(cassConf.getColumnName());
		
		//cassandraQuery 在启动的时候应该同时启动一个定时器，获取
		//更新cq.getQuerySupervisor()
		cq.connect(con);
		
		//conf=confFactory.getConfigure();
	}
	
	@Override
	public void run() {
		init();
		try {
			HttpServer hs = HttpServer.create(new InetSocketAddress(conf.getPort()), 0);
          
			// hander	
			Context[] contexts=null;
			Handler[] handlers=null;
			contexts=confFactory.getContext();
			handlers=confFactory.getHandler();
			for(int i=0;i<handlers.length;i++)
			{
				handlers[i].setCassandraQuery(cq);
			}
			
			for(int i=0;i<contexts.length;i++)
			{
				hs.createContext(contexts[i].getName(), handlers[i]);
			}
		
			hs.setExecutor(null);
			hs.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Configuration getConf() {
		return conf;
	}
	
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}
	
	public CassandraQuery getCq() {
		return cq;
	}

	public void setCq(CassandraQuery cq) {
		this.cq = cq;
	}
	
	public static void main(String[] args)
	{
		if(args.length!=1)
		{
			System.err.println("Usage:EasyServer <port>");
			System.exit(1);
		}
		
		Configuration conf=new Configuration();
		conf.setPort(Integer.parseInt(args[0]));
		QueryEasyServer es=new QueryEasyServer();
		es.setConf(conf);
		Thread easyThread = new Thread(es);
		easyThread.start();
		
	}



}
