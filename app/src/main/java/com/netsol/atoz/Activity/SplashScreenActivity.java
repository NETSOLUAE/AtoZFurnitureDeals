package com.netsol.atoz.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.OrderGroup;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.NetworkManager;

import java.io.File;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SplashScreenActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static int SPLASH_TIME_OUT = 500;
    private DatabaseManager _dbManager;
    WebserviceManager _webserviceManager;
    JsonParser jsonParser;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    Animation animation;
    ImageView logo;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        context = SplashScreenActivity.this;
        _dbManager = new DatabaseManager(this);
        _webserviceManager = new WebserviceManager(this);
        jsonParser = new JsonParser(this);
        logo=(ImageView)findViewById(R.id.zoom_image);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // create database
        if (!isDatabaseExist()) {
            createDatabase();
        }
//        _dbManager.clearCartDetails();
        _dbManager.clearCategory();
        _dbManager.clearProducts();
        int cartCount = _dbManager.getBadgeCount();
        editor.putString(Constants.BADGE_COUNT, String.valueOf(cartCount));
        editor.apply();

        if (NetworkManager.isNetAvailable(SplashScreenActivity.this)) {
            new SplashScreenActivity.GetHome().execute(this, "post", "");
        } else {
            showAlert(context.getString(R.string.network_not_available));
        }
        animation.setAnimationListener(SplashScreenActivity.this);
    }

    // Check whether the database exists
    private boolean isDatabaseExist() {
        File dbFile = getApplicationContext().getDatabasePath("ATOZTESTDB26.db");
        return dbFile.exists();
    }

    // Create the tables
    private void createDatabase() {
        _dbManager.createDb();
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String isFirstInstall = sharedPref.getString(Constants.IS_FIRST_INSTALL,"");
                if (isFirstInstall.equalsIgnoreCase("") || !isFirstInstall.equalsIgnoreCase("false")) {
                    Intent login = new Intent (SplashScreenActivity.this, WelcomeActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                } else {
                    Intent login = new Intent (SplashScreenActivity.this, HomeActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                }
            }
        }, 100);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private class GetHome extends AsyncTask<Object, Void, Object> {

        private String TAG = SplashScreenActivity.GetHome.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_HOME;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            logo.startAnimation(animation);
                        }
                    }, SPLASH_TIME_OUT);
//                    new SplashScreenActivity.BannerCall().execute(this, "post", "");
                } else {
                    showAlert(context.getString(R.string.server_busy));
                }
            } else {
                showAlert(context.getString(R.string.server_busy));
            }
        }
    }

//    private class BannerCall extends AsyncTask<Object, Void, Object> {
//
//        private String TAG = SplashScreenActivity.BannerCall.class.getSimpleName();
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Object doInBackground(Object... params) {
//            String urlString = Constants.BASE_URL + Constants.END_POINT_BANNER;
//
//            Log.e(TAG, "processing http request in async task");
//            return _webserviceManager.getHttpResponse(urlString, false);
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {
//            super.onPostExecute(result);
//
//            if (result != null) {
//                String parseUpdate = jsonParser.parseBannerDetails(result.toString());
//                if (parseUpdate.equalsIgnoreCase("Updated")) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            logo.startAnimation(animation);
//                        }
//                    }, SPLASH_TIME_OUT);
//                } else {
//                    showAlert(context.getString(R.string.server_busy));
//                }
//            } else {
//                showAlert(context.getString(R.string.server_busy));
//            }
//        }
//    }

    private void showAlert(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(message)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(true).show();
    }

}
