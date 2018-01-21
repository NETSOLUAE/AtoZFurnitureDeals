package com.netsol.atoz.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.netsol.atoz.Adapter.CategorySearchAdapter;
import com.netsol.atoz.Adapter.SliderAdapter;
import com.netsol.atoz.Adapter.SpinnerColorAdapter;
import com.netsol.atoz.Adapter.SpinnerSizeAdapter;
import com.netsol.atoz.Controller.AsyncServiceCall;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.Model.Color;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.Model.ProductImage;
import com.netsol.atoz.Model.Size;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.CircleAnimationUtil;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;
import com.netsol.atoz.Util.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by macmini on 11/7/17.
 */

public class ProductDeatils extends AppCompatActivity implements AlertAction, CircleAnimationUtil.OnAnimationFails {

    String searchText = "";
    int selectedPosition = 0;
    String colorId;
    String sizeId;
    String categoryId;
    String productId;
    String productNameVlaue;
    String sellingPriceValue;
    String actualPriceValue;
    String percentOffValue;
    String youSaveValue;
    String selectedColorName;
    String selectedSizeModel;
    Button addToCart;
    Product product;
    Context context;
    TextView productName;
    TextView sellingPrice;
    TextView actualPrice;
    TextView percentOff;
    TextView youSave;
    WebView description;
    WebView specification;
    WebView delivery;
    WebView warranty;
    ViewPager viewPager;
    TabLayout indicator;
    LayerDrawable icon;
    private Helper helper;
    Spinner spinnerSize;
    Spinner spinnerColor;
    List<String> HashMapForURL;
    DatabaseManager databaseManager;
    ArrayList<String> sizeList;
    ArrayList<String> colorList;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    SpinnerSizeAdapter sizeAdapter;
    ArrayList<ProductImage> productImageArrayList;
    public ImageView animationImage;

    Button searchViewAll;
    ListView searchList;
    LinearLayout productSearchLayout;
    LinearLayout productDetailsLayout;
    SearchView searchViewAndroidActionBar;
    WebserviceManager _webserviceManager;
    public static ArrayList<Product> productSearchArrayList;
    AsyncServiceCall _searchQueryCall;
    ArrayList<String> searchQueryList;

    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    final int PERMISSION_REQUEST_CODE = 111;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        context = ProductDeatils.this;
        helper = new Helper(this, this);
        databaseManager = new DatabaseManager(this);
        _webserviceManager = new WebserviceManager(context);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            categoryId = b.getString("CategoryID");
            productId = b.getString("ProductID");
        }

        viewPager=(ViewPager)findViewById(R.id.detailViewPager);
        indicator=(TabLayout)findViewById(R.id.detailIndicator);
        spinnerSize = (Spinner)findViewById(R.id.product_detail_spinner_size);
        spinnerColor = (Spinner)findViewById(R.id.product_detail_spinner_color);
        addToCart = (Button)findViewById(R.id.button_add_to_cart);
        productName = (TextView)findViewById(R.id.product_detail_name);
        sellingPrice = (TextView)findViewById(R.id.product_detail_selling_price);
        actualPrice = (TextView)findViewById(R.id.product_detail_actual_price);
        percentOff = (TextView)findViewById(R.id.product_detail_percent);
        youSave = (TextView)findViewById(R.id.product_detail_you_save);
        description = (WebView)findViewById(R.id.product_detail_description);
        specification = (WebView)findViewById(R.id.product_detail_specification);
        delivery = (WebView)findViewById(R.id.product_detail_delivery);
        warranty = (WebView)findViewById(R.id.product_detail_warranty);
        animationImage = (ImageView) findViewById(R.id.animation_image);

        searchViewAll = (Button) findViewById(R.id.button_search_view_all_product);
        searchList = (ListView) findViewById(R.id.product_search_list);
        productSearchLayout = (LinearLayout) findViewById(R.id.product_search_layout);
        productDetailsLayout = (LinearLayout) findViewById(R.id.product_details_layout);

        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        SetHomeDetails();
        setFooter();
        setFloatingMenu();

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String productId = productSearchArrayList.get(position).getProductId();
//                String categoryId = productSearchArrayList.get(position).getCategoryId();
//
//                productSearchLayout.setVisibility(View.GONE);
//                productDetailsLayout.setVisibility(View.VISIBLE);
//                searchList.clearTextFilter();
//                searchViewAndroidActionBar.clearFocus();
//                productSearchArrayList.clear();
//
//                new ProductDeatils.GetSingleProduct().execute(productId, "post", "");

                String searchSelected = searchQueryList.get(position);
                productSearchLayout.setVisibility(View.GONE);
                productDetailsLayout.setVisibility(View.VISIBLE);
                searchList.clearTextFilter();
                searchViewAndroidActionBar.clearFocus();

                new ProductDeatils.GetSearchProduct().execute(searchSelected, "Search Result", "");

            }
        });

        searchViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productSearchLayout.setVisibility(View.GONE);
                productDetailsLayout.setVisibility(View.VISIBLE);
                searchList.clearTextFilter();
                searchViewAndroidActionBar.clearFocus();

                new ProductDeatils.GetSearchProduct().execute(searchText, "Search Result", "");
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animationImage.getDrawable() != null) {
                    addToCart();
                }
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                animationImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(HashMapForURL.get(position))
                        .thumbnail(0.5f)
                        .fitCenter()
