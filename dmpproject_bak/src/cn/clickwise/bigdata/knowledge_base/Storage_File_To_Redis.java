package cn.clickwise.bigdata.knowledge_base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Storage_File_To_Redis {

	private static Jedis jedis = new Jedis("localhost");

	/**
	 * 连接Redis数据库
	 */
	public static void testConn() {
		try {

			jedis.connect();
			jedis.ping();
			// jedis.quit();
		} catch (JedisConnectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件读取和数据导入
	 * 
	 * @param path
	 *            文件路径
	 */
	public static void FileReader(String path,String charsetname) {
		try {
			String charsetName = charsetname;
			

			File file = new File(path);
			if (file.isFile() && file.exists()) {
				InputStreamReader insReader = new InputStreamReader(
						new FileInputStream(file), charsetName);

				BufferedReader bufReader = new BufferedReader(insReader);

				String line = new String();
				 int index=0;
				 //String word = null;
				// String area = null;
				while ((line = bufReader.readLine()) != null) {
					index++;

					int num_start = 0, num_end = 1;
					int str_long = line.length();
					while (num_end < str_long + 1) {
						String substr = line.substring(num_start, num_end);
						if (substr.equals("\t")) {
							String area = line.substring(0, num_end - 1).trim();
							String word = line.substring(num_end, str_long).trim();
							 //area = line.substring(0, num_end - 1).trim();
							// word = line.substring(num_end, str_long).trim();
							String key = getMD5Str(word).trim();
							
							if (jedis.lindex(key, 0) == null) {
								jedis.lpush(key, word);
								jedis.rpush(key, area);
							}							
							else {
								int i = 1;
								while (i < jedis.llen(key)) {
									if ((jedis.lindex(key, i).trim()).equals(area.trim()))
										break;
									i++;
								}
								if (i == jedis.llen(key))
									jedis.rpush(key, area);
							}
							
							// jedis.del(key);							
							 							 						
						}
						
						num_start++;
						num_end++;

					}
					//System.out.println(word);
					//System.out.println(area);				
					//break;

				}
				//System.out.println(index);
				bufReader.close();
				insReader.close();
			}

		} catch (Exception e) {
			System.out.println("出现错误！");
			e.printStackTrace();
		}
	}

	/**
	 * 字符串转换
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 转换之后的字符串
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	/**
	 * 查询关键词
	 * @param word：关键词
	 * @return：查询结果
	 */
	
	public static String KeyWordSearch(String word){
		String result = "";
		String key = Storage_File_To_Redis.getMD5Str(word);
		 for(int i=1;i<jedis.llen(key);i++)
			 result = result+jedis.lindex(key, i);
		return result;
	}
	public static void main(String[] args) {
		
		
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Education.txt","UTF-8");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Estate.txt","UTF-8");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Finance.txt","gbk");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Game.txt","gbk");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Sports.txt","gbk");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Car.txt","gbk");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Medical.txt","UTF-8");
		//Storage_File_To_Redis.FileReader("D:\\WorkContent\\Travel.txt","gbk");
		 System.out.println(jedis.dbSize());
		 String key = Storage_File_To_Redis.getMD5Str("马尔代夫");		
		 System.out.println(jedis.llen(key));
		 for(int i=1;i<jedis.llen(key);i++)
		 System.out.println(jedis.lindex(key, i));
	

	}

	

}
