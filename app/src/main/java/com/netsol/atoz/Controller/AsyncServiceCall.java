package com.netsol.atoz.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by macmini on 6/13/17.
 */

public class AsyncServiceCall extends AsyncTask<Integer, Context, Object> {
    private static String TAG = ""
            + AsyncServiceCall.class.getSimpleName();

    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
        } catch (Exception ex) {
            Log.e(TAG, "Exception is " + Log.getStackTraceString(ex));
        }
    }

    @Override
    protected Object doInBackground(Integer... params) {

        return null;
    }

    @Override
    protected void onPostExecute(Object result) {

        super.onPostExecute(result);
    }

}
