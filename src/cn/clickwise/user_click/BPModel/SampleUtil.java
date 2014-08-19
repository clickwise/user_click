package cn.clickwise.user_click.BPModel;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

public class SampleUtil {

	public void getSample(File uclkad, File uhost,File ushowf, File sample) {
		try {
			PrintWriter pw=FileWriterUtil.getPW(sample.getAbsolutePath());
			HashMap<String, String> uclk = FileReaderUtil.getHashFromPlainFile(
					uclkad.getAbsolutePath(), "\001");
			HashMap<String, String> ushow = FileReaderUtil.getHashFromPlainFile(
					ushowf.getAbsolutePath(), "\001");
			String[] recs = FileToArray.fileToDimArr(uhost);
			System.out.println("recs.length:"+recs.length);
			String uid="";
			String host="";
			String[] seg=null;
			String adid="";
			String adid2="";
			String line="";
			for(int i=0;i<recs.length;i++)
			{
				line=recs[i];
				if(SSO.tioe(line))
				{
					continue;
				}
				line=line.trim();
				seg=line.split("\001");
				if(seg.length!=2)
				{
					continue;
				}
				uid=seg[0].trim();
				host=seg[1].trim();
				//System.out.println("i="+i+" "+uid+":"+host);
				if(SSO.tioe(uid))
				{
					continue;
				}
				adid=uclk.get(uid);
				adid2=ushow.get(uid);
				//System.out.println("adid2:"+adid2);
				//if(SSO.tioe(adid2)||SSO.tioe(adid))
				//{
					//continue;
				//}
				
				if(adid!=null)
				{
					pw.println(host+"\001"+adid+"\001"+"1");
				}
				else if(adid2!=null)
				{
					pw.println(host+"\001"+adid2+"\001"+"0");
				}
			}
			pw.close();

		} catch (Exception e) {
            System.out.println(e.getMessage());
		}

	}
	
	public static void main(String[] args)
	{
		SampleUtil su=new SampleUtil();
		su.getSample(new File("temp/sample/uclikad_2014-07-29.txt"),new File("temp/sample/uhost_2014-07-29.log"),new File("temp/sample/ushowf_2014-07-29.txt"),new File("temp/sample/sample.txt"));
	}

}
