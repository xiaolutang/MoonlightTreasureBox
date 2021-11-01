package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class AnrTestBroadcast extends BroadcastReceiver {
    private static String TAG = AnrTestBroadcast.class.getSimpleName();
    private static final String ACTION_TEST_ANR = "com.txl.test_anr";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"onReceive action : "+action);
    }

    public static void sentBroadcast(Context context){
        Intent intent = new Intent();
        intent.addFlags( Intent.FLAG_RECEIVER_FOREGROUND );
        intent.setAction(ACTION_TEST_ANR);
        context.sendOrderedBroadcast(intent,null);
    }

    public static AnrTestBroadcast register(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TEST_ANR);
        AnrTestBroadcast anrTestBroadcast = new AnrTestBroadcast();
        context.registerReceiver(anrTestBroadcast,intentFilter);
        return anrTestBroadcast;
    }
}
