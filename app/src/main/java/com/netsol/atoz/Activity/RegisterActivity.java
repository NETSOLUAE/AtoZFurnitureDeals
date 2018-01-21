package com.netsol.atoz.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.netsol.atoz.Adapter.SpinnerNationality;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.AboutGroup;
import com.netsol.atoz.Model.Nationality;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.FacebookUser;
import com.netsol.atoz.Util.Helper;
import com.netsol.atoz.Util.TextDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 9/26/17.
 */

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AlertAction {

    String passwordFb;
    String passwordGoogle;
    String emailGoogle;
    String nameGoogle;
    String loginMode;
    String nationalityText;
    String nationalityId;
    String genderText = "";
    boolean isMale = false;
    boolean isShowing = false;

    Helper helper;
    Button signUp;
    Spinner nationalitySpinner;
    Context context;
    EditText fullName;
    EditText email;
    EditText mobile;
    EditText dob;
    EditText password;
    EditText confirmPassword;
    EditText registerCountry;
    TextView terms;
    CheckBox checkBox;
    RadioGroup gender;
    JsonParser jsonParser;
    LinearLayout dobLayout;
    LinearLayout registerBack;
    LinearLayout showPassword;
    LinearLayout passwordLayout;
    ProgressDialog progressDialog;
    DatePickerDialog datepickerdialog;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ArrayList<Nationality> nationalityList;
    public static String registrationMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            loginMode = b.getString("MODE");
            if (loginMode!= null && loginMode.equalsIgnoreCase("GOOGLE")) {
                passwordGoogle = b.getString("passwordGoogle");
                emailGoogle = b.getString("emailGoogle");
                nameGoogle = b.getString("personName");
            }
        }

        context = RegisterActivity.this;
        _webserviceManager = new WebserviceManager(this);
        databaseManager = new DatabaseManager(this);
        jsonParser = new JsonParser(this);
        progressDialog = new ProgressDialog(this);
        helper = new Helper(this, this);

        email = (EditText)findViewById(R.id.register_email);
        mobile = (EditText)findViewById(R.id.register_mobile);
        dob = (EditText)findViewById(R.id.register_dobText);
        password = (EditText)findViewById(R.id.register_password);
        dobLayout = (LinearLayout) findViewById(R.id.register_dob);
        fullName = (EditText)findViewById(R.id.register_fullName);
        terms = (TextView)findViewById(R.id.register_terms);
        showPassword = (LinearLayout)findViewById(R.id.register_showPassword);
        passwordLayout = (LinearLayout)findViewById(R.id.passwordLayout);
        registerBack = (LinearLayout)findViewById(R.id.register_back);
        gender = (RadioGroup) findViewById(R.id.register_genderRadioGroup);
        confirmPassword = (EditText)findViewById(R.id.register_confirm_password);
        registerCountry = (EditText)findViewById(R.id.register_country);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        signUp = (Button) findViewById(R.id.button_register_signup);
        nationalitySpinner = (Spinner) findViewById(R.id.register_nationality);

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mobile.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(Constants.COUNTRY_CODE, RegisterActivity.this), null, null, null);

        SpannableString SpanString = new SpannableString(
                "By clicking the Sign up button, you confirm that you accept our Terms of Use and Privacy Policy");

        ClickableSpan teremsAndCondition = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
//                Toast.makeText(context, "Clickable span terms and codition", Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(RegisterActivity.this, TermActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra("IS_TERMS", "true");
                startActivity(mIntent);

            }
        };

        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
