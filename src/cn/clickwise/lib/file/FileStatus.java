package cn.clickwise.lib.file;

import java.io.File;

public class FileStatus {

    public static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
    
    public static void main(String[] args)
    {
    	System.out.println(getTotalSizeOfFilesInDir(new File("docs")));
    }
}
