package cn.clickwise.clickad.profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import cn.clickwise.clickad.profile.com.ansj.vec.Learn;
import cn.clickwise.clickad.profile.com.ansj.vec.Word2VEC;



public class MyWord2Vec {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage:<segment_file> <model>");
			System.exit(1);
		}

		String segmentFile = args[0];
		String model = args[1];

		Learn learn = new Learn();
		try {
			learn.learnFile(new File(segmentFile));
			learn.saveModel(new File(model));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