//                Toast.makeText(context, "Clickable span privacy", Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(RegisterActivity.this, TermActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mIntent.putExtra("IS_TERMS", "false");
                startActivity(mIntent);

            }
        };

        SpanString.setSpan(teremsAndCondition, 64, 76, 0);
        SpanString.setSpan(privacy, 81, 95, 0);
        SpanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 64, 76, 0);
        SpanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 81, 95, 0);
        SpanString.setSpan(new UnderlineSpan(), 64, 76, 0);
        SpanString.setSpan(new UnderlineSpan(), 81, 95, 0);

        terms.setMovementMethod(LinkMovementMethod.getInstance());
        terms.setText(SpanString, TextView.BufferType.SPANNABLE);
        terms.setSelected(true);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datepickerdialog = new DatePickerDialog(this,this,year,month,day);
        datepickerdialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        if (loginMode.equalsIgnoreCase("FB")) {
            passwordLayout.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);
            FacebookUser facebookUser = SigninActivity.facebookUserSignIn;
            passwordFb = facebookUser.facebookID;

            if (facebookUser.name != null) {
                fullName.setText(facebookUser.name);
            }
            if (facebookUser.email != null) {
                email.setText(facebookUser.email);
            }
            if (facebookUser.user_birthday != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                String[] bod = (facebookUser.user_birthday.equals("")?dateFormat.format(new Date()):facebookUser.user_birthday).split("/");
                dob.setText(bod[0]+"-"+bod[1]+"-"+bod[2]);
            }
            if (facebookUser.gender != null) {
                if (facebookUser.gender.equalsIgnoreCase("female")) {
                    gender.check(R.id.register_radio_female);
                } else {
                    gender.check(R.id.register_radio_male);
                }
            }
        } else if (loginMode.equalsIgnoreCase("GOOGLE")) {
            passwordLayout.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);

            if (nameGoogle != null) {
                fullName.setText(nameGoogle);
            }
            if (emailGoogle != null) {
                email.setText(emailGoogle);
            }
        } else {
            passwordLayout.setVisibility(View.VISIBLE);
            confirmPassword.setVisibility(View.VISIBLE);
        }

        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepickerdialog.show();
            }
        });

        dobLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepickerdialog.show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullNameText = fullName.getText().toString();
                String emailText = email.getText().toString();
                String mobileNumber = Constants.COUNTRY_CODE + mobile.getText().toString();
                String dobText = dob.getText().toString();
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();
                String registerCountryText = registerCountry.getText().toString();
                if (loginMode.equalsIgnoreCase("SIGN_UP") && (fullNameText.equalsIgnoreCase("") || emailText.equalsIgnoreCase("") || dobText.equalsIgnoreCase("")
                        || passwordText.equalsIgnoreCase("") || confirmPasswordText.equalsIgnoreCase("") || registerCountryText.equalsIgnoreCase("")
                        || nationalityText.equalsIgnoreCase("Select Nationality"))) {
                    Toast.makeText(context, context.getString(R.string.all_fields_mandatory), Toast.LENGTH_SHORT).show();
                } if ((loginMode.equalsIgnoreCase("FB") || loginMode.equalsIgnoreCase("GOOGLE")) && (fullNameText.equalsIgnoreCase("") || emailText.equalsIgnoreCase("") || dobText.equalsIgnoreCase("")
                        || registerCountryText.equalsIgnoreCase("") || nationalityText.equalsIgnoreCase("Select Nationality"))) {
                    Toast.makeText(context, context.getString(R.string.all_fields_mandatory), Toast.LENGTH_SHORT).show();
                } else if(!Helper.checkEmail(emailText)){
                    Toast.makeText(context, context.getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
                } else if(mobileNumber.length() < 13){
                    Toast.makeText(context, context.getString(R.string.invalid_mobile), Toast.LENGTH_LONG).show();
                } else if (loginMode.equalsIgnoreCase("SIGN_UP") && !passwordText.equalsIgnoreCase(confirmPasswordText)) {
                    Toast.makeText(getBaseContext(), context.getString(R.string.password_mismatch), Toast.LENGTH_LONG).show();
                } else if (loginMode.equalsIgnoreCase("SIGN_UP") && (confirmPasswordText.length() < 6)) {
                    Toast.makeText(getBaseContext(), context.getString(R.string.password_error_message), Toast.LENGTH_LONG).show();
                }
//                else if (!checkBox.isChecked()) {
//                    Toast.makeText(getBaseContext(), context.getString(R.string.terms_error), Toast.LENGTH_LONG).show();
//                }
                else {
                    if (isMale) {
                        genderText = "Male";
                    } else {
                        genderText = "Female";
                    }

                    SimpleDateFormat spf1 = new SimpleDateFormat("dd-MM-yyyy", Locale.CANADA);
                    Date newDate1 = null;
                    try {
                        newDate1 = spf1.parse(dobText);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
                    String birthDate = spf1.format(newDate1);

                    String pass = "";
                    String userActive = "0";
                    if (loginMode.equalsIgnoreCase("FB")) {

                        pass = passwordFb;
                        userActive = "1";

                        editor.putString(Constants.PASSWORD, passwordFb);
                        editor.putString(Constants.FULL_NAME, fullNameText);
                        editor.putString(Constants.EMAIL, emailText);
                        editor.putString(Constants.MOBILE, mobileNumber);
                        editor.putString(Constants.DOB, dobText);
                        editor.putString(Constants.COUNTRY, registerCountryText);
                        editor.putString(Constants.GENDER, genderText);
//                        editor.putString(Constants.API_USER_PHOTO, user_photo);
//                        editor.putString(Constants.USER_ID, userid);
                        editor.putString(Constants.NATIONALITY, nationalityText);
                        editor.apply();

                    } else if (loginMode.equalsIgnoreCase("GOOGLE")) {

                        pass = passwordGoogle;
                        userActive = "1";

                        editor.putString(Constants.PASSWORD, passwordGoogle);
                        editor.putString(Constants.FULL_NAME, fullNameText);
                        editor.putString(Constants.EMAIL, emailText);
                        editor.putString(Constants.MOBILE, mobileNumber);
                        editor.putString(Constants.DOB, birthDate);
                        editor.putString(Constants.COUNTRY, registerCountryText);
                        editor.putString(Constants.GENDER, genderText);
//                        editor.putString(Constants.API_USER_PHOTO, user_photo);
//                        editor.putString(Constants.USER_ID, userid);
                        editor.putString(Constants.NATIONALITY, nationalityText);
                        editor.apply();
                    } else {
                        editor.putString(Constants.PASSWORD, confirmPasswordText);
                        editor.putString(Constants.FULL_NAME, fullNameText);
//                    editor.putString(Constants.LAST_NAME, lastNameText);
                        editor.apply();
                        pass = confirmPasswordText;
                        userActive = "0";
                    }

                    String terms = "No";
                    if (checkBox.isChecked()) {
                        terms = "Yes";
                    } else {
                        terms = "No";
                    }

                    RequestBody formBody = new FormBody.Builder()
                    .add("fullname", fullNameText)
                    .add("mobile", mobileNumber)
                    .add("dob", birthDate)
                    .add("gender", genderText)
                    .add("email", emailText)
                    .add("password", pass)
                    .add("nationality", nationalityId)
                    .add("useractive", userActive)
                    .add("terms", terms)
                    .build();

                    new RegisterActivity.UserRegistrationCall().execute(this, "post", formBody);
                }
            }
        });

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowing) {
                    if (password.getText().toString().length() > 0) {
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        password.setSelection(password.getText().length());
                    }

                    if (confirmPassword.getText().toString().length() > 0) {
                        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        confirmPassword.setSelection(confirmPassword.getText().length());
                    }
                    isShowing = true;
                } else {
                    if (password.getText().toString().length() > 0) {
                        password.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setSelection(password.getText().length());
                    }

                    if (confirmPassword.getText().toString().length() > 0) {
                        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmPassword.setSelection(confirmPassword.getText().length());
                    }
                    isShowing = false;
                }
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.register_radio_male) {
                    isMale = true;
                } else if(checkedId == R.id.register_radio_female) {
                    isMale = false;
                }
            }
        });

        nationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nationalityText = nationalityList.get(position).getName();
                nationalityId = nationalityList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void afterTextChanged(Editable s) {

                // Remove spacing char
                if (s.length() > 0 && s.length() == 3) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && s.length() == 3) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        new RegisterActivity.NationalityCall().execute(this, "post", "");

