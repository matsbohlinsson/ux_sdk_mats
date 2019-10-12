package com.dji.ux.sample.functions;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

interface gpsLocation
{
    public Location getLatestPosition();
}

public class Gps implements gpsLocation {
    static public Location location = new Location("gps");
    LocationManager locationManager;
    LocationListener locationListener;

    public Gps(LocationManager locationMgr) {
        locationManager = locationMgr;
        locationListener = new GpsLocationListener();
        location.setLatitude(58.93913229366001);
        location.setLongitude(11.187919929999996);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
    }

    public Location getLatestPosition() {
        return location;
    }

    private class GpsLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            location=loc;        }
        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
