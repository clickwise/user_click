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
		Context[] cs=new Context[2];
		cs[0]=c;
		cs[1]=c2;
		
		return cs;
	}

	@Override
	public Handler[] getHandler() {

        CommandHandler ch=new FileStatusHandler();
        FileCopyToHandler fch=new FileCopyToHandler();
        Handler[] chs=new Handler[2];
        chs[0]=ch;
        chs[1]=fch;
		return chs;
	}

}
