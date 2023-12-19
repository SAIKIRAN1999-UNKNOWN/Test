package com.example.mytestpeoject.Utils;

import java.util.UUID;

public class Constants {
    public static final String LOG_TAG = "SAIKIRAN";
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 15;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 15;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    public static final float MIN_DISTANCE_FROM_PREV = 50;
    public static final float MIN_ACCURACY = 100;
    public static final int DATA_UPLOAD_INTERVAL = 15;



    public static final UUID MY_UUID=UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_PAIRED_DEVICE = 2;
    public static final int REQUEST_DISCOVERABLE_BT = 3;


    public static final int connAttemptsTym = 1000;
    public static final int maxConnAttemptsTym = 16000;

    public static final int PRINT_SLEEP_TIME = 800; //200  //800

    public static final int ZERO = 0;

    public static final int REQUEST_REPRINT_DOCKET = 101;
}
