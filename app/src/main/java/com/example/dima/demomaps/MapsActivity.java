package com.example.dima.demomaps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import com.example.dima.demomaps.Directions.GoogleAPIv2;
import com.example.dima.demomaps.Directions.HttpConnection;
import com.example.dima.demomaps.Directions.PathJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";
    //private static final LatLng FIRST_POINT = new LatLng(40.732543, -73.998585);
    //private static final LatLng SECOND_POINT = new LatLng(40.7064, -74.0094);


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private EditText mLatitudeText;
    private EditText mLongitudeText;
    private LatLng FIRST_POINT;
    private LatLng SECOND_POINT;
    private String FIRST_POINT_NAME=null;
    private String SECOND_POINT_NAME=null;

    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.v(TAG, "onConnected");
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.v(TAG, "onConnectionFailed"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(!isInternetConnenctionAvailable()){
            CallDialog callDialog = new CallDialog();
            Bundle bundle = new Bundle();
            bundle.putString("dialog","Please turn on Internet Connection");
            callDialog.setArguments(bundle);
            callDialog.show(getSupportFragmentManager(), "string");
        }

        if(!isGPSEnabled()){
            CallDialog callDialog = new CallDialog();
            Bundle bundle = new Bundle();
            bundle.putString("dialog","Please turn on GPS");
            callDialog.setArguments(bundle);
            callDialog.show(getSupportFragmentManager(), "string");
        }

        Log.v("onCreate", "onCreate");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){

            FIRST_POINT_NAME=bundle.getString("From");
            SECOND_POINT_NAME=bundle.getString("To");
            Log.v("FIRST_POINT_NAME ", FIRST_POINT_NAME);
            Log.v("SECOND_POINT_NAME ", SECOND_POINT_NAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("onStop", "onStop");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap=googleMap;

        GPSTracker gpsTracker = new GPSTracker(this);

        LatLng myCurrentLocation = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());

        mMap.addMarker(new MarkerOptions().position(myCurrentLocation).title("Current location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentLocation, 13));

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        MapsButtons mapsButtons = new MapsButtons(MapsActivity.this, mMap);
        mapsButtons.onSearch();
        mapsButtons.onChangeType();
        mapsButtons.onZoom();

        setButtonDirectionsActivity();

        String url=getMapsApiDirectionsUrl();
        if(url!=null){
            GoogleAPIv2 googleAPIv2 = new GoogleAPIv2(mMap,url);
            addMarkers();
            mMap.moveCamera(CameraUpdateFactory.zoomBy(13));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FIRST_POINT, 13));
        }
    }


    private LatLng LocationNameToLatLng(String location){
        if(location==null)return null;
        LatLng result=null;
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList=null;
        try {
            addressList=geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addressList.get(0);
        result = new LatLng(address.getLatitude(),address.getLongitude());
        return result;
    }

    private String getMapsApiDirectionsUrl() {

        String url=null;

        if(FIRST_POINT_NAME!=null && SECOND_POINT_NAME!=null) {

            FIRST_POINT = LocationNameToLatLng(FIRST_POINT_NAME);
            SECOND_POINT = LocationNameToLatLng(SECOND_POINT_NAME);

            String origin = "origin=" + FIRST_POINT.latitude + "," + FIRST_POINT.longitude;
            String destination = "destination=" + SECOND_POINT.latitude + "," + SECOND_POINT.longitude;

            url = "https://maps.googleapis.com/maps/api/directions/json?" + origin + "&" + destination;
            Log.v("URL",url.toString());
            return url;

        }

        return url;
    }

    private void addMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(FIRST_POINT)
                    .title(FIRST_POINT_NAME));
            mMap.addMarker(new MarkerOptions().position(SECOND_POINT)
                    .title(SECOND_POINT_NAME));

        }
    }

    private void setButtonDirectionsActivity() {
        Button directionsButton = (Button)findViewById(R.id.DirectionsButton);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDirectionsActivity();
            }
        };
        directionsButton.setOnClickListener(onClickListener);

    }

    private void callDirectionsActivity(){
        Intent intent = new Intent(this,DirectionsActivity.class);
        startActivity(intent);
    }

    private boolean isInternetConnenctionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private boolean isGPSEnabled(){
        GPSTracker gpsTracker = new GPSTracker(this);
        if(!gpsTracker.canGetLocation)return false;
        return true;
    }
}
