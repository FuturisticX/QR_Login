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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
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
import java.util.Calendar;
import java.util.Date;







public class take_employee_photo extends Activity implements OnClickListener {
    private Button mTakePhoto;
    private Button mupload, mquit_;
    private ImageView mImageView;
    private static final String TAG = "upload";
    File photoFile = null;
    private static Context context;
    Spinner sp;
    CalendarView calendar;

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
    int year_,month_,day_;
    /* SPINNER */

    public take_employee_photo(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo_of_employee);

        mTakePhoto = (Button) findViewById(R.id.take_photo);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mupload = (Button) findViewById(R.id.upload);
        mquit_ = (Button) findViewById(R.id.quit_);
        mTakePhoto.setOnClickListener(this);
        mquit_.setOnClickListener(this);
        mupload.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view){
                Boolean x_check_two = check_detail.isChecked() && check_master.isChecked();
                if(x_check_two)
                {
                    Toast.makeText(take_employee_photo.this, "You must only pick 1 check box", Toast.LENGTH_LONG).show();
                }
                Boolean x_nothing_is_checked = !check_detail.isChecked() && !check_master.isChecked();
                if(x_nothing_is_checked)
                {
                    Toast.makeText(take_employee_photo.this, "Check at least 1 check box", Toast.LENGTH_LONG).show();
                }

                //Boolean x_selected_is_null = (x_selected == null || (TextUtils.equals(x_selected ,"null")) || (TextUtils.isEmpty(x_selected)));
                //if (x_selected_is_null)
                //{
                //    Toast.makeText(take_employee_photo.this, "Select a tool", Toast.LENGTH_LONG).show();
                //}

                //0 means dont want to upload in/out photo
                //
                if(check_master.isChecked())
                {
                    Boolean x_picture_null= mImageView.getDrawable()==null;
                    if(x_picture_null )
                    {
                        Toast.makeText(take_employee_photo.this,"Take a photo before uploading",Toast.LENGTH_LONG).show();
                    }
                    if(!x_check_two && !x_nothing_is_checked   && !x_picture_null)
                    {
                    //  Toast.makeText(take_employee_photo.this,"Valid Entry for "+ String.valueOf(user_with_picture.id),Toast.LENGTH_LONG).show();

                        try {
                            if(check_detail.isChecked())
                            {
                                save_to_blob(rotatedBMP,"d",user_id, ""); //this will never get executed
                            }else{ save_to_blob(rotatedBMP,"m",user_id,"");}
                            rotatedBMP.recycle();
                            rotatedBMP = null;

                        } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        }
                    }
                }
                if(check_detail.isChecked() && year_ > 0)
                {
                    upload_in_out_photo_to_server();
                }
                //int xyz = tools[sp.getSelectedItemPosition()].getid();
            }
        });

        check_detail = (CheckBox)findViewById(R.id.check_detail);
        check_master = (CheckBox)findViewById(R.id.check_master);
        check_detail.setOnClickListener(this);
        check_master.setOnClickListener(this);
        initializeCalendar();

        Bundle bundle = new Bundle();
        user_id=2;
        try {
            int id = getIntent().getExtras().getInt( "id" );
            user_id = id; //from bundle via extra
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void initializeCalendar()
    {
        Calendar c = Calendar.getInstance();
        year_ = c.get(Calendar.YEAR);
        month_ = c.get(Calendar.MONTH) + 1;
        day_ =  c.get(Calendar.DATE);

        calendar = (CalendarView)findViewById(R.id.calendarView);
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Toast.makeText(getApplicationContext(),day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                year_ = year;
                month_ = month + 1;
                day_ = day;
            }
        });
    }


    /*
        public void upload_in_out_photo_to_server() {
        int user_id_to_use_=0;
        int l_ =  (String.valueOf(year_).length());
        String file_ =  String.valueOf(year_) +
                       (String.valueOf(month_).length()==1? "0" + String.valueOf(month_) : String.valueOf(month_)) +
                       (String.valueOf(day_).length()==1? "0" + String.valueOf(day_) : String.valueOf(day_));
        String path_ = Environment.getExternalStorageDirectory() + "/picupload";

        File dir = new File(path_);
        String[] files = dir.list();

        int numberOfFiles = files.length;

        for(int i=0;i< numberOfFiles ;i++)
        {
            String fil_ = files[i].substring(0, 8) ; // year_ month_ day_
            fil_ = files[i].substring(0, 8) ;
            if(fil_.equals(file_))
            {
                File f = new File(path_ + "/" + files[i]);
                rotatedBMP = BitmapFactory.decodeFile(f.getAbsolutePath());

                //bmp.recycle();

                try {
                    //
                    //get the user_id in the filename
                    // string starts at 0
                    String file_name_ = files[i].substring(0,files[i].length()-4);
                    for(int ii=1; ii <  file_name_.length();  ii++){
                        int rightPosition = file_name_.length() - ii;
                        char x___ = file_name_.charAt(rightPosition);
                        if(file_name_.charAt(rightPosition) == '_')
                        {
                            String us_ = file_name_.substring( rightPosition+1);
                            user_id_to_use_ = Integer.parseInt(us_);
                            break;
                        }
                    }
                    String x_file_name =  path_ + "/" + files[i];
                    prepare_batch_image_upload(x_file_name);
                    save_to_blob(  rotatedBMP, "d", user_id_to_use_);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
     */





    public void upload_in_out_photo_to_server() {
        int user_id_to_use_=0;
        final String path_ = Environment.getExternalStorageDirectory() + "/picupload";

        File dir = new File(path_);
        String[] files = dir.list();
        try {


            int numberOfFiles = files.length;

            for (int i = 0; i < numberOfFiles; i++) {
                String contents = files[i];
                int id_ = 0;
                Image_File_Name image_file_name = new Image_File_Name(contents, id_);
                ServerRequests_Image_file_check serverRequests_image_file_check = new ServerRequests_Image_file_check(this);
                serverRequests_image_file_check.fetchUserDataInBackground(image_file_name, new Get_Image_File_Callback() {
                    @Override
                    public void done(Image_File_Name returnedImage_File_name) {
                        //
                        //Search for the filename in attendance and file_name
                        //
                        if (returnedImage_File_name == null) {
                            // not found here
                        } else {
                            //
                            //start uploading the returned file_name
                            //
                            //
                            String file_name_ = path_ + "/" + returnedImage_File_name.file_name_;
                            File f = new File(file_name_);
                            rotatedBMP = BitmapFactory.decodeFile(f.getAbsolutePath());
                            try {
                                //prepare_batch_image_upload(returnedImage_File_name.file_name_);
                                prepare_batch_image_upload(file_name_);
                                save_to_blob(rotatedBMP, "d", returnedImage_File_name.id_, returnedImage_File_name.file_name_);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }
        } catch (Exception ex)
        {
            Toast.makeText(take_employee_photo.this, "Nothing to upload", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.take_photo:
                takePhoto();
                break;
            case R.id.upload:
            case R.id.check_detail:
                break;
            case R.id.check_master:
                break;
            case R.id.quit_:
                int p = android.os.Process.myPid();
                android.os.Process.killProcess( p );
        }
    }


    private void takePhoto() {
//		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//		startActivityForResult(intent, 0);
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onActivityResult: " + this);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            setPic();
//			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//			if (bitmap != null) {
//				mImageView.setImageBitmap(bitmap);
//				try {
//					sendPhoto(bitmap);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
        }
    }
    private void save_to_blob(Bitmap bitmap, String master_detail, int id, String file_name) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            ServerRequests_with_image serverRequests = new ServerRequests_with_image(this);

            User_with_picture user_with_picture = new User_with_picture(id, imageEncoded, file_name);
            serverRequests.storeUserDataInBackground(user_with_picture, master_detail, new GetUserCallback_with_picture() {
                @Override
                public void done(User_with_picture returnedUser) {
                    String dir = Environment.getExternalStorageDirectory() + "/picupload/";
                    try {
                        File f0 = new File(dir, returnedUser.file_name);
                        f0.delete();
                    } catch (Exception ex) {
                    }
                    Log.d("Done", "");
                }
            });
        } catch (Exception ex)
        {}
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
            Toast.makeText(take_employee_photo.this, R.string.uploaded, Toast.LENGTH_LONG).show();
        }
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
    public take_employee_photo(int user_id) {

        this.user_id = user_id;
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName =  "upload" ;//"JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        //File image = new File(storageDir + "/" + imageFileName + ".jpg");
        File image = new File(storageDir + "/" + imageFileName );

        // Save a file: path for use with ACTION_VIEW intents
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

    public void prepare_batch_image_upload(String file_name_)
    {
        // Get the dimensions of the View
        int targetW = 800;
        int targetH = 257;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_name_, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file_name_, bmOptions);

        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        // Rotating Bitmap
        rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        if (rotatedBMP != bitmap)
            bitmap.recycle();
    }
}