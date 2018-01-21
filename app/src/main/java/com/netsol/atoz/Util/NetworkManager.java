package com.netsol.atoz.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by macmini on 6/13/17.
 */

public class NetworkManager {
    private static String TAG = Constants.LOG_ATOZ
            + NetworkManager.class.getSimpleName();

    /**
     * This method checks whether the Network is Available. It could be either
     * WI-FI or Cellular
     *
     * @param context
     */
    public static Boolean isNetAvailable(Context context) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getActiveNetworkInfo();

            if (wifiInfo != null && wifiInfo.isConnected()) {
                return true;
            }
        } catch (Exception ex) {

            Log.e(TAG, "Exception is " + Log.getStackTraceString(ex));
        }
        return false;
    }

    /**
     * This method returns the type of Network. (Wi-Fi or Cellular)
     *
     * @param context
     */
    public static String getNetworkType(Context context) {

        String networkType = "";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            networkType = connectivityManager.getActiveNetworkInfo().getTypeName();

        } catch (Exception ex) {

            Log.e(TAG, "Exception is " + Log.getStackTraceString(ex));
        }
        return networkType;
    }

    /**
     * This method checks whether the device is in International Roaming. If the
     * device is in International Roaming, then no service calls should be made.
     *
     * @param context
     */
    public static boolean checkIsRoaming(Context context) {

        boolean isRoaming = false;
        TelephonyManager _telephoneManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (_telephoneManager.isNetworkRoaming()) {

            isRoaming = true;
        }
        return isRoaming;

    }
}
