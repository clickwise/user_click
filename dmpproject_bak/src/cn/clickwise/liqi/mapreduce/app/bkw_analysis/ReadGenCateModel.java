package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class ReadGenCateModel {

	public static double[] line_weights;
	public static String Version;
	public static int NUM_CLASS;
	public static int NUM_WORDS;
	public static int loss_function;
	public static int kernel_type;
	public static int para_d;
	public static int para_g;
	public static int para_s;
	public static int para_r;
	public static String para_u;
	public static int NUM_FEATURES;
	public static int train_num;
	public static int suv_num;
	public static double b;
	public static double alpha;
	public static int qid;
	public static String[] label_names = { "男", "女", "噪音" };
	
	
	public void read_model(String model_path) throws Exception {

		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + model_path);
		InputStreamReader model_isr = new InputStreamReader(model_is);
		// File model_file = new File(model_path);
		// FileReader fr = new FileReader(model_file);
		BufferedReader br = new BufferedReader(model_isr);
		Version = cut_comment(br.readLine());
		NUM_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		NUM_WORDS = Integer.parseInt(cut_comment(br.readLine()));
		// System.out.println("NUM_WORDS:" + NUM_WORDS);
		loss_function = Integer.parseInt(cut_comment(br.readLine()));
		kernel_type = Integer.parseInt(cut_comment(br.readLine()));
		para_d = Integer.parseInt(cut_comment(br.readLine()));
		para_g = Integer.parseInt(cut_comment(br.readLine()));
		para_s = Integer.parseInt(cut_comment(br.readLine()));
		para_r = Integer.parseInt(cut_comment(br.readLine()));
		para_u = cut_comment(br.readLine());
		NUM_FEATURES = Integer.parseInt(cut_comment(br.readLine()));
		// System.out.println("NUM_FEATURES:" + NUM_FEATURES);
		train_num = Integer.parseInt(cut_comment(br.readLine()));
		suv_num = Integer.parseInt(cut_comment(br.readLine()));
		b = Double.parseDouble(cut_comment(br.readLine()));
		line_weights = new double[NUM_FEATURES + 2];
		for (int i = 0; i < line_weights.length; i++) {
			line_weights[i] = 0;
		}
		String line = br.readLine();
		StringTokenizer st = new StringTokenizer(line, " ");
		// System.out.println("st.count:" + st.countTokens());
		// System.out.println("end:" + line.substring(0, 1000));

		int current_pos = 0;
		int forward_num = 0;
		String temp_token = "";
		int temp_index;
		double temp_weight;
		int max_index = -1;
		int search_blank = 0;
		// System.out.println("line.length:" + line.length());
		while (current_pos < (line.length())) {
			// if((current_pos%10000==0))
			// {
			// System.out.println("current_pos:"+current_pos);
			// }
			forward_num = 0;
			temp_token = "";
			while ((current_pos + forward_num) < (line.length())) {
				// if(current_pos>26080000)
				// {
				// System.out.println("current_pos+forward_num:"+(current_pos+forward_num));
				// System.out.println("cc:"+line.charAt(current_pos+forward_num));
				// }

				if (((line.charAt(current_pos + forward_num)) != ' ')
						&& ((line.charAt(current_pos + forward_num)) != '#')) {
					temp_token = temp_token
							+ line.charAt(current_pos + forward_num);
					forward_num++;
				} else {
					temp_token = temp_token.trim();
					// if(current_pos>26080000)
					// System.out.println("temp_token:"+temp_token);
					if (((temp_token.indexOf(":")) == -1)
							&& (!temp_token.equals(""))) {
						alpha = Double.parseDouble(temp_token);
					} else if ((temp_token.indexOf("qid")) != -1) {
						qid = Integer.parseInt(temp_token
								.substring(temp_token.indexOf(":") + 1),
								temp_token.length());
					} else if (Pattern
							.matches("\\d+:[\\d\\.]+", temp_token)) {
						temp_index = Integer.parseInt(temp_token.substring(
								0, temp_token.indexOf(":")));
						temp_weight = Double.parseDouble(temp_token
								.substring(temp_token.indexOf(":") + 1,
										temp_token.length()));
						line_weights[temp_index] = temp_weight;
						if (temp_index > max_index) {
							max_index = temp_index;
						}
					}
					search_blank = 0;
					while ((current_pos + forward_num + search_blank) < line
							.length()) {
						if (line.charAt(current_pos + forward_num
								+ search_blank) == ' ') {
							search_blank++;
						} else {
							break;
						}
					}
					// if((current_pos%10000==0)||current_pos>26080000)
					// {
					// System.out.println("forward_num+search_blank:"+(forward_num+search_blank));
					// }
					if ((line.charAt(current_pos + forward_num)) == '#') {
						forward_num++;
					}
					current_pos = current_pos + forward_num + search_blank;
					break;
				}
			}
		}
		// fr.close();
		model_is.close();
		model_isr.close();
		br.close();
		// System.out.println("max_index:" + max_index);
	}
	
	
	
	public String cut_comment(String s) {
		String cut_s = "";
		if ((s.indexOf("#")) != -1) {
			cut_s = s.substring(0, s.indexOf("#"));
		} else {
			cut_s = s;
		}
		cut_s = cut_s.trim();
		return cut_s;
	}

	

	
	
}
