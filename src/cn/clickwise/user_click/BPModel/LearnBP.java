package cn.clickwise.user_click.BPModel;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.sort.SortStrArray;

import love.cq.util.MapCount;

//输入<host click>
public class LearnBP {

	private Map<String, Neuron> hostMap = new HashMap<>();
	private Map<String, Integer> ad_index = new HashMap<String, Integer>();
	private Map<Integer, String> index_ad = new HashMap<Integer, String>();

	/**
	 * 训练多少个特征
	 */
	private int layerSize = 200;

	/**
	 * 上下文窗口大小
	 */
	private int window = 5;

	private double sample = 1e-3;
	private double alpha = 0.025;
	private double startingAlpha = alpha;

	public int EXP_TABLE_SIZE = 1000;

	private Boolean isCbow = false;

	private double[] expTable = new double[EXP_TABLE_SIZE];

	private int trainWordsCount = 0;

	private int MAX_EXP = 6;

	public LearnBP(Boolean isCbow, Integer layerSize, Integer window,
			Double alpha, Double sample) {
		createExpTable();
		if (isCbow != null) {
			this.isCbow = isCbow;
		}
		if (layerSize != null)
			this.layerSize = layerSize;
		if (window != null)
			this.window = window;
		if (alpha != null)
			this.alpha = alpha;
		if (sample != null)
			this.sample = sample;
	}

	public LearnBP() {
		createExpTable();
	}

