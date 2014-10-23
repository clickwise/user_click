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

	
	
	
}
