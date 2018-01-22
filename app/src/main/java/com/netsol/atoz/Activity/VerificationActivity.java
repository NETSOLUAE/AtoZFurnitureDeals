package com.netsol.atoz.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import org.json.JSONArray;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 12/11/17.
 */

public class VerificationActivity extends AppCompatActivity implements AlertAction {

    String email;
    Helper helper;
    Button resend;
    Button verify;
    Context context;
    TextView verifyText;
    EditText emailAddress;
    JsonParser jsonParser;
    LinearLayout verifyBack;
    ProgressDialog progressDialog;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    WebserviceManager _webserviceManager;
    public static boolean is403ErrorVerify = false;
    public static String verificationMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        context = VerificationActivity.this;
        _webserviceManager = new WebserviceManager(this);
        jsonParser = new JsonParser(this);
        progressDialog = new ProgressDialog(this);
        helper = new Helper(this, this);

        resend = (Button) findViewById(R.id.button_resend);
        verify = (Button) findViewById(R.id.button_verify);
        emailAddress = (EditText) findViewById(R.id.verification_code);
        verifyText = (TextView) findViewById(R.id.verify_text);
        verifyBack = (LinearLayout) findViewById(R.id.verification_back);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        email = sharedPref.getString(Constants.EMAIL, "");
        verifyText.setText(String.format("%s%s%s", context.getString(R.string.verification), " ", email));

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailAddress.getText().toString();

                if (email.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.enter_email_error), Toast.LENGTH_SHORT).show();
                } else if (!Helper.checkEmail(email)) {
                    Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
                } else {
                    new VerificationActivity.ResendCall().execute(this, "post", "");
                }
            }
        });

        verifyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = sharedPref.getString(Constants.PASSWORD, "");

                if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.all_fields_mandatory), Toast.LENGTH_SHORT).show();
                } else if (email.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.enter_email_error), Toast.LENGTH_SHORT).show();
                } else if (!Helper.checkEmail(email)) {
                    Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
                } else {
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("pass", password)
                            .build();

                    new VerificationActivity.LoginCall().execute(this, "post", formBody);
                }
            }
        });

        setFooter();
    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    public void setFooter() {
        ImageView followFb = (ImageView) findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) findViewById(R.id.follow_cam);

        /* Footer Action
         */
        followFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String socailLink=context.getString(R.string.follow_fb);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = helper.getFacebookUrl(VerificationActivity.this, socailLink);
                    if (facebookUrl == null || facebookUrl.length() == 0) {
                        Log.d("facebook Url", " is coming as " + facebookUrl);
                        return;
                    }
                    intent.setData(Uri.parse(facebookUrl));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_fb)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(VerificationActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
        followGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_google)) );
                intent.setPackage( "com.google.android.apps.plus" );
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity( intent );
                } else {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_google)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(VerificationActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        followLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@" + "a-to-z-furniture-2aa36a156"));
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/a-to-z-furniture-2aa36a156"));
                final PackageManager packageManager = context.getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_linkendin)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(VerificationActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    startActivity(intent);
                }
            }
        });
        followTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = null;
                try{
                    // Get Twitter app
                    context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + context.getString(R.string.follow_twitter_user_id)));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_twitter)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(VerificationActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
        followCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(context.getString(R.string.follow_cam_app));
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_cam)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(VerificationActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    private class ResendCall extends AsyncTask<Object, Void, Object> {

        private String TAG = VerificationActivity.ResendCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(VerificationActivity.this,
                    VerificationActivity.this.getResources().getString(
                            R.string.resending),
                    VerificationActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_RESEND_EMAIL + "&email=" + email;
//            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String errorMessage = jsonParser.parseResendResponse(result.toString());
                if (errorMessage.equalsIgnoreCase("Updated")) {
                    if (!verificationMessage.equalsIgnoreCase("")) {
                        Toast.makeText(getBaseContext(), verificationMessage, Toast.LENGTH_LONG).show();
                    }
//                    editor.putBoolean(Constants.SESSION, true);
//                    editor.apply();
//                    Intent intent = new Intent(VerificationActivity.this, SigninActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                } else {
                    helper.cancelableAlertDialog("", verificationMessage, 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class LoginCall extends AsyncTask<Object, Void, Object> {

        private String TAG = VerificationActivity.LoginCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(VerificationActivity.this,
                    VerificationActivity.this.getResources().getString(
                            R.string.verifying),
                    VerificationActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_LOGIN;
            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String parseUpdate = jsonParser.parseLogin(result.toString());
                if (!is403ErrorVerify) {
                    editor.putBoolean(Constants.SESSION, true);
                    editor.apply();
                    Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (parseUpdate.equalsIgnoreCase("Updated")) {
                    editor.putBoolean(Constants.SESSION, true);
                    editor.apply();
                    Intent intent = new Intent(VerificationActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (parseUpdate.equalsIgnoreCase("Inactive user")) {
                    helper.cancelableAlertDialog("", parseUpdate, 1);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}
