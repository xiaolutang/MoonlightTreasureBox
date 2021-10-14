package com.example.test;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            TextView textView = findViewById(R.id.tv_text);
            textView.setText("我是：  "+num++);
            textView.postDelayed(this,2500);
        }
    };

    int num = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_text).postDelayed(runnable,2500);
        findViewById(R.id.tv_test_thread_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testThreadTime();
            }
        });
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