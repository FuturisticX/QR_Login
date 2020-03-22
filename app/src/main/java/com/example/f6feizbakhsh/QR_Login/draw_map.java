package com.example.f6feizbakhsh.QR_Login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class draw_map extends AppCompatActivity implements OnMapReadyCallback {

private GoogleMap mMap;
    Double latitude;
    Double longitude;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_actions,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_maps );
        latitude = Double.valueOf( getIntent().getExtras().getString( "latitude" ) );
        longitude = Double.valueOf( getIntent().getExtras().getString( "longitude" ) );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        mapFragment.setHasOptionsMenu(true);
        mapFragment.getMapAsync( this );
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.Back:
                Toast.makeText(getApplicationContext(),"Back Selected",Toast.LENGTH_LONG).show();
                return true;
            default: 
                return super.onOptionsItemSelected( item );
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng( latitude, longitude );
        mMap.addMarker( new MarkerOptions().position( sydney ).title( "Location" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );
        //mMap.animateCamera( CameraUpdateFactory.zoomIn() );
        //mMap.animateCamera( CameraUpdateFactory.zoomTo( 15),2000,null);

        CameraPosition cameraPosition = new CameraPosition.Builder().target( sydney ).zoom( 16 ).build();
        //Zoom in and animate the camera.
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }
}