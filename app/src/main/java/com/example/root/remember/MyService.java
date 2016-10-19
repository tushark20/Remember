package com.example.root.remember;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class MyService extends Service {
    private static int j = 0;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private static final int LOCATION_INTERVAL = 0; //5000
    private static final float LOCATION_DISTANCE = 0f;//10f
    private static int NOTIFICATION_ID = 1;
    private static final Double RADIUS = 0.1; //in km
    SQLiteDatabase db;
    Cursor c;
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        db = openOrCreateDatabase("RememberDB", Context.MODE_PRIVATE, null);


        Toast.makeText(getApplicationContext(), "Alarm is on", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
           // Toast.makeText(getApplicationContext(), ActivityCompat.checkSelfPermission
                    //(this, Manifest.permission.ACCESS_FINE_LOCATION)+", "+PackageManager.PERMISSION_GRANTED,Toast.LENGTH_SHORT).show();
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
                    LOCATION_DISTANCE, mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Alarm is off", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            //Toast.makeText(getApplicationContext(), "location changed", Toast.LENGTH_SHORT).show();
            mLastLocation.set(location);

            final ContentValues values = new ContentValues();
            values.put("status", 0);
            c=db.rawQuery("SELECT * FROM remember", null);
            if(DatabaseUtils.queryNumEntries(db, "remember")>0) {
                while (c.moveToNext()) {

                    if (c.getInt(5) == 1) {
                        //Toast.makeText(getApplicationContext(), c.getString(1) + ", " + c.getString(5), Toast.LENGTH_SHORT).show();

                        double lat2 = location.getLatitude();
                        double lng2 = location.getLongitude();
                        double lat1 = c.getDouble(3);
                        double lng1 = c.getDouble(4);
                        // lat1 and lng1 are the values of a previously stored location
                        double dis = distance(lat1, lng1, lat2, lng2);

                        if (dis < RADIUS) {
                            //Toast.makeText(getApplicationContext(), c.getString(1) + ", " + String.valueOf(dis), Toast.LENGTH_SHORT).show();
                            processStartNotification(c.getString(1) + ", " + c.getString(2), c.getInt(0));
                            db.update("remember", values, "_id" + " = " + "'" + c.getInt(0) + "'", null);

                        }

                    }
                }
            }
        }


        private double distance(double lat1, double lng1, double lat2, double lng2) {

            double earthRadius = 6371; // in miles, change to 6371 for kilometer output

            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);

            double sindLat = Math.sin(dLat / 2);
            double sindLng = Math.sin(dLng / 2);

            double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                    * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            double dist = earthRadius * c;

            return dist; // output distance, in MILES
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private void processStartNotification(String str, int i) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setSmallIcon(R.drawable.add)
                .setContentText(str);

        NOTIFICATION_ID = i;//(int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);//i
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, new Intent(this, NotificationActivity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
        // NOTIFICATION_ID = NOTIFICATION_ID +1;
        //notificationManager.notify(, notification);
    }

}