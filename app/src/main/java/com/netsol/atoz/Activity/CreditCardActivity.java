package com.netsol.atoz.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Adapter.SpinnerDeliveryAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.CCUtils;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 11/8/17.
 */

public class CreditCardActivity extends AppCompatActivity implements AlertAction {

    String expMonth;
    String expYear;
    String totalAmountValue;
    Context context;
    Spinner expiryMonth;
    Spinner expiryYear;
    TextView totalAmount;
    EditText cardNo;
    EditText cvv;
    EditText name;
    CheckBox saveCard;
    ImageView cardIcon;
    Button paySecurely;
    LayerDrawable icon;
    JsonParser jsonParser;
    private Helper helper;
    boolean isSlash = false;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    WebserviceManager _webserviceManager;
    DatabaseManager databaseManager;
    ArrayList<String> monthList;
    ArrayList<String> yearList;
    public static String fromActivity = "";
    public static boolean is403ErrorCard = false;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Credit/Debit Card");
        }

        context = CreditCardActivity.this;
        helper = new Helper(this, this);
        databaseManager = new DatabaseManager(this);
        progressDialog = new ProgressDialog(this);
        _webserviceManager = new WebserviceManager(this);
        jsonParser = new JsonParser(this);

        paySecurely = (Button) findViewById(R.id.button_pay_securely);
        cardNo = (EditText) findViewById(R.id.card_no);
        expiryMonth = (Spinner) findViewById(R.id.card_expiry_month);
        expiryYear = (Spinner) findViewById(R.id.card_expiry_year);
        cvv = (EditText) findViewById(R.id.card_cvv);
        name = (EditText) findViewById(R.id.card_name);
        saveCard = (CheckBox) findViewById(R.id.check_box_save_card);
        totalAmount = (TextView) findViewById(R.id.total_amount);
        cardIcon = (ImageView) findViewById(R.id.card_icon);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        totalAmountValue = sharedPref.getString(Constants.TOTAL_AMOUNT, "");
        double total = Double.parseDouble(totalAmountValue.replaceAll(",",""));
        DecimalFormat precision = new DecimalFormat("0.00");
        totalAmount.setText(String.valueOf(precision.format(total)));

        setFooter();
        setFloatingMenu();

        cardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void afterTextChanged(Editable s) {

                // Remove spacing char
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }

                if (s.length() > 5) {
                    String cardnumber = cardNo.getText().toString().replaceAll(" ","");
                    int cardId = CCUtils.getCardID(cardnumber);
                    if (cardId == 0) {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.visa));
                    } else if (cardId == 1) {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.card_icon));
                    } else if (cardId == 2) {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.american_express));
                    } else if (cardId == 3) {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.credit_card_default));
                    } else if (cardId == 4) {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.credit_card_default));
                    } else if (cardId == 5) {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.discover));
                    } else {
                        cardIcon.setBackground(context.getResources().getDrawable(R.drawable.credit_card_default));
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

        monthList = new ArrayList<>();
        Collections.addAll(monthList, context.getResources().getStringArray(R.array.credit_month));
        SpinnerDeliveryAdapter deliveryAdapter = new SpinnerDeliveryAdapter(context, 0, monthList);
        expiryMonth.setAdapter(deliveryAdapter);

        yearList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        yearList.add("-YYYY-");
        yearList.add(String.valueOf(year));
        for (int i = 0; i < 20; i++) {
            year = year + 1;
            yearList.add(String.valueOf(year));
        }
        SpinnerDeliveryAdapter deliveryAdapter1 = new SpinnerDeliveryAdapter(context, 0, yearList);
        expiryYear.setAdapter(deliveryAdapter1);

        expiryMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expMonth = monthList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        expiryYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expYear = yearList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paySecurely.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNoText = cardNo.getText().toString();
                String cvvText = cvv.getText().toString();
                String nameText = name.getText().toString();

                if (cardNoText.equalsIgnoreCase("") || expMonth.equalsIgnoreCase("-MM-") || expYear.equalsIgnoreCase("-YYYY-")
                        || cvvText.equalsIgnoreCase("") || nameText.equalsIgnoreCase("") ) {
                    Toast.makeText(CreditCardActivity.this, "All fields are mandatory",
                            Toast.LENGTH_SHORT).show();
                } else if (cvv.length() < 3) {
                    Toast.makeText(CreditCardActivity.this, "Please Enter Valid CVV Number",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String save = "";
                    if (saveCard.isChecked()) {
                        save = "Yes";
                    } else {
                        save = "No";
                    }
                    String orderId = sharedPref.getString(Constants.ORDER_PLACED_ID, "");
                    String year = expYear.substring(2,4);
                    String month = expMonth.substring(0,2);
                    RequestBody formBody = new FormBody.Builder()
                            .add("order_id", orderId)
                            .add("amount", totalAmountValue)
                            .add("card_holder", nameText)
                            .add("card_number", cardNoText)
                            .add("card_exp_month", month)
                            .add("card_exp_year", year)
                            .add("card_cvc", cvvText)
                            .add("save_creditcard", save)
                            .build();

                    new CreditCardActivity.PaymentCall().execute(this, "post", formBody);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                // finish the activity
                onBackPressed();
                break;
            case R.id.cart:
                startActivity(new Intent(CreditCardActivity.this, CartActivity.class));
//                Toast.makeText(getApplicationContext(), "No Items added!", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem cart = menu.findItem(R.id.cart);
        MenuItem favorite = menu.findItem(R.id.favorite);
        MenuItem search = menu.findItem(R.id.action_search);
        favorite.setVisible(false);
        search.setVisible(false);

        cart.setIcon(context.getResources().getDrawable(R.drawable.menu_cart_white));

        icon = (LayerDrawable) cart.getIcon();
        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        helper.setBadgeCount(this, icon, badgeCount);
        return true;
    }

    @Override
    public void onResume() {
        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        if (icon != null)
            helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
        super.onResume();
    }

    @Override
    public void onOkClicked() {
        if (!is403ErrorCard) {
            if (fromActivity.equalsIgnoreCase("Cart")) {
                databaseManager.clearCartDetails();
            } else if (fromActivity.equalsIgnoreCase("Wishlist")) {
                databaseManager.clearWhishlistDetails();
            }
            int badgeCount = databaseManager.getBadgeCount();
            editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
            editor.apply();
            if (icon != null)
                helper.setBadgeCount(CreditCardActivity.this, icon, String.valueOf(badgeCount));
            Intent intent = new Intent(CreditCardActivity.this, CongragActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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
                    String facebookUrl = helper.getFacebookUrl(CreditCardActivity.this, socailLink);
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
                        Toast.makeText(CreditCardActivity.this, "No application can handle this request."
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
                        Toast.makeText(CreditCardActivity.this, "No application can handle this request."
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
                        Toast.makeText(CreditCardActivity.this, "No application can handle this request."
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
                        Toast.makeText(CreditCardActivity.this, "No application can handle this request."
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
                        Toast.makeText(CreditCardActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public void setFloatingMenu() {

        transparentLayout = (LinearLayout) findViewById(R.id.transparent_layout);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionCall = (FloatingActionButton) findViewById(R.id.floating_call);
        floatingActionChat = (FloatingActionButton) findViewById(R.id.floating_chat);
        floatingActionFaq = (FloatingActionButton) findViewById(R.id.floating_faq);
        // slide-up animation
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        materialDesignFAM.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    materialDesignFAM.setMenuButtonColorNormal(context.getResources().getColor(R.color.cart));
                    materialDesignFAM.setMenuButtonColorPressed(context.getResources().getColor(R.color.cart));
                    transparentLayout.setVisibility(View.VISIBLE);
                    transparentLayout.startAnimation(fadeIn);
                } else {
                    transparentLayout.startAnimation(fadeOut);
                    materialDesignFAM.setMenuButtonColorNormal(context.getResources().getColor(R.color.colorPrimary));
                    materialDesignFAM.setMenuButtonColorPressed(context.getResources().getColor(R.color.colorPrimary));
                    transparentLayout.setVisibility(View.GONE);

                }
            }
        });

        transparentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transparentLayout.getVisibility() == View.VISIBLE) {
                    materialDesignFAM.close(true);
                }
            }
        });

        floatingActionCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                materialDesignFAM.close(true);
                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    if (!checkCallPhonePermission()) {
                        requestPermission();
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+971509941516"));
                        startActivity(callIntent);
                    }
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+971509941516"));
                    startActivity(callIntent);
                }
            }
        });
        floatingActionChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked

            }
        });
        floatingActionFaq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                materialDesignFAM.close(true);
                HomeActivity.congragTrack = 8;
                Intent intent = new Intent(CreditCardActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission) {
                    Toast.makeText(CreditCardActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(CreditCardActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+971509941516"));
                    startActivity(intent);
                }
                break;

            default:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:+971509941516"));
                startActivity(intent);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private boolean checkCallPhonePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private class PaymentCall extends AsyncTask<Object, Void, Object> {

        private String TAG = CreditCardActivity.PaymentCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CreditCardActivity.this,
                    CreditCardActivity.this.getResources().getString(
                            R.string.placing_order),
                    CreditCardActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL_PAYMENT;
            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                String parse = jsonParser.parsePayment(result.toString());
                if (parse.equalsIgnoreCase("Updated")) {
                    is403ErrorCard = false;
                    helper.dismissProgressDialog(progressDialog);
                    helper.nonCancelableAlertDialog("", "Congratulation! Your order has been successfully placed.", 1);
                } else {
                    is403ErrorCard = true;
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                is403ErrorCard = true;
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }
}
