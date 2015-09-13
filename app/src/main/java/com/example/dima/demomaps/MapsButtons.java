package com.example.dima.demomaps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Dima on 9/8/2015.
 */
public class MapsButtons  extends FragmentActivity implements View.OnClickListener {

    GoogleMap mMap;


    Activity mActivity;

    boolean directionsButtonClicked;

    public MapsButtons(Activity activity,GoogleMap map){
        mMap=map;
        mActivity=activity;

        directionsButtonClicked=false;
    }


    EditText mSearchText;
    public void onSearch(){

        mSearchText = (EditText)mActivity.findViewById(R.id.TextField);
        Button searchButton= (Button)mActivity.findViewById(R.id.SearchButton);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = mSearchText.getText().toString();

                if(location!=null && !location.equals("")){

                    LatLng latLng=LocationNameToLatLng(location);
                    if(latLng!=null) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                }
            }
        };
        searchButton.setOnClickListener(onClickListener);

    }

    public LatLng LocationNameToLatLng(String location){
        LatLng result=null;
        Geocoder geocoder = new Geocoder(mActivity);
        List<Address> addressList=null;
        try {
            addressList=geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addressList.size()>0){
            Address address = addressList.get(0);
            result = new LatLng(address.getLatitude(),address.getLongitude());
        }

        return result;
    }

    public void onChangeType(){
        Button button = (Button) mActivity.findViewById(R.id.MapTypeButton);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL)
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                else if(mMap.getMapType()==GoogleMap.MAP_TYPE_SATELLITE)
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                else if(mMap.getMapType()==GoogleMap.MAP_TYPE_TERRAIN)
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                else if(mMap.getMapType()==GoogleMap.MAP_TYPE_HYBRID)
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        };
        button.setOnClickListener(onClickListener);
    }

    public void onDirections(){
        Button buttonDirections = (Button)mActivity.findViewById(R.id.DirectionsButton);

        buttonDirections.setOnClickListener(this);
    }


    public void onZoom(){

        Button buttonUp = (Button) mActivity.findViewById(R.id.ButtonUp);
        View.OnClickListener onClickListenerUp = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        };
        buttonUp.setOnClickListener(onClickListenerUp);

        Button buttonDown = (Button) mActivity.findViewById(R.id.ButtonDown);
        View.OnClickListener onClickListenerDown = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        };
        buttonDown.setOnClickListener(onClickListenerDown);
    }



    @Override    public void onClick(View view) {

    }



}
