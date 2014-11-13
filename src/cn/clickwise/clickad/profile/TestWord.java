package cn.clickwise.clickad.profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.clickwise.clickad.profile.com.ansj.vec.Word2VEC;

public class TestWord {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage:<model>");
			System.exit(1);
		}
		String model = args[0];
		Word2VEC vec = new Word2VEC();
		try {
			vec.loadJavaModel(model);
			String inputWord = "";
			System.out.println("输入一个单词：");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			while ((inputWord = br.readLine()) != null
					&& (!(inputWord.equals("exit")))) {
				System.out.println("read word:" + inputWord);
				for (int i = 0; i < 100; i++) {
					System.out.println(vec.distance(inputWord));
				}
				System.out.println("输入一个单词：");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
