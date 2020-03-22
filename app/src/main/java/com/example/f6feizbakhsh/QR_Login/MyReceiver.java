package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 8/19/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        showMessage("OnCreate");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        Intent i = new Intent(context, UpdateService.class);
        i.putExtra("screen_state", screenOff);
        i.putExtra("Start","no");
        context.startService(i);
    }
    private static void showMessage(String message) {
        Log.i("MyReceiver", message);
    }

}
