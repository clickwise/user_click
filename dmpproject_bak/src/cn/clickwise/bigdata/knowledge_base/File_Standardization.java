package cn.clickwise.bigdata.knowledge_base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class File_Standardization {

	/**
	 * 医疗行业文件规范化
	 * 
	 * @param inputfilepath
	 *            文件输入路径
	 * @param outputfilepath
	 *            文件输出路径
	 * @param encoding
	 *            文件输出格式
	 */

	public static void FileReader_Medical(String inputfilepath,
			String outputfilepath, String encoding, int num) {
		try {
			FileOutputStream outputStream = new FileOutputStream(outputfilepath);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					outputStream, encoding));

			String charsetName = "gbk";
			// String path = ;

			File file = new File(inputfilepath);
			if (file.isFile() && file.exists()) {
				InputStreamReader insReader = new InputStreamReader(
						new FileInputStream(file), charsetName);

				BufferedReader bufReader = new BufferedReader(insReader);

				String line = new String();

				while ((line = bufReader.readLine()) != null) {
					int index = 0;
					int num2 = 0;

					int num_start = 0, num_end = 1;
					int str_long = line.length();
					int i = 1;
					while (num_end < str_long + 1) {
						String substr = line.substring(num_start, num_end);

						if (substr.equals("\t")) {
							if (index < num) {
								index++;
								line = line.replaceFirst("\t", "|");
							}
						}
						num_start++;
						num_end++;

					}
					bw.write("��|" + line + "\n");

					// break;

				}
				bufReader.close();
				insReader.close();
				bw.close();
				outputStream.close();
			}

		} catch (Exception e) {
			System.out.println("文件传输出现错误！");
			e.printStackTrace();
		}
	}

	/**
	 * 汽车行业文件规范化
	 * 
	 * @param inputfilepath
	 *            文件输入路径
	 * @param outputfilepath
	 *            文件输出路径
	 * @param encoding
	 *            文件输出格式
	 */

	public static void FileReader_Car(String inputfilepath,
			String outputfilepath, String encoding, String cate) {
		try {

			FileOutputStream outputStream = new FileOutputStream(outputfilepath);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					outputStream, encoding));

			String charsetName = "gbk";

			File file = new File(inputfilepath);
			if (file.isFile() && file.exists()) {
				InputStreamReader insReader = new InputStreamReader(
						new FileInputStream(file), charsetName);

				BufferedReader bufReader = new BufferedReader(insReader);

				String line = new String();

				while ((line = bufReader.readLine()) != null) {

					int num_start = 0, num_end = 1;
					int str_long = line.length();
					int arr[] = new int[20];
					int i = 0;
					int index = 0;

					while (num_end < str_long + 1) {
						String substr = line.substring(num_start, num_end);

						if (substr.equals("\t")) {
							index++;
							arr[i] = num_start;
							i++;

						}
						num_start++;
						num_end++;

					}
					if (cate == "品牌") {
						if (index == 7)
							bw.write("汽车|品牌\t"
									+ (line.substring(arr[4], arr[5])).trim()
									+ "\n");
						if (index == 5)
							bw.write("汽车|品牌\t"
									+ (line.substring(arr[2], arr[3])).trim()
									+ "\n");
					}

					if (cate == "型号") {
						if (index == 7)
							bw.write("汽车|"
									+ (line.substring(arr[0], arr[1])).trim()
									+ "|"
									+ (line.substring(arr[2], arr[3])).trim()
									+ "|"
									+ (line.substring(arr[4], arr[5])).trim()
									+ "\t"
									+ (line.substring(arr[6], str_long)).trim()
									+ "\n");
						if (index == 5)
							bw.write("汽车|"
									+ (line.substring(arr[0], arr[1])).trim()
									+ "|"
									+ (line.substring(arr[2], arr[3])).trim()
									+ "\t"
									+ (line.substring(arr[4], str_long)).trim()
									+ "\n");

					}

				}
				bufReader.close();
				insReader.close();
				bw.close();
				outputStream.close();

			}

		} catch (Exception e) {
			System.out.println("文件传输出现错误！");
			e.printStackTrace();
		}
	}
