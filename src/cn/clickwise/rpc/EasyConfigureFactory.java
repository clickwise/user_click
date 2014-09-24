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
		Context[] cs=new Context[1];
		cs[0]=c;
		return cs;
	}

	@Override
	public Handler[] getHandler() {

        CommandHandler ch=new FileStatusHandler();
        CommandHandler[] chs=new CommandHandler[1];
        chs[0]=ch;
		return chs;
	}

}
