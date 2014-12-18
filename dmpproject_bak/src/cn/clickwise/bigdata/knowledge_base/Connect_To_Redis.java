package cn.clickwise.bigdata.knowledge_base;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Connect_To_Redis {

	/**
	 * �������ݿ�
	 */
	private static Jedis jedis = new Jedis("localhost");
	 
	/**
	 * ��������
	 */
	public static void testConn() {
		try {

			jedis.connect();
			jedis.ping();
			//jedis.quit();						
		} 
		catch (JedisConnectionException e) {
			e.printStackTrace();
		}
	}	 
	/**
	 * ������
	 */
	public static void setTest() {
		try {
			for (int i = 0; i < 2; i++) {
				jedis.set("key" + i, "value" + i);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ȡ����
	 */
	public static void getTest() {
		try {
		for (int i = 0; i < 2; i++) {
		System.out.println(jedis.get("key" + i));
		}
		} 
		catch (Exception e) {
		e.printStackTrace();
		}
		}
	public static void decTest(){
		try {
			for (int i = 0; i < 2; i++) {
			jedis.del("key" + i);
			}
			} 
			catch (Exception e) {
			e.printStackTrace();
			}
			}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Connect_To_Redis.testConn();
		Connect_To_Redis.setTest();
		Connect_To_Redis.getTest();
		Connect_To_Redis.decTest();
		Connect_To_Redis.getTest();
		
		

	}

}
