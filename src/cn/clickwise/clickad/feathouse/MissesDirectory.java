package cn.clickwise.clickad.feathouse;

import java.io.File;
import java.util.Date;

public class MissesDirectory extends MissesStore{

	public File rootDirectory;
	
	public MissesDirectory(File rootDirectory)
	{
		this.rootDirectory=rootDirectory;
	}
	
	@Override
	public File getMissesByAreaName(Area area, TimeRange timeRange) {
		// TODO Auto-generated method stub
	    Date[] intervals=timeRange.listDays();
		
	    
		
		return null;
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
