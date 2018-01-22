package com.netsol.atoz.Util;

import android.app.ProgressDialog;
import android.util.Log;

import com.netsol.atoz.Model.CartItem;

import java.util.ArrayList;

/**
 * Created by macmini on 10/28/17.
 */

public class Constants {

    public static String COUNTRY_CODE = "+971";

    //Base URL And their end points
    //Production URL
//    public static String BASE_URL = "https://atozfurniture.ae/apisv1/azapi.php?mode=";
//    public static String BASE_URL_PAYMENT = "https://atozfurniture.ae/ajax/atoz_secure_vpc.php";

    //Development URL
    public static String BASE_URL = "https://furnituredeal.ae/apisv1/azapi.php?mode=";
    public static String BASE_URL_PAYMENT = "https://furnituredeal.ae/ajax/atoz_secure_vpc.php";

    public static String END_POINT_LOGIN = "login";
    public static String END_POINT_REGISTER = "register";
    public static String END_POINT_CONFIRM_ACCOUNT = "confirm";
    public static String END_POINT_RESEND_EMAIL = "resend";
    public static String END_POINT_CATEGORIES = "categories";
    public static String END_POINT_HOME = "home";
    public static String END_POINT_PRODUCTS_ALL = "product";
    public static String END_POINT_PRODUCTS = "product&cat_id=";
    public static String END_POINT_PRODUCT = "product&prod_id=";
    public static String END_POINT_PRODUCT_SEARCH = "search&query=";
    public static String END_POINT_SEARCH_RESULT = "search_result&query=";
    public static String END_POINT_ABOUT = "general_pages";
    public static String END_POINT_FAQ = "faqs";
    public static String END_POINT_BANNER = "home_banner";
    public static String END_POINT_CITY = "cities";
    public static String END_POINT_AREA = "areas";
    public static String END_POINT_NATIONALITY = "nationalities";
    public static String END_POINT_ADDRESS = "address&user_id=";
    public static String END_POINT_ADDRESS_SAVE = "address&action=save";
    public static String END_POINT_ADDRESS_UPDATE = "address&action=update";
    public static String END_POINT_RESET_PASSWORD = "reset_password";
    public static String END_POINT_FORGET_PASSWORD = "forgot_password";
    public static String END_POINT_CONTACT = "contact_us";
    public static String END_POINT_PROFILE_UPDATE = "profile_update";
    public static String END_POINT_ORDER = "orders&user_id=";
    public static String END_POINT_PROFILE_PIC = "user_photo&user_id=";
    public static String END_POINT_ORDER_SAVE = "save_order";
    public static String END_POINT_ORDER_COMPLETE = "order_completed";
    public static String END_POINT_ORDER_REMOVE = "remove_order_line";

    //Home Slider Alert dialog Codes
    public static int LOGOUT = 0;
    public static int CALL = 1;
    public static int SHARE = 2;
    public static int PROFILE_PIC_UPLOAD = 3;
    public static int RATE = 4;
    public static int EXIT = 5;

    //LOG Details
    public static final String LOG_ATOZ = "atoz_QA_";

    //SHARED PREFERENCE AND THEIR CONSTANTS
    public static String PREFERENCES_NAME = "com.netsol.atoz";

    //USER INFO
    public static String USER_ID = "USER_ID";
    public static String FIRST_NAME = "FIRST_NAME";
    public static String LAST_NAME = "LAST_NAME";
    public static String FULL_NAME = "FULL_NAME";
    public static String EMAIL = "EMAIL";
    public static String MOBILE = "MOBILE";
    public static String DOB = "DOB";
    public static String GENDER = "GENDER";
    public static String COUNTRY = "COUNTRY";
    public static String NATIONALITY = "NATIONALITY";
    public static String PASSWORD = "PASSWORD";
    public static String API_USER_PHOTO = "API_USER_PHOTO";
    public static String BADGE_COUNT = "BADGE_COUNT";
    public static String TOTAL_AMOUNT = "TOTAL_AMOUNT";
    public static String PRODUCT_TOTAL = "PRODUCT_TOTAL";
    public static String PRODUCT_VAT = "PRODUCT_VAT";
    public static String PRODUCT_SHIPPING = "PRODUCT_SHIPPING";
    public static String ADDRESS_ID = "ADDRESS_ID";
    public static String ORDER_PLACED_ID = "ORDER_PLACED_ID";
//    public static String ORDER_ROW_ID = "ORDER_ROW_ID";
    public static String SESSION = "SESSION";
    public static String IS_FIRST_INSTALL = "IS_FIRST_INSTALL";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    public static final String TAG_HOME = "home";
    public static final String TAG_MYORDERS = "myOrders";
    public static final String TAG_MYWHISHLIST = "myWhishList";
    public static final String TAG_SHIPPING_ADDRESS = "shippingAddress";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_RESET_PASSWORD = "reset";
    public static final String TAG_ABOUT = "about";
    public static final String TAG_CONTACT = "contact";
    public static final String TAG_CALL = "call";
    public static final String TAG_FAQ = "faq";
    public static final String TAG_SHARE = "share";
    public static final String TAG_RATE_US = "reteUs";
    public static final String TAG_LOGOUT = "logout";
}