//                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(null)
                        .into(animationImage);

                animationImage.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColorName = colorList.get(position);
                if (position == 0) {
                    selectedPosition = 0;
                    if (sizeList != null && sizeList.size() > 0) {
                        sizeList.clear();
                        sizeList.add("Select Type/Size");
                        sizeAdapter.notifyDataSetChanged();
                    }
                    viewPager.setCurrentItem(position, true);
                } else {
                    if (!selectedColorName.equalsIgnoreCase("null") && !selectedColorName.equalsIgnoreCase(null) && !selectedColorName.equalsIgnoreCase("")) {
                        selectedPosition = productImageArrayList.size()+(position-1);
                        viewPager.setCurrentItem(productImageArrayList.size()+(position-1), true);
                        colorId = databaseManager.getColorID(categoryId, productId, selectedColorName);
                        setSizeList(colorId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSizeModel = sizeList.get(position);
                if (position > 0) {
                    if (!selectedSizeModel.equalsIgnoreCase("null") && !selectedSizeModel.equalsIgnoreCase(null) && !selectedSizeModel.equalsIgnoreCase("")) {
                        String sizeImage = databaseManager.getSizeImage(categoryId, productId, colorId, selectedSizeModel);
                        if (!sizeImage.equalsIgnoreCase("null") && !sizeImage.equalsIgnoreCase(null) && !sizeImage.equalsIgnoreCase("")) {
                            selectedPosition = productImageArrayList.size()+colorList.size()+(position-1);
                            viewPager.setCurrentItem(productImageArrayList.size()+colorList.size()+(position-1), true);
                        }
                        sizeId = databaseManager.getSizeID(categoryId, productId, colorId, selectedSizeModel);
                        String sizeActual = databaseManager.getSizeActualPrice(categoryId, productId, colorId, sizeId);
                        String sizeSelling = databaseManager.getSizeSellingPrice(categoryId, productId, colorId, sizeId);
                        String sizePercent = databaseManager.getSizePercent(categoryId, productId, colorId, sizeId);
                        sellingPrice.setText(sizeSelling);
                        actualPrice.setText(String.format("AED %s", sizeActual));
                        percentOff.setText("-" + sizePercent + "%");

                        int sizeDiscountPrice = Integer.parseInt(sizeActual.replaceAll(",","")) - Integer.parseInt(sizeSelling.replaceAll(",",""));

                        String sizeYouSave = String.valueOf(sizeDiscountPrice);

                        youSaveValue = sizeYouSave;
                        youSave.setText(String.format("AED %s", youSaveValue));
                        actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                } else {
                    selectedPosition = 0;
                    sellingPrice.setText(sellingPriceValue);
                    actualPrice.setText(String.format("AED %s", actualPriceValue));
                    percentOff.setText("-" + percentOffValue + "%");
                    youSave.setText(String.format("AED %s", youSaveValue));
                    actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (productDetailsLayout.getVisibility() == View.VISIBLE) {
                    onBackPressed();
                } else {
                    productSearchLayout.setVisibility(View.GONE);
                    productDetailsLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.cart:
                startActivity(new Intent(ProductDeatils.this, CartActivity.class));
                break;
            case R.id.action_search:
                productSearchLayout.setVisibility(View.GONE);
                productDetailsLayout.setVisibility(View.GONE);
                searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(item);
                searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        productSearchLayout.setVisibility(View.GONE);
                        productDetailsLayout.setVisibility(View.VISIBLE);
                        searchList.clearTextFilter();
                        searchViewAndroidActionBar.clearFocus();

                        new ProductDeatils.GetSearchProduct().execute(searchText, "Search Result", "");
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchText = newText;
                        if (newText.equalsIgnoreCase("")) {
                            if (searchViewAndroidActionBar.isFocused()) {
                                productSearchLayout.setVisibility(View.GONE);
                                productDetailsLayout.setVisibility(View.GONE);
                            } else {
                                productSearchLayout.setVisibility(View.GONE);
                            }
                        } else {
                            productSearchLayout.setVisibility(View.VISIBLE);
                            productDetailsLayout.setVisibility(View.GONE);
//                            productSearchArrayList = new ArrayList<>();
//                            productSearchArrayList = databaseManager.getSearchProduct(newText);
                            if (_searchQueryCall != null && (_searchQueryCall.getStatus() == AsyncTask.Status.RUNNING || _searchQueryCall.getStatus() == AsyncTask.Status.PENDING)) {
                                _searchQueryCall.cancel(true);
                            }
                            GetSearchQuery ();
                        }
                        return false;
                    }
                });
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

        cart.setIcon(context.getResources().getDrawable(R.drawable.menu_cart_white));

        icon = (LayerDrawable) cart.getIcon();
        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        helper.setBadgeCount(this, icon, badgeCount);

        searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(search);
        int textViewID = searchViewAndroidActionBar.getContext().getResources().getIdentifier("android:id/search_src_text",null, null);
        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchViewAndroidActionBar.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}

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

    public void SetProductDetails(){
        HashMapForURL = new ArrayList<>();
        sizeList = new ArrayList<>();
        sizeList.add("Select Type/Size");

        colorList = new ArrayList<>();
        colorList.add("Select Color");

        product = new Product();
        product = databaseManager.getSingleProduct(categoryId,productId);
        productNameVlaue = product.getTilte();
        actualPriceValue = product.getActualPrice();
        sellingPriceValue = product.getSellingPrice();
        percentOffValue = product.getDiscountPercent();

        productName.setText(Html.fromHtml(productNameVlaue));
        description.loadDataWithBaseURL(null, product.getDescription(), "text/html", "utf-8", null);
        specification.loadDataWithBaseURL(null, product.getSpecification(), "text/html", "utf-8", null);
        delivery.loadDataWithBaseURL(null, product.getDelivery(), "text/html", "utf-8", null);
        warranty.loadDataWithBaseURL(null, product.getWarranty(), "text/html", "utf-8", null);

        int discountPrice = Integer.parseInt(actualPriceValue.replaceAll(",","")) - Integer.parseInt(sellingPriceValue.replaceAll(",",""));

        youSaveValue = String.valueOf(discountPrice);

        productImageArrayList = databaseManager.getProductImage(categoryId,productId);
        for (int i = 0; i < productImageArrayList.size(); i++) {
            String url = productImageArrayList.get(i).getProductImage();
            HashMapForURL.add(url);
        }

        ArrayList<Color> colorsImageArrayList = databaseManager.getColors(categoryId,productId);
        for (int i = 0; i < colorsImageArrayList.size(); i++) {
            String url = colorsImageArrayList.get(i).getColorImage();
            String colorName = colorsImageArrayList.get(i).getColorName();
            if (!url.equalsIgnoreCase("null") && !url.equalsIgnoreCase(null) && !url.equalsIgnoreCase(""))
                HashMapForURL.add(url);
            if (!colorList.contains(colorName))
                colorList.add(colorName);
        }

        ArrayList<Size> sizesImageArrayList = databaseManager.getSizes(categoryId, productId, "", true);
        for (int i = 0; i < sizesImageArrayList.size(); i++) {
            String url = sizesImageArrayList.get(i).getSizeImage();
            if (!url.equalsIgnoreCase("null") && !url.equalsIgnoreCase(null) && !url.equalsIgnoreCase(""))
                HashMapForURL.add(url);
        }
    }

    public void SetHomeDetails() {

        SetProductDetails();

        sellingPrice.setText(sellingPriceValue);
        actualPrice.setText(String.format("AED %s", actualPriceValue));
        percentOff.setText("-" + percentOffValue + "%");
        youSave.setText(String.format("AED %s", youSaveValue));
        actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        SpinnerColorAdapter colorAdapter = new SpinnerColorAdapter(context, 0, colorList);
        spinnerColor.setAdapter(colorAdapter);

        sizeAdapter = new SpinnerSizeAdapter(context, 0, sizeList);
        spinnerSize.setAdapter(sizeAdapter);

        viewPager.setAdapter(new SliderAdapter(context, HashMapForURL));
        indicator.setupWithViewPager(viewPager, true);

        Glide.with(context).load(HashMapForURL.get(0))
                .thumbnail(0.5f)
                .fitCenter()
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(null)
                .into(animationImage);
        animationImage.setVisibility(View.INVISIBLE);

    }

    public void setSizeList(String colorID) {

        sizeList.clear();
        sizeList.add("Select Type/Size");
        ArrayList<Size> sizesImageArrayList = databaseManager.getSizes(categoryId, productId, colorID, false);
        for (int i = 0; i < sizesImageArrayList.size(); i++) {
            String model = sizesImageArrayList.get(i).getModel();
            if (!sizeList.contains(model))
                sizeList.add(model);
        }
        sizeAdapter.notifyDataSetChanged();

    }

    public void addToCart() {

        if (selectedColorName.equalsIgnoreCase("Select Color") || selectedSizeModel.equalsIgnoreCase("Select Type/Size")) {
            Toast.makeText(context, "Select Color/Size to add to cart.", Toast.LENGTH_LONG).show();
        } else {
            int cartUniqueCount = databaseManager.getUniqueCartItem(productId,colorId,sizeId);
            if (cartUniqueCount > 0) {
                int cartProductQuantity = Integer.parseInt(databaseManager.getCartProductQuantity(productId,colorId,sizeId));
                cartProductQuantity = cartProductQuantity + 1;
                databaseManager.updateCartProductQuantity(productId,colorId,sizeId, String.valueOf(cartProductQuantity));

            } else {
                String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String currentMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
                if (currentMonth.length() == 1) {
                    currentMonth = "0" + currentMonth;
                }
                String currentDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                if (currentDate.length() == 1) {
                    currentDate = "0" + currentDate;
                }
                String md5string = productId + selectedColorName + selectedSizeModel + currentYear + currentMonth + currentDate;
                String md5 = helper.MD5(md5string);
                CartItem cartItem = new CartItem();
                cartItem.setCategoryId(categoryId);
                cartItem.setProductId(productId);
                cartItem.setProductCode(product.getProductCode());
                cartItem.setColorId(colorId);
                cartItem.setSizeId(sizeId);
                cartItem.setSaveLater("false");
                cartItem.setIsFavorite("false");
                cartItem.setIsDeletedApi("false");
                cartItem.setQuantity("1");
                cartItem.setName(productNameVlaue.replaceAll("'", "''"));
                cartItem.setColor(selectedColorName);
                cartItem.setSize(selectedSizeModel);
                cartItem.setPrice(sellingPrice.getText().toString());
                cartItem.setMd5(md5);
                if (HashMapForURL.size() > 1) {
                    if (selectedPosition != 0 && selectedPosition <= HashMapForURL.size()) {
                        cartItem.setProductImage(HashMapForURL.get(selectedPosition));
                    } else {
                        cartItem.setProductImage(product.getProductImage());
                    }
                } else {
                    cartItem.setProductImage(product.getProductImage());
                }
                databaseManager.insertCartDetails(cartItem);
                makeFlyAnimation(animationImage);
            }
        }
    }

    private void makeFlyAnimation(ImageView targetView) {
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        CircleAnimationUtil circleAnimationUtil = new CircleAnimationUtil();
        circleAnimationUtil.setOnAnimationFailListener(ProductDeatils.this);
        circleAnimationUtil
                .attachActivity(this)
                .setTargetView(targetView.getDrawable())
                .setMoveDuration(500)
                .setDestView(reuse)
                .setAnimationListener(new Animator.AnimatorListener() {
                    @Override
                        public void onAnimationStart(Animator animation) {

                        }

                    @Override
                        public void onAnimationEnd(Animator animation) {
                            int badgeCount = databaseManager.getBadgeCount();
                            editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
                            editor.apply();
                            helper.setBadgeCount(context, icon, String.valueOf(badgeCount));
                            Toast.makeText(ProductDeatils.this, "Your Product added to Cart...", Toast.LENGTH_SHORT).show();
                        }

                    @Override
                        public void onAnimationCancel(Animator animation) {
                            int badgeCount = databaseManager.getBadgeCount();
                            editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
                            editor.apply();
                            helper.setBadgeCount(context, icon, String.valueOf(badgeCount));
                            Toast.makeText(ProductDeatils.this, "Your Product added to Cart...", Toast.LENGTH_SHORT).show();
                        }

                    @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                }).startAnimation();
    }

    @Override
    public void OnAnimationFail() {
        int badgeCount = databaseManager.getBadgeCount();
        editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
        editor.apply();
        helper.setBadgeCount(context, icon, String.valueOf(badgeCount));
        Toast.makeText(ProductDeatils.this, "Your Product added to Cart...", Toast.LENGTH_SHORT).show();
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
                    String facebookUrl = helper.getFacebookUrl(ProductDeatils.this, socailLink);
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
                        Toast.makeText(ProductDeatils.this, "No application can handle this request."
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
                        Toast.makeText(ProductDeatils.this, "No application can handle this request."
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
                        Toast.makeText(ProductDeatils.this, "No application can handle this request."
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
                        Toast.makeText(ProductDeatils.this, "No application can handle this request."
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
                        Toast.makeText(ProductDeatils.this, "No application can handle this request."
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
                Intent intent = new Intent(ProductDeatils.this, HomeActivity.class);
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
                    Toast.makeText(ProductDeatils.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ProductDeatils.this,"Permission Denied",Toast.LENGTH_LONG).show();
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

    private class GetSearchProduct extends AsyncTask<String, Void, Object> {
        String searchText = "";
        private ProgressDialog progressDialog;
        private String TAG = ProductDeatils.GetSearchProduct.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ProductDeatils.this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_products));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            searchText = params[0];
            String urlString = Constants.BASE_URL + Constants.END_POINT_SEARCH_RESULT + searchText;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    helper.dismissProgressDialog(progressDialog);
                    Intent intent = new Intent(ProductDeatils.this, CategoryActivity.class);
                    intent.putExtra("IS_HOME", "true");
                    intent.putExtra("HEADING", "Search Result");
                    intent.putExtra("IS_SUB_CAT", "false");
                    intent.putExtra("ID", "0");
                    startActivity(intent);
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

    private class GetSingleProduct extends AsyncTask<String, Void, Object> {
        String productID1 = "";
        private ProgressDialog progressDialog;
        private String TAG = ProductDeatils.GetSingleProduct.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ProductDeatils.this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.getting_products));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            productID1 = params[0];
            String urlString = Constants.BASE_URL + Constants.END_POINT_PRODUCT + productID1;

            Log.e(TAG, "processing http request in async task");
            return _webserviceManager.getHttpResponse(urlString, true);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                if (result.toString().equalsIgnoreCase("Updated")) {
                    String categoryId1 = databaseManager.getCategoryId(productID1);
                    categoryId = categoryId1;
                    productId = productID1;
                    SetHomeDetails();
//                    Intent intent = new Intent(getActivity(), ProductDeatils.class);
//                    intent.putExtra("CategoryID", categoryId);
//                    intent.putExtra("ProductID", productID);
//                    startActivity(intent);
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

    public void GetSearchQuery () {
        try {
            _searchQueryCall = new AsyncServiceCall() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Object doInBackground(Integer... params) {
                    String urlString = Constants.BASE_URL + Constants.END_POINT_PRODUCT_SEARCH + searchText;

                    Log.e("GetSearchQuery", "processing http request in async task");
                    return _webserviceManager.getHttpResponse(urlString, false);
                }

                @Override
                protected void onPostExecute(Object resultObj) {
                    String result = (String) resultObj;

                    if (result != null) {
                        String response = result.toString();
                        try {
                            JSONObject searchObject = new JSONObject(response);
                            if (!searchObject.isNull("suggestions")) {
                                searchQueryList = new ArrayList<>();
                                JSONArray searchArray = searchObject.getJSONArray("suggestions");
                                for (int j = 0; j < searchArray.length(); j++) {
                                    JSONObject productObject = searchArray
                                            .getJSONObject(j);
                                    String value = productObject
                                            .getString("value");
                                    searchQueryList.add(value);
                                }
                                CategorySearchAdapter categorySearchAdapter = new CategorySearchAdapter(ProductDeatils.this,searchQueryList);
                                searchList.setAdapter(categorySearchAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    super.onPostExecute(result);
                }
            };
            try {
                if (NetworkManager.isNetAvailable(ProductDeatils.this)) {
                    _searchQueryCall.execute(0);
                }
//                else {
//                    helper.cancelableAlertDialog("", context.getString(R.string.network_not_available), 1);
//                }
            } catch (Exception ex) {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        } catch (Exception e) {
            helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
        }
    }

}
