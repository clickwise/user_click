package cn.clickwise.user_click.send;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import org.json.JSONObject;
import cn.clickwise.net.http.HttpClientTool;

public class SendToAdmatch {

	String httpurl = "";
	int httpport = 0;
	public HttpClientTool hct=null;

	public SendToAdmatch(String httpurl, int httpport) {
		this.httpurl = httpurl;
		this.httpport = httpport;
		hct=new HttpClientTool();
	}

	public void sendUserInfo() {
		//InputStreamReader isr = new InputStreamReader(System.in);
		FileReader fr=null;
		try{
		fr=new FileReader(new File("temp/offline/tt.txt"));
		}
		catch(Exception e)
		{
			
		}
		
		BufferedReader br = new BufferedReader(fr);
		String line = "";
	    JSONObject json=new JSONObject(); 
	    String uid="";
	    String datatype="";
	    String addtime="";
	    String info="";
	    
	    String url="";
	    
	    String[] seg_arr=null;
		try {
			while ((line = br.readLine()) != null) {
                seg_arr=line.split("\001");
				if(seg_arr.length!=4)
				{
					continue;
				}		
				uid=seg_arr[0].trim();
				datatype=seg_arr[1].trim();
				addtime=seg_arr[2].trim();
				info=seg_arr[3].trim();
				json.put("uid", uid);
				json.put("datatype", datatype);
				json.put("addtime", addtime);
				json.put("info", info);
		        url="http://"+httpurl+":"+httpport+"/adduseroffline?s="+URLEncoder.encode(json.toString());
		        System.out.println(uid+"|||"+datatype+"|||"+addtime+"|||"+info);
		        System.out.println(url);
		        hct.postUrl(url);
			}
		} catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage:<SendToAdmatch> <httpurl> <httpport>");
			System.exit(1);
		}

		SendToAdmatch stam = new SendToAdmatch(args[0],
				Integer.parseInt(args[1]));
		stam.sendUserInfo();

	}
}
