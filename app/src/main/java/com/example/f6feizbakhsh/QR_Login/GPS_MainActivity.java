package com.example.f6feizbakhsh.QR_Login;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GPS_MainActivity extends Activity {
    public static final int JOB_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showMessage("onCreate-----------------> "+ R.string.job_scheduled_successfully);
        check_IMEI();
        this.finish();
    }
    private void check_IMEI()
    {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String contents = tm.getDeviceId();
        int interval = 0;
        Imei imei = new Imei(contents, interval);
        ServerRequests_IMEI_check serverRequests_IMEI_check = new ServerRequests_IMEI_check(this);
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
        ComponentName serviceName = new ComponentName(this, GPS_MyJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPeriodic( 60000 * returnedInterval) //60000 milliseconds = 1 minute
                .setPersisted(true)
                .setExtras(bundle)
                //.setOverrideDeadline(1) // Remove comment for faster testing.
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            showMessage("scheduleJob----------------->"+ R.string.job_scheduled_successfully);
        }
    }
    private static void showMessage(String message) {
        Log.i("GPS_MainActivity", message);
    }
}
