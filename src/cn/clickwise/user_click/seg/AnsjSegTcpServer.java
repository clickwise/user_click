package cn.clickwise.user_click.seg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cn.clickwise.liqi.file.uitls.JarFileReader;
import cn.clickwise.liqi.str.basic.SSO;

public class AnsjSegTcpServer implements Runnable {

	public int server_port = 0;
    public AnsjSeg ansjseg=null;
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(server_port);

			while (true) {
				try {
					Socket sock = servSock.accept();
					InputStream in = sock.getInputStream();
					OutputStream out = sock.getOutputStream();
					InputStreamReader isr = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(isr);
					OutputStreamWriter osw = new OutputStreamWriter(out);
					PrintWriter pw = new PrintWriter(osw);
                    String line="";
                    String seg_text="";
                    while((line=br.readLine())!=null)
                    {
                       seg_text=ansjseg.seg(line);
                       if(SSO.tioe(seg_text))
                       {
                    	   seg_text="";
                       }
                       pw.println(seg_text);
                       pw.flush();
                    }
                    sock.close();
				} catch (Exception e) {

				}

			}

		} catch (Exception e) {

		}

	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage:<AnsjSegTcpServer> <server_port>");
			System.exit(1);
		}
		
		AnsjSegTcpServer asts = new AnsjSegTcpServer();
		asts.server_port = Integer.parseInt(args[0]);
		
		JarFileReader jfr=new JarFileReader();
		String seg_dict_file="five_dict_uniq.txt";
		String stop_dict_file="cn_stop_words_utf8.txt";
		HashMap<String,String> seg_dict=jfr.jarFile2Hash(seg_dict_file);
		HashMap<String,String> stop_dict=jfr.jarFile2Hash(stop_dict_file);
	
		asts.ansjseg.setSeg_dict(seg_dict);
		asts.ansjseg.setStop_dict(stop_dict);
		Thread serverThread = new Thread(asts);
		serverThread.start();

	}

}
