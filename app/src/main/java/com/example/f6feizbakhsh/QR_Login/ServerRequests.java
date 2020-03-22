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
public class ServerRequests {
    ProgressDialog progressDialog;
    //public static final int CONNECTION_TIMEOUT = 10000 * 15;
    public static final int CONNECTION_TIMEOUT = 20000 * 15;
    public static final String SERVER_ADDRESS = "http://www.ragelectric.com/";
    String username;
    int id;

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        Log.d("ServerRequests", "");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
        Log.d("storeUserDataIn", "");
    }

    public void fetchUserDataInBackground(User user, GetUserCallback userCallback) {
        progressDialog.show();
        new fetchUserDataAsyncTask(user,userCallback).execute();
        Log.d("fetchUser", "");
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
            Log.d("StoreUserDataAsync", "");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("doInBackground", "");
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "register.php");
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
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        int id;
        String name_;
        GetUserCallback userCallback;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            //dataToSend.add(new BasicNameValuePair("password", user.password));

            Log.d("Array ", dataToSend.toString() );
            //
            //
            //
            //


            //String transaction = array[1].toString();
            String link =  SERVER_ADDRESS + "FetchUserData.php";


            //
            //
            //
            //

            //HttpParams httpRequestParams = new BasicHttpParams();
            //HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            //HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            //HttpClient client = new DefaultHttpClient(httpRequestParams);
            //HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");
            User returnedUser = null;

            try {
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

                URL url = new URL(link);
                String paramsString = URLEncodedUtils.format(dataToSend, "UTF-8");
                HttpGet httpGet = new HttpGet(url + "?" + paramsString);
                httpGet.setHeader("Accept", "application/json");

                HttpResponse httpResponse = client.execute(httpGet);

                //post.setEntity(new UrlEncodedFormEntity(dataToSend));
                //HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);


                JSONObject jsonResponse = new JSONObject( result );
                JSONArray jsonMain = jsonResponse.optJSONArray("user");
                //
                //It will only loop once.
                //

                for(int i=0; i <jsonMain.length(); i++) {
                    JSONObject jsonChild = jsonMain.getJSONObject( i );
                    //String username = jsonChild.optString( "username" );
                    //String password = jsonChild.optString( "password" );
                    id              = jsonChild.optInt( "id" );
                    name_           = jsonChild.optString("firstname") + " " + jsonChild.optString("lastname");
                }

                if(id > 0) {
                    returnedUser =  new User(user.username,-1 ,id, name_);
                } else {
                    returnedUser = null;
                }

            } catch  ( Exception e) {
                e.printStackTrace();
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser){
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }
}