/**
 * 旅游行业文件规范化
 * @param inputfilepath：文件输入路径
 * @param outputfilepath：文件输出路径
 * @param encoding：文件输出格式
 */
	public static void FileReader_Travel(String inputfilepath,
			String outputfilepath, String encoding) {
		try {

			FileOutputStream outputStream = new FileOutputStream(outputfilepath);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					outputStream, encoding));

			String charsetName = "gbk";

			File file = new File(inputfilepath);
			if (file.isFile() && file.exists()) {
				InputStreamReader insReader = new InputStreamReader(
						new FileInputStream(file), charsetName);

				BufferedReader bufReader = new BufferedReader(insReader);

				String line = new String();

				while ((line = bufReader.readLine()) != null) {
					// System.out.println("len:");
					int num_start = 0, num_end = 1;
					int str_long = line.length();
					int arr[] = new int[100];
					int i = 1;
					int index = 0;

					while (num_end < str_long + 1) {
						String substr = line.substring(num_start, num_end);

						if (substr.equals("\t")) {
							arr[0] = num_end;
							// System.out.println("len:"+arr.length);

						}
						if (substr.equals(",")) {
							index++;
							arr[i] = num_end;
							i++;

						}

						num_start++;
						num_end++;
					}

					// System.out.println(str_long);
					// System.out.println(arr[0]);

					for (int j = 0; j < index; j++)
						bw.write("����|"
								+ (line.substring(0, arr[0])).trim()
								+ "\t"
								+ (line.substring(arr[j], arr[j + 1] - 1))
										.trim() + "\n");

				}
				bufReader.close();
				insReader.close();
				bw.close();
				outputStream.close();

			}

		} catch (Exception e) {
			System.out.println("文件传输出现错误！");
			e.printStackTrace();
		}

	}

	/**
	 * 医疗行业医院部分文件规范化
	 * 
	 * @param inputfilepath
	 *            输入文件路径
	 * @param outputfilepath
	 *            输出文件路径
	 * @param encoding
	 *            文件输出格式
	 */

	public static void FileReader_hospital(String inputfilepath,
			String outputfilepath, String encoding) {
		try {

			FileOutputStream outputStream = new FileOutputStream(outputfilepath);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					outputStream, encoding));
			String charsetName = "gbk";
			// String path = ;

			File file = new File(inputfilepath);
			if (file.isFile() && file.exists()) {
				InputStreamReader insReader = new InputStreamReader(
						new FileInputStream(file), charsetName);

				BufferedReader bufReader = new BufferedReader(insReader);

				String line = new String();

				while ((line = bufReader.readLine()) != null) {
					int index = 0;
					// int num2 = 0;

					int num_start = 0, num_end = 1;
					int str_long = line.length();
					int i = 0;
					while (num_end < str_long + 1) {
						String substr = line.substring(num_start, num_end);

						if (substr.equals("\t")) {
							index++;
							if (index < 3) {
								line = line.replaceFirst("\t", "|");
								
							}
							if (index == 4) {
								i = num_end;
								break;

							}
						}
						if (num_end == str_long) {
							i = num_end;
							break;
						}
						num_start++;
						num_end++;

					}
					// System.out.println(str_long);
					// System.out.println((line.substring(0, i)).trim());
					// break;

					bw.write("医疗|" + (line.substring(0, i)).trim() + "\n");

				}
				bufReader.close();
				insReader.close();
				bw.close();
				outputStream.close();

			}

		} catch (Exception e) {
			System.out.println("文件传输出现错误");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		/*
		 * File_Standardization.FileReader_hospital(
		 * "D:\\WorkContent\\Medical\\hospital.txt",
		 * "D:\\WorkContent\\Medical\\newhospital.txt", "gbk");
		 */
		/*
		 * File_Standardization.FileReader_Medical(
		 * "D:\\WorkContent\\Medical\\2.txt",
		 * "D:\\WorkContent\\Medical\\new2.txt", "UTF-8",1);
		 */
		/*
		 * File_Standardization.FileReader_Medical(
		 * "D:\\WorkContent\\Medical\\3.txt",
		 * "D:\\WorkContent\\Medical\\new3.txt", "gbk",2);
		 */
		/*
		 * File_Standardization.FileReader_Medical(
		 * "D:\\WorkContent\\Medical\\4.txt",
		 * "D:\\WorkContent\\Medical\\new4.txt", "gbk",3);
		 */

		/*
		 * File_Standardization.FileReader_Car("D:\\WorkContent\\car\\2.txt",
		 * "D:\\WorkContent\\car\\new2.txt", "gbk","型号");
		 */
		/*
		 * File_Standardization.FileReader_Medical(
		 * "D:\\WorkContent\\car\\1.txt", "D:\\WorkContent\\car\\new1.txt",
		 * "gbk",2);
		 */
		/*
		 * File_Standardization.FileReader_Car( "D:\\WorkContent\\car\\2.txt",
		 * "D:\\WorkContent\\car\\new22.txt", "gbk","品牌");
		 */
		/*
		 * File_Standardization.FileReader_Travel(
		 * "D:\\WorkContent\\Travel\\Travel.txt",
		 * "D:\\WorkContent\\Travel\\newTravel.txt", "gbk");
		 */
	}
}
