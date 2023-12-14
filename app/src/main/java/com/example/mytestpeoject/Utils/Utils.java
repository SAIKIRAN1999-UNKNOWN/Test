package com.example.mytestpeoject.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;


import com.example.mytestpeoject.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    void saveImage( Bitmap originalBitmap) {
        File myDir=new File("/sdcard/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);

            // NEWLY ADDED CODE STARTS HERE [
            Canvas canvas = new Canvas(originalBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE); // Text Color
            paint.setTextSize(12); // Text Size
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
            // some more settings...

            canvas.drawBitmap(originalBitmap, 0, 0, paint);
            canvas.drawText("Testing...", 10, 10, paint);
            // NEWLY ADDED CODE ENDS HERE ]

            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    public static String getUniqueDeviceId(Context c) {
        WifiManager wifiMan = (WifiManager) c
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        String macAddr = wifiInf.getMacAddress();
        if (macAddr == null)
            macAddr = "";
        String unique = macAddr; // + androidId;
        return unique;
    }
    public static String getPath(){
//        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                .toString() + "/";
        return Environment.getExternalStorageDirectory()
                + "/download/";
    }
    public static void logE(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e("LOG", msg);
        }
    }
    public static void logD(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d("LOG", msg);
        }
    }
    public static void logI(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i("LOG", msg);
        }
    }
    public static void writeToLocationsLog(Context context, String text) {
        try {
//            FileCache fileCache = new FileCache(context, "DATA", FileCache.textTyp);
//            String name = "LOCATION - " + Utils.getTime(new Date(), "dd-mm-yyyy hh:MM:ss");
////            fileCache.saveTextFile(text, name);
        } catch (Exception e) {
            Utils.logE(e.toString());
        }
    }
    public static boolean isValidGstin(String gstin) {
        //String regexGstin = "^([0-9]){2}([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}([a-zA-Z1-9]){1}([zZ]){1}([a-zA-Z0-9]){1}";
        String regexGstin  ="^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";
        Pattern gstinPattern = Pattern.compile(regexGstin);
        Matcher gstinMatcher = null;
        if (Utils.isValidString(gstin)) {
            gstinMatcher = gstinPattern.matcher(gstin);
        } else
            return false;

        return gstinMatcher.matches();
    }
    public static String getImageTime(long diff) {
        String scanTime = "";
        Date d = new Date();
        long t = d.getTime();
        long t1 = t + diff;
        Date date = new Date(t1);
        scanTime = date.getTime() + "";
        return scanTime;
    }
    public static boolean isValidString(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0)
                return true;
        }
        return false;
    }
    public static boolean isValidNum(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() > 0 && Integer.parseInt(str)>0)
                return true;
        }
        return false;
    }



}
