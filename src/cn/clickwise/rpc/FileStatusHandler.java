package cn.clickwise.rpc;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileStatusHandler extends CommandHandler {

	@Override
	public void complie(Command cmd, HttpExchange exchange) {
        //System.out.println("in FileStatusHandler complie");
		FileStatusCommand fscmd = (FileStatusCommand) cmd;
		//System.out.println("name:"+fscmd.getName());
		//System.out.println("path:"+fscmd.getPath());
		File file = null;
		try {
			file = new File(fscmd.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileStatus fs = new FileStatus();
		fs.setName(fscmd.getName());
		fs.setPath(fscmd.getPath());
		if (!(file.isDirectory())) {
			fs.setDirectory(false);
			fs.setChildren(new ArrayList<FileStatus>());
			fs.setParent(null);
		} else {
			fs.setDirectory(true);
			fs.setChildren(getChildren(fs));
			fs.setParent(null);
		}
		
		//System.out.println("fs:" + fs.getName());
		//for (FileStatus sfs : fs.getChildren()) {
		//	System.out.println("sfs:" + sfs.getName());
		//}
		
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try {
			exchange.sendResponseHeaders(200, 0);
			os = exchange.getResponseBody();
			oos = new ObjectOutputStream(os);
			oos.writeObject(fs);
			oos.flush();
			//oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<FileStatus> getChildren(FileStatus fs) {
		ArrayList<FileStatus> children = new ArrayList<FileStatus>();
		if (!(fs.isDirectory())) {
			return children;
		} else {

			File tempFile = null;

			try {
				tempFile = new File(fs.getPath());
				File[] subFiles = tempFile.listFiles();
				for (int j = 0; j < subFiles.length; j++) {
					FileStatus subFs = new FileStatus();
					if (!(subFiles[j].isDirectory())) {
						subFs.setName(subFiles[j].getName());
						subFs.setParent(fs);
						subFs.setPath(subFiles[j].getAbsolutePath());
						subFs.setDirectory(false);
						subFs.setChildren(new ArrayList<FileStatus>());
						children.add(subFs);
					} else {
						subFs.setName(subFiles[j].getName());
						subFs.setParent(fs);
						subFs.setPath(subFiles[j].getAbsolutePath());
						subFs.setDirectory(true);
						subFs.setChildren(getChildren(subFs));
						children.add(subFs);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return children;
		}
	}

	public static void main(String[] args) {
		FileStatusHandler fsh = new FileStatusHandler();
		File file = null;
		try {
			file = new File("temp");
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileStatus fs = new FileStatus();
		fs.setName(file.getName());
		fs.setPath(file.getPath());
		if (!(file.isDirectory())) {
			fs.setDirectory(false);
			fs.setChildren(new ArrayList<FileStatus>());
			fs.setParent(null);
		} else {
			fs.setDirectory(true);
			fs.setChildren(fsh.getChildren(fs));
			fs.setParent(null);
		}

		System.out.println("fs:" + fs.getName());
		for (FileStatus sfs : fs.getChildren()) {
			System.out.println("sfs:" + sfs.getName());
		}
	}

}
