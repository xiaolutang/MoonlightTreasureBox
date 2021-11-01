package com.txl.blockmoonlighttreasurebox.handle;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.txl.blockmoonlighttreasurebox.block.BlockMonitorFace;
import com.txl.blockmoonlighttreasurebox.info.AnrInfo;
import com.txl.blockmoonlighttreasurebox.info.MessageInfo;
import com.txl.blockmoonlighttreasurebox.info.ScheduledInfo;
import com.txl.blockmoonlighttreasurebox.sample.manager.IAnrSamplerListener;
import com.txl.blockmoonlighttreasurebox.utils.AppExecutors;
import com.txl.blockmoonlighttreasurebox.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：
 */
public class FileSample implements IAnrSamplerListener {

    private static final String TAG = FileSample.class.getSimpleName();
    public static final FileCache<AnrInfo> fileCache = new FileCache<>();
    public static final FileSample instance = new FileSample();

    private AnrInfo anrInfo = new AnrInfo();


    private FileSample() {
        fileCache.init(BlockMonitorFace.getBlockMonitorFace().getApplicationContext(),"block_anr",10,"1.0.0");
    }

    @Override
    public void onMessageQueueSample(long baseTime, String msgId, String msg) {
        anrInfo.messageQueueSample.append( msg );
    }

    @Override
    public void onCpuSample(long baseTime, String msgId, String msg) {

    }

    @Override
    public void onMemorySample(long baseTime, String msgId, String msg) {
    }

    @Override
    public void onMainThreadStackSample(long baseTime, String msgId, String msg) {
        anrInfo.mainThreadStack = msg;
    }

    @Override
    public void onSampleAnrMsg() {
        synchronized (this){
            AnrInfo temp = anrInfo;
            String path = FileCache.sFormat.format(new Date());
            if(TextUtils.isEmpty(temp.markTime)){
                temp.markTime = path;
            }
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG,"cacheData schedule size "+temp.scheduledSamplerCache.getAll().size()+"  file name : "+temp.markTime);
                    fileCache.cacheData(temp.markTime,temp);
                    //通知可以展示ui
                }
            });
        }
    }

    @Override
    public void onScheduledSample(boolean start,long baseTime, String msgId, long dealt) {
        synchronized (this){
            anrInfo.scheduledSamplerCache.put( baseTime,new ScheduledInfo( dealt,msgId ,start) );
        }
    }

    @Override
    public void onMsgSample(long baseTime, String msgId, MessageInfo msg) {
        synchronized (this){
            if(msg.msgType == MessageInfo.MSG_TYPE_GAP && anrInfo.messageSamplerCache.getLastValue().msgType == MessageInfo.MSG_TYPE_GAP){
                Log.e(TAG,"error continuous gap");
            }
            anrInfo.messageSamplerCache.put( baseTime,msg );
        }
    }

    @Override
    public void onJankSample(String msgId, MessageInfo msg) {
        StringBuilder builder = new StringBuilder();
        builder.append( "onJankSample" )
                .append( " msgId : " )
                .append( msgId )
                .append( "  msg : " )
                .append( msg );
        Log.d( TAG,new String(builder) );
    }

    @Override
    public void messageQueueDispatchAnrFinish() {
        AnrInfo temp = anrInfo;
        anrInfo = new AnrInfo();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                fileCache.cacheData(temp.markTime,temp);
            }
        });

    }

    public static class FileCache<T extends Serializable> {
        private static final String TAG = FileCache.class.getSimpleName();
        private final String LAST_VERSION = "last_version";
        private File diskCacheDir;

        /**
         * 指定目录下最多能够存储多少个文件
         * */
        private int maxSize = 20;
        private int currentSize;
        private static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        private FileCache() {
        }

        private File getDiskCacheDir(Context context, String uniqueName){
            boolean externalStorageAvailable = Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED );
            final String cachePath;
            if(externalStorageAvailable){
                cachePath = context.getExternalCacheDir().getPath();
            }else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File( cachePath+File.separator+uniqueName );
        }


        public void init(Context context, String rootDir, int maxSize, String appVersion){
            if(maxSize > 2){
                this.maxSize = maxSize;
            }
            SharedPreferences sp = context.getSharedPreferences("moonlight_anr",Context.MODE_PRIVATE);
            String lastVersion = sp.getString(LAST_VERSION,"");
            sp.edit().putString(LAST_VERSION,appVersion)
                    .apply();
            diskCacheDir = getDiskCacheDir(context, rootDir);
            if(diskCacheDir.exists() && !appVersion.equals(lastVersion)){//版本不一致删除
                FileUtils.deleteFile(diskCacheDir);
            }
            if(!diskCacheDir.exists()){
                diskCacheDir.mkdirs();
            }
            Log.d(TAG,"cache path : "+diskCacheDir.getPath());
            File[] files = diskCacheDir.listFiles();
            currentSize = files == null ? 0 : files.length;
            long m = getUsableSpace(diskCacheDir);
        }

        public synchronized void cacheData(String path, T serializable){
//如果文件不存在就创建文件
            File file=new File(diskCacheDir.getPath()+ File.separator+path);
            //file.createNewFile();
            //获取输出流
            //这里如果文件不存在会创建文件，  如果文件存在，新写会覆盖以前的内容吗？
            ObjectOutputStream fos = null;
            try {
                fos=new ObjectOutputStream(new FileOutputStream(file));
                fos.writeObject(serializable);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                FileUtils.closeStream(fos);
            }
            currentSize = diskCacheDir.listFiles() == null ? 0 : diskCacheDir.listFiles().length;
            if(currentSize>maxSize){
                removeLastFile();
            }
        }

        private synchronized void removeLastFile(){
            File[] files = diskCacheDir.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName()) ;
                }
            });
            //把最小的移除掉
            Log.d(TAG,"removeLastFile file name: "+files[0].getName());
            FileUtils.deleteFile(files[0]);
        }

        public  synchronized List<T>  restoreData(){
            List<T> result = new ArrayList<>();
            File[] files = diskCacheDir.listFiles();
            if(files == null){
                return result;
            }
            for (File file:files){
                ObjectInputStream ois=null;
                try {
                    //获取输入流
                    ois=new ObjectInputStream(new FileInputStream(file));
                    //获取文件中的数据
                    T data= (T) ois.readObject();
                    result.add(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    try {
                        if (ois!=null) {
                            ois.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }


        private long getUsableSpace(File path){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
                return path.getUsableSpace();
            }
            final StatFs statFs = new StatFs( path.getPath() );
            return statFs.getBlockSizeLong() + statFs.getAvailableBlocksLong();
        }
    }
}
