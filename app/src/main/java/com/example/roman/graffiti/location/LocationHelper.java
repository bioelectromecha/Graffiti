package com.example.roman.graffiti.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.apkfuns.logutils.LogUtils;
import com.example.roman.graffiti.activities.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by roman on 8/17/17.
 */

public class LocationHelper implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    // location services stuff
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation = null;
    private final int LOCATION_UPDATE_INTERVAL = 1;
    private MainActivity mCallbackActivity;

    public LocationHelper(MainActivity activity){

        mCallbackActivity = activity;

        // bind the google api client - for user location
        mGoogleApiClient = new GoogleApiClient.Builder(mCallbackActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void startLocationUpdates(){
        //connect the api
        mGoogleApiClient.connect();
    }


    public void stopLocationUpdates(){
        // disconnect the api
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
//        LogUtils.d("Location is: " + mLastLocation.getLatitude() + " , " + mLastLocation.getLongitude() + " , " + mLastLocation.getAltitude());
        mCallbackActivity.updateGraffitiSize(mLastLocation);
    }


    @Override
    public void onConnected(Bundle bundle) {
        updateLastLocation();
        if (mLastLocation != null) {
           // a location already exists
        }
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            LogUtils.d(e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void updateLastLocation() {
        try {
            //get the last location of the device. This is mostly for before getting actual GPS position
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if fine location permission not granted
        } catch (SecurityException e) {
            LogUtils.d("LOCATION PERMISSION NOT GRANTED");
            LogUtils.d(e.getMessage());
            e.printStackTrace();
        }
    }

    public Location getLastLocation() {
        return mLastLocation;
    }
}
