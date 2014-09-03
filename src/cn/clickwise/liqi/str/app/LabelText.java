package cn.clickwise.liqi.str.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

public class LabelText {

	public void labelText(File labelFile, File hostInfo, File labelTextFile) {
		
		HashMap labelHash = FileReaderUtil.getHashFromPlainFile(
				labelFile.getAbsolutePath(), "\\s+");
		PrintWriter pw=FileWriterUtil.getPWFile(labelTextFile);
			
		try {
			String[] samples = FileToArray.fileToDimArr(hostInfo);
            String line="";
            
            String host="";
            String text="";
            String label="";
            String[] seg_arr=null;
            for(int i=0;i<samples.length;i++)
            {
            	line=samples[i];
            	if(SSO.tioe(line))
            	{
            		continue;
            	}
            	
            	seg_arr=line.split("\\s+");
            	host=seg_arr[0];
            	host=host.trim();
            	text="";
            	for(int j=1;j<seg_arr.length;j++)
            	{
            		text+=(seg_arr[j]+" ");
            	}
            	text=text.trim();
            	label=labelHash.get(host)+"";
            	if(SSO.tioe(label))
            	{
            		continue;
            	}
            	label=label.trim();
            	if(label.equals("null"))
            	{
            		continue;
            	}
            	
            	pw.println(host+"\001"+label+"\001"+text);
            }
			
            pw.close();
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		LabelText lt=new LabelText();
		lt.labelText(new File("D:/projects/dataanalysis_win_workplace/DataAnalysis/training_set/hostcate/host_label_28/label/labelwithname.txt"), new File("D:/projects/dataanalysis_win_workplace/DataAnalysis/training_set/hostcate/host_label_28/label/host_info.txt"), new File("D:/projects/dataanalysis_win_workplace/DataAnalysis/training_set/hostcate/host_label_28/label/labeltext.txt"));
	}

}