	/**
	 * trainModel
	 * 
	 * @throws IOException
	 */
	private void trainModel(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)))) {
			String temp = null;
			long nextRandom = 5;
			int wordCount = 0;
			int lastWordCount = 0;
			int wordCountActual = 0;
			String host = "";
			String ad = "";
			String click = "";
			while ((temp = br.readLine()) != null) {
				if (wordCount - lastWordCount > 10000) {
					System.out.println("alpha:"
							+ alpha
							+ "\tProgress: "
							+ (int) (wordCountActual
									/ (double) (trainWordsCount + 1) * 100)
							+ "%");
					wordCountActual += wordCount - lastWordCount;
					lastWordCount = wordCount;
					alpha = startingAlpha
							* (1 - wordCountActual
									/ (double) (trainWordsCount + 1));
					if (alpha < startingAlpha * 0.0001) {
						alpha = startingAlpha * 0.0001;
					}
				}

				wordCount += 1;
				String[] split = temp.split("\001");
				if (split.length != 3) {
					continue;
				}
				host = split[0].trim();
				ad = split[1].trim();
				click = split[2].trim();
				if (SSO.tioe(host) || SSO.tioe(ad) || SSO.tioe(click)) {
					continue;
				}
				update(host, ad, click);
			}
			System.out.println("Vocab size: " + hostMap.size());
			System.out.println("Words in train file: " + trainWordsCount);
			System.out.println("sucess train over!");
		}
	}

	private void update(String host, String ad, String click) {
		WordNeuron word = (WordNeuron) hostMap.get(host);
		if (word == null) {
			return;
		}
		int adIndex = ad_index.get(ad);
		double f = 0;
		// Propagate hidden -> output
		f = word.syn0[adIndex];

		if (f <= -MAX_EXP || f >= MAX_EXP) {
			return;
		} else {
			// f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
			// f = expTable[(int) f];
			f = (Math.exp(f) / (Math.exp(f) + 1));
		}

		double g = (1 - Integer.parseInt(click) - f) * alpha;

		word.syn0[adIndex] += g;
	}

	/**
	 * 统计词频
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void readVocab(File file) throws IOException {
		MapCount<String> mc = new MapCount<>();
		String host = "";
		String ad = "";
		String click = "";

		int maxAdIndex = -1;
		for (Map.Entry<String, Integer> map : ad_index.entrySet()) {
			if ((map.getValue()) > maxAdIndex) {
				maxAdIndex = map.getValue();
			}
		}

		int adIndexPtr = maxAdIndex + 1;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				String[] split = temp.split("\001");
				if (split.length != 3) {
					continue;
				}
				host = split[0].trim();
				ad = split[1].trim();
				click = split[2].trim();
				if (SSO.tioe(host) || SSO.tioe(ad) || SSO.tioe(click)) {
					continue;
				}
				if (!(ad_index.containsKey(ad))) {
					ad_index.put(ad, adIndexPtr);
					index_ad.put(adIndexPtr, ad);
					adIndexPtr++;
				}

				trainWordsCount += 1;
				mc.add(host);
			}
		}

		for (Entry<String, Integer> element : mc.get().entrySet()) {
			hostMap.put(element.getKey(), new WordNeuron(element.getKey(),
					element.getValue(), adIndexPtr));
		}

	}

	// 读取广告和编号的对应关系
	// line.=.<adid>\001<adindex>
	private void readAdIndex(File adIndexFile) {
		String map = "";
		String adid = "";
		String adindex = "";
		String[] seg = null;
		try {
			String[] maps = FileToArray.fileToDimArr(adIndexFile);
			for (int i = 0; i < maps.length; i++) {
				map = maps[i];
				if (SSO.tioe(map)) {
					continue;
				}

				seg = map.split("\001");
				if (seg.length != 2) {
					continue;
				}

				adid = seg[0].trim();
				adindex = seg[1].trim();
				if (!(ad_index.containsKey(adid))) {
					ad_index.put(adid, Integer.parseInt(adindex));
				}

				if (index_ad.containsKey(Integer.parseInt(adindex))) {
					index_ad.put(Integer.parseInt(adindex), adid);
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	// 写入广告和编号的对应关系
	// line.=.<adid>\001<adindex>
	private void writeAdIndex(File adIndexFile) {
		PrintWriter pw = FileWriterUtil.getPW(adIndexFile.getAbsolutePath());
		for (Map.Entry<String, Integer> map : ad_index.entrySet()) {
			pw.println(map.getKey() + "\001" + map.getValue());
		}
		pw.close();
	}

	/**
	 * Precompute the exp() table f(x) = x / (x + 1)
	 */
	private void createExpTable() {
		for (int i = 0; i < EXP_TABLE_SIZE; i++) {
			expTable[i] = Math
					.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
			expTable[i] = expTable[i] / (expTable[i] + 1);
		}
	}

	/**
	 * 根据文件学习
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void learnFile(File file, File adIndexFile, File resFile)
			throws IOException {

		readAdIndex(adIndexFile);
		readVocab(file);
		writeAdIndex(adIndexFile);
		trainModel(file);
		analysis_result_reg(resFile);
	}

	public void analysis_result(File resFile) {
		PrintWriter pw = FileWriterUtil.getPW(resFile.getAbsolutePath());

		for (Map.Entry<String, Integer> ad : ad_index.entrySet()) {
			ArrayList<String> hostScoreList = new ArrayList<String>();
			WordNeuron word = null;
			for (Map.Entry<String, Neuron> neuron : hostMap.entrySet()) {
				word = (WordNeuron) neuron.getValue();
				double f = word.syn0[ad.getValue()];
				if (f <= -MAX_EXP || f >= MAX_EXP) {
					return;
				} else {
					/*
					 * f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2); f =
					 * expTable[(int) f];
					 */
					f = (Math.exp(f) / (Math.exp(f) + 1));
				}
				hostScoreList.add(neuron.getKey() + "\001" + f);
			}

			String[] sortHostScore = SortStrArray.sort_List(hostScoreList, 1,
					"dou", 2, "\001");
			pw.println("ad:" + ad.getKey());
			for (int i = 0; (i < sortHostScore.length && i < 1000); i++) {
				pw.println(ad.getKey() + "\001" + sortHostScore[i]);
			}
		}
		pw.close();

	}

	public void analysis_result_reg(File resFile) {
		PrintWriter pw = FileWriterUtil.getPW(resFile.getAbsolutePath());

		int i=0;
		for (Map.Entry<String, Neuron> neuron : hostMap.entrySet()) {
		
			WordNeuron word = null;
			word = (WordNeuron) neuron.getValue();
			if(word==null)
			{
				continue;
			}
			ArrayList<String> adScoreList = new ArrayList<String>();
			System.out.println("i="+i+"  "+word.name);
			i++;
			for (Map.Entry<String, Integer> ad : ad_index.entrySet()) {
				
				double f = word.syn0[ad.getValue()];
				if (f <= -MAX_EXP || f >= MAX_EXP) {
					continue;
				} else {
					
					 //f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
					// f =expTable[(int) f];
					 
					f = (Math.exp(f) / (Math.exp(f) + 1));
				}
				adScoreList.add("adshow_"+ad.getKey() + "\001" + f);
			}
			if(adScoreList.size()<1)
			{
				continue;
			}
			String[] sortHostScore = SortStrArray.sort_List(adScoreList,
					1, "dou", 2, "\001");
		    pw.println(neuron.getKey()+"\001"+sortHostScore[0]);
				
		}

		pw.close();

	}

	/**
	 * 保存模型
	 */
	public void saveModel(File file) {
		// TODO Auto-generated method stub

		try (DataOutputStream dataOutputStream = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(file)))) {
			dataOutputStream.writeInt(hostMap.size());
			dataOutputStream.writeInt(layerSize);
			double[] syn0 = null;
			for (Entry<String, Neuron> element : hostMap.entrySet()) {
				dataOutputStream.writeUTF(element.getKey());
				syn0 = ((WordNeuron) element.getValue()).syn0;
				for (double d : syn0) {
					dataOutputStream.writeFloat(((Double) d).floatValue());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getLayerSize() {
		return layerSize;
	}

	public void setLayerSize(int layerSize) {
		this.layerSize = layerSize;
	}

	public int getWindow() {
		return window;
	}

	public void setWindow(int window) {
		this.window = window;
	}

	public double getSample() {
		return sample;
	}

	public void setSample(double sample) {
		this.sample = sample;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		this.startingAlpha = alpha;
	}

	public Boolean getIsCbow() {
		return isCbow;
	}

	public void setIsCbow(Boolean isCbow) {
		this.isCbow = isCbow;
	}

	public static void main(String[] args) throws IOException {
		LearnBP learn = new LearnBP();
		long start = System.currentTimeMillis();
		learn.learnFile(new File("temp/sample/sample.txt"), new File(
				"temp/sample/label.txt"), new File("temp/sample/res2.txt"));
		// learn.learnFile(new File("library/xh.txt"));
		System.out.println("use time " + (System.currentTimeMillis() - start));
		// learn.saveModel(new File("library/javaVector"));

	}

}
