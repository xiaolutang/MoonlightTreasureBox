package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

public class AnrTestActivity extends AppCompatActivity {
    boolean sleep = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_test);
        findViewById(R.id.tvTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sleep){
                    sleep = true;
                    SystemClock.sleep(10000);
                    sleep = false;
                }

            }
        });
    }
}