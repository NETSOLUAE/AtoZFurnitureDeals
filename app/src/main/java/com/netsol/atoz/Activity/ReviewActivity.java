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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 12/26/17.
 */

public class ReviewActivity extends AppCompatActivity implements AlertAction {

//    double vatAmount = 0.00;
    String shipmentRate = "0";
    String finalTotal = "0";
    Button makePayment;
    Context context;
    TextView reviewAddress;
//    TextView service;
    TextView subtotal;
    TextView shipping;
    TextView total;
    TextView vat;
    TextView grandTotalReview;
    LinearLayout productLayout;
    LayerDrawable icon;
    private Helper helper;
    ProgressDialog progressDialog;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public ArrayList<CartItem> orderGroupProductArrayList;
    public static boolean is403ErrorReview = false;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    public static String fromActivity = "";
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Review Information");
        }

        context = ReviewActivity.this;
        helper = new Helper(this, this);
        progressDialog = new ProgressDialog(this);
        _webserviceManager = new WebserviceManager(this);
        databaseManager = new DatabaseManager(this);

        reviewAddress = (TextView) findViewById(R.id.review_address);
//        service = (TextView) findViewById(R.id.review_address_service);
        subtotal = (TextView) findViewById(R.id.review_sub_total);
        shipping = (TextView) findViewById(R.id.review_shipping);
        total = (TextView) findViewById(R.id.review_total);
        vat = (TextView) findViewById(R.id.review_vat);
        grandTotalReview = (TextView) findViewById(R.id.review_grand_total);
        makePayment = (Button) findViewById(R.id.review_make_payment);
        productLayout = (LinearLayout) findViewById(R.id.review_product_view);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReviewActivity.SendOrderDetails().execute(this, "post", "");
            }
        });
        SetCompletedOrderDetails();
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
                startActivity(new Intent(ReviewActivity.this, CartActivity.class));
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

    }

    @Override
    public void onCancelClicked() {

    }

    private void SetCompletedOrderDetails() {

        //Horizontal Layout Start
        int grandTotal = 0;

        orderGroupProductArrayList = new ArrayList<>();
        if (fromActivity.equalsIgnoreCase("cart")) {
            orderGroupProductArrayList = databaseManager.getCartItemWithoutSaveLater();
        } else {
            orderGroupProductArrayList = databaseManager.getCartItemFavorite();
        }
        if (orderGroupProductArrayList.size() > 0) {
            for(int j = 0; j < orderGroupProductArrayList.size(); j++)  {
                LayoutInflater mInflater1 = LayoutInflater.from(context);
                View order_group_product_view = mInflater1.inflate(R.layout.order_group_product_view, productLayout, false);
                ImageView productImage = (ImageView) order_group_product_view.findViewById(R.id.order_group_product_image);
                TextView productName = (TextView) order_group_product_view.findViewById(R.id.order_group_product_name);
                TextView productCode = (TextView) order_group_product_view.findViewById(R.id.order_group_product_code);
                TextView productColor = (TextView) order_group_product_view.findViewById(R.id.order_group_product_color);
                TextView productQuantity = (TextView) order_group_product_view.findViewById(R.id.order_group_quantity);
                TextView productSize = (TextView) order_group_product_view.findViewById(R.id.order_group_size);
                TextView productPrice = (TextView) order_group_product_view.findViewById(R.id.order_group_price);
                TextView productSubTotal = (TextView) order_group_product_view.findViewById(R.id.order_group_sub_total_price);
                View productDivider = (View) order_group_product_view.findViewById(R.id.order_group_product_divider);

                if (j == orderGroupProductArrayList.size()-1) {
                    productDivider.setVisibility(View.GONE);
                } else {
                    productDivider.setVisibility(View.VISIBLE);
                }
                Glide.with(context).load(orderGroupProductArrayList.get(j).getProductImage())
                        .fitCenter()
//                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(null)
                        .into(productImage);

                productName.setText(orderGroupProductArrayList.get(j).getName());
                productCode.setText(String.format("Item Code: %s", orderGroupProductArrayList.get(j).getProductCode()));
                productColor.setText(String.format("Color: %s", orderGroupProductArrayList.get(j).getColor()));
                String cartProductQuantity = databaseManager.getCartProductQuantity(orderGroupProductArrayList.get(j).getProductId(),
                        orderGroupProductArrayList.get(j).getColorId(),orderGroupProductArrayList.get(j).getSizeId());
                productQuantity.setText(String.format("Quantity: %s", cartProductQuantity));
                productSize.setText(String.format("Type/Size: %s", orderGroupProductArrayList.get(j).getSize()));

                String price =orderGroupProductArrayList.get(j).getPrice();
                double total = Double.parseDouble(price.replaceAll(",",""));
                DecimalFormat precision = new DecimalFormat("0.00");
                productPrice.setText(String.format(" %s", String.valueOf(precision.format(total))));
//                productPrice.setText(String.format(" %s", String.valueOf(price)));

                int subTotal = Integer.parseInt(cartProductQuantity)
                        * Integer.parseInt(orderGroupProductArrayList.get(j).getPrice().replaceAll(",",""));
                grandTotal = grandTotal + subTotal;

                String subPrice = String.valueOf(subTotal);
                double subtotal = Double.parseDouble(subPrice.replaceAll(",",""));
                DecimalFormat precision1 = new DecimalFormat("0.00");
                productSubTotal.setText(String.format(" %s", String.valueOf(precision1.format(subtotal))));
//                productSubTotal.setText(String.format(" %s", String.valueOf(subTotal)));

                productLayout.addView(order_group_product_view);
            }
            finalTotal = String.valueOf(grandTotal);

            String grandPrice = String.valueOf(grandTotal);
            double grandtotal = Double.parseDouble(grandPrice.replaceAll(",",""));
            DecimalFormat precision1 = new DecimalFormat("0.00");
            subtotal.setText(String.format("%s AED", String.valueOf(precision1.format(grandtotal))));
//            subtotal.setText(String.format("%s AED", String.valueOf(grandTotal)));
        }

        String addressId = sharedPref.getString(Constants.ADDRESS_ID,"");
        Address address = databaseManager.getAddressById(addressId);

        String addressLine = "";
        String area = "";
        if (!address.getFloor().equals("null") && !address.getFloor().equals("")) {
            addressLine = address.getFloor() + ", ";
        }
        if (!address.getApartment().equals("null") && !address.getApartment().equals("")){
            addressLine = addressLine + address.getApartment() + ", ";
        }
        if (!address.getBuilding().equals("null") && !address.getBuilding().equals("")){
            addressLine = addressLine + address.getBuilding() + ", ";
        }
        if (!address.getStreet().equals("null") && !address.getStreet().equals("")){
            addressLine = addressLine + address.getStreet() + ", ";
        }
        if (!address.getArea().equals("null") && !address.getArea().equals("")){
            area = address.getArea();
            addressLine = addressLine + area + ", ";
        }
        if (!address.getCity().equals("null") && !address.getCity().equals("")){
            addressLine = addressLine + address.getCity() + ", ";
        }
        if (!address.getCountry().equals("null") && !address.getCountry().equals("")){
            addressLine = addressLine + address.getCountry() + ". ";
        }
        if (!address.getLandmark().equals("null") && !address.getLandmark().equals("")){
            addressLine = addressLine + "\n" + String.format("Landmark: %s", address.getLandmark());
        }
        if (!address.getMobile().equals("null") && !address.getMobile().equals("")){
            addressLine = addressLine + "\n" + String.format("Mobile: %s", address.getMobile());
        }
//        if (!address.getPhone().equals("null") && !address.getPhone().equals("")){
//            addressLine = addressLine + "\n" + String.format("Phone: %s", address.getPhone());
//        }
        if (!address.getDelivery().equals("null") && !address.getDelivery().equals("")){
            addressLine = addressLine + "\n" + String.format("Preferable Time: %s", address.getDelivery());
        }
        reviewAddress.setText(addressLine);

        if (!area.equals("null") && !area.equals("")){
            shipmentRate = databaseManager.getShipmentRate(area);
        }

        double shippingrate = Double.parseDouble(shipmentRate.replaceAll(",",""));
        DecimalFormat precision1 = new DecimalFormat("0.00");

//        service.setText("Product shipping and handling charges by courier " + String.valueOf(precision1.format(shippingrate)) + " AED");
        shipping.setText(String.format("%s AED", String.valueOf(precision1.format(shippingrate))));

        String totalBeforeTax = String.valueOf(Integer.parseInt(finalTotal) + Integer.parseInt(shipmentRate));
        double totalbeforetax = Double.parseDouble(totalBeforeTax.replaceAll(",",""));
        DecimalFormat precision2 = new DecimalFormat("0.00");
        total.setText(String.format("%s AED", String.valueOf(precision2.format(totalbeforetax))));

//        vatAmount = (Double.parseDouble(totalBeforeTax) * 5) / 100;
//        DecimalFormat df = new DecimalFormat("#.##");
//        vatAmount = Double.valueOf(df.format(vatAmount));
//        vat.setText(String.format("%s AED", String.valueOf(vatAmount)));
        vat.setText("N/A");

//        String grandFinalTotal = String.valueOf(Integer.parseInt(totalBeforeTax) + vatAmount);
        String grandFinalTotal = String.valueOf(Integer.parseInt(totalBeforeTax));
        double grandfinaltotal = Double.parseDouble(grandFinalTotal.replaceAll(",",""));
        DecimalFormat precision3 = new DecimalFormat("0.00");
        grandTotalReview.setText(String.format("%s AED", String.valueOf(precision3.format(grandfinaltotal))));

        editor.putString(Constants.PRODUCT_TOTAL,finalTotal);
//        editor.putString(Constants.PRODUCT_VAT,String.valueOf(vatAmount));
        editor.putString(Constants.PRODUCT_SHIPPING,shipmentRate);
        editor.putString(Constants.TOTAL_AMOUNT,grandFinalTotal);
        editor.apply();
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
                    String facebookUrl = helper.getFacebookUrl(ReviewActivity.this, socailLink);
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
                        Toast.makeText(ReviewActivity.this, "No application can handle this request."
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
                        Toast.makeText(ReviewActivity.this, "No application can handle this request."
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
                        Toast.makeText(ReviewActivity.this, "No application can handle this request."
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
                        Toast.makeText(ReviewActivity.this, "No application can handle this request."
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
                        Toast.makeText(ReviewActivity.this, "No application can handle this request."
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
                Intent intent = new Intent(ReviewActivity.this, HomeActivity.class);
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
                    Toast.makeText(ReviewActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ReviewActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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

    private class SendOrderDetails extends AsyncTask<Object, Void, Object> {

        private String TAG = ReviewActivity.SendOrderDetails.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ReviewActivity.this,
                    ReviewActivity.this.getResources().getString(
                            R.string.placing_order),
                    ReviewActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String addressId = sharedPref.getString(Constants.ADDRESS_ID, "");
            String userId = sharedPref.getString(Constants.USER_ID, "");
//            String json = makeOrderDetailsJson(addressId,String.valueOf(vatAmount),shipmentRate,finalTotal,"Pending","Pending",userId,"28-12-2017",orderGroupProductArrayList);
            String json = makeOrderDetailsJson(addressId,"0",shipmentRate,finalTotal,"Pending","Pending",userId,"28-12-2017",orderGroupProductArrayList);
            String urlString = Constants.BASE_URL + Constants.END_POINT_ORDER_SAVE;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postJson(urlString, json);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (!is403ErrorReview) {
                    for (int i = 0; i < orderGroupProductArrayList.size(); i++) {
                        databaseManager.updateDeletedAPI(orderGroupProductArrayList.get(i).getProductId(),
                                orderGroupProductArrayList.get(i).getColorId(), orderGroupProductArrayList.get(i).getSizeId(), "false");
                    }
                    helper.dismissProgressDialog(progressDialog);
                    Intent intent = new Intent(ReviewActivity.this, CreditCardActivity.class);
                    startActivity(intent);
//                    helper.cancelableAlertDialog("", result.toString(), 1);
                } else {
                    helper.dismissProgressDialog(progressDialog);
                    helper.cancelableAlertDialog("", result.toString(), 1);
                }
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    private String makeOrderDetailsJson(String addressID, String vat, String shipping, String total, String status, String paymentStatus, String userId,
                                        String date, ArrayList<CartItem> cartItemArrayList) {

        String orderID = sharedPref.getString(Constants.ORDER_PLACED_ID,"");
//        String rowID = sharedPref.getString(Constants.ORDER_ROW_ID,"");
        JSONObject authenticationObject = new JSONObject();
        JSONObject orderGroupOuter = new JSONObject();
        JSONArray orderGroupInner = new JSONArray();
        try {
            orderGroupOuter.put("address_id", addressID);
//            orderGroupOuter.put("ord_vat", vat);
            orderGroupOuter.put("ord_vat", "0");
            orderGroupOuter.put("ord_shipping", shipping);
            orderGroupOuter.put("ord_total", total);
            orderGroupOuter.put("ord_status", status);
            orderGroupOuter.put("ord_payment_status", paymentStatus);
            orderGroupOuter.put("ord_user_id", userId);
            orderGroupOuter.put("ord_date", date);
            if (!orderID.equalsIgnoreCase("")) {
                orderGroupOuter.put("ord_id", orderID);
            }
//            if (!rowID.equalsIgnoreCase("")) {
//                orderGroupOuter.put("row_id", rowID);
//            }

            for (int i = 0; i < cartItemArrayList.size(); i++) {
                JSONObject orderChild = new JSONObject();
                orderChild.put("line_prod_id", cartItemArrayList.get(i).getProductId());
                orderChild.put("line_name", cartItemArrayList.get(i).getName());
                orderChild.put("line_color", cartItemArrayList.get(i).getColor());
                orderChild.put("line_size", cartItemArrayList.get(i).getSize());
                String qty = cartItemArrayList.get(i).getQuantity();
                orderChild.put("line_qty", qty);
                String sellingPrice = cartItemArrayList.get(i).getPrice().replace(",","");
//                String totalPrice = String.valueOf(Integer.parseInt(sellingPrice) * Integer.parseInt(qty));
                orderChild.put("line_price", sellingPrice);
                orderChild.put("line_user_id", userId);
                orderChild.put("line_dated", date);
                orderChild.put("line_status", "Placed");
                orderChild.put("row_id", cartItemArrayList.get(i).getMd5());
                orderGroupInner.put(orderChild);
            }
            orderGroupOuter.put("order_lines",orderGroupInner);
            authenticationObject.put("order",orderGroupOuter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authenticationObject.toString();
    }

}