package com.netsol.atoz.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Log;
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
import com.netsol.atoz.Controller.AsyncServiceCall;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Fragment.AboutFragment;
import com.netsol.atoz.Fragment.AccountSettingsFragment;
import com.netsol.atoz.Fragment.ContactUsFragment;
import com.netsol.atoz.Fragment.FaqFragment;
import com.netsol.atoz.Fragment.HomeFragment;
import com.netsol.atoz.Fragment.MyOrderFragment;
import com.netsol.atoz.Fragment.MyWhishlistFragment;
import com.netsol.atoz.Fragment.ResetPasswordFragment;
import com.netsol.atoz.Fragment.ShippingAddressFragment;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;
import com.netsol.atoz.Util.NetworkManager;
import com.netsol.atoz.Util.ProfileAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 9/26/17.
 */

public class HomeActivity extends AppCompatActivity implements AlertAction, HomeFragment.OnFragmentInteractionListener {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public NavigationView navigationView;
    public static TextView slidingName;
    public  TextView slidingEmail;
    private DrawerLayout drawer;
    private ImageView imgNavHeaderBg;
    private ImageView imgNavHeaderProfile;
    private TextView uploadText;
    private Toolbar toolbar;
    private Helper helper;
    private int alertType = 0;
    private boolean session = false;
    private Context context;
    private Uri mCropImageUri;
    ProgressDialog progressDialog;
    WebserviceManager _webserviceManager;
    public static ProfileAction profileAction;
    public static String CURRENT_TAG = Constants.TAG_HOME;
    public static int congragTrack = 0;
    final int PERMISSION_REQUEST_CODE = 111;
    DatabaseManager databaseManager;
    LayerDrawable icon;

    // toolbar titles respected to selected nav menu item
    public static String[] activityTitles;

