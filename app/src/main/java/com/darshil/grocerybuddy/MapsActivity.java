package com.darshil.grocerybuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
GoogleMap.OnMarkerClickListener,
GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationMarker;
    private Marker ResultMarker = null;
    public static final int REQUEST_LOCATION_CODE = 99;
    Button btnGet,btnTo;
    Polyline currentPolyline;
    int PROXIMITY_RADIUS=10000;
    double latitude,longitude;
    double end_latitude,end_logitude;
    boolean doubleBackToExitPressedOnce = false;
    Address userAddress;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
         btnTo = (Button) findViewById( R.id.btnMyLocation );
        btnTo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Intent io = getIntent();
                String getAdd = io.getStringExtra( "PostalCode" );
                List<Address> addressList = null;
                MarkerOptions userMarkerOptions = new MarkerOptions();
                if(!TextUtils.isEmpty( getAdd ))
                {
                    Geocoder geocoder = new Geocoder( MapsActivity.this );
                    try {
                        addressList = geocoder.getFromLocationName( getAdd,1 );
                        if(addressList != null)
                        {
                                userAddress = addressList.get( 0 );
                                LatLng latLng = new LatLng( userAddress.getLatitude(),userAddress.getLongitude() );
                                userMarkerOptions.position( latLng );
                                userMarkerOptions.title( getAdd );
                                userMarkerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) );
                                mMap.addMarker( userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng) );
                                mMap.animateCamera( CameraUpdateFactory.zoomTo( 14 ) );
                             btnTo.setOnLongClickListener( new View.OnLongClickListener() {
                                 @Override
                                 public boolean onLongClick(View v) {
                                     String groceryStore = "grocery_or_supermarket";
                                     String url = getUrl(userAddress.getLatitude(),userAddress.getLongitude(),groceryStore);
                                     Object dataTransfer[] = new Object[2];
                                     dataTransfer[0] = mMap;
                                     dataTransfer[1] = url;
                                     GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                                     getNearbyPlacesData.execute( dataTransfer );
                                     Toast.makeText( MapsActivity.this, "Showing Nearby Grocery Stores From Postal Code!!", Toast.LENGTH_LONG).show();
                                     return false;
                                 }
                             } );

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        } );
        btnGet = (Button) findViewById(R.id.btnNearStore);
        btnGet.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                String groceryStore = "grocery_or_supermarket";
                String url = getUrl(latitude,longitude,groceryStore);
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute( dataTransfer );
                Toast.makeText( MapsActivity.this, "Showing Nearby Grocery Stores!!", Toast.LENGTH_LONG).show();
            }
        } );
        btnGet.setTypeface( myCustomFont );
    }
    private String getUrl(double latitude,double longitude,String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder( "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" );
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyAKcZAJfsa6kjOWsQMHf-j60zymoJiMC3Q");

        Log.d("GoogleMapsActivity","url = "+googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
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
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled( true );
        }
        mMap.setOnMarkerClickListener( this );
        mMap.setOnMarkerDragListener( this );
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
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastlocation = location;
        if (currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
       LatLng latLng = new LatLng( location.getLatitude(),location.getLongitude() );
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( latLng );
        markerOptions.title( "Current Location" );
        markerOptions.icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) );
        currentLocationMarker = mMap.addMarker( markerOptions );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng) );
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10 ) );


        if (client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates( client,this );
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_logitude = marker.getPosition().longitude;
        float results[] = new float[10];

        Location.distanceBetween( latitude,longitude,end_latitude,end_logitude,results );
        String dest = String.valueOf( results[0] );
        String title = marker.getTitle();
        String lat = String.valueOf( end_latitude );
        String lng = String.valueOf( end_logitude );

        //  markerOptions.snippet( "Distance = "+results[0] );
        Toast.makeText( MapsActivity.this, "Distance = "+results[0], Toast.LENGTH_SHORT ).show();
        Intent io = new Intent(  MapsActivity.this,OrderActivity.class);
        io.putExtra( "Distance",dest );
        io.putExtra( "Destination",title );
        io.putExtra( "Latitude",lat );
        io.putExtra( "Longitude",lng);
        startActivity( io );
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