//        ArrayList<AboutGroup> aboutGroupArrayList = databaseManager.getAbout();
//        if (aboutGroupArrayList.size() > 0) {
//            new RegisterActivity.NationalityCall().execute(this, "post", "");
//        } else {
//            new RegisterActivity.AboutCall().execute(this, "post", "");
//        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        if (month < 10) {
            dob.setText((dayOfMonth >= 10 ? dayOfMonth : ("0" + dayOfMonth)) + "-" + "0" + (month) + "-" + year);
        } else {
            dob.setText((dayOfMonth >= 10 ? dayOfMonth : ("0" + dayOfMonth)) + "-" + (month) + "-" + year);
        }
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
                    String facebookUrl = helper.getFacebookUrl(RegisterActivity.this, socailLink);
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
                        Toast.makeText(RegisterActivity.this, "No application can handle this request."
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
                        Toast.makeText(RegisterActivity.this, "No application can handle this request."
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
                        Toast.makeText(RegisterActivity.this, "No application can handle this request."
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
                        Toast.makeText(RegisterActivity.this, "No application can handle this request."
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
                        Toast.makeText(RegisterActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private class UserRegistrationCall extends AsyncTask<Object, Void, Object> {

        private String TAG = RegisterActivity.UserRegistrationCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RegisterActivity.this,
                    RegisterActivity.this.getResources().getString(
                            R.string.registering),
                    RegisterActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_REGISTER;
            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String errorMessage = jsonParser.parseRegistration(result.toString());
                if (errorMessage.equalsIgnoreCase("Updated")) {
                    if (!registrationMessage.equalsIgnoreCase("")) {
                        Toast.makeText(getBaseContext(), registrationMessage, Toast.LENGTH_LONG).show();
                    }
                    Log.e(TAG, "populate UI after response from service using OkHttp client");

                    if (loginMode.equalsIgnoreCase("FB") || loginMode.equalsIgnoreCase("GOOGLE")) {
                        editor.putBoolean(Constants.SESSION, true);
                        editor.apply();
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    helper.cancelableAlertDialog("", registrationMessage, 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class AboutCall extends AsyncTask<Object, Void, Object> {

        private String TAG = RegisterActivity.AboutCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ABOUT;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, false);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                String parseUpdate = jsonParser.parseAboutDetails(result.toString());
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    new RegisterActivity.NationalityCall().execute(this, "post", "");
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class NationalityCall extends AsyncTask<Object, Void, Object> {

        private String TAG = RegisterActivity.NationalityCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RegisterActivity.this,
                    RegisterActivity.this.getResources().getString(
                            R.string.fetching_data),
                    RegisterActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_NATIONALITY;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    nationalityList = new ArrayList<>();
                    nationalityList = databaseManager.getNationality();
                    SpinnerNationality locationAdapter = new SpinnerNationality(context, 0, nationalityList);
                    nationalitySpinner.setAdapter(locationAdapter);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}