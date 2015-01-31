package cn.clickwise.web.proxy;

import java.util.Properties;

public class Server implements Runnable{

	Properties properties = new Properties();
	
	public Server()
	{
	   super();
	   init();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	//初始化server
	public void init(){
		
	}
	
	public void read_input_parameters(String[] args) {
		int i;
		for (i = 0; (i < args.length) && ((args[i].charAt(0)) == '-'); i++) {
			switch ((args[i].charAt(1))) {
			case 'h':
				print_help();
				System.exit(0);
			case 'p':
				i++;
				properties.setProperty("port", args[i]);
				break;
			case 'd':
				i++;
				properties.setProperty("dict", args[i]);
				break;
			case 't':
				i++;
				properties.setProperty("server_type", args[i]);
				break;
			default:
				System.out.println("Unrecognized option " + args[i] + "!");
				print_help();
				System.exit(0);
			}
		}

		System.err.println(properties.toString());
	}
	
	
	public static void print_help() {
		System.out.println("usage: Server [options]");
		System.out.println("options: -h  -> this help");
		System.out.println("         -p  server port");
		System.out.println("         -d  dict file");
		System.out.println("         -t  server type");
	}

}
