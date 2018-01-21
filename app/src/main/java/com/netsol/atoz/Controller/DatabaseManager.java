package com.netsol.atoz.Controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.netsol.atoz.Model.AboutGroup;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.Model.Area;
import com.netsol.atoz.Model.Banner;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.Model.Category;
import com.netsol.atoz.Model.City;
import com.netsol.atoz.Model.Color;
import com.netsol.atoz.Model.Faq;
import com.netsol.atoz.Model.FilterChild;
import com.netsol.atoz.Model.HomeGroup;
import com.netsol.atoz.Model.HomeProduct;
import com.netsol.atoz.Model.Nationality;
import com.netsol.atoz.Model.OrderGroup;
import com.netsol.atoz.Model.OrderProduct;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.Model.ProductImage;
import com.netsol.atoz.Model.Size;
import com.netsol.atoz.Util.Constants;

import java.util.ArrayList;

/**
 * Created by macmini on 11/19/17.
 */

public class DatabaseManager {
    private static String TAG = Constants.LOG_ATOZ + DatabaseManager.class.getSimpleName();
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "ATOZTESTDB26.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
//    private static String dbPassword = "";
    RMSDataHelper openHelper;

    public DatabaseManager(Context context) {
        this.context = context;
//        dbPassword = Encryption.toAscii(context);
    }

    private void openConnection() {
        try {
            if (this.db == null || !this.db.isOpen()) {
                openHelper = new RMSDataHelper(this.context);
                this.db = openHelper.getWritableDatabase();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception is " + Log.getStackTraceString(ex));
        }
    }

    private void closeConnection() {
        try {
            if (this.db != null && this.db.isOpen()) {
                db.close();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception is " + Log.getStackTraceString(ex));
        }
    }

    /**
     * This method creates all the tables.
     */

    public void createDb() {
        openConnection();
        db.execSQL("CREATE Table CART_ITEM(CATEGORY_ID TEXT, PRODUCT_ID TEXT, COLOR_ID TEXT, SIZE_ID TEXT, PRODUCT_CODE TEXT, NAME TEXT, COLOR TEXT, SIZE TEXT, PRICE TEXT, QUANTITY TEXT, IMAGE TEXT, IS_SAVE_LATER TEXT, " +
                "IS_FAVORITE TEXT, IS_DELETED_API TEXT, MDHASH TEXT)");
        db.execSQL("CREATE Table HOME_GROUP(GROUP_ID TEXT, GROUP_TITLE TEXT)");
        db.execSQL("CREATE Table HOME_PRODUCT(GROUP_ID TEXT, PRODUCT_ID TEXT, GROUP_TITLE TEXT, PRODUCT_TITLE TEXT, SELLING TEXT, ACTUAL TEXT, PERCENT TEXT, COLOR TEXT, IMAGE TEXT)");
        db.execSQL("CREATE Table CATEGORY(CATEGORY_ID TEXT, PARENT TEXT, NAME TEXT, IMAGE TEXT, TOTAL TEXT, PATH TEXT, ENABLED TEXT, POSITION TEXT, IS_HOME TEXT)");
        db.execSQL("CREATE Table PRODUCT(CATEGORY_ID TEXT, PRODUCT_ID TEXT, PRODUCT_CODE TEXT, TITLE TEXT, SELLING_PRICE TEXT, ACTUAL_PRICE TEXT, DISCOUNT_PERCENT TEXT, PRODUCT_IMAGE TEXT, DESCRIPTION TEXT, " +
                "SPECIFICATION TEXT, DELIVERY TEXT, WARRANTY TEXT, DEAL_TYPE TEXT, IS_SEARCH TEXT)");
        db.execSQL("CREATE Table COLOR(CATEGORY_ID TEXT, PRODUCT_ID TEXT, COLOR_ID TEXT, COLOR_NAME TEXT, COLOR_IMAGE TEXT)");
        db.execSQL("CREATE Table SIZE(CATEGORY_ID TEXT, PRODUCT_ID TEXT, COLOR_ID TEXT, SIZE_ID TEXT, SIZE TEXT, QUANTITY TEXT, SELLING TEXT, ACTUAL TEXT, PERCENT TEXT, MODEL TEXT, WEIGHT TEXT, SIZE_IMAGE TEXT)");
        db.execSQL("CREATE Table SLIDING_IMAGE(CATEGORY_ID TEXT, PRODUCT_ID TEXT, IMAGE_ID TEXT, PRODUCT_IMAGE TEXT)");
        db.execSQL("CREATE Table BANNER_IMAGE(ID TEXT, TITLE TEXT, DESCRIPTION TEXT, IMAGE TEXT, BANNER TEXT, LOCATION TEXT, ENABLED TEXT, POSITION TEXT)");
        db.execSQL("CREATE Table ABOUT(ABOUT_ID TEXT, TITLE TEXT, DESCRIPTION TEXT, LAST_SAVED TEXT)");
        db.execSQL("CREATE Table FAQ(GROUP_ID TEXT, GROUP_NAME TEXT, GROUP_TOTAL TEXT, CHILD_ID TEXT, TITLE TEXT, DESCRIPTION TEXT, FAQ_ORDER TEXT)");
        db.execSQL("CREATE Table ADDRESS(ID TEXT, NAME TEXT, STREET TEXT, BUILDING TEXT, APARTMENT TEXT, FLOOR TEXT, LANDMARK TEXT, PHONE TEXT, MOBILE TEXT, AREA TEXT, LOCATION TEXT," +
                "DELIVERY TEXT, NOTE TEXT, CITY TEXT, COUNTRY TEXT, ENABLED TEXT)");
        db.execSQL("CREATE Table CITY(ID TEXT, NAME TEXT, POSITION TEXT, ENABLED TEXT)");
        db.execSQL("CREATE Table AREA(ID TEXT, NAME TEXT, SHIP_RATE TEXT, CITY_ID TEXT)");
        db.execSQL("CREATE Table NATIONALITY(ID TEXT, NAME TEXT)");
        db.execSQL("CREATE Table ORDER_GROUP(ORDER_NO TEXT, TOTAL TEXT, DATE TEXT, ADDRESS TEXT, ADDRESS_NAME TEXT, ADDRESS_ID TEXT, STATUS TEXT, PAYMENT_STATUS TEXT, LEVEL TEXT)");
        db.execSQL("CREATE Table ORDER_PRODUCT(ORDER_NO TEXT, PRODUCT_ID TEXT, PRODUCT_CODE TEXT, NAME TEXT, COLOR TEXT, SIZE TEXT, QUANTITY TEXT, PRICE TEXT, SUB_TOTAL TEXT, IMAGE TEXT)");
        closeConnection();
    }

    /**
     * Start of Table Transaction
     */

    public void clearCartDetails() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM CART_ITEM");
        closeConnection();
    }

    public void clearCategory() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM CATEGORY");
        db.execSQL("DELETE FROM HOME_GROUP");
        db.execSQL("DELETE FROM HOME_PRODUCT");
        db.execSQL("DELETE FROM BANNER_IMAGE");
        db.execSQL("DELETE FROM CITY");
        db.execSQL("DELETE FROM AREA");
        db.execSQL("DELETE FROM NATIONALITY");
        closeConnection();
    }

