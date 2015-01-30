package cn.clickwise.clickad.radiusClient;




public class EasyConfigureFactory extends ConfigureFactory{

	@Override
	public String getPcapDirectory() {
		// TODO Auto-generated method stub
		return "parsedLogs/";
	}

	@Override
	public String getPcapFileDay(int day) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPcapFile() {
		// TODO Auto-generated method stub
		return "radius";
	}

	@Override
	public long getResetConnectionSuspend() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public RedisCenter getRedisCenter() {
		
		return new RedisCenter("127.0.0.1",6379,"10");
	}

	@Override
	public OnlineDatabase getOnlineDatabase() {
	
		return new RedisOnlineDatabase();
	}

	
	@Override
	public Context[] getContext() {

		Context c=new Context("/queryUser");
		Context c1=new Context("/queryIp");
		
		Context[] cs=new Context[2];
		
		cs[0]=c;
        cs[1]=c1;
		
		return cs;
	}

	@Override
	public Handler[] getHandler() {             
        Handler[] chs=new Handler[2];
        
        chs[0]=new UserQueryHandler();
        chs[1]=new IpQueryHandler();
 
		return chs;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 7535;
	}

	@Override
	public int getRedisPort() {
		
		return 6379;
	}

	@Override
	public int getRedisDB() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRedisIp() {
		// TODO Auto-generated method stub
		return "127.0.0.1";
	}

	@Override
	public int getRSPort() {
		// TODO Auto-generated method stub
		return 9035;
	}
	
}
