package com.example.f6feizbakhsh.QR_Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Date;
/*
User can never see the layout view because on the onStart(), it automatically performs a click that calls the camera,
on the OnStop. I put the this.finish() so the program goes back to the calling program after click the check sign.
In the
*/

//public class picture_on_login extends Activity   implements View.OnClickListener {
public class picture_on_login extends Activity   implements View.OnClickListener {
    private Button mTakePhoto;
    private Button mupload, mquit_;
    private ImageView mImageView;
    private static final String TAG = "upload";
    File photoFile = null;
    private static Context context;
    Spinner sp;
    String name_;
    String transaction;

    /* SPINNER */
    private String jsonResult;
    private ProgressDialog pDialog;
    private String url = "http://www.ragelectric.com/u_transaction.php";
    private String url_tools = "http://www.ragelectric.com/u_tools_summary_final.php";
    private String x_selected;
    ArrayList<String> listItems=new ArrayList<>();
    ArrayAdapter<String> adapter_spinner;
    TextView  status, role;
    public static final int CONNECTION_TIMEOUT = 10000 * 15;
    CheckBox check_master, check_detail;
    Bitmap rotatedBMP;
    User_with_picture user_with_picture;
    /* SPINNER */

    public picture_on_login(){
        transaction=transaction;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Bundle bundle = new Bundle();
        try {
            name_ = getIntent().getExtras().getString( "name_string" );
            user_id = getIntent().getExtras().getInt( "id" ); //from bundle via extra
            transaction = getIntent().getExtras().getString("transaction");
            mCurrentPhotoPath = getIntent().getExtras().getString("file_name_");
        } catch (Exception e) { e.printStackTrace(); }
        mTakePhoto.performClick();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.take_photo_of_employee);

        mTakePhoto = (Button) findViewById(R.id.take_photo);
        mTakePhoto.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        //takePhoto();
        dispatchTakePictureIntent();

    }


    private void takePhoto() {
        //disabled this so it will call camera_ class
        dispatchTakePictureIntent();

        /*
        Intent intent = new Intent(getApplicationContext(), camera_.class);
        intent.putExtra("name_string",name_);
        intent.putExtra("id", user_id);
        intent.putExtra("transaction", transaction);
        intent.putExtra("file_name_", mCurrentPhotoPath);
        startActivity(intent);
        this.finish(); */
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
            Toast.makeText(picture_on_login.this, R.string.uploaded, Toast.LENGTH_LONG).show();

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

    String mCurrentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;
    //File photoFile = null;
    int user_id;
    public picture_on_login(int user_id, String name_, String transaction) {

        this.user_id = user_id;
        this.name_ = name_;
        this.transaction = transaction;
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*
     * http://developer.android.com/training/camera/photobasics.html
     */

    /*
    */

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

/*
    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String date_directory = new SimpleDateFormat("yyyyMMdd").format(new Date());
        // String imageFileName = "upload" ;


        String imageFileName = timeStamp + (transaction.equals("1")?"_IN_":"_OUT_") + "_" + name_ + "_" + String.valueOf(user_id);
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        //String storageDir = Environment.getExternalStorageDirectory() + "/picupload/" + name_ ;

        File dir = new File(storageDir);
        if (!dir.exists())
        {
            dir.mkdir();

        }

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }
*/







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