    private Handler mHandler;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Animation fadeIn;
    Animation fadeOut;
    LinearLayout transparentLayout;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionCall, floatingActionChat, floatingActionFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = HomeActivity.this;
        databaseManager = new DatabaseManager(this);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        _webserviceManager = new WebserviceManager(this);
        helper = new Helper(this, this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgNavHeaderProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        uploadText = (TextView) navHeader.findViewById(R.id.upload_text);
        slidingName = (TextView) navHeader.findViewById(R.id.sliding_name);
        slidingEmail = (TextView) navHeader.findViewById(R.id.sliding_email);

        mHandler = new Handler();
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        session = sharedPref.getBoolean(Constants.SESSION, false);
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
//                    transparentLayout.startAnimation(fadeOut);
//                    transparentLayout.setVisibility(View.GONE);
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
                Constants.navItemIndex = 8;
                CURRENT_TAG = Constants.TAG_FAQ;
                loadHomeFragment();
                materialDesignFAM.close(true);
            }
        });

        profileAction = new ProfileAction() {
            @Override
            public void onProfileReceived(Bitmap image, String uriString) {
                Log.d(Constants.LOG_ATOZ, "**************");
                if (image != null) {
                    drawer.closeDrawers();
//                    UploadProfilePic(uriString);
                    new HomeActivity.UploadProfilePic().execute(uriString, "post", "");
                } else {
                    uploadText.setVisibility(View.VISIBLE);
                }
            }
        };

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        // Marshmallow+
        if (!checkCallPhonePermission() || !checkReadStatePermission() || !checkReadExternalStorage() || !checkWriteExternalStorage()) {
            requestPermission();
        }

        if (savedInstanceState == null) {
            if (congragTrack == 0) {
                Constants.navItemIndex = 0;
                CURRENT_TAG = Constants.TAG_HOME;
            } else if (congragTrack == 1) {
                Constants.navItemIndex = congragTrack;
                CURRENT_TAG = Constants.TAG_MYORDERS;
                congragTrack = 0;
            } else if (congragTrack == 8) {
                Constants.navItemIndex = congragTrack;
                CURRENT_TAG = Constants.TAG_FAQ;
                congragTrack = 0;
            }
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {

//        String name = sharedPref.getString(Constants.FIRST_NAME, "") + " " + sharedPref.getString(Constants.LAST_NAME, "");
        String name = sharedPref.getString(Constants.FULL_NAME, "");
        String email = sharedPref.getString(Constants.EMAIL, "");
        String profileImgUrl = sharedPref.getString(Constants.API_USER_PHOTO,"");
//        String encodedProfileString = sharedPref.getString(Constants.PROFILE_PIC,"");

        // loading header background image
        imgNavHeaderBg.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.sliding_background, 100, 100));

        if (session) {
            slidingName.setText(name);
            slidingEmail.setText(email);

            if (profileImgUrl.equalsIgnoreCase("")) {
                uploadText.setVisibility(View.VISIBLE);
            } else {
                uploadText.setVisibility(View.GONE);

                Glide.with(context).load(profileImgUrl)
                        .fitCenter()
//                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.sliding_profile_icon)
                        .into(imgNavHeaderProfile);
//                byte[] imageAsBytes = Base64.decode(encodedProfileString, Base64.DEFAULT);
//                imgNavHeaderProfile.setImageBitmap(helper.getCroppedBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)));
            }
        } else {
            slidingName.setText(context.getResources().getString(R.string.welcome));
            slidingEmail.setText(context.getResources().getString(R.string.loginSignUp));
            uploadText.setVisibility(View.VISIBLE);
        }

        imgNavHeaderProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session) {
                    startActivityForResult(getPickImageChooserIntent(), 200);
                } else {
                    Intent intent = new Intent(HomeActivity.this, SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        slidingEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!session) {
                    Intent intent = new Intent(HomeActivity.this, SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });


    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        if (Constants.navItemIndex != 0 && Constants.navItemIndex != 9 && Constants.navItemIndex != 10
                && Constants.navItemIndex != 6  && Constants.navItemIndex != 7  && Constants.navItemIndex != 8
                && Constants.navItemIndex != 11) {
            if (!session) {
                Intent logoutIntent = new Intent(HomeActivity.this, SigninActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                return;
            }
        }

        if (Constants.navItemIndex == 0 || Constants.navItemIndex == 1 || Constants.navItemIndex == 2 || Constants.navItemIndex == 3 ||
            Constants.navItemIndex == 4 || Constants.navItemIndex == 5 || Constants.navItemIndex == 6 || Constants.navItemIndex == 7 || Constants.navItemIndex == 8) {
            // set toolbar title
            setToolbarTitle();

            // Sometimes, when fragment has huge data, screen seems hanging
            // when switching between navigation menus
            // So using runnable, the fragment is loaded with cross fade effect
            // This effect can be seen in GMail app
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    // update the main content by replacing fragments
                    Fragment fragment = getHomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };

            // If mPendingRunnable is not null, then add to the message queue
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (Constants.navItemIndex) {

            case 0:
                return new HomeFragment();

            case 1:
                return new MyOrderFragment();

            case 2:
                return new MyWhishlistFragment();

            case 3:
                return new ShippingAddressFragment();

            case 4:
                return new AccountSettingsFragment();

            case 5:
                return new ResetPasswordFragment();

            case 6:
                return new AboutFragment();

            case 7:
                return new ContactUsFragment();

            case 8:
                return new FaqFragment();

            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        if (CURRENT_TAG.equalsIgnoreCase(Constants.TAG_HOME)) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setIcon(context.getResources().getDrawable(R.drawable.appbar_logo, context.getTheme()));
                getSupportActionBar().setTitle("");
            }
            toolbar.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            helper.darkenStatusBar(this, R.color.colorAccent);
            actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.menu);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setIcon(null);
                getSupportActionBar().setTitle(activityTitles[Constants.navItemIndex]);
            }
            toolbar.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            helper.darkenStatusBar(this, R.color.colorPrimaryDark);
            actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.menu_white);
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(Constants.navItemIndex).setChecked(true);
    }

    public void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        Constants.navItemIndex = 0;
                        CURRENT_TAG = Constants.TAG_HOME;
                        break;
                    case R.id.nav_myorders:
                        Constants.navItemIndex = 1;
                        CURRENT_TAG = Constants.TAG_MYORDERS;
                        break;
                    case R.id.nav_whishlist:
                        Constants.navItemIndex = 2;
                        CURRENT_TAG = Constants.TAG_MYWHISHLIST;
                        break;
                    case R.id.nav_shippingAddress:
                        Constants.navItemIndex = 3;
                        CURRENT_TAG = Constants.TAG_SHIPPING_ADDRESS;
                        break;
                    case R.id.nav_settings:
                        Constants.navItemIndex = 4;
                        CURRENT_TAG = Constants.TAG_SETTINGS;
                        break;
                    case R.id.nav_resetPassword:
                        Constants.navItemIndex = 5;
                        CURRENT_TAG = Constants.TAG_RESET_PASSWORD;
                        break;
                    case R.id.nav_about:
                        Constants.navItemIndex = 6;
                        CURRENT_TAG = Constants.TAG_ABOUT;
                        break;
                    case R.id.nav_contactUs:
                        Constants.navItemIndex = 7;
                        CURRENT_TAG = Constants.TAG_CONTACT;
                        break;
                    case R.id.nav_faq:
                        Constants.navItemIndex = 8;
                        CURRENT_TAG = Constants.TAG_FAQ;
                        break;
                    case R.id.nav_callUs:
                        Constants.navItemIndex = 9;
                        CURRENT_TAG = Constants.TAG_CALL;
                        drawer.closeDrawers();
                        alertType = Constants.CALL;
                        helper.cancelableAlertDialog("","Please contact +971509941516 for queries.", 2);
                        return true;
                    case R.id.nav_shareWithFriends:
                        Constants.navItemIndex = 10;
                        CURRENT_TAG = Constants.TAG_SHARE;
                        drawer.closeDrawers();
                        alertType = Constants.SHARE;
                        String shareText = "Check it out! A to Z Furniture Deals for Money Saving, Attractive, Amazing offers on furniture sale! Click on the below link to get it.";
                        shareText += "\n" + "https://play.google.com/store/apps/details?id=com.netsol.atoz";
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;
                    case R.id.nav_rateUs:
                        Constants.navItemIndex = 11;
                        CURRENT_TAG = Constants.TAG_RATE_US;
                        drawer.closeDrawers();
                        alertType = Constants.RATE;
                        helper.cancelableAlertDialog("","Rate us on Play Store.", 1);
                        return true;
                    case R.id.nav_logout:
                        Constants.navItemIndex = 12;
                        CURRENT_TAG = Constants.TAG_LOGOUT;
                        drawer.closeDrawers();
                        alertType = Constants.LOGOUT;
                        helper.cancelableAlertDialog("","Do you really want to Logout?", 2);
                        return true;
                    default:
                        Constants.navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);

        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.menu);

        if (session) {
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.nav_logout);
            target.setVisible(true);
        } else {
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.nav_logout);
            target.setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // checking if user is on other navigation menu
        // rather than home
        if (Constants.navItemIndex != 0) {
            Constants.navItemIndex = 0;
            CURRENT_TAG = Constants.TAG_HOME;
            loadHomeFragment();
        } else {
            drawer.closeDrawers();
            alertType = Constants.EXIT;
            helper.cancelableAlertDialog("","Do you really want to Exit?", 2);
        }
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

        if (Constants.navItemIndex == 0) {
            cart.setIcon(context.getResources().getDrawable(R.drawable.menu_cart));
        } else {
            cart.setIcon(context.getResources().getDrawable(R.drawable.menu_cart_white));
        }
        icon = (LayerDrawable) cart.getIcon();
