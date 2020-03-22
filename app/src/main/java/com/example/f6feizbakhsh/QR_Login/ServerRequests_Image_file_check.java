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

/*import android.content.Entity;
import android.provider.Settings;*/
//import org.w3c.dom.NameList;//
/*import org.apache.*;*/

/**
 * Created by f6feizbakhsh on 2/12/2016.
 */
public class ServerRequests_Image_file_check {
    //ProgressDialog progressDialog;
    //public static final int CONNECTION_TIMEOUT = 10000 * 15;
    public static final int CONNECTION_TIMEOUT = 20000 * 15;
    public static final String SERVER_ADDRESS = "http://www.ragelectric.com/";
    //String username;
    int id;

    public ServerRequests_Image_file_check(Context context) {
       /* progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");*/
        showMessage("ServerRequests");
    }

    public void storeUserDataInBackground(Image_File_Name image_file_name, Get_Image_File_Callback image_file_callback) {
       /* progressDialog.show();*/
        new StoreUserDataAsyncTask(image_file_name, image_file_callback).execute();
        showMessage("storeUserDataIn");
    }

    public void fetchUserDataInBackground(Image_File_Name image_file_name, Get_Image_File_Callback image_file_callback) {
        //progressDialog.show();
        new fetchUserDataAsyncTask(image_file_name, image_file_callback).execute();
        showMessage("fetchUser");

    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Image_File_Name image_file_name;
        Get_Image_File_Callback image_file_callback;

        public StoreUserDataAsyncTask(Image_File_Name image_file_name, Get_Image_File_Callback image_file_callback) {
            this.image_file_name= image_file_name;
            this.image_file_callback = image_file_callback;
            Log.d("StoreUserDataAsync", "");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("doInBackground", "");
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image_file_name", image_file_name.file_name_));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "image_file_name.php");
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
            super.onPostExecute(aVoid);
            showMessage("onPostExecute");
            //progressDialog.dismiss();
            image_file_callback.done(null);

        }
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, Image_File_Name> {
        Image_File_Name image_file_name;
        int id;
        String name_;
        Get_Image_File_Callback image_file_callback;

        public fetchUserDataAsyncTask(Image_File_Name image_file_name, Get_Image_File_Callback image_file_callback) {
            this.image_file_name = image_file_name;
            this.image_file_callback = image_file_callback;
        }

        @Override
        protected Image_File_Name doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("file_name", image_file_name.file_name_));
            //dataToSend.add(new BasicNameValuePair("password", user.password));
            showMessage( dataToSend.toString());
            //
            //
            //
            //


            //String transaction = array[1].toString();
            String link =  SERVER_ADDRESS + "image_file_name.php";

            //
            //
            //
            //

            //HttpParams httpRequestParams = new BasicHttpParams();
            //HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            //HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            //HttpClient client = new DefaultHttpClient(httpRequestParams);
            //HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");
            Image_File_Name returnedImage_File_Name = null;

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
                //JSONObject jObject = new JSONObject(result);


                JSONObject jsonResponse = new JSONObject( result );
                JSONArray jsonMain = jsonResponse.optJSONArray("image_file_name");
                //
                //It will only loop once.
                //
                String returned_image_file_name = null;
                int id_=0;
                for(int i=0; i <jsonMain.length(); i++) {
                    JSONObject jsonChild     = jsonMain.getJSONObject( i );
                    returned_image_file_name = jsonChild.optString( "file_name");
                    id_                      = Integer.valueOf(jsonChild.optString( "id" ));
                }
                //if (jObject.length() == 0) {
                //     returnedUser = null;
                //} else {
                //    String name = jObject.getString("name");
                //    int age = jObject.getInt("age");
                try {
                    if (!returned_image_file_name.isEmpty()) {
                        //returnedUser = new User(  username ); //new User(name,age,user.username, user.password);
                        returnedImage_File_Name = new Image_File_Name(returned_image_file_name, id_);
                    } else {
                        returnedImage_File_Name = null;
                    }
                } catch (Exception ex)
                { returnedImage_File_Name = null; }
                //}
            } catch  ( Exception e) {
                e.printStackTrace();
            }
            return returnedImage_File_Name;
        }

        @Override
        protected void onPostExecute(Image_File_Name returnedImage_File_Name){
            //progressDialog.dismiss();
            super.onPostExecute(returnedImage_File_Name);
            image_file_callback.done(returnedImage_File_Name);

        }
    }
    private static void showMessage(String message) {
        Log.i("ServerRqsts_Img_fil_chk", message);
    }
}