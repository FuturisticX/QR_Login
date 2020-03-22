package com.example.f6feizbakhsh.QR_Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by f6feizbakhsh on 4/23/2016.
 */


public class camera extends Activity {
    Button button_take_photo;
    ImageView image_taken;
    private static final int CAM_REQUEST = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState);
        setContentView(R.layout.camera_activity );
        button_take_photo = (Button) findViewById( R.id.button_take_photo );
        image_taken = (ImageView) findViewById( R.id.imageView1 );
        button_take_photo.setOnClickListener( new button_take_photo_clicker());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode,resultCode, data );
        if(requestCode ==CAM_REQUEST)
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image_taken.setImageBitmap(thumbnail);
        }
    }

    class button_take_photo_clicker implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent cameraintent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult( cameraintent,  CAM_REQUEST );
        }
    }
}
