package com.example.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class AnrService extends Service {
    public AnrService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(20000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}