package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 8/19/2016.
 */

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GPS_Receiver extends BroadcastReceiver {

    public static final int JOB_ID = 1;
    Context context_;
    public GPS_Receiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())  ) {
            /*
            Intent serviceIntent = new Intent(context, GPS_MyJobService.class);
            context.startService(serviceIntent);
            */
            showMessage("onReceive----------------------->ACTION_BOOT_COMPLETED" );

            context_ = context;

            showMessage("onReceive----------------------->Checking IMEI" );
            check_IMEI();
        }
    }

    private void check_IMEI()
    {
        TelephonyManager tm = (TelephonyManager) context_.getSystemService(context_.TELEPHONY_SERVICE);
        String contents = tm.getDeviceId();
        int interval = 0;
        Imei imei = new Imei(contents, interval);
        ServerRequests_IMEI_check serverRequests_IMEI_check = new ServerRequests_IMEI_check(context_);
        serverRequests_IMEI_check.fetchUserDataInBackground(imei, new GetImeiCallback() {
            @Override
            public void done(Imei returnedImei) {
                if (returnedImei == null) {
                    showMessage("CALL JAMES AT (323)954-7111 EXT 109 TO REGISTER THIS DEVICE");
                } else {
                    showMessage("check_IMEI----------------->"+ R.string.job_scheduled_successfully);
                    scheduleJob(returnedImei.imei_.toString(),returnedImei.interval_);
                }
            }
        });
    }

    private void scheduleJob(String returnedImei, int returnedInterval) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("id", returnedImei);
        ComponentName serviceName = new ComponentName(context_, GPS_MyJobService.class);
        int _milli = 60000;
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setExtras(bundle)
                .setPeriodic(returnedInterval==1? _milli : _milli * returnedInterval)
                //.setOverrideDeadline(1) // Remove comment for faster testing.
                .build();

        JobScheduler scheduler = (JobScheduler) context_.getSystemService(context_.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            showMessage("scheduleJob----------------->"+ R.string.job_scheduled_successfully);
            showMessage(" Interval is ------>" + String.valueOf(returnedInterval==1? _milli : _milli * returnedInterval));
        }
    }

    private static void showMessage(String message) {
        Log.i("GPS_Receiver", message);
    }

}
