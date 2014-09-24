package cn.clickwise.rpc;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;


public class EasyServer extends Server{

	private Configuration conf;
	
	private ConfigureFactory confFactory;
	
	public void init()
	{
		confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
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
	
	
	public static void main(String[] args)
	{
		if(args.length!=1)
		{
			System.err.println("Usage:EasyServer <port>");
			System.exit(1);
		}
		
		Configuration conf=new Configuration();
		conf.setPort(2733);
		EasyServer es=new EasyServer();
		es.setConf(conf);
		Thread easyThread = new Thread(es);
		easyThread.start();
	}

}
