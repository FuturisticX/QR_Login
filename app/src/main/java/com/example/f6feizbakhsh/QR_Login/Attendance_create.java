package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 4/13/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

//public class Attendance_create  extends AsyncTask<String,Void,String>{
public class Attendance_create  extends AsyncTask<ArrayList<String>, Void, ArrayList<String>>{
    private TextView statusField,roleField;
    private Context context;
    private int byGetOrPost = 0;

    //flag 0 means get and 1 means post.(By default it is get.)
    public Attendance_create(Context context,TextView statusField,TextView roleField,int flag) {
        this.context = context;
        this.statusField = statusField;
        this.roleField = roleField;
        byGetOrPost = flag;
    }

    protected void onPreExecute(){
    }

    @Override
    //protected String doInBackground(String... arg0) {
    protected ArrayList<String> doInBackground(ArrayList<String>... params) {
        ArrayList<String> xy= new ArrayList<String>();
        xy.add("HELLO");

        if(byGetOrPost == 0){ //means by Get Method

            try{
                String user_id     = params[0].toString();
                String transaction = params[3].toString();
                String latitude    = params[2].toString();
                String longitude   = params[2].toString();
                String time_in_out = params[2].toString();

                String link =  "http://www.ragelectric.com/u_insert_into_attendance.php";
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                //return sb.toString();

            }

            catch(Exception e){
                //return new String("Exception: " + e.getMessage());
                return new ArrayList<String>( Integer.parseInt( "Exception: " + e.getMessage() ) );
            }
        }
        else{
            try{
                //String user_id     = "ben"; //Arrays.toString(params[1].toArray());

                String[] array     = params[0].toArray(new String[params[0].size()]);
                String user_id     = array[0].toString();
                //String in_         = array[1].toString();
                //String out_        = array[2].toString();
                String transaction = array[1].toString();
                String latitude    = array[2].toString();
                String longitude   = array[3].toString();
                String time_in_out = array[4].toString();
                String mFile       = array[4].toString(); // change from 5 to 4

                String link= "http://www.ragelectric.com/u_insert_into_attendance.php";
                String data  = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                data += "&" + URLEncoder.encode("transaction", "UTF-8") + "=" + URLEncoder.encode(transaction, "UTF-8");
                data += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8");
                data += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");
                //data += "&" + URLEncoder.encode("time_in_out", "UTF-8") + "=" + URLEncoder.encode(time_in_out, "UTF-8");


                data += "&" + URLEncoder.encode("file_name", "UTF-8") + "=" + URLEncoder.encode(mFile, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                    break;
                }
                //return sb.toString();
                return xy;
            }
            catch(Exception e){
                //return new String("Exception: " + e.getMessage());
                return xy;
            }
        }
        return xy;
    }

    @Override
    protected void onPostExecute(ArrayList<String>result){
        this.statusField.setText("Record Created"); //change this so there is parameter send back to calling program
        this.roleField.setText(result.toString());
    }
}