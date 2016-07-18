package com.juliusmh.remotegeofix;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MockLocationProvider {
    static String TAG = "MockLocationProvider";
    //static String locationProviderName = "MockGeoFix";
    static String locationProviderName = LocationManager.GPS_PROVIDER;

    static private MockLocationProvider instance = new MockLocationProvider();
    static public MockLocationProvider getInstance() { return instance; }
    private MockLocationProvider() {}

    protected Context mContext;
    protected LocationManager mLocationManager;
    protected SharedPreferences mPref = null;
    protected int accuracy = 1;

    static public void init(Context context) {
        getInstance()._init(context);
    }

    static public void register() { getInstance()._register(); }

    static public void unregister() { getInstance()._unregister(); }

    static public void simulate(double longitude, double latitude) {
        getInstance()._verifyInitiated();
        getInstance()._simulate(longitude, latitude, 0, -1);
    }

    static public void simulate(double longitude, double latitude, double altitude) {
        getInstance()._verifyInitiated();
        getInstance()._simulate(longitude, latitude, altitude, -1);
    }

    static public void simulate(double longitude, double latitude, double altitude,
                                int satellites) {
        getInstance()._verifyInitiated();
        getInstance()._simulate(longitude, latitude, altitude, satellites);
    }

    static public void simulate(Location location) {
        getInstance()._verifyInitiated();
        getInstance()._simulate(location);
    }

    static public Location getLocation() {
        return new Location(locationProviderName);
    }

    protected void _init(Context context) {
        if (mContext != null) return;
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    protected void _register() {
        // if the test provider already exists, android handles this fine
        try {
            mLocationManager.addTestProvider(locationProviderName, false, false, false, false, true, true, true, 0, accuracy);
            mLocationManager.setTestProviderEnabled(locationProviderName, true);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "IllegalArgumentException thrown in _register");
        }
    }

    protected void _unregister() {
        try {
            mLocationManager.removeTestProvider(locationProviderName);
        } catch(Exception ignored) {}
    }

    protected void _simulate(double longitude, double latitude, double altitude, int satellites) {
        Location mockLocation = new Location(locationProviderName); // a string
        mockLocation.setLatitude(latitude);  // double
        mockLocation.setLongitude(longitude);
        mockLocation.setAltitude(altitude);
        if (satellites != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("satellites", satellites);
            mockLocation.setExtras(bundle);
        }
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(accuracy);
        _simulate(mockLocation);
    }

    protected void _simulate(Location location) {
        if (!location.hasAccuracy()) {
            location.setAccuracy(accuracy);
        }
        if (!location.hasAltitude()) {
            location.setAltitude(0);
        }
        try {
            Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
            if (locationJellyBeanFixMethod != null) {
                locationJellyBeanFixMethod.invoke(location);
            }
        } catch (IllegalAccessException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException ignored) {}
        mLocationManager.setTestProviderLocation(locationProviderName, location);
    }

    protected void _verifyInitiated() {
        if (mContext == null) {
            throw new AssertionError("CommandDispatcher.init has not been called!");
        }
    }


}