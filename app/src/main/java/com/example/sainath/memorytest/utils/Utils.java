package com.example.sainath.memorytest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by sainath on 10/18/2015.
 */
public class Utils {

    /**
     * Utility method to check if the device is connected to Internet
     * @param ctx Context
     * @return true if the device is connected to Internet; false otherwise.
     */
    public static boolean isConnected(Context ctx) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
