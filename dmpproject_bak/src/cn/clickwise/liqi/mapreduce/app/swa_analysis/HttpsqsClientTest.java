package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import com.daguu.lib.httpsqs4j.Httpsqs4j;
import com.daguu.lib.httpsqs4j.HttpsqsClient;
import com.daguu.lib.httpsqs4j.HttpsqsException;
import com.daguu.lib.httpsqs4j.HttpsqsStatus;


public class HttpsqsClientTest {
	
		public static void main(String[] args) {
			try {
				Httpsqs4j.setConnectionInfo("222.85.64.100", 1218, "UTF-8");
				HttpsqsClient client = Httpsqs4j.createNewClient();
				client.putString("bd_sw", "向着炮火前进");
				HttpsqsStatus status = client.getStatus("bd_sw");
				System.out.println(status.version);
				System.out.println(status.queueName);
				System.out.println(status.maxNumber);
				System.out.println(status.getLap);
				System.out.println(status.getPosition);
				System.out.println(status.putLap);
				System.out.println(status.putPosition);
				System.out.println(status.unreadNumber);
			
				//client.putString("bd_sw", "向着炮火前进1");
				//client.putString("bd_sw", "向着炮火前进2");
				//client.putString("bd_sw", "向着炮火前进3");
				/*
				System.out.println(status.version);
				System.out.println(status.queueName);
				System.out.println(status.maxNumber);
				System.out.println(status.getLap);
				System.out.println(status.getPosition);
				System.out.println(status.putLap);
				System.out.println(status.putPosition);
				System.out.println(status.unreadNumber);
				*/
				System.out.println(client.getString("bd_sw"));
			} catch (HttpsqsException e) {
				e.printStackTrace();
			}
		}

	
}
