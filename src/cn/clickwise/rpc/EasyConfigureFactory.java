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
		Context c5=new Context("/hiveFetchTable");
		Context c6=new Context("/hiveStatisticByKeys");
		
		Context[] cs=new Context[6];
		cs[0]=c;
		cs[1]=c2;
		cs[2]=c3;
		cs[3]=c4;
		cs[4]=c5;
		cs[5]=c6;
		
		return cs;
	}

	@Override
	public Handler[] getHandler() {

        CommandHandler ch=new FileStatusHandler();
        FileCopyToHandler fcth=new FileCopyToHandler();
        FileCopyFromHandler fcfh=new FileCopyFromHandler();
        HiveFetchByKeysHandler hfkh=new HiveFetchByKeysHandler();
        HiveFetchTableHandler hfth=new HiveFetchTableHandler();
        HiveStatisticByKeysHandler hskh=new HiveStatisticByKeysHandler();
             
        Handler[] chs=new Handler[6];
        chs[0]=ch;
        chs[1]=fcth;
        chs[2]=fcfh;
        chs[3]=hfkh;
        chs[4]=hfth;
        chs[5]=hskh;
        
		return chs;
	}

}
