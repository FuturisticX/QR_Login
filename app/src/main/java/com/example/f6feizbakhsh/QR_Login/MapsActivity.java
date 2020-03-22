package com.example.f6feizbakhsh.QR_Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//public class MapsActivity extends AppCompatActivity implements View.OnClickListener {
public class MapsActivity extends Activity implements View.OnClickListener {
    private Button login, quit, logout,photo, show_it;
    private String url_insert = "http://www.ragelectric.com/u_insert_into_attendance.php";
    //String url_show = "http://www.ragelectric.com/u_show_attendance.php";
    private String  transaction;
    //private String time_in_out;
    Double longitude, latitude;
    int id, user_id;
    TextView  status, role; //, result, lati;
   // String last_id_created="";
    GetUserCallback callback;
   // Boolean GPS_is_good = false;
    private static Context context;
    GPSTracker gps;
    TextView name_;
    String mCurrentPhotoPath;
    //Location location;

    //
    //put this.finish here because if the user turns off the screen and
    //and when it turns back on, the screen will still be in
    //MapsActivity instead of AndroidQRCode
    //
    @Override
    protected void onStop()
    {
        super.onStop();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate( savedInstance );
        setContentView( R.layout.login_logout_screen_ );
        login = (Button) findViewById( R.id.login );
        logout = (Button) findViewById( R.id.logout );
        show_it = (Button) findViewById( R.id.show_it );
        photo = (Button) findViewById( R.id.photo );
        quit = (Button) findViewById( R.id.quit_);
        //result = (TextView) findViewById( R.id.show );
        //lati = (TextView) findViewById( R.id.lati );
        status = (TextView) findViewById( R.id.status );
        role = (TextView) findViewById( R.id.role );
        name_ = (TextView) findViewById(R.id.name_);
        //Bundle bundle = new Bundle();
        user_id=2;
        try {
            id = getIntent().getExtras().getInt( "id" );
            String name__= getIntent().getExtras().getString("name_");;
            name_.setText(name__);
            user_id = id; //from bundle via extra
        } catch (Exception e) { e.printStackTrace(); }

        gps = new GPSTracker(this);


        MapsActivity.context = getApplicationContext();

        login.setOnClickListener( this );
        logout.setOnClickListener( this );
        show_it.setOnClickListener( this );
        photo.setOnClickListener( this );
        quit.setOnClickListener( this );
        //disable_keys();
    }

