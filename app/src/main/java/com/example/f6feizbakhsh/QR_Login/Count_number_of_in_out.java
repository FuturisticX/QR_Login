package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 4/13/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//public class Attendance_create  extends AsyncTask<String,Void,String>{
public class Count_number_of_in_out  extends AsyncTask<ArrayList<String>, Void, ArrayList<String>>{
    private TextView statusField,roleField;
    private Context context;
    private int byGetOrPost = 0;
    Count_number_of_in_out exxon;
    Button logout;
    Button login;
    int diff=0;
    String user_id;
    int transaction=0;
    public static final int CONNECTION_TIMEOUT = 20000 * 15;

    //flag 0 means get and 1 means post.(By default it is get.)
    public Count_number_of_in_out(Context context, TextView statusField, TextView roleField, Button login, Button logout, int flag) {

        this.context = context;
        this.statusField = statusField;
        this.roleField = roleField;
        this.login = login;
        this.logout=logout;
        byGetOrPost = flag;
    }

    protected void onPreExecute(){
    }

    @Override
    protected ArrayList<String> doInBackground(ArrayList<String>... params) {
        ArrayList<String> xy= new ArrayList<String>();

        if(byGetOrPost == 0){ //means by Get Method
            try{
                String result="";
                String[] array     = params[0].toArray(new String[params[0].size()]);
                String user_id     = array[0].toString();

                List<NameValuePair> x_name_value_pairs = new ArrayList<NameValuePair>(1);
                x_name_value_pairs.add(new BasicNameValuePair("user_id", user_id));

                //String transaction = array[1].toString();
                String link =  "http://www.ragelectric.com/u_count_in_out.php";
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                //
                //
                //
                HttpParams httpParams=client.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
                //
                //
                //
                //

                String paramsString = URLEncodedUtils.format(x_name_value_pairs, "UTF-8");
                HttpGet httpGet = new HttpGet(url + "?" + paramsString);
                httpGet.setHeader("Accept", "application/json");
                HttpResponse response = client.execute(httpGet);
                //HttpGet request = new HttpGet();
                //request.setURI(new URI(link));
                // HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while ((line = in.readLine()) != null) {
                    result +=line;
                    break;
                }
                in.close();

                JSONObject jsonResponse = new JSONObject( result );
                JSONArray jsonMain = jsonResponse.optJSONArray("count_in_out");
                for(int i=0; i <jsonMain.length(); i++) {
                    JSONObject jsonChild = jsonMain.getJSONObject( i );
                    transaction = jsonChild.optInt( "transaction" );
                }
                //return sb.toString();

                return xy;
            } catch(Exception e){
                //return new String("Exception: " + e.getMessage());
                return new ArrayList<String>( Integer.parseInt( "Exception: " + e.getMessage() ) );
            }
        } return xy;
    }

    @Override
    protected void onPostExecute(ArrayList<String>result){
        //
        // this will serve as status bar.
        //
        //this.statusField.setText(x_message) ;
        //this.roleField.setText(result.toString());
        //
        //if last transaction =(1)login, then turn on logout button.
        //
        String tran_str="";
        if(transaction==1)
        {
            tran_str="LOGIN";
        }
        if(transaction==2)
        {
            tran_str="LOGOUT";
        }
        if(transaction==0)
        {
            tran_str="NO NO NO NO NO";
        }

        if(transaction==1 )
        {
            this.login.setClickable(false);
            this.login.setEnabled( false );
            this.logout.setEnabled( true );
            this.logout.setClickable(true);
            Log.d("d","turned off login---->After checking last transaction was " + tran_str);
        }
        if(transaction==2)
        {
            this.login.setClickable(true);
            this.login.setEnabled( true);
            this.logout.setClickable(false);
            this.logout.setEnabled( false );
            Log.d("d","turned off logout---->After checking last transaction was " + tran_str);
        }
        //
        //If theres no login and Logout is enabled. You must turn off logout and turn on login
        //
        if(transaction==0 &&  logout.isClickable())
        {
            this.login.setClickable(true);
            this.login.setEnabled( true);
            this.logout.setClickable(false);
            this.logout.setEnabled( false );
            Log.d("","Logout is on and login is not found. Very important erro to remove");
        }
        if( transaction==0 && !(login.isClickable()) )
        {
            this.login.setClickable(true);
            this.login.setEnabled( true);
            this.logout.setClickable(false);
            this.logout.setEnabled( false );
            Log.d("d","turned off logout---->After checking theres no transaction");
        }

    }
}