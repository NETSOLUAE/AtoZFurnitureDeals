package com.netsol.atoz.Activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
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
import com.netsol.atoz.Model.OrderGroup;
import com.netsol.atoz.Model.OrderProduct;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 12/20/17.
 */

public class OrderDetailActivity extends AppCompatActivity implements AlertAction {

    String orderId;
    Context context;
    TextView orderNo;
    TextView orderDate;
    TextView orderAmountTop;
    TextView productHeading;
    TextView paymentStatus;
    TextView orderEstDelivery;
    TextView orderTotal;
    TextView orderShipping;
    TextView orderTotalBeforeVat;
    TextView orderVat;
    TextView orderGrandTotal;
    TextView address;
    LinearLayout productLayout;
    LayerDrawable icon;
    private Helper helper;
    DatabaseManager databaseManager;
    SharedPreferences sharedPref;
    public OrderGroup orderGroup;
    public ArrayList<OrderProduct> orderGroupProductArrayList;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Detail");
        }

        context = OrderDetailActivity.this;
        databaseManager = new DatabaseManager(this);
        helper = new Helper(this, this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            orderId = b.getString("ORDER_ID");
        }

        orderNo = (TextView) findViewById(R.id.order_detail_no);
        orderDate = (TextView) findViewById(R.id.order_detail_date);
        orderAmountTop = (TextView) findViewById(R.id.order_detail_price);
        productHeading = (TextView) findViewById(R.id.order_detail_product_items);
        paymentStatus = (TextView) findViewById(R.id.order_detail_payment_status);
        orderEstDelivery = (TextView) findViewById(R.id.order_detail_est_delivery);
        orderTotal = (TextView) findViewById(R.id.order_detail_order_total);
        orderShipping = (TextView) findViewById(R.id.order_detail_shipping_fee);
        orderTotalBeforeVat = (TextView) findViewById(R.id.order_detail_total_before_vat);
        orderVat = (TextView) findViewById(R.id.order_detail_vat);
        orderGrandTotal = (TextView) findViewById(R.id.order_detail_total);
        address = (TextView) findViewById(R.id.order_details_address);
        productLayout = (LinearLayout) findViewById(R.id.order_detail_product_view);
        orderEstDelivery.setVisibility(View.INVISIBLE);

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

        SetStandardOrderDetails();
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
                startActivity(new Intent(OrderDetailActivity.this, CartActivity.class));
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

    private void SetStandardOrderDetails() {

        orderGroup = databaseManager.getOrderGroup(orderId);
        orderGroupProductArrayList = new ArrayList<>();
        orderGroupProductArrayList = databaseManager.getOrderProducts(orderId);

        orderNo.setText(orderGroup.getNo());
        orderDate.setText(orderGroup.getDate());
        productHeading.setText("You have " + String.valueOf(orderGroupProductArrayList.size()) + " item in your Order.");
        paymentStatus.setText(orderGroup.getPaymentStatus());

        DecimalFormat precision1 = new DecimalFormat("0.00");
        //Horizontal Layout Start
        if (productLayout.getChildCount() > 0)
            productLayout.removeAllViews();

        double subTotalFinal = 0;
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

                Glide.with(context).load(orderGroupProductArrayList.get(j).getImage())
                        .fitCenter()
//                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(null)
                        .into(productImage);

                productName.setText(orderGroupProductArrayList.get(j).getName());
                productCode.setText(String.format("Item Code: %s", orderGroupProductArrayList.get(j).getProductCode()));
                productColor.setText(String.format("Color: %s", orderGroupProductArrayList.get(j).getColor()));
                productQuantity.setText(String.format("Quantity: %s", orderGroupProductArrayList.get(j).getQty()));
                productSize.setText(String.format("Type/Size: %s", orderGroupProductArrayList.get(j).getSize()));
                String price = orderGroupProductArrayList.get(j).getPrice().replaceAll(",","");
                double priced = Double.parseDouble(price);
                productPrice.setText(String.format(" %s", precision1.format(priced)));
                String subtotal = orderGroupProductArrayList.get(j).getSubTotal().replaceAll(",","");
                double subtotald = Double.parseDouble(subtotal);
                productSubTotal.setText(String.format(" %s", precision1.format(subtotald)));

                productLayout.addView(order_group_product_view);

                subTotalFinal = subTotalFinal + subtotald;
            }
        }
//        String subtotal = String.valueOf(subTotalFinal).replaceAll(",","");
        orderTotal.setText(String.format("%s AED", String.valueOf(precision1.format(subTotalFinal))));
        String addressId = orderGroup.getAddressID();
        String areaName = databaseManager.getAreaByAddressId(addressId);
        String shippmentFee = databaseManager.getShipmentRate(areaName);
        double shippment = Double.parseDouble(shippmentFee.replaceAll(",",""));
        orderShipping.setText(String.format("%s AED", precision1.format(shippment)));

        String totalBeforeTax = String.valueOf(precision1.format(subTotalFinal + shippment));
        orderTotalBeforeVat.setText(String.format("%s AED", totalBeforeTax));

//        double vatAmount = (Double.parseDouble(totalBeforeTax) * 5) / 100;
//        DecimalFormat df = new DecimalFormat("#.##");
//        vatAmount = Double.valueOf(df.format(vatAmount));
//        orderVat.setText(String.format("%s AED", String.valueOf(vatAmount)));
        orderVat.setText("N/A");

//        String grandFinalTotal = String.valueOf(Double.parseDouble(totalBeforeTax) + vatAmount);
//        String grandFinalTotal = String.valueOf(Double.parseDouble(totalBeforeTax.replaceAll(",","")));
//        double grand = Double.parseDouble(grandFinalTotal.replaceAll(",",""));
        orderGrandTotal.setText(String.format("%s AED", totalBeforeTax));
        orderAmountTop.setText(String.format("%s AED", totalBeforeTax));

        address.setText(orderGroup.getAddress());

    }

    public void setFooter() {

        ImageView followFb = (ImageView) findViewById(R.id.follow_fb);
        ImageView followGoogle = (ImageView) findViewById(R.id.follow_google);
        ImageView followLinkedin = (ImageView) findViewById(R.id.follow_linkend);
        ImageView followTwitter = (ImageView) findViewById(R.id.follow_twitter);
        ImageView followCam = (ImageView) findViewById(R.id.follow_cam);
        ImageView followPin = (ImageView) findViewById(R.id.follow_pintrest);

        /* Footer Action
         */
        followFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String socailLink=context.getString(R.string.follow_fb);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = helper.getFacebookUrl(OrderDetailActivity.this, socailLink);
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
                        Toast.makeText(OrderDetailActivity.this, "No application can handle this request."
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
                        Toast.makeText(OrderDetailActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        followLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@" + "atozfurniture"));
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/a-to-z-furniture-2aa36a156"));
                final PackageManager packageManager = context.getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_linkendin)));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(OrderDetailActivity.this, "No application can handle this request."
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
                        Toast.makeText(OrderDetailActivity.this, "No application can handle this request."
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
                        Toast.makeText(OrderDetailActivity.this, "No application can handle this request."
                                + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
        followPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_pintrest_app))));
                } catch (Exception e) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.follow_pintrest))));
                    } catch (ActivityNotFoundException e1) {
                        Toast.makeText(OrderDetailActivity.this, "No application can handle this request."
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
                Intent intent = new Intent(OrderDetailActivity.this, HomeActivity.class);
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
                    Toast.makeText(OrderDetailActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(OrderDetailActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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
}
