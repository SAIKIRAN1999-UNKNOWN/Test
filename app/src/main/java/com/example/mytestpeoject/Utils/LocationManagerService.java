package com.example.mytestpeoject.Utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.example.mytestpeoject.LocarionServicePractise;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationManagerService extends Service implements LocationListener
{
    private static final String TAG = "LocationManagerService";


    boolean isPassiveEnabled = false;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private Context context;


    private int level;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();
    private Location location = new Location("");
    private PowerManager.WakeLock wakeLock;

    List<Location> locList = new ArrayList<>();

    public LocationManagerService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();

        context = LocationManagerService.this;

        context.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //code here


        Log.e("Location","location listener");
        try {

            if (locationManager == null) {
                locationManager = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);
            }

            // getting Passive Provider's status
            isPassiveEnabled = locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled && !isPassiveEnabled) {
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    // First get location from Network Provider
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, Constants.FASTEST_INTERVAL, Constants.MIN_DISTANCE_FROM_PREV, this);
                }
                // get location from Passive Provider
                if (isPassiveEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER, Constants.FASTEST_INTERVAL, Constants.MIN_DISTANCE_FROM_PREV, this);
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, Constants.FASTEST_INTERVAL, Constants.MIN_DISTANCE_FROM_PREV, this);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startServiceinForeground();
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "";
        String channelName = "My Background Service";
        NotificationChannel chan = null;
        chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_notif_vmiles)
//                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Location Capture")
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (wakeLock != null) {
            wakeLock.release();
        }
        super.onDestroy();
