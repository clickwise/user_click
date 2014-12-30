package cn.clickwise.web;

public abstract class ConfigureFactory {

	public abstract int getWordResultType();

	public abstract String getPrefixSE(); 

	public abstract String getWordAnalyticOutputDir();
	
    public abstract int getFetcherOpt();
    
    public abstract FetchResolve getFetchResolve();
	
}
