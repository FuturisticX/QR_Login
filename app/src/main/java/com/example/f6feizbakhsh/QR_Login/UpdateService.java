package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 8/19/2016.ou
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class UpdateService extends Service {

    BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        showMessage("OnCreate");
        // register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(mReceiver);
        showMessage("onDestroy Receiver Called");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        boolean screenOn=false;
        String _start = "";
        showMessage("Inside on Start");
        try {
            screenOn = intent.getBooleanExtra("screen_state", false);

            Bundle extras = intent.getExtras();
            if (extras != null) {
                _start = (String) extras.get("Start");
            }
        } catch (Exception ex)
        {  }
        finally
        {}

        if (!screenOn && !_start.equals("yes")) {
            showMessage("OOOOOOONNNNNNN   value of _start is " + _start + "   applicationContext " +  getApplicationContext());
            Intent i = new Intent(getApplicationContext(), AndroidQrCode.class);
            //
            //You need this i.set otherwise exception will happen
            //android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
            //

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(i);
        } else {
            showMessage("OFF");
            if(!screenOn && _start.equals("yes")) {
                showMessage("ON pala   value of _start is " + _start);
            }
            if(!screenOn && _start.equals("no")) {
                showMessage("ON pala1   value of _start is " + _start);
            }

        }
    }
    private static void showMessage(String message) {
        Log.i("UpdateService", message);
    }
}