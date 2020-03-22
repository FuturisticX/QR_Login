package com.example.f6feizbakhsh.QR_Login;

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


/**
 * Created by f6feizbakhsh on 2/12/2016.
 */
public class ServerRequests_IMEI_check {


    public static final int CONNECTION_TIMEOUT = 20000 * 15;
    public static final String SERVER_ADDRESS = "http://www.ragelectric.com/";
    int id;

    public ServerRequests_IMEI_check(Context context) {
        showMessage("ServerRequests");
    }

    public void storeUserDataInBackground(Imei imei, GetImeiCallback imeiCallback) {
        new StoreUserDataAsyncTask(imei, imeiCallback).execute();
        showMessage("storeuserDataInBackground");
    }

    public void fetchUserDataInBackground(Imei imei, GetImeiCallback imeiCallback) {

        new fetchUserDataAsyncTask(imei,imeiCallback).execute();
        showMessage("fetchUserDataInBackground");
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Imei imei;
        GetImeiCallback imeiCallback;

        public StoreUserDataAsyncTask(Imei imei, GetImeiCallback imeiCallback) {
            this.imei = imei;
            this.imeiCallback = imeiCallback;
            showMessage("StoreUserDataAsyncTask");
        }

        @Override
        protected Void doInBackground(Void... params) {

            showMessage("doInBackground");
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
                showMessage("Posted it");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            showMessage("onPostExecute");

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
            showMessage(dataToSend.toString());
            String link =  SERVER_ADDRESS + "imei_returned_id.php"; //"spinner_week_from_server.php"; //
            Imei returnedImei = null;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpParams httpParams=client.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
                URL url = new URL(link);
                String paramsString = URLEncodedUtils.format(dataToSend, "UTF-8");
                HttpGet httpGet = new HttpGet(url + "?" + paramsString);
                httpGet.setHeader("Accept", "application/json");

                HttpResponse httpResponse = client.execute(httpGet);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);


                JSONObject jsonResponse = new JSONObject( result );
                JSONArray jsonMain = jsonResponse.optJSONArray("imei_id");

                //
                //It will only loop once.
                //
                String returned_imei = null;
                int returned_interval = 0;
                for(int i=0; i <jsonMain.length(); i++) {
                    JSONObject jsonChild = jsonMain.getJSONObject( i );
                    returned_imei = jsonChild.optString( "id" ); //this will return ID of the IMEI in the file
                    returned_interval = jsonChild.optInt( "interval_" ); ;
                }

                if(!returned_imei.isEmpty()) {
                    returnedImei =  new Imei(returned_imei, returned_interval);
                } else {
                    returnedImei = null;
                }

            } catch  ( Exception e) {
                e.printStackTrace();
            }
            return returnedImei;
        }

        @Override
        protected void onPostExecute(Imei returnedImei){

            imeiCallback.done(returnedImei);
            super.onPostExecute(returnedImei);
        }
    }
    private static void showMessage(String message) {
        Log.i("SrvrRequests_IMEI_chck", message);
    }
}