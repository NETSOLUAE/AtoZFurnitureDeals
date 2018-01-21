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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Adapter.SpinnerAreaAdapter;
import com.netsol.atoz.Adapter.SpinnerCityAdapter;
import com.netsol.atoz.Adapter.SpinnerCountryAdapter;
import com.netsol.atoz.Adapter.SpinnerDeliveryAdapter;
import com.netsol.atoz.Adapter.SpinnerLocationAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.Model.Area;
import com.netsol.atoz.Model.City;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;
import com.netsol.atoz.Util.TextDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 12/14/17.
 */

public class AddressActivity extends AppCompatActivity implements AlertAction {

    Button save;
    String parseUpdate = "";
    String selectedSpinnerArea = "";
    String heading;
    String activity;
    String addressId;
    String city;
    String cityId;
    String area;
    String areaId;
    String deliveryTime;
    String location;
    Context context;
    Address address;
    Spinner citySpinner;
    Spinner deliveryTimeSpinner;
    Spinner locationSpinner;
    Spinner areaSpinner;
    EditText name;
    EditText floor;
    EditText apartment;
    EditText building;
    EditText streetNo;
    EditText landmark;
    EditText phone;
    EditText mobile;
    EditText note;
    JsonParser jsonParser;
    LayerDrawable icon;
    private Helper helper;
    ProgressDialog progressDialog;
    ArrayList<City> cityList;
    ArrayList<Area> areaList;
    ArrayList<String> locationList;
    ArrayList<String> deliveryList;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    SpinnerCityAdapter cityAdapter;
    SpinnerAreaAdapter areaAdapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public static boolean is403ErrorAdd = false;
    public static String addressIdNewSelect = "";

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        context = AddressActivity.this;
        helper = new Helper(this, this);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            heading = b.getString("HEADING");
            activity = b.getString("ACTIVITY");
            if (heading != null && heading.equalsIgnoreCase("Edit Location"))
                addressId = b.getString("ADDRESS_ID");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(heading);
        }

        databaseManager = new DatabaseManager(this);
        progressDialog = new ProgressDialog(this);
        _webserviceManager = new WebserviceManager(this);
        jsonParser = new JsonParser(this);

        citySpinner = (Spinner) findViewById(R.id.edit_address_city);
        deliveryTimeSpinner = (Spinner) findViewById(R.id.edit_address_delivery);
        locationSpinner = (Spinner) findViewById(R.id.edit_address_location);
        areaSpinner = (Spinner) findViewById(R.id.edit_address_area);
        name = (EditText) findViewById(R.id.edit_address_name);
        floor = (EditText) findViewById(R.id.edit_address_floor);
        apartment = (EditText) findViewById(R.id.edit_address_apartment);
        building = (EditText) findViewById(R.id.edit_address_building);
        streetNo = (EditText) findViewById(R.id.edit_address_street_no);
        landmark = (EditText) findViewById(R.id.edit_address_landmark);
        phone = (EditText) findViewById(R.id.edit_address_phone);
        mobile = (EditText) findViewById(R.id.edit_address_mobile);
        note = (EditText) findViewById(R.id.edit_address_note);
        save = (Button) findViewById(R.id.button_save_address);

        if (activity.equalsIgnoreCase("true")) {
            save.setText("Save and Continue");
        } else {
            save.setText("Save");
        }
        address = new Address();

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        mobile.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(Constants.COUNTRY_CODE, AddressActivity.this), null, null, null);

        cityList = new ArrayList<>();
        cityList = databaseManager.getCity();
        if (cityList.size() == 0) {
            new AddressActivity.CityCall().execute(this, "post", "");
        } else {
            cityAdapter = new SpinnerCityAdapter(context, 0, cityList);
            citySpinner.setAdapter(cityAdapter);
        }

        locationList = new ArrayList<>();
        Collections.addAll(locationList, context.getResources().getStringArray(R.array.address_location));
        SpinnerLocationAdapter locationAdapter = new SpinnerLocationAdapter(context, 0, locationList);
        locationSpinner.setAdapter(locationAdapter);

        deliveryList = new ArrayList<>();
        Collections.addAll(deliveryList, context.getResources().getStringArray(R.array.address_delivery_time));
        SpinnerDeliveryAdapter deliveryAdapter = new SpinnerDeliveryAdapter(context, 0, deliveryList);
        deliveryTimeSpinner.setAdapter(deliveryAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = cityList.get(position).getName();
                if (position > 0) {
                    cityId = cityList.get(position).getId();
                    setArea(cityId);
                } else {
                    setArea("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = areaList.get(position).getName();
                if (position > 0) {
                    areaId = areaList.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = locationList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        deliveryTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deliveryTime = deliveryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (heading != null && heading.equalsIgnoreCase("Edit Location")) {
            address = databaseManager.getAddressById(addressId);
            setAddress();
        } else {
            setArea("");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameText = name.getText().toString();
                String floorText = floor.getText().toString();
                String aparmentText = apartment.getText().toString();
                String buildingText = building.getText().toString();
                String streetText = streetNo.getText().toString();
                String landmarkText = landmark.getText().toString();
                String noteText = note.getText().toString();
                String phoneText = "";
                if (phone.getText().toString().equalsIgnoreCase("")) {
                    phoneText = "";
                } else {
                    phoneText = phone.getText().toString();
                }
                String mobileText = Constants.COUNTRY_CODE + mobile.getText().toString();
                String id = "";
                if (heading.equalsIgnoreCase("Edit Location")) {
                    id = addressId;
                }

                if (nameText.equalsIgnoreCase("") || floorText.equalsIgnoreCase("") || aparmentText.equalsIgnoreCase("") || buildingText.equalsIgnoreCase("")
                        || streetText.equalsIgnoreCase("") || area.equalsIgnoreCase("Select Area") || landmarkText.equalsIgnoreCase("")
                        || mobileText.equalsIgnoreCase("") || city.equalsIgnoreCase("Select City") || deliveryTime.equalsIgnoreCase("Set Delivery Time")
                        || location.equalsIgnoreCase("Set Location") || noteText.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.all_fields_mandatory), Toast.LENGTH_SHORT).show();
                } else if(mobileText.length() < 13){
                    Toast.makeText(context, context.getString(R.string.invalid_mobile), Toast.LENGTH_LONG).show();
                } else if(phoneText.length() > 20 && !phoneText.equalsIgnoreCase("")){
                    Toast.makeText(context, context.getString(R.string.invalid_mobile), Toast.LENGTH_LONG).show();
                } else {
                    String userId = sharedPref.getString(Constants.USER_ID, "");
                    RequestBody formBody = new FormBody.Builder()
                            .add("add_id", id)
                            .add("add_fullname", nameText)
                            .add("add_street", streetText)
                            .add("add_building", buildingText)
                            .add("add_apartment", aparmentText)
                            .add("add_floor", floorText)
                            .add("add_landmark", landmarkText)
                            .add("add_phone", phoneText)
                            .add("add_mobile", mobileText)
                            .add("add_area", areaId)
                            .add("add_location_type", location)
                            .add("add_delivery_time", deliveryTime)
                            .add("add_shipping_note", noteText)
                            .add("add_city", cityId)
                            .add("add_country", "225")
                            .add("user_id", userId)
                            .build();

                    new AddressActivity.SaveAddressCall().execute(this, "post", formBody);
                }
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

        setFooter();
        setFloatingMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cart:
                startActivity(new Intent(AddressActivity.this, CartActivity.class));
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
        if (!is403ErrorAdd) {
            if (activity.equalsIgnoreCase("true")) {
                editor.putString(Constants.ADDRESS_ID, addressIdNewSelect);
                editor.apply();
                Intent intent = new Intent(AddressActivity.this, ReviewActivity.class);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onCancelClicked() {

    }

    public void setAddress() {

        if (!address.getArea().equals("null") || !address.getArea().equals("")) {
            selectedSpinnerArea = address.getArea();
        }

        if (!address.getCity().equals("null") || !address.getCity().equals("")) {
            setSelectedSpinnerValue(address.getCity());
        }

        if (address.getFullname().equals("null") || address.getFullname().equals("")) {
            name.setText("");
        } else {
            name.setText(address.getFullname());
        }
        if (address.getFloor().equals("null") || address.getFloor().equals("")) {
            floor.setText("");
        } else {
            floor.setText(address.getFloor());
        }
        if (address.getApartment().equals("null") || address.getApartment().equals("")) {
            apartment.setText("");
        } else {
            apartment.setText(address.getApartment());
        }
        if (address.getBuilding().equals("null") || address.getBuilding().equals("")) {
            building.setText("");
        } else {
            building.setText(address.getBuilding());
        }
        if (address.getStreet().equals("null") || address.getStreet().equals("")) {
            streetNo.setText("");
        } else {
            streetNo.setText(address.getStreet());
        }

        if (address.getLandmark().equals("null") || address.getLandmark().equals("")) {
            landmark.setText("");
        } else {
            landmark.setText(address.getLandmark());
        }

        if (address.getMobile().equals("null") || address.getMobile().equals("")) {
            mobile.setText("");
        } else {
            String localNumber = address.getMobile();
            if (localNumber.contains("+971"))
                localNumber = localNumber.replace("+971","");
            else if (localNumber.contains("971"))
                localNumber = localNumber.replace("971","");
//            localNumber = localNumber.replaceAll(" ", "");
            mobile.setText(localNumber);
        }

        if (address.getPhone().equals("null") || address.getPhone().equals("") || address.getPhone().equals("0")) {
            phone.setText("");
        } else {
            phone.setText(address.getPhone());
        }

        if (!address.getDelivery().equals("null") || !address.getDelivery().equals("")) {
            int index = deliveryList.indexOf(address.getDelivery());
            deliveryTimeSpinner.setSelection(index);
        }

        if (address.getNote().equals("null") || address.getNote().equals("")) {
            note.setText("");
        } else {
            note.setText(address.getNote());
        }

        if (!address.getLocation().equals("null") || !address.getLocation().equals("")) {
            int index = locationList.indexOf(address.getLocation());
            locationSpinner.setSelection(index);
        }
    }

    public void setArea(String cityID) {
        areaList = new ArrayList<>();
        Area area1 = new Area();
        area1.setId("0");
        area1.setName("Select Area");
        area1.setShipRate("");
        area1.setCityId("");
        areaList.add(area1);
        if (!cityID.equalsIgnoreCase("")) {
            areaList.addAll(databaseManager.getArea(cityID));
        }
        areaAdapter = new SpinnerAreaAdapter(context, 0, areaList);
        areaSpinner.setAdapter(areaAdapter);
        if (!selectedSpinnerArea.equalsIgnoreCase("")) {
            setSelectedSpinnerValueArea(selectedSpinnerArea);
            selectedSpinnerArea = "";
        }
    }

    public void setSelectedSpinnerValue (String value) {
        for (int i = 0; i < cityList.size(); i++) {
            if (value.equalsIgnoreCase(cityList.get(i).getName())) {
                cityId = cityList.get(i).getId();
                citySpinner.setSelection(i);
            }
        }
    }

    public void setSelectedSpinnerValueArea (String value) {
        for (int i = 0; i < areaList.size(); i++) {
            if (value.equalsIgnoreCase(areaList.get(i).getName())) {
                areaId = areaList.get(i).getId();
                areaSpinner.setSelection(i);
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
                    String facebookUrl = helper.getFacebookUrl(AddressActivity.this, socailLink);
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
                        Toast.makeText(AddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(AddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(AddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(AddressActivity.this, "No application can handle this request."
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
                        Toast.makeText(AddressActivity.this, "No application can handle this request."
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
                Intent intent = new Intent(AddressActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    Toast.makeText(AddressActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(AddressActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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

    private class SaveAddressCall extends AsyncTask<Object, Void, Object> {

        private String TAG = AddressActivity.SaveAddressCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AddressActivity.this,
                    AddressActivity.this.getResources().getString(
                            R.string.saving_add),
                    AddressActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ADDRESS_SAVE;
            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
//            http://atozfurniture.ae/apisv1/azapi.php?mode=address&action=save&add_id=3&add_fullname=SaranyaMarappan&add_street=12&add_building=AlAttarTower&add_apartment=1501&add_floor=15&add_landmark=NearToMetro&add_phone=971586329944&add_mobile=971586983399&add_area=290&add_location_type=work&add_delivery_time=10.00A.Mto02.00P.M&add_shipping_note=Quick&add_city=3&add_country=225&user_id=4

            if (result != null) {
                parseUpdate = jsonParser.parseSaveAddress(result.toString());
                if (!is403ErrorAdd) {
                    String userId = sharedPref.getString(Constants.USER_ID, "");
                    new AddressActivity.AddressCall().execute(userId, "post", "");
                } else {
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", parseUpdate, 1);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private class AddressCall extends AsyncTask<String, Void, Object> {

        private String TAG = AddressActivity.AddressCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(String... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ADDRESS + params[0];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, false);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                String parseUpdate1 = jsonParser.parseAddressResponse(result.toString());
                if (parseUpdate1.equalsIgnoreCase("Updated")) {
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", parseUpdate, 1);
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

    private class CityCall extends AsyncTask<Object, Void, Object> {

        private String TAG = AddressActivity.CityCall.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddressActivity.this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.fetching_data));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_CITY;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, false);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                String parseUpdate = jsonParser.parseCityDetails(result.toString());
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    new AddressActivity.AreaCall().execute(this, "post", "");
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

    private class AreaCall extends AsyncTask<Object, Void, Object> {

        private String TAG = AddressActivity.AreaCall.class.getSimpleName();

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

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    cityList = new ArrayList<>();
                    cityList = databaseManager.getCity();
                    cityAdapter = new SpinnerCityAdapter(context, 0, cityList);
                    citySpinner.setAdapter(cityAdapter);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}
