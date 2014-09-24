package cn.clickwise.rpc;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class EasyServer extends Server{

	private Configuration conf;
	
	private ConfigureFactory confFactory;
	
	public void init()
	{
		confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
		conf=confFactory.getConfigure();
	}
	
	@Override
	public void run() {
		
		try {
			HttpServer hs = HttpServer.create(new InetSocketAddress(Integer.parseInt(conf.getHost())), 0);

			// hander
		
			Context[] contexts=null;
			
			//hs.createContext("/seg", ansj_handler);

			//TestHandler test_handler = new TestHandler();
			//hs.createContext("/test", test_handler);

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
	
	
	

}
