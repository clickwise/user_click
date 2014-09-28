package cn.clickwise.clickad.feathouse;

import java.io.File;

public class QueryLogDirectory extends QueryLogStore{
	
	private File rootDirectory;

	private ConfigureFactory confFactory;
	
	public  QueryLogDirectory()
	{
		confFactory=ConfigureFactoryInstantiate.getConfigureFactory();
	    rootDirectory=confFactory.getQueryLogDirectory();	
	}
	
	public File getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public File getQueryLogByDay(int day)
	{		
		return new File(rootDirectory.getAbsolutePath()+"/queryLog_"+day);
	}
	
}
