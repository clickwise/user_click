package cn.clickwise.clickad.radiusClient;

import java.net.InetSocketAddress;

import redis.clients.jedis.Jedis;

import com.sun.net.httpserver.HttpServer;

/**
 * 接收RTB传过来的用户数据查询请求， 查询cassandra数据库，返回结果
 * 
 * @author zkyz
 */
public class QueryEasyServer extends Server {

	private ConfigureFactory confFactory;
	private int serverPort = 0;

	public void init() {
		confFactory = ConfigureFactoryInstantiate.getConfigureFactory();
	}

	@Override
	public void run() {
		init();

		try {
			HttpServer hs = HttpServer.create(
					new InetSocketAddress(serverPort), 0);

			Context[] contexts = null;
			Handler[] handlers = null;
			contexts = confFactory.getContext();
			handlers = confFactory.getHandler();
			Jedis jedis = new Jedis(confFactory.getRedisIp(),
					confFactory.getRedisPort(), 1000);// redis服务器地址
			jedis.ping();

			for (int i = 0; i < handlers.length; i++) {
				handlers[i].setJedis(jedis);
			}
			// jedis.select(10);

			for (int i = 0; i < contexts.length; i++) {
				hs.createContext(contexts[i].getName(), handlers[i]);
			}
			hs.setExecutor(null);
			hs.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ConfigureFactory getConfFactory() {
		return confFactory;
	}

	public void setConfFactory(ConfigureFactory confFactory) {
		this.confFactory = confFactory;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage:QueryEasyServer <port>");
			System.exit(1);
		}

		QueryEasyServer es = new QueryEasyServer();
		es.setServerPort(Integer.parseInt(args[0]));
		Thread easyThread = new Thread(es);
		easyThread.start();
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

}
