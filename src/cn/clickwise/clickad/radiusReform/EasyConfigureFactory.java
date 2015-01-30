package cn.clickwise.clickad.radiusReform;



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

	@Override
	public OnlineDatabase getOnlineDatabase() {
			return new RedisOnlineDatabase();
	}
	
}
