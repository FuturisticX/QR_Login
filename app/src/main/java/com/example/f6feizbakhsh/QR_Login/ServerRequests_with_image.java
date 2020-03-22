package com.example.f6feizbakhsh.QR_Login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*import android.content.Entity;
import android.provider.Settings;*/
//import org.w3c.dom.NameList;//
/*import org.apache.*;*/

/**
 * Created by f6feizbakhsh on 2/12/2016.
 */
public class ServerRequests_with_image {
    //ProgressDialog progressDialog;
    //public static final int CONNECTION_TIMEOUT = 10000 * 15;
    public static final int CONNECTION_TIMEOUT = 20000 * 15;
    public static final String SERVER_ADDRESS = "http://www.ragelectric.com/";

    public ServerRequests_with_image(Context context) {
        /*
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        */
        showMessage("Inside ServerRequest");
    }

    public void storeUserDataInBackground(User_with_picture user_with_picture, String master_detail, GetUserCallback_with_picture userCallback_with_picture ) {
        //progressDialog.show();
        new StoreUserDataAsyncTask(user_with_picture, master_detail, userCallback_with_picture).execute();
        showMessage("storeUserDataIn");
    }



    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, User_with_picture> {
        User_with_picture user_with_picture;
        GetUserCallback_with_picture userCallback_with_picture;
        String master_detail;

        public StoreUserDataAsyncTask(User_with_picture user_with_picture, String master_detail, GetUserCallback_with_picture userCallback_with_picture) {
            this.user_with_picture = user_with_picture;
            this.userCallback_with_picture = userCallback_with_picture;
            this.master_detail=master_detail;
            showMessage("StoreUserDataAsync" );
        }

        @Override
        protected User_with_picture doInBackground(Void... params) {
            InputStream is = null;
            String result = "";
            User_with_picture returnedUser=null;

            showMessage("doInBackground");
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", user_with_picture.id + ""));
            dataToSend.add(new BasicNameValuePair("image_", user_with_picture.image_ + "" ));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post;
            if(master_detail=="m") {
                post = new HttpPost(SERVER_ADDRESS + "u_personnel_photo.php");
            } else
            {
                post = new HttpPost(SERVER_ADDRESS + "u_upload_in_out_photo.php");
            }
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                //
                //
                //
                HttpParams httpParams=new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
                //
                //
                //


                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"utf-8")); // InputStreamReader(is,"utf-18"));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    is.close();
                    returnedUser =  user_with_picture;
                } catch (IOException e) { e.printStackTrace();  }
                showMessage("Posted it");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User_with_picture returnedUser) {
            super.onPostExecute(returnedUser);
            showMessage("OnPostExecute");
           // progressDialog.dismiss();
            userCallback_with_picture.done(returnedUser);

        }
    }
    private static void showMessage(String message) {
        Log.i("ServerRequest_with_imge", message);
    }
}