//        context.startService(new Intent(context, LocationManagerService.class));
//        Intent intent = new Intent(context, SplashScreen.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }
    private void startServiceinForeground() {
        Intent notificationIntent = new Intent(this, LocationManagerService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_notif_vmiles)
//                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Location Capture")
                .setContentIntent(pendingIntent).build();

        int m = (int) (System.currentTimeMillis() % 10000);

        startForeground(m, notification);
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null) {
            location = loc;
            String locate = new Gson().toJson(location);
            Log.d(TAG, locate);
            Toast.makeText(context,locate,Toast.LENGTH_LONG).show();
            locList.add(location);
            Intent intent = new Intent("your.package.action.UPDATE_LIST_VIEW");
            intent.putExtra("listData",new Gson().toJson(locList));
            sendBroadcast(intent);
//            new LocationAsync().execute();
        }else {
            Toast.makeText(context,"Null Location Recieved",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(context,"StatusChanged",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context,"ProviderEnabled",Toast.LENGTH_LONG).show();

    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context,"ProviderDisabled",Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag:");
        wakeLock.acquire();
        return START_STICKY;
    }
    public class LocalBinder extends Binder {
        LocationManagerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationManagerService.this;
        }
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }


    /*public class LocationAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                //consider only if location.getTime() is within 3 seconds from the currentTimeMillis and put an accuracy filter with 50 - 100m(?)
                Utils.writeToLocationsLog(context, DateFormat.getDateTimeInstance().format(new Date()) + " Location Provider: " + location.getProvider());
                Utils.writeToLocationsLog(context, DateFormat.getDateTimeInstance().format(new Date()) + " " + mDataSource.vmilesLocations.getAllLocations().size() + " DistanceCumulative: " + distanceTravelled + " Speed: " + location.getSpeed() + " " + location.getLatitude() + " , " + location.getLongitude() + " Accuracy: " + location.getAccuracy() + ", " + location.getTime());
//            Utils.appendLog(DateFormat.getDateTimeInstance().format(new Date()) + mDataSource.vmilesLocations.getAllLocations().size() + " DistanceCumulative: " + distanceTravelled + "Speed: " + location.getSpeed() + " " + location.getLatitude() + " , " + location.getLongitude() + " " + location.getAccuracy() + " " + location.getTime(), Constants.LOCATION_FILE);

                //save to sqlite, upload to server for every 20 location updates
                float distFromPrev = 0, distFromPrevValid = 0;
                //check with the permissible speed levels, ignore if it's unusual speed.
                //this is because there are some location updates for which the accuracy is misleading
                //but the location seems to be incredibly far away from the previous point
                //within a second
                if (prevLocation != null && prevLocation.getLongitude() != 0 && prevLocation.getLatitude() != 0) {
                    long timeDifference = location.getTime() - prevLocation.getTime(); //in milliseconds
                    float distanceInBetween = location.distanceTo(prevLocation); //in meters
                    if (timeDifference == 0) {
                        prevLocation.set(location);
                        return null;
                    }
                    float speed = (distanceInBetween / timeDifference) * 1000; //*1000 to get the speed in meters per second
                    if (speed > 30) {
                        prevLocation.set(location);
                        return null;
                    }
                }
                if (prevLocation != null) {
                    if (prevLocation.getLatitude() != 0 && prevLocation.getLongitude() != 0) {
                        distFromPrev = prevLocation.distanceTo(location);
                        distanceTravelled += distFromPrev;
                    }
                }

                Calendar previousLocCalendar = Calendar.getInstance();
                previousLocCalendar.setTimeInMillis(prevLocation != null ? prevLocation.getTime() : 0);
                Calendar presentCalendar = Calendar.getInstance();

                Log.d("previousLocCalendar", previousLocCalendar.get(Calendar.YEAR) + "-" + (previousLocCalendar.get(Calendar.MONTH) + 1) + "-" + previousLocCalendar.get(Calendar.DAY_OF_MONTH));
                Log.d("presentCalendar", presentCalendar.get(Calendar.YEAR) + "-" + (presentCalendar.get(Calendar.MONTH) + 1) + "-" + presentCalendar.get(Calendar.DAY_OF_MONTH));
                if (previousLocCalendar.get(Calendar.YEAR) == presentCalendar.get(Calendar.YEAR) && previousLocCalendar.get(Calendar.MONTH) == presentCalendar.get(Calendar.MONTH)
                        && previousLocCalendar.get(Calendar.DAY_OF_MONTH) == presentCalendar.get(Calendar.DAY_OF_MONTH)) {
                    //date has not changed since the last location. do nothing and proceed
                } else {
                    distFromPrevValid = 0;
                    distanceTravelled = 0;
                    prevValidLocation = new Location("");
                    prevLocation = new Location("");
                }

                if (prevValidLocation != null) {
                    if (prevValidLocation.getLatitude() != 0 && prevValidLocation.getLongitude() != 0) {
                        if (location.getAccuracy() <= 25) {
                            distFromPrevValid = prevValidLocation.distanceTo(location);
                        } else {
                            distFromPrevValid = 0;
                        }
                    }
                }

                DecimalFormat format = new DecimalFormat("##.00");
                distanceTravelled = Float.parseFloat(format.format(distanceTravelled));

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(location != null ? location.getTime() : 0);
                String timeStampStr = formatter.format(calendar.getTime());

                //Geocoding
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(LocationManagerService.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String address = null, pincode = null;
                if (addresses != null) {
                    if (addresses.size() != 0) {
                        pincode = addresses.get(0).getPostalCode();
                        address = addresses.get(0).getAddressLine(0);
                    }
                }

                //setting the json ready
                UserLocationsModel model = new UserLocationsModel();
                model.setLatitude(location.getLatitude());
                model.setLongitude(location.getLongitude());
                model.setAccuracy(location.getAccuracy());
                model.setDeviceTimestamp(timeStampStr);
                model.setAddress(address);
                model.setPincode(pincode);
                model.setDistance(distFromPrevValid);
                model.setBattery(level + "");
                model.setUserId(userName);
                model.setDeviceImei(mDataSource.sharedPreferences.getValue(Constants.IMEI));
                model.setCumulativeDistance(distanceTravelled);
                model.setDeviceId(deviceId);
                model.setBranchCode(branch);

                */
    /*
                 * pivotFlag = 3 => poor accuracy
                 * pivotFlag = 2 => good accuracy but, not far enough from the previous point
                 * pivotFlag = 1 => good accuracy and far enough from the previous point
                 * */
    /*
                if (location.getAccuracy() > Constants.MIN_ACCURACY) {
                    model.setPlotFlag(3);
                } else {
                    if (prevValidLocation.getLatitude() != 0 && prevValidLocation.getLongitude() != 0 && distFromPrevValid < Constants.MIN_DISTANCE_FROM_PREV) {
                        if (!(mDataSource.sharedPreferences.getValue(Constants.PIVOT_USERNAME)).equals(mDataSource.sharedPreferences.getValue(Constants.USERNAME_PREF))) {
                            model.setPlotFlag(1);
                        } else {
                            model.setPlotFlag(2);
                        }
                    } else {
                        model.setPlotFlag(1);
                        prevValidLocation.set(location);
                    }
                }

                distanceTravelled = Float.parseFloat(format.format(distanceTravelled));

                model.setCumulativeDistance(distanceTravelled);

                List<UserLocationsModel> list = new ArrayList<>();
                list.add(model);
//                UserLocationHistory json = new UserLocationHistory();
//                json.setUserLocationModels(list);

                Log.d("json", list.toString());

                //logic for sending updates starts here
                if (location.getAccuracy() > Constants.MIN_ACCURACY) { //send poor accuracy update
                    plotFlag = 3;
                    if (prevLocation != null && !(prevLocation.getLatitude() == location.getLatitude() && prevLocation.getLongitude() == location.getLongitude())) {
                        long l = mDataSource.vmilesLocations.insertLocations(location.getLatitude(), location.getLongitude(), location.getAccuracy(),
                                address, deviceId, userName, distFromPrevValid, model.getDeviceTimestamp(), 0, null, null, distanceTravelled, plotFlag, level + "");
                        Log.d("Location Inserted", "" + l);
                    }
                } else { //the accuracy is good enough

                    //make a basic check if the username has changed. That means a different user has logged in from the same device. Then we need an immediate location update
                    String userName = mDataSource.sharedPreferences.getValue(Constants.USERNAME_PREF);
                    if (userName.length() != 0) {
                        if (!(mDataSource.sharedPreferences.getValue(Constants.PIVOT_USERNAME)).equals(mDataSource.sharedPreferences.getValue(Constants.USERNAME_PREF))) { //username changed
                            //since it's an immediate update set plotFlag = 1. Even if the same update gets submitted again, no problem.
                            // Just one extra update on a different user login
                            //won't cause a problem
                            String data = null;
                            try {
                                data = mapper.writeValueAsString(list);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            long insert = mDataSource.offlineRequests.insertRequest(VXUtils.getUploadLocationsUrl(), data, userName);
                            Log.d("Username change Update", insert + " " + data);
//                            if (!Globals.isPrintingActive) {
                            Intent intent = new Intent(LocationManagerService.this, OfflinePushService.class);
                            startService(intent);
//                            }
                            mDataSource.sharedPreferences.set(Constants.PIVOT_USERNAME, userName);
                            prevLocation.set(location);
                            return null;
                        }
                    }

                    String pivotDateStr = mDataSource.sharedPreferences.getValue(Constants.PIVOT_TIMESTAMP);
                    if (pivotDateStr.length() != 0) { //pivot values exist
                        try {
                            Date pivotDate = formatter.parse(pivotDateStr);
                            Calendar todayCalendar = Calendar.getInstance();
                            Calendar pivotCalendar = Calendar.getInstance();
                            pivotCalendar.setTime(pivotDate);
                            Log.d("pivotDate", pivotCalendar.get(Calendar.YEAR) + "-" + (pivotCalendar.get(Calendar.MONTH) + 1) + "-" + pivotCalendar.get(Calendar.DAY_OF_MONTH));
                            Log.d("todayDate", todayCalendar.get(Calendar.YEAR) + "-" + (todayCalendar.get(Calendar.MONTH) + 1) + "-" + todayCalendar.get(Calendar.DAY_OF_MONTH));
                            if (pivotCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) && pivotCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH)
                                    && pivotCalendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH)) {
                                //pivot values are of today. do nothing & proceed
                            } else {
                                //pivot values are not of today. upload location anyway normally and reset pivot values
                                UserLocationsModel pivotModel = new UserLocationsModel();
                                String latitudeStr = mDataSource.sharedPreferences.getValue(Constants.PIVOT_LATITUDE);
                                String longitudeStr = mDataSource.sharedPreferences.getValue(Constants.PIVOT_LONGITUDE);
                                String batteryStr = mDataSource.sharedPreferences.getValue(Constants.PIVOT_BATTERY);
                                pivotModel.setBattery(batteryStr);
                                pivotModel.setLatitude(latitudeStr.length() != 0 ? Double.parseDouble(latitudeStr) : 0);
                                pivotModel.setLongitude(longitudeStr.length() != 0 ? Double.parseDouble(longitudeStr) : 0);
                                String accuracyStr = mDataSource.sharedPreferences.getValue(Constants.PIVOT_ACCURACY);
                                pivotModel.setAccuracy(accuracyStr.length() != 0 ? Float.parseFloat(accuracyStr) : 0);
                                pivotModel.setPlotFlag(1); //since it's probably yesterday's last pivot location, show it anyway. Hence, plotFlag =1
                                pivotModel.setCumulativeDistance(distanceTravelled);
                                pivotModel.setDeviceTimestamp(pivotDateStr);
                                pivotModel.setAddress(mDataSource.sharedPreferences.getValue(Constants.PIVOT_ADDRESS));
                                pivotModel.setUserId(userName);
                                pivotModel.setDeviceImei(mDataSource.sharedPreferences.getValue(Constants.IMEI));
                                pivotModel.setDistance(distFromPrevValid);// ignore distance here

                                distanceTravelled = 0;
                                distFromPrevValid = 0;
                                prevValidLocation = new Location("");
                                prevLocation = new Location("");

                                List<UserLocationsModel> pivotList = new ArrayList<>();
                                pivotList.add(pivotModel);
//                                UserLocationHistory pivotJson = new UserLocationHistory();
//                                pivotJson.setUserLocationModels(pivotList);
                                String data = mapper.writeValueAsString(pivotList);//gson.toJson(pivotJson);
                                if (pivotModel.getLatitude() != 0 && pivotModel.getLongitude() != 0) {
                                    long insert = mDataSource.offlineRequests.insertRequest(VXUtils.getUploadLocationsUrl(), data, userName);
                                    Log.d("Previous Day Update", insert + " " + data);
//                                    if (!Globals.isPrintingActive) {
                                    Intent intent = new Intent(LocationManagerService.this, OfflinePushService.class);
                                    startService(intent);
//                                    }
                                }

                                mDataSource.sharedPreferences.set(Constants.PIVOT_LATITUDE, "");
                                mDataSource.sharedPreferences.set(Constants.PIVOT_LONGITUDE, "");
                                mDataSource.sharedPreferences.set(Constants.PIVOT_ACCURACY, "");
                                mDataSource.sharedPreferences.set(Constants.PIVOT_ADDRESS, "");
                                mDataSource.sharedPreferences.set(Constants.PIVOT_TIMESTAMP, "");
                                mDataSource.sharedPreferences.set(Constants.PIVOT_BATTERY, "");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (JsonGenerationException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //check whether the pivot values exist (they might not have been initialized yet or might have reset in the previous step)
                    if (mDataSource.sharedPreferences.getValue(Constants.PIVOT_LATITUDE).length() == 0) { //set pivot values and send an update. show it anyway. Hence, plotFlag =1
                        mDataSource.sharedPreferences.set(Constants.PIVOT_LATITUDE, location.getLatitude() + "");
                        mDataSource.sharedPreferences.set(Constants.PIVOT_LONGITUDE, location.getLongitude() + "");
                        mDataSource.sharedPreferences.set(Constants.PIVOT_ACCURACY, location.getAccuracy() + "");
                        mDataSource.sharedPreferences.set(Constants.PIVOT_ADDRESS, address);
                        mDataSource.sharedPreferences.set(Constants.PIVOT_TIMESTAMP, timeStampStr);
                        mDataSource.sharedPreferences.set(Constants.PIVOT_BATTERY, level + "");

                        //send pivot values to server
//                        UserLocationHistory firstLoc = new UserLocationHistory();
                        List<UserLocationsModel> firstLocList = new ArrayList<>();
                        UserLocationsModel firstLocModel = new UserLocationsModel();
                        firstLocModel.setLatitude(location.getLatitude());
                        firstLocModel.setLongitude(location.getLongitude());
                        firstLocModel.setAccuracy(location.getAccuracy());
                        firstLocModel.setAddress(address);
                        firstLocModel.setDeviceImei(mDataSource.sharedPreferences.getValue(Constants.IMEI));
                        firstLocModel.setUserId(userName);
                        firstLocModel.setDistance(distFromPrevValid);
                        firstLocModel.setDeviceTimestamp(model.getDeviceTimestamp());
                        firstLocModel.setActivityFlag(0);
                        firstLocModel.setPlotFlag(1);
                        if (prevValidLocation != null && prevValidLocation.getLatitude() != 0 && prevValidLocation.getLongitude() != 0) {
                            distanceTravelled += prevValidLocation.distanceTo(location);
                        }
                        firstLocModel.setCumulativeDistance(distanceTravelled);
                        firstLocModel.setBattery(level + "");
                        firstLocList.add(firstLocModel);
//                        firstLoc.setUserLocationModels(firstLocList);

                        String submitJson = null;
                        try {
                            submitJson = mapper.writeValueAsString(firstLocList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        long insert = mDataSource.offlineRequests.insertRequest(VXUtils.getUploadLocationsUrl(), submitJson, userName);

                        prevValidLocation.set(location);
                        Log.d("Location Inserted", "" + insert);
                    } else { //pivot values exist
                        double lat = Double.parseDouble(mDataSource.sharedPreferences.getValue(Constants.PIVOT_LATITUDE));
                        double lng = Double.parseDouble(mDataSource.sharedPreferences.getValue(Constants.PIVOT_LONGITUDE));
                        float pivotAccuracy = mDataSource.sharedPreferences.getValue(Constants.PIVOT_ACCURACY).length() != 0 ?
                                Float.parseFloat(mDataSource.sharedPreferences.getValue(Constants.PIVOT_ACCURACY)) : 0;
                        String pivotAddress = mDataSource.sharedPreferences.getValue(Constants.PIVOT_ADDRESS);
                        String pivotBattery = mDataSource.sharedPreferences.getValue(Constants.PIVOT_BATTERY);

                        //compare upto 4 decimals to check the proximity of the location with pivot
                        if (Math.floor(lat * 10000) == Math.floor(location.getLatitude() * 10000) &&
                                Math.floor(lng * 10000) == Math.floor(location.getLongitude() * 10000)) { //location and pivot are near to each other

                            String data = null;//gson.toJson(json);
                            try {
                                data = mapper.writeValueAsString(list);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //save to locations table with appropriate plotFlag
                            if (!(prevLocation.getLatitude() == location.getLatitude() && prevLocation.getLongitude() == location.getLongitude())) {
                                long l = mDataSource.vmilesLocations.insertLocations(location.getLatitude(), location.getLongitude(), location.getAccuracy(),
                                        address, deviceId, userName,distFromPrevValid, model.getDeviceTimestamp(), 0, null, null, distanceTravelled, model.getPlotFlag(), level + "");
                            }
                        } else { //location and pivot are not very near to each other
                            //send pivot values to server
                            //compare pivot captured time with current time and if >10 minutes, set delay parameters
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date pivotDate = null;
                            try {
                                pivotDate = sdf.parse(mDataSource.sharedPreferences.getValue(Constants.PIVOT_TIMESTAMP));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date currentTime = null;
                            try {
                                currentTime = sdf.parse(sdf.format(new Date()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            int delayFlag = 0;
                            String startTime = null, endTime = null;
                            if (currentTime.getTime() - pivotDate.getTime() > 10 * 60 * 1000) {
                                delayFlag = 1;
                                startTime = mDataSource.sharedPreferences.getValue(Constants.PIVOT_TIMESTAMP);
                                endTime = sdf.format(currentTime);
                            }
//                            String branch = (userName.length() != 0) ? userName.substring(0, 4) : "";

                            //if the user is in any one of the dly/pickup geofences, delay need not be considered. Hence, put this as an exceptional case in delay points.
//                            if (mDataSource.pudGeofences.getGeofencesEnteredCount(branch) == 0) { //user hasn't entered any of the dly/pickup geofences. send delay point
//                                Log.d("GeofencesEnteredCount", "zero");

                            //insert both delay point and current point
                            if (!(prevLocation.getLatitude() == lat && prevLocation.getLongitude() == lng)) {
                                long lDelay = mDataSource.vmilesLocations.insertLocations(lat, lng, pivotAccuracy,
                                        pivotAddress, deviceId, userName, distFromPrevValid, pivotDateStr, delayFlag, startTime,
                                        endTime, distanceTravelled, 1, pivotBattery);//plotFlag as 1 since, it's a delay point
                                Log.d("Delay Location Inserted", "" + lDelay);
                            }
                            if (!(prevLocation.getLatitude() == location.getLatitude() && prevLocation.getLongitude() == location.getLongitude())) {
                                long lCurrent = mDataSource.vmilesLocations.insertLocations(location.getLatitude(), location.getLongitude(), location.getAccuracy(),
                                        address, deviceId, userName, distFromPrevValid, model.getDeviceTimestamp(), 0, null,
                                        null, distanceTravelled, 1, level + ""); //plotFlag as 1 since, it's a point immediately after delay
                                prevValidLocation.set(location);
                                Log.d("Cur loc Inserted", "" + lCurrent);
                            }
                            //update pivot values
                            mDataSource.sharedPreferences.set(Constants.PIVOT_LATITUDE, location.getLatitude() + "");
                            mDataSource.sharedPreferences.set(Constants.PIVOT_LONGITUDE, location.getLongitude() + "");
                            mDataSource.sharedPreferences.set(Constants.PIVOT_ACCURACY, location.getAccuracy() + "");
                            mDataSource.sharedPreferences.set(Constants.PIVOT_ADDRESS, address);
                            mDataSource.sharedPreferences.set(Constants.PIVOT_TIMESTAMP, timeStampStr);
                            mDataSource.sharedPreferences.set(Constants.PIVOT_BATTERY, level + "");
//                            } else { //the user is in one of the dly/pickup geofence. hence, location shouldn't be considered as a delay point
//                                Log.d("GeofencesEnteredCount", "non-zero");
//                                //set plot flag as 1 since there's a difference in 4 decimal places of lat longs. but delayflag as 0 since it shouldn't be considered as a delay point
//                                if (!(prevLocation.getLatitude() == location.getLatitude() && prevLocation.getLongitude() == location.getLongitude())) {
//                                    long lDelay = mDataSource.vmilesLocations.insertLocations(lat, lng, pivotAccuracy,
//                                            pivotAddress, info.getId(), user.getId(), user.getAppUserName(), distFromPrevValid, model.getDeviceTimestamp(), 0, null, null, distanceTravelled, 1, pivotBattery);
//                                    Log.d("Delay loc as valid", "" + lDelay);
//                                }
//                                if (!(prevLocation.getLatitude() == location.getLatitude() && prevLocation.getLongitude() == location.getLongitude())) {
//                                    long lCurrent = mDataSource.vmilesLocations.insertLocations(location.getLatitude(), location.getLongitude(), location.getAccuracy(),
//                                            address, info.getId(), user.getId(), user.getAppUserName(), distFromPrevValid, model.getDeviceTimestamp(), 0, null, null, distanceTravelled, 1, level + "");
//                                    prevValidLocation.set(location);
//                                    Log.d("Cur loc Inserted", "" + lCurrent);
//                                }
//                            }
                        }
                    }
                }

                //check if there is any old data (yesterday's), remove it, upload all the existing records to server and delete the records after successful upload
                List<UserLocationsModel> locationsList = mDataSource.vmilesLocations.getAllLocations();
                Log.d("locationsListsize", "" + locationsList.size() + " " + locationsList.toString());
                if (locationsList.size() >= 5) { //uploading to server every 5 records captured at 1 min each
//                    UserLocationHistory historyJson = new UserLocationHistory();
                    List<UserLocationsModel> listLocations = new ArrayList<>();
                    for (UserLocationsModel locationModel : locationsList) {
                        String timestamp = locationModel.getDeviceTimestamp();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date capturedDate = null;
                        try {
                            capturedDate = sdf.parse(timestamp);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (System.currentTimeMillis() - capturedDate.getTime() > 24 * 60 * 60 * 1000) { // || locationModel.getAccuracy() > 100
                            continue;
                        }
                        listLocations.add(locationModel);
                    }

                    //Algorithm for noise reduction and smoothening
//                    if (listLocations.size() >= 5) {
//                        Location loc = new Location("");
//                        loc.setLatitude(listLocations.get(0).getLatitude());
//                        loc.setLongitude(listLocations.get(0).getLongitude());
//                        Location thisLoc = new Location("");
//                        thisLoc.setLatitude(listLocations.get(listLocations.size() - 1).getLatitude());
//                        thisLoc.setLongitude(listLocations.get(listLocations.size() - 1).getLongitude());
//                        double cumulativeDistance = loc.distanceTo(thisLoc);
//                        if (cumulativeDistance >= 3 * Constants.MIN_DISTANCE_FROM_PREV) { //smoothen the path
//                            for (int k = 0; k > listLocations.size(); k++) {
//                                if(k == 0) {
//                                    if (listLocations.get(k).getPlotFlag() != 3) {
//                                        listLocations.get(k).setPlotFlag(1);
//                                    }
//                                }
//                            }
//                        }
////                    else if (cumulativeDistance < 2 * Constants.MIN_DISTANCE_FROM_PREV) { //reduce the noise
////                        for (UserLocationModel locationModel : listLocations) {
////                            if (locationModel.getPlotFlag() != 3 && locationModel.getFlag() != 1) { //since delay points shouldn't be considered invalid
////                                locationModel.setPlotFlag(2);
////                            }
////                        }
////                    }
//                    }

                    Log.d(TAG, "" + listLocations.size());
//                    historyJson.setUserLocationModels(listLocations);
//                    new UploadLocations(historyJson).execute();
                    String submitData = null;//gson.toJson(historyJson);
                    try {
                        submitData = mapper.writeValueAsString(listLocations);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    long insert = mDataSource.offlineRequests.insertRequest(VXUtils.getUploadLocationsUrl(), submitData, userName);
                    if (insert != -1) {
                        mDataSource.vmilesLocations.clearAll();
                        Log.d("ClearLocations", "");
//                        if (!Globals.isPrintingActive) {
                        Intent intent = new Intent(LocationManagerService.this, OfflinePushService.class);
                        startService(intent);
//                        }
                    }
                }
//        }
                prevLocation.set(location);
            }

            return null;
        }
    }*/

   /* public Location getLastKnownLocation() {
        Location location = null;

        try {

            if (!isGPSEnabled && !isNetworkEnabled && !isPassiveEnabled) {
                // no network provider is enabled
                Utils.writeToLocationsLog(context, DateFormat.getDateTimeInstance().format(new Date()) + " No Location Provider Available");
//                Utils.appendLog(DateFormat.getDateTimeInstance().format(new Date()) + " No Location Provider Available", Constants.LOCATION_FILE);
            } else {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                // First get location from Passive Provider
                if (isPassiveEnabled) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.PASSIVE_PROVIDER, 0, 0, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                }

                // First get location from Network Provider
                if (isNetworkEnabled) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                //can also include passive provider in a similar way and test if needed
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER, 0, 0, this);
                    if (location == null) {
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }


        } catch (Exception e) {
            LogUtil.error(LocationManagerService.class, e.toString());
            location = null;
        }

        return location;
    }*/
}
