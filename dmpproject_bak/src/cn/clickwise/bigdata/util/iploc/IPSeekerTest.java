package cn.clickwise.bigdata.util.iploc;

import java.net.UnknownHostException;

/**
 * IPSeeker测试程序
 * @author gao
 *
 */
public class IPSeekerTest {

	public static void main(String[] args) throws UnknownHostException {
		IPSeeker ips = IPSeeker.getInstance();
		
		long ip2=IP2long.ip2long("3221234342");
		
		String ip = IP2long.long2ip(ip2);
		if(args.length>1)
			ip = args[1];
		String area = ips.getAddress(ip);
		System.out.println("ip="+ip+",area="+area);
		return;
	}

}
