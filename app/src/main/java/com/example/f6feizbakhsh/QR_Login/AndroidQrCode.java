package com.example.f6feizbakhsh.QR_Login;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


//public class AndroidBarcodeQrExample extends Activity {
public class AndroidQrCode extends Activity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final int JOB_ID = 1;
    User user_verify;


    String contents ;
    int interval;
    Imei imei;



    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        showMessage("----------onStop()");
    }

    @Override
    public void onPause(){
        super.onPause();
        showMessage("-------------onPause()");
    }

    @Override
    public void onRestart(){
        super.onRestart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMessage("onCreate ");
        //set the main content layout of the Activity
        setContentView(R.layout.scan_qr_bar);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        //
        //Check if Job is not running
        //
        /*
        if(!isServiceRunning("com.example.f6feizbakhsh.QR_Login.GPS_MyJobService"))
        {
            showMessage("First Run of GPS");
            check_IMEI();
        }
        */

        if(!isServiceRunning("com.example.f6feizbakhsh.QR_Login.UpdateService"))
        {
            showMessage("First Run of ON/OFF");
            Context context = getApplicationContext();
            KeyguardManager _guard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock _keyguardLock = _guard.newKeyguardLock("KeyguardLockWrapper");
            _keyguardLock.disableKeyguard();
            //AndroidQrCode.this.startService(new Intent(AndroidQrCode.this, UpdateService.class));
            Intent serviceIntent = new Intent(this, UpdateService.class);
            serviceIntent.putExtra("Start", "yes");
            this.startService(new Intent(AndroidQrCode.this, UpdateService.class));
            //startService(serviceIntent);
        }
    }

    public boolean isServiceRunning(String _param) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            //showMessage(service.service.getClassName());
            //if("com.example.f6feizbakhsh.QR_Login.Take_Picture".equals(service.service.getClassName())) {
            //if("com.ragelectric.player.MusicService".equals(service.service.getClassName())) {
            if(_param.equals(service.service.getClassName())) {
                showMessage(_param + " is RUNNING is " + _param);
                return true;
            }
        }
        showMessage("....it is starting");
        return false;
    }

    private void check_IMEI()
    {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        contents = tm.getDeviceId();
        interval = 0;
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
        int _milli = 60000;
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresDeviceIdle(false)   //should be true because if its false, when screen is open and it triggers GPS, MAP will suddenly open
                .setRequiresCharging(false)
                .setPeriodic( returnedInterval==1? _milli : _milli * returnedInterval) //60000 milliseconds = 1 minute
                .setPersisted(true)
                .setExtras(bundle)
                //.setOverrideDeadline(1) // Remove comment for faster testing.
                .setOverrideDeadline(1) // added this
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            showMessage("scheduleJob----------------->"+ R.string.job_scheduled_successfully);
        }
    }

    private static void showMessage(String message) {
        Log.i("AndroidQRCode-------->", message);
    }

    //product barcode mode
    public void scanBar(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(AndroidQrCode.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }
            //product qr code mode
    public void scanQR(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            //showDialog(AndroidBarcodeQrExample.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
            showDialog(AndroidQrCode.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();

        }
    }
    public void quit_(View v) {
        try {

            this.finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (ActivityNotFoundException anfe){

        }
    }

            //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                }
            }

        });

        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        return downloadDialog.show();
    }

            //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                authenticate(contents);

            }
        }
    }


    private void authenticate(String contents){
        User user = new User(contents );
        showMessage("Entered authenticate");
        ServerRequests serverRequests = new ServerRequests(this);

        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {

                user_verify = returnedUser;
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    loginUserIn(user_verify);

                }
            }
        });
    }

    private void loginUserIn(User returnedUser) {


        final User return_ = returnedUser;
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String contents = tm.getDeviceId();
        int interval = 0;
        Imei imei = new Imei(contents,interval);

        ServerRequests_IMEI_check serverRequests_IMEI_check = new ServerRequests_IMEI_check(this);
        serverRequests_IMEI_check.fetchUserDataInBackground(imei, new GetImeiCallback() {
            @Override
            public void done(Imei returnedImei) {
                if (returnedImei == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "CALL JAMES AT (323)954-7111 EXT 109 TO REGISTER THIS DEVICE", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("id", return_.id);
                    intent.putExtra("name_", return_.name_);
                    startActivity(intent);
                }
            }
        });

    }


    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AndroidQrCode.this);
        dialogBuilder.setMessage("Por favor puede chequiar las sus ajuste en sus telefono \"Mobile HotSpot and Thethering.\" A la mejor esta a pagado ");
        dialogBuilder.setPositiveButton("Ok",null);
        dialogBuilder.show();
    }
}
