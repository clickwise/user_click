package cn.clickwise.rpc;

public class EasyConfigureFactory extends ConfigureFactory{

	@Override
	public Configuration getConfigure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context[] getContext() {
		
		Context c=new Context("/fileStatus");
		Context c2=new Context("/fileCopyTo");
		Context c3=new Context("/fileCopyFrom");
		Context c4=new Context("/hiveFetchByKeys");
		
		Context[] cs=new Context[4];
		cs[0]=c;
		cs[1]=c2;
		cs[2]=c3;
		cs[3]=c4;
		return cs;
	}

	@Override
	public Handler[] getHandler() {

        CommandHandler ch=new FileStatusHandler();
        FileCopyToHandler fcth=new FileCopyToHandler();
        FileCopyFromHandler fcfh=new FileCopyFromHandler();
        HiveFetchByKeysHandler hfkh=new HiveFetchByKeysHandler();
        
        Handler[] chs=new Handler[4];
        chs[0]=ch;
        chs[1]=fcth;
        chs[2]=fcfh;
        chs[3]=hfkh;
        
		return chs;
	}

}
