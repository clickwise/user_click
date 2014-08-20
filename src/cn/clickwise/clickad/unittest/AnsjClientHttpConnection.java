package cn.clickwise.clickad.unittest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnsjClientHttpConnection extends AuxiliaryTestBase {

	@Override
	public String test(String text) {
		// TODO Auto-generated method stub

		try {
			URL url = new URL("http://192.168.110.182:8080/seg?s=");
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setDoOutput(true);
			//urlConn.setRequestProperty("Content-type","application/x-java-serialized-object");
			urlConn.setRequestProperty("Content-type","text/plain");

			// 设定请求的方法为"POST"，默认是GET
			urlConn.setRequestMethod("GET");
			urlConn.connect();
			
			OutputStream outStrm = urlConn.getOutputStream();
			// 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。
			ObjectOutputStream oos = new ObjectOutputStream(outStrm);

			String wcontent="<start> 欧美风格超性感诱惑裸色露背温泉连体比基尼出口外贸游泳衣女泳装 <br> 反季清仓包邮 COO LUXE australia 皮毛一体 两穿马丁雪地靴  <br> 送礼绝佳 世界顶级设计者品牌 华贵典雅 经典标志款 真皮钱包 男 <br> 原装正品OPI指甲油2013年米妮 米老鼠小姐 芭比粉 嫩粉色M55<end>";
			// 向对象输出流写出数据，这些数据将存到内存缓冲区中
			oos.writeObject(wcontent.getBytes());

			// 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
			oos.flush();
			
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	        		urlConn.getInputStream()));
	        System.out.println("=============================");
	        System.out.println("Contents of get request");
	        System.out.println("=============================");
	        String lines;
	        while ((lines = reader.readLine()) != null) {
	            System.out.println(lines);
	        }
	        reader.close();
	        urlConn.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void main(String[] args)
	{
		AnsjClientHttpConnection achc=new AnsjClientHttpConnection();
		achc.test("");
	}

}