    public void Add_record() {
        try {

            //SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            //String currentTimeStamp = dateFormat.format( new java.util.Date() ); // Find todays date

            //time_in_out = String.valueOf( currentTimeStamp );

            //new Attendance_in_out(this,status,role,1).execute(user_id,in,out,transaction,latitude,longitude);
            ArrayList<String> parm = new ArrayList<String>();
            parm.add( String.valueOf(  user_id ));
            parm.add( transaction );

            if(gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                parm.add( latitude.toString() );
                parm.add( longitude.toString() );
                //parm.add( time_in_out.toString() );
                mCurrentPhotoPath = createImageFile();
                parm.add( mCurrentPhotoPath);
                //
                //add record
                //
                new Attendance_create( context, status, role, 1 ).execute( parm );

                //
                //disable keys after adding.
                //
                disable_keys();
                //
                // Take photo of every login/logout
                //
                take_employee_photo_when_logging_in();

            } else {
                Toast.makeText(getApplicationContext(), "longitude/latitude is null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText( getApplicationContext(), "longitude/latitude is null", Toast.LENGTH_LONG ).show();
            e.printStackTrace();
        }
    }

    private String createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String date_directory = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = timeStamp + (transaction.equals("1")?"_IN_":"_OUT_") + "_" + name_.getText().toString().replace(" ","_") + "_" + String.valueOf(user_id) +".JPG"  ;

        return(imageFileName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //
        //on start, this will prevent any key from being clicked and calling disable_keys will only enable 1 button to to clickable
        //
        logout.setClickable( false );
        login.setClickable(false);
        disable_keys();
    }

    //
    //
    //
    //
    //When the program starts, it checks the last transaction. If there's no transaction, Login is enabled and logoff disabled.
    //If there's transaction and the latest was login, logoff is turned on and login is turned off.
    //If the last transaction is logoff, login button is turned on and logoff is turned off.
    //disable_key is called in Oncreate, on initial execution.
    //
    //When users clicks, it checks for the button if its disabled/enabled. If its enabled, it turns off the clickable
    // and Add_record() is invoked. Inside Add_record(), after record in inserted, disable_keys() will be called right away
    // and inside disable_keys() program. refer to the beginning comment.
    //
    //Time delay is included to avoid double clicking
    //
    @Override
    public void onClick(View v) {
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        Button b = (Button) v;
        if (gps.canGetLocation()) {
            if ((b.getId() == R.id.login) && login.isClickable() && login.isEnabled()) {
                Log.d( "", "Clicked Login" );
                login.setClickable( false );
                transaction = "1";
                Add_record();
            }
            if ((b.getId() == R.id.logout) && logout.isClickable() && logout.isEnabled()) {
                Log.d( "", "Clicked Logout" );
                logout.setClickable( false );
                transaction = "2";
                Add_record();
            }
            if ((b.getId() == R.id.show_it)) {
                Log.d( "", "Clicked Map" );
                try {
                    Intent intent = new Intent( getApplicationContext(), draw_map.class );
                    intent.putExtra( "longitude", Double.toString( longitude ) );
                    intent.putExtra( "latitude", Double.toString( latitude ) );
                    startActivity( intent );
                    //MapsActivity.this.finish();
                } catch (Exception e) {
                    Toast.makeText( getApplicationContext(), "longitude/latitude is null", Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "longitude/latitude is null", Toast.LENGTH_LONG).show();
        }
        if(b.getId() == R.id.photo){
            take_employee_photo();
        }
        if ((b.getId() == R.id.quit_)) {
                gps.stopUsingGPS();
                int p = android.os.Process.myPid();
                android.os.Process.killProcess( p );
        }

        long lastClickTime = 0;
        lastClickTime = SystemClock.elapsedRealtime();
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1500) {
            return;
        }
        lastClickTime = SystemClock.elapsedRealtime();

    }

    public  void disable_keys(){
        ArrayList<String> parmx = new ArrayList<String>();
        parmx.add(String.valueOf( user_id));
        //
        //pass the logout/login button so it will enable/disable the button on Count_of_in_out in the post_execute
        //
        new Count_number_of_in_out(context,status,role,login,logout,0).execute(parmx);
    }
    public void take_employee_photo(){
        ArrayList<String> parmx = new ArrayList<String>();
        parmx.add(String.valueOf( user_id));
        //
        //pass the user_id
        //
        new take_employee_photo(user_id);
        Intent intent = new Intent(getApplicationContext(), take_employee_photo.class);
        intent.putExtra("id", user_id);
        startActivity(intent);
    }

    public void take_employee_photo_when_logging_in(){
        String name_string = name_.getText().toString();
        /*
        ArrayList<String> parmx = new ArrayList<String>();
        parmx.add(String.valueOf( name_string));
        parmx.add(String.valueOf(user_id));
        parmx.add(String.valueOf(transaction));
        //
        //pass the user_id-
        //

        new picture_on_login(id, name_string,transaction);
        */
        //Intent intent = new Intent(getApplicationContext(), picture_on_login.class); //this will take picture but with preview
        Intent intent = new Intent(getApplicationContext(), Take_Picture.class);
        intent.putExtra("id", user_id);
        intent.putExtra("name_string",name_string);
        intent.putExtra("transaction", transaction);
        intent.putExtra("file_name_", mCurrentPhotoPath);

        this.startService(intent);
        this.finish();
    }

}