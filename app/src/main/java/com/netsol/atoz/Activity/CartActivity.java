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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Adapter.CartItemAdapter;
import com.netsol.atoz.Adapter.CartSaveLaterAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 11/16/17.
 */

public class CartActivity extends AppCompatActivity implements AlertAction, CartItemAdapter.OnCartDeleted,
        CartItemAdapter.OnSaveLater, CartItemAdapter.OnChangeGrandTotal {

    boolean cartItemDeleted = false;
    String deleteProductId = "";
    String deleteColorId = "";
    String deleteSizeId = "";
    Button proceedCheckout;
    Button continueShopping;
    Button exploreProducts;
    Context context;
    TextView totalPrice;
    TextView saveLaterCount;
    ListView cartItemListView;
    JsonParser jsonParser;
    RelativeLayout emptyCart;
    LinearLayout cartItem;
    LinearLayout savedLayout;
    private Helper helper;
    ProgressDialog progressDialog;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public ArrayList<CartItem> cartItemArrayList;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Shopping Cart");
        }

        helper = new Helper(this, this);
        context = CartActivity.this;
        progressDialog = new ProgressDialog(this);
        _webserviceManager = new WebserviceManager(this);
        databaseManager = new DatabaseManager(this);
        jsonParser = new JsonParser(this);

        cartItemListView = (ListView) findViewById(R.id.cart_item_list);
        emptyCart = (RelativeLayout) findViewById(R.id.layoutEmptyCart);
        cartItem = (LinearLayout) findViewById(R.id.cartItem);
        savedLayout = (LinearLayout) findViewById(R.id.savedLayout);
        totalPrice = (TextView) findViewById(R.id.cart_total_price);
        saveLaterCount = (TextView) findViewById(R.id.cart_save_later_count);
        proceedCheckout = (Button) findViewById(R.id.button_checkout);
        continueShopping = (Button) findViewById(R.id.button_continue_shopping);
        exploreProducts = (Button) findViewById(R.id.button_explore_products);

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        setFooter();
        setFloatingMenu();

        proceedCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean session = sharedPref.getBoolean(Constants.SESSION, false);
                if (session) {
                    ArrayList<CartItem> cartItemsWithoutSaveLater = databaseManager.getCartItemWithoutSaveLater();
                    if (cartItemsWithoutSaveLater.size() > 0) {
//                        String grandTotal = totalPrice.getText().toString();
//                        editor.putString(Constants.TOTAL_AMOUNT, grandTotal);
//                        editor.apply();
                        Intent intent = new Intent(CartActivity.this, SelectAddressActivity.class);
                        CreditCardActivity.fromActivity = "Cart";
                        ReviewActivity.fromActivity = "Cart";
                        startActivity(intent);
                    } else {
                        Toast.makeText(CartActivity.this, "You do not have any item in your cart to Checkout.",  Toast.LENGTH_LONG).show();
                    }
                } else {
                    Intent intent = new Intent(CartActivity.this, SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        exploreProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        savedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CartSaveLater.class);
                startActivity(intent);
            }
        });

        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // finish the activity
                onBackPressed();
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
        cart.setVisible(false);
        return true;
    }

    @Override
    public void onResume() {
        setCartItem();
        super.onResume();
    }

    @Override
    public void OnCartItemDeleted(String productID, String color, String size, String colorId, String sizeId, String md5) {
        cartItemDeleted = true;
        String orderId = sharedPref.getString(Constants.ORDER_PLACED_ID, "");
        String userId = sharedPref.getString(Constants.USER_ID, "");
        deleteProductId = productID;
        deleteColorId = colorId;
        deleteSizeId = sizeId;

        if (orderId.equalsIgnoreCase("")) {
            databaseManager.deleteCartItem(deleteProductId, deleteColorId, deleteSizeId);
            int badgeCount = databaseManager.getBadgeCount();
            editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
            editor.apply();
            setCartItem();
            deleteProductId = "";
            deleteColorId = "";
            deleteSizeId = "";
        } else {
            RequestBody formBody = new FormBody.Builder()
                    .add("line_color", color)
                    .add("line_size", size)
                    .add("line_status", "Deleted")
                    .add("row_id", md5)
                    .add("user_id", userId)
                    .add("ord_id", orderId)
                    .add("prod_id", productID)
                    .build();

            new CartActivity.DeleteOrderline().execute(this, "post", formBody);
        }
    }

    @Override
    public void OnSaveLaterItem(String productID, String color, String size, String colorId, String sizeId, String md5) {
        cartItemDeleted = false;
        String orderId = sharedPref.getString(Constants.ORDER_PLACED_ID, "");
        String userId = sharedPref.getString(Constants.USER_ID, "");
        deleteProductId = productID;
        deleteColorId = colorId;
        deleteSizeId = sizeId;

        if (orderId.equalsIgnoreCase("")) {
            databaseManager.updateSaveLater(productID, colorId, sizeId, "true");
            int badgeCount = databaseManager.getBadgeCount();
            editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
            editor.apply();
//          helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
            setCartItem();
        } else {
            String is_deleted_api = databaseManager.getIsDeletedApi(productID, colorId, sizeId);
            if (!is_deleted_api.equalsIgnoreCase("0")) {
                if (is_deleted_api.equalsIgnoreCase("true")) {
                    databaseManager.updateSaveLater(productID, colorId, sizeId, "true");
                    int badgeCount = databaseManager.getBadgeCount();
                    editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
                    editor.apply();
//          helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
                    setCartItem();
                } else {
                    RequestBody formBody = new FormBody.Builder()
                            .add("line_color", color)
                            .add("line_size", size)
                            .add("line_status", "Deleted")
                            .add("row_id", md5)
                            .add("user_id", userId)
                            .add("ord_id", orderId)
                            .add("prod_id", productID)
                            .build();
                    new CartActivity.DeleteOrderline().execute(this, "post", formBody);
                }
            }
        }
        Toast.makeText(CartActivity.this, "Product is saved for later use.",  Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void OnCartItemAddedBack() {
//        setCartItem();
//    }

    @Override
    public void OnChangeTotal(String price, boolean isPlus) {
        double currentGrandTotal = Double.parseDouble(totalPrice.getText().toString().replaceAll(",",""));
        if (isPlus) {
            currentGrandTotal = currentGrandTotal + Double.parseDouble(price.replaceAll(",",""));
        } else {
            currentGrandTotal = currentGrandTotal - Double.parseDouble(price.replaceAll(",",""));
        }
        double total = Double.parseDouble(String.valueOf(currentGrandTotal));
        DecimalFormat precision = new DecimalFormat("0.00");
        totalPrice.setText(String.valueOf(precision.format(total)));
//        totalPrice.setText(String.valueOf(currentGrandTotal));
    }

//    @Override
//    public void OnCartItemDeleted() {
//        setCartItem();
//    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    private void setCartItem() {
        int totalPriceCalculated = 0;
        cartItemArrayList = databaseManager.getCartItem();
        if (cartItemArrayList.size() > 0) {
            ArrayList<CartItem> cartItemList = new ArrayList<>();
            cartItem.setVisibility(View.VISIBLE);
            emptyCart.setVisibility(View.GONE);

            for (int i = 0; i < cartItemArrayList.size(); i++) {
                boolean isSaveLater = Boolean.parseBoolean(cartItemArrayList.get(i).isSaveLater());
                if (!isSaveLater) {
                    int price = Integer.parseInt(cartItemArrayList.get(i).getPrice().replaceAll(",",""));
                    int qty = Integer.parseInt(cartItemArrayList.get(i).getQuantity());
                    price = price * qty;
                    totalPriceCalculated = totalPriceCalculated + price;
                    cartItemList.add(cartItemArrayList.get(i));
                }
            }
            CartItemAdapter cartItemAdapter = new CartItemAdapter(CartActivity.this, cartItemList);
            cartItemAdapter.setOnCartDeletedListener(this);
            cartItemAdapter.setOnSaveLaterListener(this);
            cartItemAdapter.setOnChangeTotalListener(this);
            cartItemListView.setAdapter(cartItemAdapter);

            ArrayList<CartItem> cartSaveLaterList = databaseManager.getCartItemSaveLater();
            saveLaterCount.setText("(" + cartSaveLaterList.size() + " items)");
        } else {
            cartItem.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
        }

        double total = Double.parseDouble(String.valueOf(totalPriceCalculated));
        DecimalFormat precision = new DecimalFormat("0.00");
        totalPrice.setText(String.valueOf(precision.format(total)));
//        totalPrice.setText(String.valueOf(totalPriceCalculated));
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
                    String facebookUrl = helper.getFacebookUrl(CartActivity.this, socailLink);
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
                        Toast.makeText(CartActivity.this, "No application can handle this request."
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
                        Toast.makeText(CartActivity.this, "No application can handle this request."
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
                        Toast.makeText(CartActivity.this, "No application can handle this request."
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
                        Toast.makeText(CartActivity.this, "No application can handle this request."
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
                        Toast.makeText(CartActivity.this, "No application can handle this request."
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
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
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
                    Toast.makeText(CartActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(CartActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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

    private class DeleteOrderline extends AsyncTask<Object, Void, Object> {

        private String TAG = CartActivity.DeleteOrderline.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CartActivity.this,
                    CartActivity.this.getResources().getString(
                            R.string.processing),
                    CartActivity.this.getResources().getString(
                            R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            String urlString = Constants.BASE_URL + Constants.END_POINT_ORDER_REMOVE;
            RequestBody requestParam = (RequestBody) params[2];

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.postHttpResponse(urlString, requestParam);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result != null) {
                String parseUpdate = jsonParser.parseSaveOrder(result.toString(), "cart");
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    if (cartItemDeleted) {
                        databaseManager.deleteCartItem(deleteProductId, deleteColorId, deleteSizeId);
                        deleteProductId = "";
                        deleteColorId = "";
                        deleteSizeId = "";
                        helper.dismissProgressDialog(progressDialog);
                        setCartItem();
                    } else {
                        databaseManager.updateSaveLater(deleteProductId, deleteColorId, deleteSizeId, "true");
                        databaseManager.updateDeletedAPI(deleteProductId, deleteColorId, deleteSizeId, "true");
                        helper.dismissProgressDialog(progressDialog);
                        setCartItem();
                    }
                    int badgeCount = databaseManager.getBadgeCount();
                    editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
                    editor.apply();
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
