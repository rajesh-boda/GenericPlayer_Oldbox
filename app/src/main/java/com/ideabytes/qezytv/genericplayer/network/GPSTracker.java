package com.ideabytes.qezytv.genericplayer.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

/**
 * Created by suman on 10/12/15.
 */public class GPSTracker implements LocationListener {
    protected LocationManager locationManager;
    Location location;
    private Context context;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public GPSTracker(Context context) {
        this.context = context;
    }

    public double getLatitude() {
        double latitude = 0.0;
        double longitude = 0.0;
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (netEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= 13 &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        System.out.println("suman ll "+latitude);
                        longitude = location.getLongitude();
                        System.out.println("suman ll "+longitude);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return latitude;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


}