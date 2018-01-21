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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Adapter.AddressAdapter;
import com.netsol.atoz.Adapter.SelectAddressAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 12/23/17.
 */

public class SelectAddressActivity  extends AppCompatActivity implements AlertAction {

    Helper helper;
    Context context;
    ListView selectAddressList;
    ImageView addAddress;
    LayerDrawable icon;
    ProgressDialog progressDialog;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ArrayList<Address> selectAddressArrayList;
    SelectAddressAdapter selectAddressAdapter;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Delivery Address");
        }

        context = SelectAddressActivity.this;
        helper = new Helper(this, this);
        databaseManager = new DatabaseManager(this);
        progressDialog = new ProgressDialog(this);
        _webserviceManager = new WebserviceManager(this);

        addAddress = (ImageView) findViewById(R.id.button_add_location_select);
        selectAddressList = (ListView) findViewById(R.id.select_address_list);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        setFooter();
        setFloatingMenu();

        selectAddressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String addressId = selectAddressArrayList.get(position).getId();
                editor.putString(Constants.ADDRESS_ID, addressId);
                editor.apply();
                Intent intent = new Intent(SelectAddressActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectAddressActivity.this, AddressActivity.class);
                intent.putExtra("HEADING", "Add New Location");
                intent.putExtra("ACTIVITY", "true");
                startActivity(intent);
            }
        });

        String userId = sharedPref.getString(Constants.USER_ID, "");
        new SelectAddressActivity.AddressCall().execute(userId, "post", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cart:
                startActivity(new Intent(SelectAddressActivity.this, CartActivity.class));
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

        if (!progressDialog.isShowing()) {
            selectAddressArrayList = new ArrayList<>();
            selectAddressArrayList = databaseManager.getEnabledAddress();
            selectAddressAdapter = new SelectAddressAdapter(SelectAddressActivity.this, selectAddressArrayList);
            selectAddressList.setAdapter(selectAddressAdapter);
        }
        super.onResume();
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
                    String facebookUrl = helper.getFacebookUrl(SelectAddressActivity.this, socailLink);
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
                        Toast.makeText(SelectAddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(SelectAddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(SelectAddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(SelectAddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(SelectAddressActivity.this, "No application can handle this request."
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
                Intent intent = new Intent(SelectAddressActivity.this, HomeActivity.class);
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
                    Toast.makeText(SelectAddressActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(SelectAddressActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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

    private class AddressCall extends AsyncTask<String, Void, Object> {

        private String TAG = SelectAddressActivity.AddressCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SelectAddressActivity.this,
                    SelectAddressActivity.this.getResources().getString(
                            R.string.getting_add),
                    SelectAddressActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(String... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ADDRESS + params[0];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    new SelectAddressActivity.AreaCall().execute(this, "post", "");
                } else if (result.toString().equalsIgnoreCase("NoData")) {
                    helper.dismissProgressDialog(progressDialog);
                } else {
                    helper.dismissProgressDialog(progressDialog);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class AreaCall extends AsyncTask<Object, Void, Object> {

        private String TAG = SelectAddressActivity.AreaCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_AREA;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    helper.dismissProgressDialog(progressDialog);
                    selectAddressArrayList = new ArrayList<>();
                    selectAddressArrayList = databaseManager.getEnabledAddress();
                    selectAddressAdapter = new SelectAddressAdapter(SelectAddressActivity.this, selectAddressArrayList);
                    selectAddressList.setAdapter(selectAddressAdapter);
                } else {
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}
