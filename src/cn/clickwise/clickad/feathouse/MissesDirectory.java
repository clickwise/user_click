package cn.clickwise.clickad.feathouse;

import java.io.File;
import java.util.Date;

import cn.clickwise.lib.file.MulFileOpera;

public class MissesDirectory extends MissesStore{

	private File rootDirectory;
	private MissesTmpStore missesTmp;
	
	public MissesDirectory(File rootDirectory)
	{
		this.rootDirectory=rootDirectory;
		ConfigureFactory confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
		missesTmp=confFactory.getMissesTmpStore();
	}
	
	@Override
	public File getMissesByAreaName(Area area, TimeRange timeRange) {
		// TODO Auto-generated method stub
		
	    Date[] intervals=timeRange.listDays();
		
	    File destFile=missesTmp.findFileByAreaTimeRange(area, timeRange,true);
		File[] sourceFiles=new File[intervals.length];
		
	    for(int i=0;i<sourceFiles.length;i++)
	    {
	    	sourceFiles[i]=findFileByAreaDate(area,intervals[i]);
	    }
	    
	    MulFileOpera.mergeMulFileNoDup(sourceFiles, destFile);
	    
		return destFile;
	}

	
	@Override
	public File getFileByName(String name) {	    
		File newFile=new File(FileName.getSubFileName(rootDirectory, name));
		return newFile;
	}

	@Override
	public File findFileByAreaDate(Area area, Date date) {
		
		File objectFile=null;
		
		File[] subFiles=rootDirectory.listFiles();	
		String tempName=FileName.getNameByAreaDate(area, date);
			
		for(int i=0;i<subFiles.length;i++)
		{
			if(tempName.equals(subFiles[i].getName()))
			{
		      objectFile=subFiles[i];		
			}
		}
		
		return objectFile;
	}
	
	
}
