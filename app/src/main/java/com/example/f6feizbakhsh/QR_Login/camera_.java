package com.example.f6feizbakhsh.QR_Login;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

;

/**
 * Created by f6feizbakhsh on 4/23/2016.
 */


public class camera_ extends Activity
{
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    //File photoFile = null;
    int user_id;
    File photoFile = null;
    String name_;
    String transaction;
    Bitmap rotatedBMP;
    private ImageView mImageView=null;
    public static final int CONNECTION_TIMEOUT = 10000 * 15;
    private static final String TAG = "upload";

    public camera_(int user_id, String name_, String transaction)
    {
        this.user_id = user_id;
        this.name_ = name_;
        this.transaction = transaction;
    }

        @Override
        protected void onStart()
        {
            super.onStart();
            try {
                name_ = getIntent().getExtras().getString( "name_string" );
                user_id = getIntent().getExtras().getInt( "id" );
                transaction = getIntent().getExtras().getString("transaction");
                mCurrentPhotoPath = getIntent().getExtras().getString("file_name_");
            } catch (Exception e) { e.printStackTrace(); }
            dispatchTakePictureIntent();
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onActivityResult: " + this);
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                setPic();
            }
        }

        @Override
        public void onStop()
        {
            super.onStop();
            Log.i(TAG,"onStop" + this);
            this.finish();
        }
        @Override
        protected void onResume() {
            // TODO Auto-generated method stub
            super.onResume();
            Log.i(TAG, "onResume: " + this);
        }

        @Override
        protected void onPause() {
            // TODO Auto-generated method stub
            super.onPause();
            Log.i(TAG, "onPause: " + this);
        }

        @Override
        public void onBackPressed()
        {
            super.onBackPressed();
            Log.i(TAG, "onBackPressed: " + this);
        }

        private void save_to_blob(Bitmap bitmap, String master_detail, int id) throws Exception {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            ServerRequests_with_image serverRequests = new ServerRequests_with_image(this);

            User_with_picture user_with_picture = new User_with_picture( id, imageEncoded,"");
            serverRequests.storeUserDataInBackground(user_with_picture, master_detail, new GetUserCallback_with_picture() {
                @Override
                public void done(User_with_picture returnedUser){
                    Log.d("Done", "");
                }
            });
        }
        private void sendPhoto(Bitmap bitmap) throws Exception {
            new UploadTask().execute(bitmap);
        }

        private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

            protected Void doInBackground(Bitmap... bitmaps) {
                if (bitmaps[0] == null)
                    return null;
                setProgress(0);

                Bitmap bitmap = bitmaps[0];
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
                InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

                HttpParams httpRequestParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

                HttpClient httpclient = new DefaultHttpClient(httpRequestParams);
                //DefaultHttpClient httpclient = new DefaultHttpClient();
                try {
                    HttpPost httppost = new HttpPost("http://www.ragelectric.com/savetofile.php"); // server

                    MultipartEntity reqEntity = new MultipartEntity();
                    //reqEntity.addPart("myFile",System.currentTimeMillis() + ".jpg", in);
                    reqEntity.addPart("myFile","upload.jpg",in);
                    httppost.setEntity(reqEntity);

                    Log.i(TAG, "request " + httppost.getRequestLine());
                    HttpResponse response = null;
                    try {
                        response = httpclient.execute(httppost);
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        if (response != null)
                            Log.i(TAG, "response " + response.getStatusLine().toString());
                    } finally {

                    }
                } finally {

                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                // TODO Auto-generated method stub
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
            }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            // TODO Auto-generated method stub
            super.onConfigurationChanged(newConfig);
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            // TODO Auto-generated method stub
            super.onSaveInstanceState(outState);
            Log.i(TAG, "onSaveInstanceState");
        }


        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                //File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }

        private File createImageFile() throws IOException {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String date_directory = new SimpleDateFormat("yyyyMMdd").format(new Date());


            String imageFileName = mCurrentPhotoPath;
            String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
            //String storageDir = Environment.getExternalStorageDirectory() + "/picupload/" + name_ ;

            File dir = new File(storageDir);
            if (!dir.exists())
            {
                dir.mkdir();
            }

            File image = new File(storageDir + "/" + imageFileName );

            mCurrentPhotoPath = image.getAbsolutePath();
            Log.i(TAG, "photo path = " + mCurrentPhotoPath);
            return image;
        }


        private void setPic() {
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor << 1;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            // Rotating Bitmap
            rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            if (rotatedBMP != bitmap)
                bitmap.recycle();

            mImageView.setImageBitmap(rotatedBMP);

            try {
                //sendPhoto(rotatedBMP);
                //save_to_blob(rotatedBMP);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }