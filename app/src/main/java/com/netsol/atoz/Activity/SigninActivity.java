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
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.FacebookHelper;
import com.netsol.atoz.Util.FacebookResponse;
import com.netsol.atoz.Util.FacebookUser;
import com.netsol.atoz.Util.Helper;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 9/26/17.
 */

public class SigninActivity extends AppCompatActivity implements AlertAction, FacebookResponse, GoogleApiClient.OnConnectionFailedListener {

    String personName = "";
    String emailGoogle = "";
    String passwordGoogle = "";
    String loginMode = "";
    Helper helper;
    Button signUp;
    Button signIn;
//    Button facebook;
//    Button google;
    Context context;
    EditText email;
    EditText password;
    TextView forgetPassword;
    JsonParser jsonParser;
    LinearLayout signBack;
    ProgressDialog progressDialog;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    WebserviceManager _webserviceManager;
    public static boolean is403Error = false;
    public static FacebookUser facebookUserSignIn;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;

    private FacebookHelper mFbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fb api initialization
        mFbHelper = new FacebookHelper(this,
                "id,name,email,gender,birthday,picture,cover",
                this);
        setContentView(R.layout.activity_signin);

        context = SigninActivity.this;
        _webserviceManager = new WebserviceManager(this);
        jsonParser = new JsonParser(this);
        progressDialog = new ProgressDialog(this);
        helper = new Helper(this, this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signUp = (Button) findViewById(R.id.buttonSignUp);
        signIn = (Button) findViewById(R.id.buttonSignIn);
//        facebook = (Button) findViewById(R.id.buttonFacebook);
//        google = (Button) findViewById(R.id.buttonGoogle);
        email = (EditText) findViewById(R.id.signin_email);
        password = (EditText) findViewById(R.id.signin_password);
        forgetPassword = (TextView) findViewById(R.id.signin_forget_password);
        signBack = (LinearLayout) findViewById(R.id.signin_back);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        signBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginMode = "SIGN_UP";
                Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("MODE", "SIGN_UP");
                startActivity(intent);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginMode = "SIGN_IN";
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if (emailText.equalsIgnoreCase("") || passwordText.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.all_fields_mandatory), Toast.LENGTH_SHORT).show();
                } else {

                    editor.putString(Constants.PASSWORD, passwordText);
                    editor.apply();

                    RequestBody formBody = new FormBody.Builder()
                            .add("email", emailText)
                            .add("pass", passwordText)
                            .build();

                    new SigninActivity.LoginCall().execute(this, "post", formBody);
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, ForgetPassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

//        facebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginMode = "FB";
//                mFbHelper.performSignIn(SigninActivity.this);
//            }
//        });
//
//        google.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginMode = "GOOGLE";
//                mGoogleApiClient.connect();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });

        setFooter();
    }

    @Override
    public void onOkClicked() {
    }

    @Override
    public void onCancelClicked() {

    }

    @Override
    public void onFbSignInFail() {
        Toast.makeText(this, "Facebook sign in failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFbSignInSuccess() {
        Toast.makeText(this, "Facebook sign in Success.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFbProfileReceived(FacebookUser facebookUser) {
//        {"id":"166758977421651","name":"Saranya Marappan","email":"saranya.namasthe@gmail.com","gender":"female",
//                "picture":{"data":{"height":50,"is_silhouette":true,
//                "url":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-1\/c15.0.50.50\/p50x50\/1379841_10150004552801901_469209496895221757_n.jpg?oh=ef2ea8eb8c792b56ff67f460f47f79dd&oe=5ADEBA33",
//                "width":50}}}
        facebookUserSignIn = new FacebookUser();
        facebookUserSignIn = facebookUser;

        if (facebookUserSignIn.email != null) {
            editor.putString(Constants.PASSWORD, facebookUserSignIn.facebookID);
            editor.apply();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", facebookUserSignIn.email)
                    .add("pass", facebookUserSignIn.facebookID)
                    .build();

            new SigninActivity.LoginCall().execute(this, "post", formBody);
        } else {
            helper.cancelableAlertDialog("", "Dear Customer, We are unable to find your credentials", 1);
//            Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("MODE", "FB");
//            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //handle results
        mFbHelper.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Dear Customer, We are unable to authenticate with your credentials. Please try again.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
        super.onStart();

//        if (mGoogleApiClient.isConnected()) {
//            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                    new ResultCallback<Status>() {
//                        @Override
//                        public void onResult(Status status) {
//
//
//                        }
//                    });
//        }

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//
//            Log.d("", "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        }
//        else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("", "display name: " + acct.getDisplayName());

            personName = acct.getDisplayName();
            passwordGoogle = acct.getId();
            emailGoogle = acct.getEmail();

            Log.e("", "Name: " + personName + ", email: " + email);
            if (emailGoogle != null) {
                editor.putString(Constants.PASSWORD, passwordGoogle);
                editor.apply();
                RequestBody formBody = new FormBody.Builder()
                        .add("email", emailGoogle)
                        .add("pass", passwordGoogle)
                        .build();

                new SigninActivity.LoginCall().execute(this, "post", formBody);
            } else {
                Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("MODE", "GOOGLE");
                intent.putExtra("personName", personName);
                intent.putExtra("emailGoogle", emailGoogle);
                intent.putExtra("passwordGoogle", passwordGoogle);
                startActivity(intent);
            }
        }
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
                    String facebookUrl = helper.getFacebookUrl(SigninActivity.this, socailLink);
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
                        Toast.makeText(SigninActivity.this, "No application can handle this request."
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
                        Toast.makeText(SigninActivity.this, "No application can handle this request."
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
                        Toast.makeText(SigninActivity.this, "No application can handle this request."
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
                        Toast.makeText(SigninActivity.this, "No application can handle this request."
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
                        Toast.makeText(SigninActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private class LoginCall extends AsyncTask<Object, Void, Object> {

        private String TAG = SigninActivity.LoginCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SigninActivity.this,
                    SigninActivity.this.getResources().getString(
                            R.string.authenticating),
                    SigninActivity.this.getResources().getString(
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
                if (is403Error) {
                    if (loginMode.equalsIgnoreCase("FB")) {
                        Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("MODE", "FB");
                        startActivity(intent);
                    } else if (loginMode.equalsIgnoreCase("GOOGLE")) {
                        Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("MODE", "GOOGLE");
                        intent.putExtra("personName", personName);
                        intent.putExtra("emailGoogle", emailGoogle);
                        intent.putExtra("passwordGoogle", passwordGoogle);
                        startActivity(intent);
                    } else {
                        helper.cancelableAlertDialog("", parseUpdate, 1);
                    }
                } else if (parseUpdate.equalsIgnoreCase("Updated")) {
                    editor.putBoolean(Constants.SESSION, true);
                    editor.apply();
                    Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}
