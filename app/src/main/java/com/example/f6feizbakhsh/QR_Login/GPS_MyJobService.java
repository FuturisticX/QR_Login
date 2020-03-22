package com.example.f6feizbakhsh.QR_Login;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class GPS_MyJobService extends JobService {
    static final String TAG = GPS_MyJobService.class.getSimpleName();

    Double longitude, latitude;

    GPS_Create_Latitude_Longitude_Record create_latitude_longitude_record;
    private static Context context;

    @Override
    public boolean onStartJob(JobParameters params) {
        // Note: this is preformed on the main thread.
        String pb =params.getExtras().getString("id");
        GPS_MyJobService.context = getApplicationContext();
        GPSTracker gps = new GPSTracker(getApplicationContext());

        /*
        Check if hour falls  between 5:00 AM to 5:00 PM
         */
        //24 hour format
        Calendar cal = Calendar.getInstance();
    //    SimpleDateFormat dateFormat = new SimpleDateFormat("kk");
    //    dateFormat.format(cal.getTime());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        showMessage("onStartJob-----------------------hour is ------------->" + hour);
        /*
        add this
         */

        if(gps.canGetLocation() && (gps.getLatitude()!=0 && gps.getLongitude()!=0 ) && (hour > 4 && hour < 18  )) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            ArrayList<String> parm = new ArrayList<String>();
            parm.add(pb.toString());
            parm.add(latitude.toString());
            parm.add(longitude.toString());
            create_latitude_longitude_record = (GPS_Create_Latitude_Longitude_Record) new GPS_Create_Latitude_Longitude_Record(context, 1).execute(parm);
            showMessage("onStartJob----------------------->Created Latitude-->"+latitude.toString() + " Longitude-->"+longitude.toString());
        } else {
            showMessage("onStartJob----------------------->Walang GPS");
        }
        return true;
    }

    // Stopping jobs if our job requires change.

    @Override
    public boolean onStopJob(JobParameters params) {
        // Note: return true to reschedule this job.
        boolean shouldReschedule=false;
        showMessage("onStopJob----------------------->onStopJob id=" + params.getJobId());
        return shouldReschedule;
    }
    private static void showMessage(String message) {
        Log.i("GPS_MyJobService", message);
    }
}