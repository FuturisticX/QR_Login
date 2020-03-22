package com.example.f6feizbakhsh.QR_Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/*import android.content.Entity;
import android.provider.Settings;*/
//import org.w3c.dom.NameList;//
/*import org.apache.*;*/

/**
 * Created by f6feizbakhsh on 2/12/2016.
 */
public class ServerRequests_IMEI_check_ {
    ProgressDialog progressDialog;
    //public static final int CONNECTION_TIMEOUT = 10000 * 15;
    public static final int CONNECTION_TIMEOUT = 20000 * 15;
    public static final String SERVER_ADDRESS = "http://www.ragelectric.com/";
    //String username;
    int id;

    public ServerRequests_IMEI_check_(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        Log.d("ServerRequests", "");
    }

    public void storeUserDataInBackground(Imei imei, GetImeiCallback imeiCallback) {
        progressDialog.show();
        new StoreUserDataAsyncTask(imei, imeiCallback).execute();
        Log.d("storeUserDataIn", "");
    }

    public void fetchUserDataInBackground(Imei imei, GetImeiCallback imeiCallback) {
        progressDialog.show();
        new fetchUserDataAsyncTask(imei,imeiCallback).execute();
        Log.d("fetchUser", "");
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Imei imei;
        GetImeiCallback imeiCallback;

        public StoreUserDataAsyncTask(Imei imei, GetImeiCallback imeiCallback) {
            this.imei = imei;
            this.imeiCallback = imeiCallback;
            Log.d("StoreUserDataAsync", "");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("doInBackground", "");
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("imei", imei.imei_));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "imei.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
                Log.d(" Posted it","");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("onPostExecute", "");
            progressDialog.dismiss();
            imeiCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, Imei> {
        Imei imei;
        int id;
        String name_;
        GetImeiCallback imeiCallback;

        public fetchUserDataAsyncTask( Imei imei, GetImeiCallback imeiCallback) {
            this.imei = imei;
            this.imeiCallback = imeiCallback;
        }

        @Override
        protected Imei doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("imei", imei.imei_));
            //dataToSend.add(new BasicNameValuePair("password", user.password));

            Log.d("Array ", dataToSend.toString() );
            //
            //
            //
            //


            //String transaction = array[1].toString();
            String link =  SERVER_ADDRESS + "imei.php";


            //
            //
            //
            //

            //HttpParams httpRequestParams = new BasicHttpParams();
            //HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            //HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            //HttpClient client = new DefaultHttpClient(httpRequestParams);
            //HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");
            Imei returnedImei = null;

            try {
                HttpClient client = new DefaultHttpClient();
                //
                //
                //
                HttpParams httpParams=client.getParams();
              //HttpParams httpParams=new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
                //
                //
                //
                //
                URL url = new URL(link);
                String paramsString = URLEncodedUtils.format(dataToSend, "UTF-8");
                HttpGet httpGet = new HttpGet(url + "?" + paramsString);
                httpGet.setHeader("Accept", "application/json");

                HttpResponse httpResponse = client.execute(httpGet);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);


                JSONObject jsonResponse = new JSONObject( result );
                JSONArray jsonMain = jsonResponse.optJSONArray("imei");
                //
                //It will only loop once.
                //
                String returned_imei = null;
                for(int i=0; i <jsonMain.length(); i++) {
                    JSONObject jsonChild = jsonMain.getJSONObject( i );
                    returned_imei = jsonChild.optString( "imei" );
                }

                if(!returned_imei.isEmpty()) {
                    //returnedImei =  new Imei(returned_imei);
                } else {
                    returnedImei = null;
                }
                //}
            } catch  ( Exception e) {
                e.printStackTrace();
            }
            return returnedImei;
        }

        @Override
        protected void onPostExecute(Imei returnedImei){
            progressDialog.dismiss();
            imeiCallback.done(returnedImei);
            super.onPostExecute(returnedImei);
        }
    }
}