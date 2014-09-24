package cn.clickwise.rpc;

import java.io.OutputStream;

//rpc client端
public  abstract class Client {

	public abstract void connect(Connection con);
	
	public abstract State execute(Command cmd);
	
	
}
