package cn.clickwise.clickad.radiusClient;

public abstract class ConfigureFactory {

	//返回pcap文件存放的目录
	public abstract String getPcapDirectory();
	
	//返回某天生成的pcap文件文件名
	public abstract String getPcapFileDay(int day);
	
	//返回pcap文件名
	public abstract String getPcapFile();
	
}