    public void clearAbout() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM ABOUT");
        closeConnection();
    }

    public void clearFaq() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM FAQ");
        closeConnection();
    }

    public void clearProducts() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM PRODUCT");
        db.execSQL("DELETE FROM COLOR");
        db.execSQL("DELETE FROM SIZE");
        db.execSQL("DELETE FROM SLIDING_IMAGE");
        closeConnection();
    }

    public void clearProducts(String productId) {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM PRODUCT WHERE PRODUCT_ID = " + productId);
        db.execSQL("DELETE FROM COLOR WHERE PRODUCT_ID = " + productId);
        db.execSQL("DELETE FROM SIZE WHERE PRODUCT_ID = " + productId);
        db.execSQL("DELETE FROM SLIDING_IMAGE WHERE PRODUCT_ID = " + productId);
        closeConnection();
    }

    public void clearProductColor(String productId) {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM COLOR WHERE PRODUCT_ID = " + productId);
        closeConnection();
    }

    public void clearAddress() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM ADDRESS");
        closeConnection();
    }

    public void clearOrderDetails() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM ORDER_GROUP");
        db.execSQL("DELETE FROM ORDER_PRODUCT");
        closeConnection();
    }

    public void clearWhishlistDetails() {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM CART_ITEM WHERE IS_FAVORITE = 'true'");
        closeConnection();
    }

    public void deleteCartItem(String productID, String colorId, String sizeId) {
        openConnection();
        // To clear transaction Tables
        db.execSQL("DELETE FROM CART_ITEM WHERE PRODUCT_ID = '" + productID + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'");
        closeConnection();
    }

    public void insertCartDetails(CartItem _cartItem) {
        openConnection();
        try {
            String qryString = "Insert into CART_ITEM(CATEGORY_ID, PRODUCT_ID, COLOR_ID, SIZE_ID, PRODUCT_CODE, NAME, COLOR, SIZE, PRICE, QUANTITY, IMAGE, IS_SAVE_LATER, IS_FAVORITE, IS_DELETED_API, " +
                    "MDHASH) Values ('"
                    + _cartItem.getCategoryId()
                    + "', '"
                    + _cartItem.getProductId()
                    + "', '"
                    + _cartItem.getColorId()
                    + "', '"
                    + _cartItem.getSizeId()
                    + "', '"
                    + _cartItem.getProductCode()
                    + "', '"
                    + _cartItem.getName()
                    + "', '"
                    + _cartItem.getColor()
                    + "', '"
                    + _cartItem.getSize()
                    + "', '"
                    + _cartItem.getPrice()
                    + "', '"
                    + _cartItem.getQuantity()
                    + "', '"
                    + _cartItem.getProductImage()
                    + "', '"
                    + _cartItem.getIsSaveLater()
                    + "', '"
                    + _cartItem.getIsFavorite()
                    + "', '"
                    + _cartItem.getIsDeletedApi()
                    + "', '"
                    + _cartItem.getMd5() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<CartItem> getCartItem() {
        openConnection();
        String query = "SELECT * FROM CART_ITEM";
        Cursor c = db.rawQuery(query, null);

        ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    CartItem cartItem = new CartItem();
                    cartItem.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    cartItem.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    cartItem.setColorId(c.getString(c
                            .getColumnIndex("COLOR_ID")));
                    cartItem.setSizeId(c.getString(c
                            .getColumnIndex("SIZE_ID")));
                    cartItem.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    cartItem.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    cartItem.setColor(c.getString(c
                            .getColumnIndex("COLOR")));
                    cartItem.setSize(c.getString(c
                            .getColumnIndex("SIZE")));
                    cartItem.setPrice(c.getString(c
                            .getColumnIndex("PRICE")));
                    cartItem.setQuantity(c.getString(c
                            .getColumnIndex("QUANTITY")));
                    cartItem.setProductImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    cartItem.setIsSaveLater(c.getString(c
                            .getColumnIndex("IS_SAVE_LATER")));
                    cartItem.setIsFavorite(c.getString(c
                            .getColumnIndex("IS_FAVORITE")));
                    cartItem.setIsDeletedApi(c.getString(c
                            .getColumnIndex("IS_DELETED_API")));
                    cartItem.setMd5(c.getString(c
                            .getColumnIndex("MDHASH")));
                    cartItemList.add(cartItem);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public ArrayList<CartItem> getCartItemWithoutSaveLater() {
        openConnection();
        String query = "SELECT * FROM CART_ITEM WHERE IS_SAVE_LATER = 'false'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    CartItem cartItem = new CartItem();
                    cartItem.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    cartItem.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    cartItem.setColorId(c.getString(c
                            .getColumnIndex("COLOR_ID")));
                    cartItem.setSizeId(c.getString(c
                            .getColumnIndex("SIZE_ID")));
                    cartItem.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    cartItem.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    cartItem.setColor(c.getString(c
                            .getColumnIndex("COLOR")));
                    cartItem.setSize(c.getString(c
                            .getColumnIndex("SIZE")));
                    cartItem.setPrice(c.getString(c
                            .getColumnIndex("PRICE")));
                    cartItem.setQuantity(c.getString(c
                            .getColumnIndex("QUANTITY")));
                    cartItem.setProductImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    cartItem.setIsSaveLater(c.getString(c
                            .getColumnIndex("IS_SAVE_LATER")));
                    cartItem.setIsFavorite(c.getString(c
                            .getColumnIndex("IS_FAVORITE")));
                    cartItem.setIsDeletedApi(c.getString(c
                            .getColumnIndex("IS_DELETED_API")));
                    cartItem.setMd5(c.getString(c
                            .getColumnIndex("MDHASH")));
                    cartItemList.add(cartItem);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public ArrayList<CartItem> getCartItemFavorite() {
        openConnection();
        String query = "SELECT * FROM CART_ITEM WHERE IS_FAVORITE = 'true'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    CartItem cartItem = new CartItem();
                    cartItem.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    cartItem.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    cartItem.setColorId(c.getString(c
                            .getColumnIndex("COLOR_ID")));
                    cartItem.setSizeId(c.getString(c
                            .getColumnIndex("SIZE_ID")));
                    cartItem.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    cartItem.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    cartItem.setColor(c.getString(c
                            .getColumnIndex("COLOR")));
                    cartItem.setSize(c.getString(c
                            .getColumnIndex("SIZE")));
                    cartItem.setPrice(c.getString(c
                            .getColumnIndex("PRICE")));
                    cartItem.setQuantity(c.getString(c
                            .getColumnIndex("QUANTITY")));
                    cartItem.setProductImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    cartItem.setIsSaveLater(c.getString(c
                            .getColumnIndex("IS_SAVE_LATER")));
                    cartItem.setIsFavorite(c.getString(c
                            .getColumnIndex("IS_FAVORITE")));
                    cartItem.setIsDeletedApi(c.getString(c
                            .getColumnIndex("IS_DELETED_API")));
                    cartItem.setMd5(c.getString(c
                            .getColumnIndex("MDHASH")));
                    cartItemList.add(cartItem);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public ArrayList<CartItem> getCartItemSaveLater() {
        openConnection();
        String query = "SELECT * FROM CART_ITEM WHERE IS_SAVE_LATER = 'true'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    CartItem cartItem = new CartItem();
                    cartItem.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    cartItem.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    cartItem.setColorId(c.getString(c
                            .getColumnIndex("COLOR_ID")));
                    cartItem.setSizeId(c.getString(c
                            .getColumnIndex("SIZE_ID")));
                    cartItem.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    cartItem.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    cartItem.setColor(c.getString(c
                            .getColumnIndex("COLOR")));
                    cartItem.setSize(c.getString(c
                            .getColumnIndex("SIZE")));
                    cartItem.setPrice(c.getString(c
                            .getColumnIndex("PRICE")));
                    cartItem.setQuantity(c.getString(c
                            .getColumnIndex("QUANTITY")));
                    cartItem.setProductImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    cartItem.setIsSaveLater(c.getString(c
                            .getColumnIndex("IS_SAVE_LATER")));
                    cartItem.setIsFavorite(c.getString(c
                            .getColumnIndex("IS_FAVORITE")));
                    cartItem.setIsDeletedApi(c.getString(c
                            .getColumnIndex("IS_DELETED_API")));
                    cartItem.setMd5(c.getString(c
                            .getColumnIndex("MDHASH")));
                    cartItemList.add(cartItem);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public int getBadgeCount() {
        openConnection();
        Cursor cursor = db
                .rawQuery(
                        "SELECT PRODUCT_ID FROM CART_ITEM WHERE IS_SAVE_LATER = 'false'",
                        null);
        int badgeCount = cursor.getCount();
        cursor.close();
        closeConnection();
        return badgeCount;
    }

    public int getUniqueCartItem(String productId, String colorId, String sizeId) {
        openConnection();
        Cursor cursor = db
                .rawQuery(
                        "SELECT PRODUCT_ID FROM CART_ITEM WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'",
                        null);
        int badgeCount = cursor.getCount();
        cursor.close();
        closeConnection();
        return badgeCount;
    }

    public String getCartProductQuantity(String productId, String colorId, String sizeId) {
        String qty = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT QUANTITY FROM CART_ITEM WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            qty = String.valueOf(c.getString(c
                    .getColumnIndex("QUANTITY")));
            c.close();
            closeConnection();
            return qty;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getCartMD5(String productId, String colorId, String sizeId) {
        String colorID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT MDHASH FROM CART_ITEM WHERE PRODUCT_ID = "  + productId + " AND COLOR_ID = '"  + colorId + "' AND SIZE_ID = '" + sizeId + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            colorID = String.valueOf(c.getString(c
                    .getColumnIndex("MDHASH")));
            c.close();
            closeConnection();
            return colorID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getIsDeletedApi(String productId, String colorId, String sizeId) {
        String colorID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT IS_DELETED_API FROM CART_ITEM WHERE PRODUCT_ID = "  + productId + " AND COLOR_ID = '"  + colorId + "' AND SIZE_ID = '" + sizeId + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            colorID = String.valueOf(c.getString(c
                    .getColumnIndex("IS_DELETED_API")));
            c.close();
            closeConnection();
            return colorID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public void updateCartProductQuantity(String productId, String colorId, String sizeId, String quantity) {
        openConnection();
        String strSQL = "UPDATE CART_ITEM SET QUANTITY = " + quantity + " WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'";
        db.execSQL(strSQL);
        closeConnection();
    }

    public void updateSaveLater(String productId, String colorId, String sizeId, String isSave) {
        openConnection();
        String strSQL = "UPDATE CART_ITEM SET IS_SAVE_LATER = '" + isSave + "' WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'";
        db.execSQL(strSQL);
        closeConnection();
    }

    public void updateDeletedAPI(String productId, String colorId, String sizeId, String isDeletedApi) {
        openConnection();
        String strSQL = "UPDATE CART_ITEM SET IS_DELETED_API = '" + isDeletedApi + "' WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'";
        db.execSQL(strSQL);
        closeConnection();
    }

    public void updateFavorite(String productId, String colorId, String sizeId, String value) {
        openConnection();
        String strSQL = "UPDATE CART_ITEM SET IS_FAVORITE = '" + value + "' WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'";
        db.execSQL(strSQL);
        closeConnection();
    }

    public void updateMD5(String productId, String colorId, String sizeId, String md5) {
        openConnection();
        String strSQL = "UPDATE CART_ITEM SET MDHASH = '" + md5 + "' WHERE PRODUCT_ID = '" + productId + "' AND COLOR_ID = '" + colorId + "' AND SIZE_ID = '" + sizeId + "'";
        db.execSQL(strSQL);
        closeConnection();
    }

    public void insertHomeGroup(HomeGroup _homeGroup) {
        openConnection();
        try {
            String qryString = "Insert into HOME_GROUP(GROUP_ID, GROUP_TITLE) Values ('"
                    + _homeGroup.getGroupId()
                    + "', '"
                    + _homeGroup.getGroupTitle() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<HomeGroup> getHomeGroup() {
        openConnection();
        String query = "SELECT * FROM HOME_GROUP";
        Cursor c = db.rawQuery(query, null);

        ArrayList<HomeGroup> homeGroupList = new ArrayList<HomeGroup>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    HomeGroup homeGroup = new HomeGroup();
                    homeGroup.setGroupId(c.getString(c
                            .getColumnIndex("GROUP_ID")));
                    homeGroup.setGroupTitle(c.getString(c
                            .getColumnIndex("GROUP_TITLE")));
                    homeGroupList.add(homeGroup);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return homeGroupList;
    }

    public void insertHomeProduct(HomeProduct _homeProduct) {
        openConnection();
        try {
            String qryString = "Insert into HOME_PRODUCT(GROUP_ID, PRODUCT_ID, GROUP_TITLE, PRODUCT_TITLE, SELLING, ACTUAL, PERCENT, COLOR, IMAGE) Values ('"
                    + _homeProduct.getGroupId()
                    + "', '"
                    + _homeProduct.getProductId()
                    + "', '"
                    + _homeProduct.getGroupTitle()
                    + "', '"
                    + _homeProduct.getProductTitle()
                    + "', '"
                    + _homeProduct.getSelling_price()
                    + "', '"
                    + _homeProduct.getActual_price()
                    + "', '"
                    + _homeProduct.getDiscount_percent()
                    + "', '"
                    + _homeProduct.getColors()
                    + "', '"
                    + _homeProduct.getImage() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<HomeProduct> getHomeProductByGroupId(String groupId) {
        openConnection();
        String query = "SELECT * FROM HOME_PRODUCT WHERE GROUP_ID = " + groupId;
        Cursor c = db.rawQuery(query, null);

        ArrayList<HomeProduct> homeProductList = new ArrayList<HomeProduct>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    HomeProduct homeProduct = new HomeProduct();
                    homeProduct.setGroupId(c.getString(c
                            .getColumnIndex("GROUP_ID")));
                    homeProduct.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    homeProduct.setGroupTitle(c.getString(c
                            .getColumnIndex("GROUP_TITLE")));
                    homeProduct.setProductTitle(c.getString(c
                            .getColumnIndex("PRODUCT_TITLE")));
                    homeProduct.setSelling_price(c.getString(c
                            .getColumnIndex("SELLING")));
                    homeProduct.setActual_price(c.getString(c
                            .getColumnIndex("ACTUAL")));
                    homeProduct.setDiscount_percent(c.getString(c
                            .getColumnIndex("PERCENT")));
                    homeProduct.setColors(c.getString(c
                            .getColumnIndex("COLOR")));
                    homeProduct.setImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    homeProductList.add(homeProduct);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return homeProductList;
    }

    public void insertCategory(Category _category) {
        openConnection();
        try {
            String qryString = "Insert into CATEGORY(CATEGORY_ID, PARENT, NAME, IMAGE, TOTAL, PATH, ENABLED, POSITION, IS_HOME) Values ('"
                    + _category.getCategoryID()
                    + "', '"
                    + _category.getParent()
                    + "', '"
                    + _category.getCategoryName()
                    + "', '"
                    + _category.getCategoryImage()
                    + "', '"
                    + _category.getTotal()
                    + "', '"
                    + _category.getPath()
                    + "', '"
                    + _category.getEnabled()
                    + "', '"
                    + _category.getPosition()
                    + "', '"
                    + _category.getIsHome() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Category> getCategory() {
        openConnection();
        String query = "SELECT * FROM CATEGORY";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Category> cartItemList = new ArrayList<Category>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setCategoryID(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    category.setParent(c.getString(c
                            .getColumnIndex("PARENT")));
                    category.setCategoryName(c.getString(c
                            .getColumnIndex("NAME")));
                    category.setCategoryImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    category.setTotal(c.getString(c
                            .getColumnIndex("TOTAL")));
                    category.setPath(c.getString(c
                            .getColumnIndex("PATH")));
                    category.setEnabled(c.getString(c
                            .getColumnIndex("ENABLED")));
                    category.setPosition(c.getString(c
                            .getColumnIndex("POSITION")));
                    category.setIsHome(c.getString(c
                            .getColumnIndex("IS_HOME")));
                    cartItemList.add(category);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public ArrayList<Category> getCategoryForHome() {
        openConnection();
        String query = "SELECT * FROM CATEGORY WHERE IS_HOME = 'true'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Category> cartItemList = new ArrayList<Category>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setCategoryID(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    category.setParent(c.getString(c
                            .getColumnIndex("PARENT")));
                    category.setCategoryName(c.getString(c
                            .getColumnIndex("NAME")));
                    category.setCategoryImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    category.setTotal(c.getString(c
                            .getColumnIndex("TOTAL")));
                    category.setPath(c.getString(c
                            .getColumnIndex("PATH")));
                    category.setEnabled(c.getString(c
                            .getColumnIndex("ENABLED")));
                    category.setPosition(c.getString(c
                            .getColumnIndex("POSITION")));
                    category.setIsHome(c.getString(c
                            .getColumnIndex("IS_HOME")));
                    cartItemList.add(category);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public void updateCategoryHome(String categoryId) {
        openConnection();
        String strSQL = "UPDATE CATEGORY SET IS_HOME = 'true'" + " WHERE CATEGORY_ID = " + categoryId;
        db.execSQL(strSQL);
        closeConnection();
    }

    public int getCategoryCount(String categoryId) {
        openConnection();
        Cursor cursor = db
                .rawQuery(
                        "SELECT CATEGORY_ID FROM CATEGORY WHERE CATEGORY_ID = " + categoryId,
                        null);
        int badgeCount = cursor.getCount();
        cursor.close();
        closeConnection();
        return badgeCount;
    }

    public String getCategoryName(String categoryId) {
        String userID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT NAME FROM CATEGORY WHERE CATEGORY_ID = " + categoryId,
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            userID = String.valueOf(c.getString(c
                    .getColumnIndex("NAME")));
            c.close();
            closeConnection();
            return userID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public void insertProduct(Product _product) {
        openConnection();
        try {
            String qryString = "Insert into PRODUCT(CATEGORY_ID, PRODUCT_ID, PRODUCT_CODE, TITLE, SELLING_PRICE, ACTUAL_PRICE, DISCOUNT_PERCENT, PRODUCT_IMAGE, DESCRIPTION, SPECIFICATION, DELIVERY, WARRANTY, " +
                    "DEAL_TYPE, IS_SEARCH) Values ('"
                    + _product.getCategoryId()
                    + "', '"
                    + _product.getProductId()
                    + "', '"
                    + _product.getProductCode()
                    + "', '"
                    + _product.getTilte()
                    + "', '"
                    + _product.getSellingPrice()
                    + "', '"
                    + _product.getActualPrice()
                    + "', '"
                    + _product.getDiscountPercent()
                    + "', '"
                    + _product.getProductImage()
                    + "', '"
                    + _product.getDescription()
                    + "', '"
                    + _product.getSpecification()
                    + "', '"
                    + _product.getDelivery()
                    + "', '"
                    + _product.getWarranty()
                    + "', '"
                    + _product.getDeal_type()
                    + "', '"
                    + _product.getIsSearch() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Product> getAllProduct() {
        openConnection();
        String query = "SELECT * FROM PRODUCT";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Product> productList = new ArrayList<Product>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    product.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    product.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    product.setTilte(c.getString(c
                            .getColumnIndex("TITLE")));
                    product.setSellingPrice(c.getString(c
                            .getColumnIndex("SELLING_PRICE")));
                    product.setActualPrice(c.getString(c
                            .getColumnIndex("ACTUAL_PRICE")));
                    product.setDiscountPercent(c.getString(c
                            .getColumnIndex("DISCOUNT_PERCENT")));
                    product.setProductImage(c.getString(c
                            .getColumnIndex("PRODUCT_IMAGE")));
                    product.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    product.setSpecification(c.getString(c
                            .getColumnIndex("SPECIFICATION")));
                    product.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    product.setWarranty(c.getString(c
                            .getColumnIndex("WARRANTY")));
                    product.setDeal_type(c.getString(c
                            .getColumnIndex("DEAL_TYPE")));
                    product.setIsSearch(c.getString(c
                            .getColumnIndex("IS_SEARCH")));
                    productList.add(product);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return productList;
    }

    public ArrayList<Product> getAllSearchProduct() {
        openConnection();
        String query = "SELECT * FROM PRODUCT WHERE IS_SEARCH == 'true'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Product> productList = new ArrayList<Product>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    product.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    product.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    product.setTilte(c.getString(c
                            .getColumnIndex("TITLE")));
                    product.setSellingPrice(c.getString(c
                            .getColumnIndex("SELLING_PRICE")));
                    product.setActualPrice(c.getString(c
                            .getColumnIndex("ACTUAL_PRICE")));
                    product.setDiscountPercent(c.getString(c
                            .getColumnIndex("DISCOUNT_PERCENT")));
                    product.setProductImage(c.getString(c
                            .getColumnIndex("PRODUCT_IMAGE")));
                    product.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    product.setSpecification(c.getString(c
                            .getColumnIndex("SPECIFICATION")));
                    product.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    product.setWarranty(c.getString(c
                            .getColumnIndex("WARRANTY")));
                    product.setDeal_type(c.getString(c
                            .getColumnIndex("DEAL_TYPE")));
                    product.setIsSearch(c.getString(c
                            .getColumnIndex("IS_SEARCH")));
                    productList.add(product);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return productList;
    }

    public ArrayList<Product> getSearchProduct(String searchText) {
        openConnection();
        String query = "SELECT * FROM PRODUCT WHERE TITLE LIKE '%" + searchText + "%'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Product> productList = new ArrayList<Product>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    product.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    product.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    product.setTilte(c.getString(c
                            .getColumnIndex("TITLE")));
                    product.setSellingPrice(c.getString(c
                            .getColumnIndex("SELLING_PRICE")));
                    product.setActualPrice(c.getString(c
                            .getColumnIndex("ACTUAL_PRICE")));
                    product.setDiscountPercent(c.getString(c
                            .getColumnIndex("DISCOUNT_PERCENT")));
                    product.setProductImage(c.getString(c
                            .getColumnIndex("PRODUCT_IMAGE")));
                    product.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    product.setSpecification(c.getString(c
                            .getColumnIndex("SPECIFICATION")));
                    product.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    product.setWarranty(c.getString(c
                            .getColumnIndex("WARRANTY")));
                    product.setDeal_type(c.getString(c
                            .getColumnIndex("DEAL_TYPE")));
                    product.setIsSearch(c.getString(c
                            .getColumnIndex("IS_SEARCH")));
                    productList.add(product);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return productList;
    }

    public ArrayList<Product> getProduct(String categoryID) {
        openConnection();
        String query = "SELECT * FROM PRODUCT WHERE CATEGORY_ID = " + categoryID;
        Cursor c = db.rawQuery(query, null);

        ArrayList<Product> productList = new ArrayList<Product>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    product.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    product.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    product.setTilte(c.getString(c
                            .getColumnIndex("TITLE")));
                    product.setSellingPrice(c.getString(c
                            .getColumnIndex("SELLING_PRICE")));
                    product.setActualPrice(c.getString(c
                            .getColumnIndex("ACTUAL_PRICE")));
                    product.setDiscountPercent(c.getString(c
                            .getColumnIndex("DISCOUNT_PERCENT")));
                    product.setProductImage(c.getString(c
                            .getColumnIndex("PRODUCT_IMAGE")));
                    product.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    product.setSpecification(c.getString(c
                            .getColumnIndex("SPECIFICATION")));
                    product.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    product.setWarranty(c.getString(c
                            .getColumnIndex("WARRANTY")));
                    product.setDeal_type(c.getString(c
                            .getColumnIndex("DEAL_TYPE")));
                    product.setIsSearch(c.getString(c
                            .getColumnIndex("IS_SEARCH")));
                    productList.add(product);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return productList;
    }

    public ArrayList<Product> getProduct(String categoryID, String search) {
        openConnection();
        String query = "SELECT * FROM PRODUCT WHERE CATEGORY_ID = " + categoryID + " AND IS_SEARCH == 'true'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Product> productList = new ArrayList<Product>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    product.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    product.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    product.setTilte(c.getString(c
                            .getColumnIndex("TITLE")));
                    product.setSellingPrice(c.getString(c
                            .getColumnIndex("SELLING_PRICE")));
                    product.setActualPrice(c.getString(c
                            .getColumnIndex("ACTUAL_PRICE")));
                    product.setDiscountPercent(c.getString(c
                            .getColumnIndex("DISCOUNT_PERCENT")));
                    product.setProductImage(c.getString(c
                            .getColumnIndex("PRODUCT_IMAGE")));
                    product.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    product.setSpecification(c.getString(c
                            .getColumnIndex("SPECIFICATION")));
                    product.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    product.setWarranty(c.getString(c
                            .getColumnIndex("WARRANTY")));
                    product.setDeal_type(c.getString(c
                            .getColumnIndex("DEAL_TYPE")));
                    product.setIsSearch(c.getString(c
                            .getColumnIndex("IS_SEARCH")));
                    productList.add(product);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return productList;
    }

    public Product getSingleProduct (String categoryId, String productId) {
        openConnection();
        String query = "SELECT * FROM PRODUCT WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = " + productId;
        Cursor c = db.rawQuery(query, null);

        Product product = new Product();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                product.setCategoryId(c.getString(c
                        .getColumnIndex("CATEGORY_ID")));
                product.setProductId(c.getString(c
                        .getColumnIndex("PRODUCT_ID")));
                product.setProductCode(c.getString(c
                        .getColumnIndex("PRODUCT_CODE")));
                product.setTilte(c.getString(c
                        .getColumnIndex("TITLE")));
                product.setSellingPrice(c.getString(c
                        .getColumnIndex("SELLING_PRICE")));
                product.setActualPrice(c.getString(c
                        .getColumnIndex("ACTUAL_PRICE")));
                product.setDiscountPercent(c.getString(c
                        .getColumnIndex("DISCOUNT_PERCENT")));
                product.setProductImage(c.getString(c
                        .getColumnIndex("PRODUCT_IMAGE")));
                product.setDescription(c.getString(c
                        .getColumnIndex("DESCRIPTION")));
                product.setSpecification(c.getString(c
                        .getColumnIndex("SPECIFICATION")));
                product.setDelivery(c.getString(c
                        .getColumnIndex("DELIVERY")));
                product.setWarranty(c.getString(c
                        .getColumnIndex("WARRANTY")));
                product.setDeal_type(c.getString(c
                        .getColumnIndex("DEAL_TYPE")));
                product.setIsSearch(c.getString(c
                        .getColumnIndex("IS_SEARCH")));
            }
            c.close();
            closeConnection();
            return product;
        } else {
            c.close();
            closeConnection();
            return null;
        }
    }

    public String getCategoryId(String productId) {
        String userID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT CATEGORY_ID FROM PRODUCT WHERE PRODUCT_ID = " + productId,
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            userID = String.valueOf(c.getString(c
                    .getColumnIndex("CATEGORY_ID")));
            c.close();
            closeConnection();
            return userID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public void updateSearchProduct() {
        openConnection();
        String strSQL = "UPDATE PRODUCT SET IS_SEARCH = 'false'";
        db.execSQL(strSQL);
        closeConnection();
    }

    public void insertColor(Color _color) {
        openConnection();
        try {
            String qryString = "Insert into COLOR(CATEGORY_ID, PRODUCT_ID, COLOR_ID, COLOR_NAME, COLOR_IMAGE) Values ('"
                    + _color.getCategoryId()
                    + "', '"
                    + _color.getProductId()
                    + "', '"
                    + _color.getColorId()
                    + "', '"
                    + _color.getColorName()
                    + "', '"
                    + _color.getColorImage() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Color> getColors(String categoryId, String productId) {
        openConnection();
        String query = "SELECT * FROM COLOR WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = " + productId;
        Cursor c = db.rawQuery(query, null);

        ArrayList<Color> colorList = new ArrayList<Color>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Color color = new Color();
                    color.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    color.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    color.setColorId(c.getString(c
                            .getColumnIndex("COLOR_ID")));
                    color.setColorName(c.getString(c
                            .getColumnIndex("COLOR_NAME")));
                    color.setColorImage(c.getString(c
                            .getColumnIndex("COLOR_IMAGE")));
                    colorList.add(color);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return colorList;
    }

    public String getColorID(String categoryId, String productId, String colorName) {
        String colorID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT COLOR_ID FROM COLOR WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = "  + productId + " AND COLOR_NAME = '"  + colorName + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            colorID = String.valueOf(c.getString(c
                    .getColumnIndex("COLOR_ID")));
            c.close();
            closeConnection();
            return colorID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getColorID(String productId, String colorName) {
        String colorID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT COLOR_ID FROM COLOR WHERE PRODUCT_ID = "  + productId + " AND COLOR_NAME = '"  + colorName + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            colorID = String.valueOf(c.getString(c
                    .getColumnIndex("COLOR_ID")));
            c.close();
            closeConnection();
            return colorID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public ArrayList<FilterChild> getDistinctColorName() {
        openConnection();
        String query = "SELECT DISTINCT COLOR_NAME FROM COLOR";
        Cursor c = db.rawQuery(query, null);

        ArrayList<FilterChild> colorNameList = new ArrayList<FilterChild>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    FilterChild filterChild = new FilterChild();
                    filterChild.setId("");
                    filterChild.setTitle(c.getString(c
                            .getColumnIndex("COLOR_NAME")));
                    filterChild.setIsSelected("false");
                    colorNameList.add(filterChild);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return colorNameList;
    }

    public int getProductCountByColorID(String productId, String colorId) {
        openConnection();
        Cursor cursor = db
                .rawQuery(
                        "SELECT PRODUCT_ID FROM COLOR WHERE PRODUCT_ID = " + productId + " AND COLOR_ID = " + colorId,
                        null);
        int badgeCount = cursor.getCount();
        cursor.close();
        closeConnection();
        return badgeCount;
    }

    public void insertSize(Size _size) {
        openConnection();
        try {
            String qryString = "Insert into SIZE(CATEGORY_ID, PRODUCT_ID, COLOR_ID, SIZE_ID, SIZE, QUANTITY, SELLING, ACTUAL, PERCENT, MODEL, WEIGHT, SIZE_IMAGE) Values ('"
                    + _size.getCategoryId()
                    + "', '"
                    + _size.getProductId()
                    + "', '"
                    + _size.getColorId()
                    + "', '"
                    + _size.getSizeId()
                    + "', '"
                    + _size.getSize()
                    + "', '"
                    + _size.getQuantity()
                    + "', '"
                    + _size.getSellingPrice()
                    + "', '"
                    + _size.getActualPrice()
                    + "', '"
                    + _size.getDiscountPercent()
                    + "', '"
                    + _size.getModel()
                    + "', '"
                    + _size.getWeight()
                    + "', '"
                    + _size.getSizeImage() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Size> getSizes(String categoryId, String productId, String colorID, boolean allSizes) {
        openConnection();
        String query = "";
        if (allSizes) {
            query = "SELECT * FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = " + productId;
        } else {
            query = "SELECT * FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = " + productId + " AND COLOR_ID = " + colorID;
        }
        Cursor c = db.rawQuery(query, null);

        ArrayList<Size> sizeList = new ArrayList<Size>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Size size = new Size();
                    size.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    size.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    size.setColorId(c.getString(c
                            .getColumnIndex("COLOR_ID")));
                    size.setSizeId(c.getString(c
                            .getColumnIndex("SIZE_ID")));
                    size.setSize(c.getString(c
                            .getColumnIndex("SIZE")));
                    size.setQuantity(c.getString(c
                            .getColumnIndex("QUANTITY")));
                    size.setSellingPrice(c.getString(c
                            .getColumnIndex("SELLING")));
                    size.setActualPrice(c.getString(c
                            .getColumnIndex("ACTUAL")));
                    size.setDiscountPercent(c.getString(c
                            .getColumnIndex("PERCENT")));
                    size.setModel(c.getString(c
                            .getColumnIndex("MODEL")));
                    size.setWeight(c.getString(c
                            .getColumnIndex("WEIGHT")));
                    size.setSizeImage(c.getString(c
                            .getColumnIndex("SIZE_IMAGE")));
                    sizeList.add(size);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return sizeList;
    }

    public String getSizeImage(String categoryId, String productId, String colorId, String Model) {
        String percent = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT SIZE_IMAGE FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = "  + productId + " AND COLOR_ID = "  + colorId + " AND MODEL = '"  + Model + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            percent = String.valueOf(c.getString(c
                    .getColumnIndex("SIZE_IMAGE")));
            c.close();
            closeConnection();
            return percent;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getSizeID(String categoryId, String productId, String colorId, String Model) {
        String percent = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT SIZE_ID FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = "  + productId + " AND COLOR_ID = "  + colorId + " AND MODEL = '"  + Model + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            percent = String.valueOf(c.getString(c
                    .getColumnIndex("SIZE_ID")));
            c.close();
            closeConnection();
            return percent;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getSizeID(String productId, String Model) {
        String percent = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT SIZE_ID FROM SIZE WHERE PRODUCT_ID = "  + productId + " AND MODEL = '"  + Model + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            percent = String.valueOf(c.getString(c
                    .getColumnIndex("SIZE_ID")));
            c.close();
            closeConnection();
            return percent;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public int getProductCountBySizeID(String productId, String sizeId) {
        openConnection();
        Cursor cursor = db
                .rawQuery(
                        "SELECT PRODUCT_ID FROM SIZE WHERE PRODUCT_ID = " + productId + " AND SIZE_ID = " + sizeId,
                        null);
        int badgeCount = cursor.getCount();
        cursor.close();
        closeConnection();
        return badgeCount;
    }

    public ArrayList<FilterChild> getDistinctSizeName() {
        openConnection();
        String query = "SELECT DISTINCT MODEL FROM SIZE";
        Cursor c = db.rawQuery(query, null);

        ArrayList<FilterChild> sizeList = new ArrayList<FilterChild>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    FilterChild filterChild = new FilterChild();
                    filterChild.setId("");
                    filterChild.setTitle(c.getString(c
                            .getColumnIndex("MODEL")));
                    filterChild.setIsSelected("false");
                    sizeList.add(filterChild);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return sizeList;
    }

    public String getSizePercent(String categoryId, String productId, String colorId, String sizeId) {
        String percent = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT PERCENT FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = "  + productId + " AND COLOR_ID = "  + colorId + " AND SIZE_ID = "  + sizeId,
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            percent = String.valueOf(c.getString(c
                    .getColumnIndex("PERCENT")));
            c.close();
            closeConnection();
            return percent;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getSizeActualPrice(String categoryId, String productId, String colorId, String sizeId) {
        String actualPrice = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT ACTUAL FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = "  + productId + " AND COLOR_ID = "  + colorId + " AND SIZE_ID = "  + sizeId,
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            actualPrice = String.valueOf(c.getString(c
                    .getColumnIndex("ACTUAL")));
            c.close();
            closeConnection();
            return actualPrice;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public String getSizeSellingPrice(String categoryId, String productId, String colorId, String sizeId) {
        String sellingPrice = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT SELLING FROM SIZE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = "  + productId + " AND COLOR_ID = "  + colorId + " AND SIZE_ID = "  + sizeId,
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            sellingPrice = String.valueOf(c.getString(c
                    .getColumnIndex("SELLING")));
            c.close();
            closeConnection();
            return sellingPrice;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public void insertProductImage(ProductImage _productImage) {
        openConnection();
        try {
            String qryString = "Insert into SLIDING_IMAGE(CATEGORY_ID, PRODUCT_ID, IMAGE_ID, PRODUCT_IMAGE) Values ('"
                    + _productImage.getCategoryId()
                    + "', '"
                    + _productImage.getProductId()
                    + "', '"
                    + _productImage.getProductImageId()
                    + "', '"
                    + _productImage.getProductImage() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<ProductImage> getProductImage(String categoryId, String productId) {
        openConnection();
        String query = "SELECT * FROM SLIDING_IMAGE WHERE CATEGORY_ID = " + categoryId + " AND PRODUCT_ID = " + productId;
        Cursor c = db.rawQuery(query, null);

        ArrayList<ProductImage> productImageList = new ArrayList<ProductImage>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    ProductImage productImage = new ProductImage();
                    productImage.setCategoryId(c.getString(c
                            .getColumnIndex("CATEGORY_ID")));
                    productImage.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    productImage.setProductImageId(c.getString(c
                            .getColumnIndex("IMAGE_ID")));
                    productImage.setProductImage(c.getString(c
                            .getColumnIndex("PRODUCT_IMAGE")));
                    productImageList.add(productImage);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return productImageList;
    }

    public void insertBannerImage(Banner _banner) {
        openConnection();
        try {
            String qryString = "Insert into BANNER_IMAGE(ID, TITLE, DESCRIPTION, IMAGE, BANNER, LOCATION, ENABLED, POSITION) Values ('"
                    + _banner.getId()
                    + "', '"
                    + _banner.getTitle()
                    + "', '"
                    + _banner.getDescription()
                    + "', '"
                    + _banner.getImage()
                    + "', '"
                    + _banner.getBanner()
                    + "', '"
                    + _banner.getLocation()
                    + "', '"
                    + _banner.getEnabled()
                    + "', '"
                    + _banner.getPosition() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Banner> getBannerImage() {
        openConnection();
        String query = "SELECT * FROM BANNER_IMAGE";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Banner> bannerList = new ArrayList<Banner>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Banner banner = new Banner();
                    banner.setId(c.getString(c
                            .getColumnIndex("ID")));
                    banner.setTitle(c.getString(c
                            .getColumnIndex("TITLE")));
                    banner.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    banner.setImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    banner.setBanner(c.getString(c
                            .getColumnIndex("BANNER")));
                    banner.setLocation(c.getString(c
                            .getColumnIndex("LOCATION")));
                    banner.setEnabled(c.getString(c
                            .getColumnIndex("ENABLED")));
                    banner.setPosition(c.getString(c
                            .getColumnIndex("POSITION")));
                    bannerList.add(banner);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return bannerList;
    }

    public ArrayList<Banner> getTopBanner() {
        openConnection();
        String query = "SELECT * FROM BANNER_IMAGE WHERE LOCATION = 'slider'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Banner> bannerList = new ArrayList<Banner>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Banner banner = new Banner();
                    banner.setId(c.getString(c
                            .getColumnIndex("ID")));
                    banner.setTitle(c.getString(c
                            .getColumnIndex("TITLE")));
                    banner.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    banner.setImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    banner.setBanner(c.getString(c
                            .getColumnIndex("BANNER")));
                    banner.setLocation(c.getString(c
                            .getColumnIndex("LOCATION")));
                    banner.setEnabled(c.getString(c
                            .getColumnIndex("ENABLED")));
                    banner.setPosition(c.getString(c
                            .getColumnIndex("POSITION")));
                    bannerList.add(banner);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return bannerList;
    }

    public void insertAbout(AboutGroup _aboutGroup) {
        openConnection();
        try {
            String qryString = "Insert into ABOUT(ABOUT_ID, TITLE, DESCRIPTION, LAST_SAVED) Values ('"
                    + _aboutGroup.getId()
                    + "', '"
                    + _aboutGroup.getHeading()
                    + "', '"
                    + _aboutGroup.getExplanation()
                    + "', '"
                    + _aboutGroup.getLast_saved() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<AboutGroup> getAbout() {
        openConnection();
        String query = "SELECT * FROM ABOUT";
        Cursor c = db.rawQuery(query, null);

        ArrayList<AboutGroup> aboutGroupList = new ArrayList<AboutGroup>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    AboutGroup aboutGroup = new AboutGroup();
                    aboutGroup.setId(c.getString(c
                            .getColumnIndex("ABOUT_ID")));
                    aboutGroup.setHeading(c.getString(c
                            .getColumnIndex("TITLE")));
                    aboutGroup.setExplanation(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    aboutGroup.setLast_saved(c.getString(c
                            .getColumnIndex("LAST_SAVED")));
                    aboutGroupList.add(aboutGroup);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return aboutGroupList;
    }

    public void insertAddress(Address address) {
        openConnection();
        try {
            String qryString = "Insert into ADDRESS(ID, NAME, STREET, BUILDING, APARTMENT, FLOOR, LANDMARK, PHONE, MOBILE, AREA, LOCATION, DELIVERY, NOTE, CITY, COUNTRY, ENABLED) Values ('"
                    + address.getId()
                    + "', '"
                    + address.getFullname()
                    + "', '"
                    + address.getStreet()
                    + "', '"
                    + address.getBuilding()
                    + "', '"
                    + address.getApartment()
                    + "', '"
                    + address.getFloor()
                    + "', '"
                    + address.getLandmark()
                    + "', '"
                    + address.getPhone()
                    + "', '"
                    + address.getMobile()
                    + "', '"
                    + address.getArea()
                    + "', '"
                    + address.getLocation()
                    + "', '"
                    + address.getDelivery()
                    + "', '"
                    + address.getNote()
                    + "', '"
                    + address.getCity()
                    + "', '"
                    + address.getCountry()
                    + "', '"
                    + address.getEnable() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Address> getAddress() {
        openConnection();
        String query = "SELECT * FROM ADDRESS";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Address> cartItemList = new ArrayList<Address>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Address address = new Address();
                    address.setId(c.getString(c
                            .getColumnIndex("ID")));
                    address.setFullname(c.getString(c
                            .getColumnIndex("NAME")));
                    address.setStreet(c.getString(c
                            .getColumnIndex("STREET")));
                    address.setBuilding(c.getString(c
                            .getColumnIndex("BUILDING")));
                    address.setApartment(c.getString(c
                            .getColumnIndex("APARTMENT")));
                    address.setFloor(c.getString(c
                            .getColumnIndex("FLOOR")));
                    address.setLandmark(c.getString(c
                            .getColumnIndex("LANDMARK")));
                    address.setPhone(c.getString(c
                            .getColumnIndex("PHONE")));
                    address.setMobile(c.getString(c
                            .getColumnIndex("MOBILE")));
                    address.setArea(c.getString(c
                            .getColumnIndex("AREA")));
                    address.setLocation(c.getString(c
                            .getColumnIndex("LOCATION")));
                    address.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    address.setNote(c.getString(c
                            .getColumnIndex("NOTE")));
                    address.setCity(c.getString(c
                            .getColumnIndex("CITY")));
                    address.setCountry(c.getString(c
                            .getColumnIndex("COUNTRY")));
                    address.setEnable(c.getString(c
                            .getColumnIndex("ENABLED")));
                    cartItemList.add(address);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public ArrayList<Address> getEnabledAddress() {
        openConnection();
        String query = "SELECT * FROM ADDRESS WHERE ENABLED = 'Yes'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Address> cartItemList = new ArrayList<Address>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Address address = new Address();
                    address.setId(c.getString(c
                            .getColumnIndex("ID")));
                    address.setFullname(c.getString(c
                            .getColumnIndex("NAME")));
                    address.setStreet(c.getString(c
                            .getColumnIndex("STREET")));
                    address.setBuilding(c.getString(c
                            .getColumnIndex("BUILDING")));
                    address.setApartment(c.getString(c
                            .getColumnIndex("APARTMENT")));
                    address.setFloor(c.getString(c
                            .getColumnIndex("FLOOR")));
                    address.setLandmark(c.getString(c
                            .getColumnIndex("LANDMARK")));
                    address.setPhone(c.getString(c
                            .getColumnIndex("PHONE")));
                    address.setMobile(c.getString(c
                            .getColumnIndex("MOBILE")));
                    address.setArea(c.getString(c
                            .getColumnIndex("AREA")));
                    address.setLocation(c.getString(c
                            .getColumnIndex("LOCATION")));
                    address.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    address.setNote(c.getString(c
                            .getColumnIndex("NOTE")));
                    address.setCity(c.getString(c
                            .getColumnIndex("CITY")));
                    address.setCountry(c.getString(c
                            .getColumnIndex("COUNTRY")));
                    address.setEnable(c.getString(c
                            .getColumnIndex("ENABLED")));
                    cartItemList.add(address);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cartItemList;
    }

    public Address getAddressById(String addressId) {
        openConnection();
        String query = "SELECT * FROM ADDRESS WHERE ID = " + addressId;
        Cursor c = db.rawQuery(query, null);

        Address address = new Address();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                    address.setId(c.getString(c
                            .getColumnIndex("ID")));
                    address.setFullname(c.getString(c
                            .getColumnIndex("NAME")));
                    address.setStreet(c.getString(c
                            .getColumnIndex("STREET")));
                    address.setBuilding(c.getString(c
                            .getColumnIndex("BUILDING")));
                    address.setApartment(c.getString(c
                            .getColumnIndex("APARTMENT")));
                    address.setFloor(c.getString(c
                            .getColumnIndex("FLOOR")));
                    address.setLandmark(c.getString(c
                            .getColumnIndex("LANDMARK")));
                    address.setPhone(c.getString(c
                            .getColumnIndex("PHONE")));
                    address.setMobile(c.getString(c
                            .getColumnIndex("MOBILE")));
                    address.setArea(c.getString(c
                            .getColumnIndex("AREA")));
                    address.setLocation(c.getString(c
                            .getColumnIndex("LOCATION")));
                    address.setDelivery(c.getString(c
                            .getColumnIndex("DELIVERY")));
                    address.setNote(c.getString(c
                            .getColumnIndex("NOTE")));
                    address.setCity(c.getString(c
                            .getColumnIndex("CITY")));
                    address.setCountry(c.getString(c
                            .getColumnIndex("COUNTRY")));
                    address.setEnable(c.getString(c
                            .getColumnIndex("ENABLED")));
            }
            c.close();
            closeConnection();
            return address;
        } else {
            c.close();
            closeConnection();
            return null;
        }
    }

    public String getAreaByAddressId(String addressID) {
        String actualPrice = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT AREA FROM ADDRESS WHERE ID = '" + addressID + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            actualPrice = String.valueOf(c.getString(c
                    .getColumnIndex("AREA")));
            c.close();
            closeConnection();
            return actualPrice;
        } else {
            c.close();
            closeConnection();
            return "";
        }
    }

    public void insertCity(City city) {
        openConnection();
        try {
            String qryString = "Insert into CITY(ID, NAME, POSITION, ENABLED) Values ('"
                    + city.getId()
                    + "', '"
                    + city.getName()
                    + "', '"
                    + city.getPosition()
                    + "', '"
                    + city.getEnabled() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<City> getCity() {
        openConnection();
        String query = "SELECT * FROM CITY";
        Cursor c = db.rawQuery(query, null);

        ArrayList<City> cityList = new ArrayList<City>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    City city = new City();
                    city.setId(c.getString(c
                            .getColumnIndex("ID")));
                    city.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    city.setPosition(c.getString(c
                            .getColumnIndex("POSITION")));
                    city.setEnabled(c.getString(c
                            .getColumnIndex("ENABLED")));
                    cityList.add(city);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return cityList;
    }

    public void insertArea(Area area) {
        openConnection();
        try {
            String qryString = "Insert into AREA(ID, NAME, SHIP_RATE, CITY_ID) Values ('"
                    + area.getId()
                    + "', '"
                    + area.getName()
                    + "', '"
                    + area.getShipRate()
                    + "', '"
                    + area.getCityId() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Area> getArea(String cityID) {
        openConnection();
        String query = "SELECT * FROM AREA WHERE CITY_ID = " + cityID;
        Cursor c = db.rawQuery(query, null);

        ArrayList<Area> areaList = new ArrayList<Area>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Area area = new Area();
                    area.setId(c.getString(c
                            .getColumnIndex("ID")));
                    area.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    area.setShipRate(c.getString(c
                            .getColumnIndex("SHIP_RATE")));
                    area.setCityId(c.getString(c
                            .getColumnIndex("CITY_ID")));
                    areaList.add(area);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return areaList;
    }

    public String getShipmentRate(String areaName) {
        String actualPrice = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT SHIP_RATE FROM AREA WHERE NAME = '" + areaName + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            actualPrice = String.valueOf(c.getString(c
                    .getColumnIndex("SHIP_RATE")));
            c.close();
            closeConnection();
            return actualPrice;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public void insertNationality(Nationality nationality) {
        openConnection();
        try {
            String qryString = "Insert into NATIONALITY(ID, NAME) Values ('"
                    + nationality.getId()
                    + "', '"
                    + nationality.getName() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<Nationality> getNationality() {
        openConnection();
        String query = "SELECT * FROM NATIONALITY";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Nationality> nationalityList = new ArrayList<Nationality>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Nationality nationality = new Nationality();
                    nationality.setId(c.getString(c
                            .getColumnIndex("ID")));
                    nationality.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    nationalityList.add(nationality);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return nationalityList;
    }

    public void insertOrderGroup(OrderGroup orderGroup) {
        openConnection();
        try {
            String qryString = "Insert into ORDER_GROUP(ORDER_NO, TOTAL, DATE, ADDRESS, ADDRESS_NAME, ADDRESS_ID, STATUS, PAYMENT_STATUS, LEVEL) Values ('"
                    + orderGroup.getNo()
                    + "', '"
                    + orderGroup.getTotal()
                    + "', '"
                    + orderGroup.getDate()
                    + "', '"
                    + orderGroup.getAddress()
                    + "', '"
                    + orderGroup.getAddressName()
                    + "', '"
                    + orderGroup.getAddressID()
                    + "', '"
                    + orderGroup.getStatus()
                    + "', '"
                    + orderGroup.getPaymentStatus()
                    + "', '"
                    + orderGroup.getLevelCompleted() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<OrderGroup> getOrderGroup() {
        openConnection();
        String query = "SELECT * FROM ORDER_GROUP";
        Cursor c = db.rawQuery(query, null);

        ArrayList<OrderGroup> orderGroupList = new ArrayList<OrderGroup>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    OrderGroup orderGroup = new OrderGroup();
                    orderGroup.setNo(c.getString(c
                            .getColumnIndex("ORDER_NO")));
                    orderGroup.setTotal(c.getString(c
                            .getColumnIndex("TOTAL")));
                    orderGroup.setDate(c.getString(c
                            .getColumnIndex("DATE")));
                    orderGroup.setAddress(c.getString(c
                            .getColumnIndex("ADDRESS")));
                    orderGroup.setAddressName(c.getString(c
                            .getColumnIndex("ADDRESS_NAME")));
                    orderGroup.setAddressID(c.getString(c
                            .getColumnIndex("ADDRESS_ID")));
                    orderGroup.setStatus(c.getString(c
                            .getColumnIndex("STATUS")));
                    orderGroup.setPaymentStatus(c.getString(c
                            .getColumnIndex("PAYMENT_STATUS")));
                    orderGroup.setLevelCompleted(c.getString(c
                            .getColumnIndex("LEVEL")));
                    orderGroupList.add(orderGroup);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return orderGroupList;
    }

    public OrderGroup getOrderGroup(String orderId) {
        openConnection();
        String query = "SELECT * FROM ORDER_GROUP WHERE ORDER_NO = '" + orderId + "'";
        Cursor c = db.rawQuery(query, null);

        OrderGroup orderGroup = new OrderGroup();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                    orderGroup.setNo(c.getString(c
                            .getColumnIndex("ORDER_NO")));
                    orderGroup.setTotal(c.getString(c
                            .getColumnIndex("TOTAL")));
                    orderGroup.setDate(c.getString(c
                            .getColumnIndex("DATE")));
                    orderGroup.setAddress(c.getString(c
                            .getColumnIndex("ADDRESS")));
                    orderGroup.setAddressName(c.getString(c
                            .getColumnIndex("ADDRESS_NAME")));
                    orderGroup.setAddressID(c.getString(c
                            .getColumnIndex("ADDRESS_ID")));
                    orderGroup.setStatus(c.getString(c
                            .getColumnIndex("STATUS")));
                    orderGroup.setPaymentStatus(c.getString(c
                            .getColumnIndex("PAYMENT_STATUS")));
                    orderGroup.setLevelCompleted(c.getString(c
                            .getColumnIndex("LEVEL")));
            }
            c.close();
            closeConnection();
            return orderGroup;
        } else {
            c.close();
            closeConnection();
            return null;
        }
    }

    public void insertOrderProducts(OrderProduct orderProduct) {
        openConnection();
        try {
            String qryString = "Insert into ORDER_PRODUCT(ORDER_NO, PRODUCT_ID, PRODUCT_CODE, NAME, COLOR, SIZE, QUANTITY, PRICE, SUB_TOTAL, IMAGE) Values ('"
                    + orderProduct.getNo()
                    + "', '"
                    + orderProduct.getProductId()
                    + "', '"
                    + orderProduct.getProductCode()
                    + "', '"
                    + orderProduct.getName()
                    + "', '"
                    + orderProduct.getColor()
                    + "', '"
                    + orderProduct.getSize()
                    + "', '"
                    + orderProduct.getQty()
                    + "', '"
                    + orderProduct.getPrice()
                    + "', '"
                    + orderProduct.getSubTotal()
                    + "', '"
                    + orderProduct.getImage() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public ArrayList<OrderProduct> getOrderProducts(String orderNo) {
        openConnection();
        String query = "SELECT * FROM ORDER_PRODUCT WHERE ORDER_NO = '" + orderNo + "'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<OrderProduct> orderProductList = new ArrayList<OrderProduct>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setNo(c.getString(c
                            .getColumnIndex("ORDER_NO")));
                    orderProduct.setProductId(c.getString(c
                            .getColumnIndex("PRODUCT_ID")));
                    orderProduct.setProductCode(c.getString(c
                            .getColumnIndex("PRODUCT_CODE")));
                    orderProduct.setName(c.getString(c
                            .getColumnIndex("NAME")));
                    orderProduct.setColor(c.getString(c
                            .getColumnIndex("COLOR")));
                    orderProduct.setSize(c.getString(c
                            .getColumnIndex("SIZE")));
                    orderProduct.setQty(c.getString(c
                            .getColumnIndex("QUANTITY")));
                    orderProduct.setPrice(c.getString(c
                            .getColumnIndex("PRICE")));
                    orderProduct.setSubTotal(c.getString(c
                            .getColumnIndex("SUB_TOTAL")));
                    orderProduct.setImage(c.getString(c
                            .getColumnIndex("IMAGE")));
                    orderProductList.add(orderProduct);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return orderProductList;
    }

    public void insertFaq(Faq faq) {
        openConnection();
        try {
            String qryString = "Insert into FAQ(GROUP_ID, GROUP_NAME, GROUP_TOTAL, CHILD_ID, TITLE, DESCRIPTION, FAQ_ORDER) Values ('"
                    + faq.getGroupID()
                    + "', '"
                    + faq.getGroupName()
                    + "', '"
                    + faq.getGroupTotal()
                    + "', '"
                    + faq.getChildId()
                    + "', '"
                    + faq.getTitle()
                    + "', '"
                    + faq.getDescription()
                    + "', '"
                    + faq.getOrder() + "')";
            db.execSQL(qryString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public String getFaqName(String faqGroupId) {
        String userID = "";
        openConnection();
        Cursor c = db
                .rawQuery(
                        "SELECT GROUP_NAME FROM FAQ WHERE GROUP_ID = '" + faqGroupId + "'",
                        null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            userID = String.valueOf(c.getString(c
                    .getColumnIndex("GROUP_NAME")));
            c.close();
            closeConnection();
            return userID;
        } else {
            c.close();
            closeConnection();
            return "0";
        }
    }

    public ArrayList<Faq> getUniqueFaqID() {
        openConnection();
        String query = "SELECT DISTINCT GROUP_ID FROM FAQ";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Faq> faqList = new ArrayList<Faq>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Faq faq = new Faq();
                    faq.setGroupID(c.getString(c
                            .getColumnIndex("GROUP_ID")));
                    faqList.add(faq);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return faqList;
    }

    public ArrayList<Faq> getFaq(String groupId) {
        openConnection();
        String query = "SELECT * FROM FAQ WHERE GROUP_ID = '" + groupId + "'";
        Cursor c = db.rawQuery(query, null);

        ArrayList<Faq> faqList = new ArrayList<Faq>();

        if ((c != null) && (c.getCount() > 0)) {
            if (c.moveToFirst()) {
                do {
                    Faq faq = new Faq();
                    faq.setGroupID(c.getString(c
                            .getColumnIndex("GROUP_ID")));
                    faq.setGroupName(c.getString(c
                            .getColumnIndex("GROUP_NAME")));
                    faq.setGroupTotal(c.getString(c
                            .getColumnIndex("GROUP_TOTAL")));
                    faq.setChildId(c.getString(c
                            .getColumnIndex("CHILD_ID")));
                    faq.setTitle(c.getString(c
                            .getColumnIndex("TITLE")));
                    faq.setDescription(c.getString(c
                            .getColumnIndex("DESCRIPTION")));
                    faq.setOrder(c.getString(c
                            .getColumnIndex("FAQ_ORDER")));
                    faqList.add(faq);
                } while (c.moveToNext());
            }
        }
        c.close();
        closeConnection();
        return faqList;
    }

    // Sqlite DB Helper Class
    private static class RMSDataHelper extends SQLiteOpenHelper {
        RMSDataHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}