package com.example.test;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            TextView textView = findViewById(R.id.tv_text);
            textView.setText("我是：  "+num++);
//            textView.postDelayed(this,2500);

            int i = 1;
            while (i < 500_000_000){
                i++;
            }
            textView.postDelayed(this,16);
        }
    };

    int num = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            Toast.makeText(MainActivity.this,"获取部分权限成功，但部分权限未正常授予",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(MainActivity.this,"被永久拒绝授权，请手动授予文件读写权限权限",Toast.LENGTH_SHORT).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页
                        } else {
                            Toast.makeText(MainActivity.this,"获取文件读写权限失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        findViewById(R.id.tv_text).postDelayed(runnable,2500);
        findViewById(R.id.tv_text).postDelayed(new Runnable() {
            @Override
            public void run() {
                consumeCpu();
            }
        },3*5000);
        findViewById(R.id.tv_test_thread_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testThreadTime();
            }
        });

    }

    private void consumeCpu(){
        int i = 1;
        while (i<50){
            int j = 1;
            while (j < 500_000_000){
                j++;
            }
            i++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        consumeCpu();
    }

    Object object = new Object();

    private void testThreadTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = SystemClock.currentThreadTimeMillis();
                int i = 0;
                while (i < 1000000000){
                    i++;
                }
                long time2 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread2 time1 : "+time1+"  time2 : "+time2 + " dealt : "+(time2 - time1));
            }
        },"thread2").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread1 time1 : start "+time1);
                try {
                    synchronized (object){
                        Thread.sleep(3000);
                        SystemClock.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long time2 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread1 time1 : "+time1+"  time2 : "+time2 + " dealt : "+(time2 - time1));
            }
        },"thread1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread3 time1 : start "+time1);
                synchronized (object){
                    Log.d(TAG,"get object");
                }
                long time2 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG,"thread3 time1 : "+time1+"  time2 : "+time2 + " dealt : "+(time2 - time1));
            }
        },"thread3").start();
    }
}