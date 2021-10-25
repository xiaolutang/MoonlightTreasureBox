package com.txl.blockmoonlighttreasurebox.utils;

import java.io.File;

public class FileUtils {
    public static boolean deleteFile(File file){
        if(file != null && file.exists()){
            if(file.isDirectory()){
                File[] files = file.listFiles();
                if(files != null){
                    for (File value : files) {
                        return deleteFile(value);
                    }
                }
            }
            return file.delete();
        }
        return false;
    }

    public static void closeStream(AutoCloseable closeable){
        if(closeable == null){
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
