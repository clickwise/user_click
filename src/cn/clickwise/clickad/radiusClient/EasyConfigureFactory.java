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
		
		return new RedisCenter("180.96.26.203",6379,"10");
	}

	@Override
	public OnlineDatabase getOnlineDatabase() {
	
		return new RedisOnlineDatabase();
	}

	
	
	
}
