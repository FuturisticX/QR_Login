package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 7/21/2016.
 */

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Take_Picture extends Service {
    static String mCurrentPhotoPath="";
    String name_;
    String transaction;
    int user_id;
    PowerManager.WakeLock wakeLock;
    final Context context=this;
    final Context context_service=this;

    @Override
    public void onCreate() {
        super.onCreate();

       // final Context context=this;
        PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

        //takePhoto(this); //move this to onStart so program can get parameters from calling program

    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            showMessage(service.service.getClassName());
            if("com.example.f6feizbakhsh.QR_Login.Take_Picture".equals(service.service.getClassName())) {
                showMessage("RUNNING");
                return true;
            }
        }
        showMessage("STOPPED");
        return false;
    }

    @Override
    public void onDestroy()
    {
        showMessage("INSIDE onDestroy");
        if(!isServiceRunning())
        {
            wakeLock.release();
            showMessage("POWER STOPPED");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        showMessage("XXXXXXXXXXXXXXX on Start XXXXXXXXXXXXXXXXXXx");
        //Bundle bundle = new Bundle();
        try {
            name_ = intent.getExtras().getString( "name_string" );
            user_id = intent.getExtras().getInt( "id" ); //from bundle via extra
            transaction = intent.getExtras().getString("transaction");
            mCurrentPhotoPath = intent.getExtras().getString("file_name_");
        } catch (Exception e) { e.printStackTrace(); }
        takePhoto(this);
        return 0;
    }


    @SuppressWarnings("deprecation")
    private static void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {



            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created");

                int cameraCount = 0;
                Camera camera = null;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();
                //count # of camera
                for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
                    Camera.getCameraInfo( camIdx, cameraInfo );

                    //if camera front is front then get it.
                    if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                        try {
                            camera = Camera.open( camIdx );
                            Camera.Parameters params = camera.getParameters();
                            params.setPictureSize(1280,800);
                            camera.setParameters(params);
                            showMessage("Opened camera");

                            try {
                                camera.setPreviewDisplay(holder);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            camera.startPreview();
                            showMessage("Started preview");

                            camera.takePicture(null, null, new Camera.PictureCallback() {

                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    showMessage("Took picture");
                                    String imageFileName =  mCurrentPhotoPath;
                                    String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
                                    File dir = new File(storageDir);
                                    if (!dir.exists())
                                        dir.mkdir();

                                    File image = new File(storageDir + "/" + imageFileName );
                                    String x_ = storageDir + "/" + imageFileName;
                                    FileOutputStream outStream = null;
                                    try {
                                        outStream = new FileOutputStream(x_);
                                        outStream.write(data);
                                        outStream.close();
                                    } catch (Exception ex) {  }

                                    mCurrentPhotoPath = image.getAbsolutePath();
                                    showMessage(" Create Image = " + mCurrentPhotoPath);
                                    camera.release(); // without these, it will generate an error every 3 picture take
                                    Image_File_Name image_file_name = new Image_File_Name(imageFileName,0); //0 is not use
                                    prepare_send p = new prepare_send(image_file_name,storageDir, context);


                                }


                            });
                        } catch (Exception e) {
                            if (camera != null)
                                camera.release();
                            throw new RuntimeException(e);
                        }

                    }
                }


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }

    private static void showMessage(String message) {
        Log.i("Take_Picture", message);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private static class prepare_send {
        Bitmap rotatedBMP;

        public prepare_send(Image_File_Name image_file_name, String storageDir, Context context) {
            final String path_ = storageDir;
            final Context context_ = context;
            ServerRequests_Image_file_check serverRequests_image_file_check = new ServerRequests_Image_file_check(context_);
            showMessage(" called server_request_image_file_check");
            serverRequests_image_file_check.fetchUserDataInBackground(image_file_name, new Get_Image_File_Callback() {
                @Override
                public void done(Image_File_Name returnedImage_File_name) {
                    //
                    //Search for the filename in attendance and file_name
                    //
                    showMessage("done with uploading");
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
                            save_to_blob(rotatedBMP, "d", returnedImage_File_name.id_, returnedImage_File_name.file_name_,context_);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

        }

        public void prepare_batch_image_upload(String file_name_)
        {
            showMessage("Inside prepare_batch_image");
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
            //mtx.postRotate(90);
            mtx.postRotate(270);
            // Rotating Bitmap
            rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            if (rotatedBMP != bitmap)
                bitmap.recycle();
        }
        private void save_to_blob(Bitmap bitmap, String master_detail, int id, String file_name, Context context) throws Exception {
            //
            //without this. I cannot call stopservice from within static
            //
            final Context context_service  = context;
            //
            //
            //
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //try {
            showMessage(  "Inside Save to Blob");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            //final ServerRequests_with_image serverRequests = new ServerRequests_with_image(context);
            ServerRequests_with_image serverRequests = new ServerRequests_with_image(context);

            User_with_picture user_with_picture = new User_with_picture(id, imageEncoded, file_name);
            serverRequests.storeUserDataInBackground(user_with_picture, master_detail, new GetUserCallback_with_picture() {
                @Override
                public void done(User_with_picture returnedUser) {
                    String dir = Environment.getExternalStorageDirectory() + "/picupload/";
                    try {
                        File f0 = new File(dir, returnedUser.file_name);
                        f0.delete();
                        //
                        //this will stop wakelock
                        //
                        Intent intent = new Intent(context_service, Take_Picture.class);
                        context_service.stopService(intent);

                        showMessage("deleted " + returnedUser.file_name );
                    } catch (Exception ex) {}
                    showMessage( "Uploaded Photo to SERVER");
                }

            });
        }
    }
}

