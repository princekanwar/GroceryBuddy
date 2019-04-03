package com.darshil.grocerybuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import com.darshil.grocerybuddy.Database.DirectionParser;
import com.darshil.grocerybuddy.Database.FetchURL;
import com.darshil.grocerybuddy.Database.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewDirection extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,TaskLoadedCallback {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationMarker,DestMarker;
    private Marker ResultMarker = null;
    public static final int REQUEST_LOCATION_CODE = 99;
    Polyline currentPolyline;
    int PROXIMITY_RADIUS=10000;
    double latitude,longitude;
    String Lat,lang,title;
    Double dest_lat,dest_lng;
    MarkerOptions place1,place2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_direction );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        Lat = getIntent().getStringExtra( "Latitude" );
        lang = getIntent().getStringExtra( "Longitude" );
        title = getIntent().getStringExtra( "Destination" );
        dest_lat = Double.parseDouble( Lat );
        dest_lng = Double.parseDouble( lang );

    }
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastlocation = location;
        if (currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
        LatLng latLng = new LatLng( location.getLatitude(),location.getLongitude() );
        place1 = new MarkerOptions();
        place1.position( latLng );
        place1.title( "Current Location" );
        place1.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW) );
        currentLocationMarker = mMap.addMarker( place1 );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng) );
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10 ) );


        if (client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates( client,this );
        }
        // Create Marker For Destination
        LatLng destinationLatLng = new LatLng( dest_lat,dest_lng );
        place2 = new MarkerOptions()
                .position( new LatLng( dest_lat,dest_lng ) )
                .title( title )
                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) );
        DestMarker = mMap.addMarker( place2 );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng) );
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10 ) );
        String url = getUrl(currentLocationMarker.getPosition(),DestMarker.getPosition(),"driving");
        new FetchURL( ViewDirection.this ).execute( url,"driving" );

    }

    private String getUrl(LatLng position, LatLng position1, String driving) {
        String str_origin = "origin=" + position.latitude + "," + position.longitude;
        String str_dest = "destination=" + position1.latitude + "," + position1.longitude;
        String Mode = "mode=" +driving;
        String parameters = str_origin + "&" + str_dest + "&" + Mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" +  output + "?" + parameters + "&key=" + getString( R.string.key );
        return url;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled( true );
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval( 1000 );
        locationRequest.setFastestInterval( 1000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates( client, locationRequest, this );
        }

    }
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.ACCESS_FINE_LOCATION ))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    protected synchronized void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi(LocationServices.API )
                .build();
        client.connect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (client == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled( true );
                    }
                }
                else
                {
                    Toast.makeText( this, "Permission Denied!!", Toast.LENGTH_SHORT ).show();
                }
                return;
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline( (PolylineOptions) values[0] );

    }
}