//        editor.putString(Constants.BADGE_COUNT, "100");
//        editor.apply();

        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        helper.setBadgeCount(this, icon, badgeCount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart) {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
//            Toast.makeText(getApplicationContext(), "No Items added!", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    public void onOkClicked() {

        if (alertType == Constants.LOGOUT) {
            editor.putBoolean(Constants.SESSION, false);
            editor.apply();
            Intent logoutIntent = new Intent(HomeActivity.this, SigninActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
        } else if (alertType == Constants.EXIT) {
//            finish();
            finishAffinity();
        } else if (alertType == Constants.PROFILE_PIC_UPLOAD) {

        } else if (alertType == Constants.RATE) {
            try {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://play.google.com/store/apps/details?id=com.netsol.atoz"));
                startActivity(viewIntent);
            }catch(Exception e1) {
                Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                        Toast.LENGTH_LONG).show();
                e1.printStackTrace();
            }
        } else if (alertType == Constants.CALL) {
            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                if (!checkCallPhonePermission() || !checkReadStatePermission() || !checkReadExternalStorage() || !checkWriteExternalStorage()) {
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
    }

    @Override
    public void onCancelClicked() {

    }

    /**
     * Create a chooser intent to select the  source to get image from.<br/>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the  intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to  save.
        Uri outputFileUri =  getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager =  getPackageManager();

        // collect all camera intents
        Intent captureIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam =  packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new  Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

    // collect all gallery intents
        Intent galleryIntent = new  Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery =  packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new  Intent(galleryIntent);
            intent.setComponent(new  ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

    // the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent =  allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if  (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity"))  {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

    // Create a chooser from the main  intent
        Intent chooserIntent =  Intent.createChooser(mainIntent, "Select source");

    // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,  allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new  File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int  requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri =  getPickImageResultUri(data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                Intent cropIntent = new Intent(HomeActivity.this, ImageCropActivity.class);
                cropIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                cropIntent.putExtra("ImageUri", imageUri.toString());
                startActivity(cropIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission) {
                    Toast.makeText(HomeActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(HomeActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
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
                    if (alertType == Constants.CALL) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:+971509941516"));
                        startActivity(intent);
                    }
                }
                break;

            default:
                if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cropIntent = new Intent(HomeActivity.this, ImageCropActivity.class);
                    cropIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    cropIntent.putExtra("ImageUri", mCropImageUri.toString());
                    startActivity(cropIntent);
                } else {
                    Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Get the URI of the selected image from  {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera  and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent  data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null  && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ?  getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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

    private boolean checkReadStatePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkReadExternalStorage() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWriteExternalStorage() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction() {
        int badgeCount = databaseManager.getBadgeCount();
        editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
        editor.apply();
        helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
    }

    @Override
    public void onResume() {
        String badgeCount = sharedPref.getString(Constants.BADGE_COUNT, "");
        if (icon != null)
            helper.setBadgeCount(this, icon, String.valueOf(badgeCount));
        super.onResume();
    }

//    public void UploadProfilePic(final String fileName) {
//        try {
//            AsyncServiceCall _companyDetails = new AsyncServiceCall() {
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//                    progressDialog = new ProgressDialog(HomeActivity.this, R.style.MyAlertDialogStyle);
//                    progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
//                    progressDialog.setTitle(context.getResources().getString(R.string.uploading_profile_pic));
//                    progressDialog.setIndeterminate(false);
////                    progressDialog.setCancelable(true);
//                    progressDialog.show();
//                }
//
//                @Override
//                protected Object doInBackground(Integer... params) {
//                    ContentResolver contentResolver = getContentResolver();
//                    String userId = sharedPref.getString(Constants.USER_ID,"");
//                    String urlString = Constants.BASE_URL + Constants.END_POINT_PROFILE_PIC + userId;
//                    Uri uri = Uri.parse(fileName);
//                    String filepath = helper.getFilePathFromContentUri(uri, contentResolver);
//                    return _webserviceManager.multipart(urlString, uri.toString(), filepath);
//                }
//
//                @Override
//                protected void onPostExecute(Object resultObj) {
//                    String result = (String) resultObj;
//
//                    alertType = Constants.PROFILE_PIC_UPLOAD;
//                    if (result != null) {
//                        helper.dismissProgressDialog(progressDialog);
//                        helper.cancelableAlertDialog("", result, 1);
//                    } else {
//                        helper.dismissProgressDialog(progressDialog);
//                        helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
//                    }
//                    super.onPostExecute(result);
//                }
//            };
//            try {
//
//                if (NetworkManager.isNetAvailable(HomeActivity.this)) {
//                    _companyDetails.execute(0);
//                } else {
//                    helper.dismissProgressDialog(progressDialog);
//                    helper.cancelableAlertDialog("", context.getString(R.string.network_not_available), 1);
//                }
//            } catch (Exception ex) {
//                helper.dismissProgressDialog(progressDialog);
//                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
//            }
//        } catch (Exception e) {
//            helper.dismissProgressDialog(progressDialog);
//            helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
//        }
//    }

    private class UploadProfilePic extends AsyncTask<String, Void, Object> {

        private String TAG = HomeActivity.UploadProfilePic.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeActivity.this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
            progressDialog.setTitle(context.getResources().getString(R.string.uploading_profile_pic));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(String... params) {
            ContentResolver contentResolver = getContentResolver();
            String userId = sharedPref.getString(Constants.USER_ID,"");
            String urlString = Constants.BASE_URL + Constants.END_POINT_PROFILE_PIC + userId;
            Uri uri = Uri.parse(params[0]);
            String filepath = helper.getFilePathFromContentUri(uri, contentResolver);
            return _webserviceManager.multipart(urlString, uri.toString(), filepath);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            alertType = Constants.PROFILE_PIC_UPLOAD;
            if (result != null) {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", result.toString(), 1);
            } else {
                helper.dismissProgressDialog(progressDialog);
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}