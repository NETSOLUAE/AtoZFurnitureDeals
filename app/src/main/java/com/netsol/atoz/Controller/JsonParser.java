package com.netsol.atoz.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.netsol.atoz.Activity.AddressActivity;
import com.netsol.atoz.Activity.CreditCardActivity;
import com.netsol.atoz.Activity.EditPersonalInfoActivity;
import com.netsol.atoz.Activity.ForgetPassword;
import com.netsol.atoz.Activity.RegisterActivity;
import com.netsol.atoz.Activity.ReviewActivity;
import com.netsol.atoz.Activity.SigninActivity;
import com.netsol.atoz.Activity.VerificationActivity;
import com.netsol.atoz.Fragment.ContactUsFragment;
import com.netsol.atoz.Fragment.ResetPasswordFragment;
import com.netsol.atoz.Model.AboutGroup;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.Model.Area;
import com.netsol.atoz.Model.Banner;
import com.netsol.atoz.Model.Category;
import com.netsol.atoz.Model.City;
import com.netsol.atoz.Model.Color;
import com.netsol.atoz.Model.Faq;
import com.netsol.atoz.Model.HomeGroup;
import com.netsol.atoz.Model.HomeProduct;
import com.netsol.atoz.Model.Nationality;
import com.netsol.atoz.Model.OrderGroup;
import com.netsol.atoz.Model.OrderProduct;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.Model.ProductImage;
import com.netsol.atoz.Model.Size;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by macmini on 12/11/17.
 */

public class JsonParser {
    private Context context;
    private DatabaseManager databaseManager;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public JsonParser(Context context) {
        this.context = context;
    }

    public String parseHomeDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONArray homeResponse = new JSONArray(response);
//            String require_login_update = authenticationResp
//                    .getString("require_update");

            for (int i = 0; i < homeResponse.length(); i++) {
                JSONObject homeObject = homeResponse
                        .getJSONObject(i);
                String code = homeObject
                        .getString("code");
                if (code.equalsIgnoreCase("200")) {
                    if (homeObject.has("home_products") && !homeObject.isNull("home_products")) {
                        JSONArray homeProductsArray = homeObject.getJSONArray("home_products");
                        for (int j = 0; j < homeProductsArray.length(); j++) {
                            JSONObject homeProductsObject = homeProductsArray.getJSONObject(j);
                            String homeTitle = homeProductsObject
                                    .getString("title").replaceAll("'", "''");
                            HomeGroup homeGroup = new HomeGroup();
                            homeGroup.setGroupId(String.valueOf(j));
                            homeGroup.setGroupTitle(homeTitle);
                            dbManager.insertHomeGroup(homeGroup);
                            if (homeProductsObject.has("items") && !homeProductsObject.isNull("items")) {
                                JSONArray newProductObjectArray = homeProductsObject.getJSONArray("items");
                                for (int k = 0; k < newProductObjectArray.length(); k++) {
                                    JSONObject itemsObject = newProductObjectArray.getJSONObject(k);
                                    if (homeTitle.equalsIgnoreCase("Categories")) {
                                        Category category = new Category();
                                        category.setCategoryID(itemsObject
                                                .getString("id"));
                                        category.setParent(itemsObject
                                                .getString("parent"));
                                        String categoryName = itemsObject
                                                .getString("name").replaceAll("'", "''");
                                        categoryName = categoryName.replaceAll("&","\u0026");
                                        category.setCategoryName(categoryName);
                                        category.setCategoryImage(itemsObject
                                                .getString("image"));
                                        category.setTotal(itemsObject
                                                .getString("total"));
                                        category.setIsHome("true");
                                        dbManager.insertCategory(category);
//                                        String categoryId = itemsObject
//                                                .getString("id");
//                                        dbManager.updateCategoryHome(categoryId);
                                    } else if (homeTitle.equalsIgnoreCase("Top Banner")) {
                                        Banner banner = new Banner();
                                        banner.setId(itemsObject
                                                .getString("id"));
                                        banner.setTitle(itemsObject
                                                .getString("title"));
                                        banner.setDescription(itemsObject
                                                .getString("description").replaceAll("'", "''"));
                                        banner.setImage(itemsObject
                                                .getString("image"));
                                        banner.setBanner(itemsObject
                                                .getString("banner"));
                                        banner.setLocation(itemsObject
                                                .getString("location"));
                                        banner.setEnabled(itemsObject
                                                .getString("enabled"));
                                        banner.setPosition(itemsObject
                                                .getString("position"));
                                        dbManager.insertBannerImage(banner);
                                    } else {
                                        HomeProduct homeProduct = new HomeProduct();
                                        homeProduct.setGroupId(String.valueOf(j));
                                        homeProduct.setProductId(itemsObject
                                                .getString("id"));
                                        homeProduct.setGroupTitle(homeProductsObject
                                                .getString("title").replaceAll("'", "''"));
                                        homeProduct.setProductTitle(itemsObject
                                                .getString("title").replaceAll("'", "''"));
                                        homeProduct.setSelling_price(itemsObject
                                                .getString("selling_price"));
                                        homeProduct.setActual_price(itemsObject
                                                .getString("actual_price"));
                                        homeProduct.setDiscount_percent(itemsObject
                                                .getString("discount_percent"));
                                        homeProduct.setImage(itemsObject
                                                .getString("image"));
                                        dbManager.insertHomeProduct(homeProduct);

                                        if (itemsObject.has("colors")) {
                                            dbManager.clearProductColor(itemsObject
                                                    .getString("id"));
                                            JSONArray colorsArray = itemsObject.getJSONArray("colors");
                                            for (int l = 0; l < colorsArray.length(); l++) {
                                                JSONObject colorsObject = colorsArray
                                                        .getJSONObject(l);
                                                Color colors = new Color();
                                                colors.setProductId(itemsObject
                                                        .getString("id"));
                                                colors.setColorId(colorsObject
                                                        .getString("color_id"));
                                                colors.setColorName(colorsObject
                                                        .getString("color_name"));
                                                dbManager.insertColor(colors);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    statusString = "Updated";
                } else {
                    statusString = "NoData";
                }
            }
        } catch (JSONException e) {
            statusString = "NoData";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseCategoryDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONArray categoryResponse = new JSONArray(response);
//            String require_login_update = authenticationResp
//                    .getString("require_update");

            for (int i = 0; i < categoryResponse.length(); i++) {
                JSONObject categoryObject = categoryResponse
                        .getJSONObject(i);
                String enabled = categoryObject
                        .getString("enabled");
                if (enabled.equalsIgnoreCase("Yes")) {
                    Category category = new Category();
                    String categoryId = categoryObject
                            .getString("id");
                    int categoryCount = dbManager.getCategoryCount(categoryId);
                    if (categoryCount == 0) {
                        category.setCategoryID(categoryId);
                        category.setParent(categoryObject
                                .getString("parent"));
                        String categoryName = categoryObject
                                .getString("name").replaceAll("'", "''");
                        categoryName = categoryName.replaceAll("&","\u0026");
                        category.setCategoryName(categoryName);
                        category.setCategoryImage(categoryObject
                                .getString("image"));
                        category.setTotal(categoryObject
                                .getString("total"));
                        category.setPath(categoryObject
                                .getString("path"));
                        category.setEnabled(categoryObject
                                .getString("enabled"));
                        category.setPosition(categoryObject
                                .getString("position"));
                        category.setIsHome("false");
                        dbManager.insertCategory(category);
                    }

//                    if (categoryObject.has("products") && !categoryObject.isNull("products")) {
//                        JSONArray productArray = categoryObject.getJSONArray("products");
//                        for (int j = 0; j < productArray.length(); j++) {
//                            JSONObject productObject = productArray
//                                    .getJSONObject(j);
//                            Product product = new Product();
//                            product.setCategoryId(productObject
//                                    .getString("cat_id"));
//                            product.setProductId(productObject
//                                    .getString("id"));
//                            product.setTilte(productObject
//                                    .getString("title").replaceAll("'", "''"));
//                            product.setSellingPrice(productObject
//                                    .getString("selling_price"));
//                            product.setActualPrice(productObject
//                                    .getString("actual_price"));
//                            product.setDiscountPercent(productObject
//                                    .getString("discount_percent"));
//                            product.setProductImage(productObject
//                                    .getString("image"));
//                            product.setDescription(productObject
//                                    .getString("description").replaceAll("'", "''"));
//                            product.setIsSearch("false");
//                            dbManager.insertProduct(product);
//                        }
//                    }
                }
            }
            statusString = "Updated";
        } catch (JSONException e) {
            statusString = "NoData";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseProductDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            databaseManager = new DatabaseManager(context);
            JSONArray productArrayList = new JSONArray(response);

            for (int i = 0; i < productArrayList.length(); i++) {
                JSONObject productObjectMain = productArrayList
                        .getJSONObject(i);
                String code = productObjectMain.getString("code");
                if (code.equalsIgnoreCase("200") && productObjectMain.has("products") && !productObjectMain.isNull("products")) {
                    JSONArray productArray = productObjectMain.getJSONArray("products");
                    for (int j = 0; j < productArray.length(); j++) {
                        JSONObject productObject = productArray
                                .getJSONObject(j);
                        String productId = productObject
                                .getString("id");
                        databaseManager.clearProducts(productId);
                        Product product = new Product();
                        product.setCategoryId(productObject
                                .getString("category"));
                        product.setProductId(productId);
                        product.setTilte(productObject
                                .getString("title").replaceAll("'", "''"));
                        product.setSellingPrice(productObject
                                .getString("selling_price"));
                        product.setActualPrice(productObject
                                .getString("actual_price"));
                        product.setDiscountPercent(productObject
                                .getString("discount_percent"));
                        product.setProductCode(productObject
                                .getString("item_code"));
                        product.setProductImage(productObject
                                .getString("image"));
                        product.setDescription(productObject
                                .getString("description").replaceAll("'", "''"));
                        product.setSpecification(productObject
                                .getString("specification").replaceAll("'", "''"));
                        product.setDelivery(productObject
                                .getString("delivery").replaceAll("'", "''"));
                        product.setWarranty(productObject
                                .getString("warranty").replaceAll("'", "''"));
                        product.setDeal_type(productObject
                                .getString("deal_type").replaceAll("'", "''"));
                        product.setIsSearch("false");
                        dbManager.insertProduct(product);

                        if (productObject.has("images")) {
                            JSONArray slidingImageArray = productObject.getJSONArray("images");
                            for (int k = 0; k < slidingImageArray.length(); k++) {
                                JSONObject imageObject = slidingImageArray
                                        .getJSONObject(k);
                                ProductImage productImage = new ProductImage();
                                productImage.setCategoryId(productObject
                                        .getString("category"));
                                productImage.setProductId(productObject
                                        .getString("id"));
                                productImage.setProductImageId(imageObject
                                        .getString("id"));
                                productImage.setProductImage(imageObject
                                        .getString("image"));
                                dbManager.insertProductImage(productImage);
                            }
                        }

                        if (productObject.has("colors")) {
                            JSONArray colorsArray = productObject.getJSONArray("colors");
                            for (int k = 0; k < colorsArray.length(); k++) {
                                JSONObject colorsObject = colorsArray
                                        .getJSONObject(k);
                                Color colors = new Color();
                                colors.setCategoryId(productObject
                                        .getString("category"));
                                colors.setProductId(productObject
                                        .getString("id"));
                                colors.setColorId(colorsObject
                                        .getString("color_id"));
                                colors.setColorName(colorsObject
                                        .getString("color_name"));
                                colors.setColorImage(colorsObject
                                        .getString("image"));
                                dbManager.insertColor(colors);

                                if (colorsObject.has("sizes")) {
                                    JSONArray sizesArray = colorsObject.getJSONArray("sizes");
                                    for (int l = 0; l < sizesArray.length(); l++) {
                                        JSONObject sizesObject = sizesArray
                                                .getJSONObject(l);
                                        Size size = new Size();
                                        size.setCategoryId(productObject
                                                .getString("category"));
                                        size.setProductId(productObject
                                                .getString("id"));
                                        size.setColorId(colorsObject
                                                .getString("color_id"));
                                        size.setSizeId(sizesObject
                                                .getString("id"));
                                        size.setSize(sizesObject
                                                .getString("size"));
                                        size.setQuantity(sizesObject
                                                .getString("qty"));
                                        size.setSellingPrice(sizesObject
                                                .getString("selling"));
                                        size.setActualPrice(sizesObject
                                                .getString("price"));
                                        size.setDiscountPercent(sizesObject
                                                .getString("percent"));
                                        size.setModel(sizesObject
                                                .getString("model").replaceAll("'", "''"));
                                        size.setWeight(sizesObject
                                                .getString("weight"));
                                        size.setSizeImage(sizesObject
                                                .getString("image"));
                                        dbManager.insertSize(size);
                                    }
                                }
                            }
                        }
                    }
                    statusString = "Updated";
                } else {
                    statusString = "NoData";
                }
            }
        } catch (JSONException e) {
            statusString = "NoData";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseSearchProductDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            databaseManager = new DatabaseManager(context);
            JSONObject productResponse = new JSONObject(response);

            String code = productResponse.getString("code");
            if (code.equalsIgnoreCase("200")) {
                if (productResponse.has("products") && !productResponse.isNull("products")) {
                    databaseManager.updateSearchProduct();
                    JSONArray productArray = productResponse.getJSONArray("products");
                    for (int j = 0; j < productArray.length(); j++) {
                        JSONObject productObject = productArray
                                .getJSONObject(j);
                        String productId = productObject
                                .getString("id");
                        databaseManager.clearProducts(productId);
                        Product product = new Product();
                        product.setCategoryId(productObject
                                .getString("cat_id"));
                        product.setProductId(productId);
                        product.setTilte(productObject
                                .getString("title").replaceAll("'", "''"));
                        product.setSellingPrice(productObject
                                .getString("discount"));
                        product.setActualPrice(productObject
                                .getString("price"));
                        product.setDiscountPercent(productObject
                                .getString("percent"));
                        product.setProductImage(productObject
                                .getString("image"));
                        product.setIsSearch("true");
                        dbManager.insertProduct(product);

                        if (productObject.has("colors")) {
                            JSONArray colorsArray = productObject.getJSONArray("colors");
                            for (int k = 0; k < colorsArray.length(); k++) {
                                JSONObject colorsObject = colorsArray
                                        .getJSONObject(k);
                                Color colors = new Color();
                                colors.setCategoryId(productObject
                                        .getString("cat_id"));
                                colors.setProductId(productObject
                                        .getString("id"));
                                colors.setColorId(colorsObject
                                        .getString("color_id"));
                                colors.setColorName(colorsObject
                                        .getString("color_name"));
                                dbManager.insertColor(colors);
                            }
                        }
                    }
                    statusString = "Updated";
                } else {
                    statusString = "NoData";
                }
            } else {
                statusString = "NoData";
            }
        } catch (JSONException e) {
            statusString = "NoData";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseAboutDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject aboutResponse = new JSONObject(response);
//            String require_login_update = authenticationResp
//                    .getString("require_update");

            String code = aboutResponse.getString("code");
            if (code.equalsIgnoreCase("200")) {
                dbManager.clearAbout();
                JSONArray aboutResponseArray = aboutResponse.getJSONArray("pages");
                for (int i = 0; i < aboutResponseArray.length(); i++) {
                    JSONObject aboutObject = aboutResponseArray
                            .getJSONObject(i);
                    AboutGroup aboutGroup = new AboutGroup();
                    aboutGroup.setId(aboutObject
                            .getString("id"));
                    aboutGroup.setHeading(aboutObject
                            .getString("title"));
                    aboutGroup.setExplanation(aboutObject
                            .getString("description").replaceAll("'", "''"));
                    aboutGroup.setLast_saved(aboutObject
                            .getString("last_updated"));
                    dbManager.insertAbout(aboutGroup);
                }
                statusString = "Updated";
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseBannerDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject bannerResponse = new JSONObject(response);
//            String require_login_update = authenticationResp
//                    .getString("require_update");

            String code = bannerResponse.getString("code");
            if (code.equalsIgnoreCase("200")) {
                JSONArray bannerResponseArray = bannerResponse.getJSONArray("banner");
                for (int i = 0; i < bannerResponseArray.length(); i++) {
                    JSONObject bannerObject = bannerResponseArray
                            .getJSONObject(i);
                    String enabled = bannerObject
                            .getString("enabled");
                    if (enabled.equalsIgnoreCase("Yes")) {
                        Banner banner = new Banner();
                        banner.setId(bannerObject
                                .getString("id"));
                        banner.setTitle(bannerObject
                                .getString("title"));
                        banner.setDescription(bannerObject
                                .getString("description").replaceAll("'", "''"));
                        banner.setImage(bannerObject
                                .getString("image"));
                        banner.setBanner(bannerObject
                                .getString("banner"));
                        banner.setLocation(bannerObject
                                .getString("location"));
                        banner.setEnabled(bannerObject
                                .getString("enabled"));
                        banner.setPosition(bannerObject
                                .getString("position"));
                        dbManager.insertBannerImage(banner);
                    }
                }
                statusString = "Updated";
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseCityDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject cityResponse = new JSONObject(response);

            String code = cityResponse.getString("code");
            if (code.equalsIgnoreCase("200")) {
                JSONArray citiesResponseArray = cityResponse.getJSONArray("cities");
                City city1 = new City();
                city1.setId(String.valueOf((citiesResponseArray.length()+1)));
                city1.setName("Select City");
                city1.setPosition("0");
                city1.setEnabled("1");
                dbManager.insertCity(city1);
                for (int i = 0; i < citiesResponseArray.length(); i++) {
                    JSONObject cityObject = citiesResponseArray
                            .getJSONObject(i);
                    String enabled = cityObject
                            .getString("enabled");
                    if (enabled.equalsIgnoreCase("Yes")) {
                        City city = new City();
                        city.setId(cityObject
                                .getString("id"));
                        city.setName(cityObject
                                .getString("name"));
                        city.setPosition(cityObject
                                .getString("position"));
                        city.setEnabled(cityObject
                                .getString("enabled"));
                        dbManager.insertCity(city);
                    }
                }
                statusString = "Updated";
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseAreaDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject areaResponse = new JSONObject(response);

            String code = areaResponse.getString("code");
            if (code.equalsIgnoreCase("200") && areaResponse.has("areas") && !areaResponse.isNull("areas")) {
                JSONArray areaResponseArray = areaResponse.getJSONArray("areas");
                for (int i = 0; i < areaResponseArray.length(); i++) {
                    JSONObject areaObject = areaResponseArray
                            .getJSONObject(i);
                    Area area = new Area();
                    area.setId(areaObject
                            .getString("id"));
                    area.setName(areaObject
                            .getString("area_name").replaceAll("'", "''"));
                    area.setShipRate(areaObject
                            .getString("ship_rate"));
                    area.setCityId(areaObject
                            .getString("city_id"));
                    dbManager.insertArea(area);
                }
                statusString = "Updated";
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseNationalityDetails(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject nationalityResponse = new JSONObject(response);

            String code = nationalityResponse.getString("code");
            if (code.equalsIgnoreCase("200") && nationalityResponse.has("nationalities") && !nationalityResponse.isNull("nationalities")) {
                JSONArray nationalityResponseArray = nationalityResponse.getJSONArray("nationalities");
                Nationality nationality1 = new Nationality();
                nationality1.setId(String.valueOf((nationalityResponseArray.length()+1)));
                nationality1.setName("Select Nationality");
                dbManager.insertNationality(nationality1);
                for (int i = 0; i < nationalityResponseArray.length(); i++) {
                    JSONObject nationalityObject = nationalityResponseArray
                            .getJSONObject(i);
                    Nationality nationality = new Nationality();
                    nationality.setId(nationalityObject
                            .getString("id"));
                    nationality.setName(nationalityObject
                            .getString("name").replaceAll("'", "''"));
                    dbManager.insertNationality(nationality);
                }
                statusString = "Updated";
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseRegistration(String response) {
        String statusString = "";
        sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        try {
//            [{"code":"200","error":"Congrat! You account is created and an SMS PIN is sent to you to activate your account.","server_time":"2018-01-21 02:51:34"}]
            JSONArray verificationResponse = new JSONArray(response);

            for (int i = 0; i < verificationResponse.length(); i++) {
                JSONObject verificationObject = verificationResponse
                        .getJSONObject(i);
                String code = verificationObject
                        .getString("code");
                RegisterActivity.registrationMessage = verificationObject
                        .getString("error");
                if (code.equalsIgnoreCase("200")) {
                    String server_time = verificationObject
                            .getString("server_time");
                    String userid = verificationObject
                            .getString("userid");
                    editor.putString(Constants.USER_ID, userid);
                    editor.apply();
                    statusString = "Updated";
                } else {
                    statusString = "NotUpdated";
                }
            }
        } catch (JSONException e) {
            RegisterActivity.registrationMessage = context.getString(R.string.server_busy);
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseVerificationResponse(String response) {
        String statusString = "";
        try {
            JSONArray verificationResponse = new JSONArray(response);

            for (int i = 0; i < verificationResponse.length(); i++) {
                JSONObject verificationObject = verificationResponse
                        .getJSONObject(i);
                String code = verificationObject
                        .getString("code");
                VerificationActivity.verificationMessage = verificationObject
                        .getString("error");
                if (code.equalsIgnoreCase("200")) {
                    String server_time = verificationObject
                            .getString("server_time");
                    statusString = "Updated";
                } else {
                    statusString = "NotUpdated";
                }
            }
        } catch (JSONException e) {
            VerificationActivity.verificationMessage = context.getString(R.string.server_busy);
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseLogin(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            JSONArray loginResponse = new JSONArray(response);

            for (int i = 0; i < loginResponse.length(); i++) {
                JSONObject loginObject = loginResponse
                        .getJSONObject(i);
                String code = loginObject
                        .getString("code");
                if (code.equalsIgnoreCase("200")) {
                    SigninActivity.is403Error = false;
                    String userid = loginObject
                            .getString("userid");
                    String server_time = loginObject
                            .getString("server_time");
                    String email = loginObject
                            .getString("email");
                    String fullname = loginObject
                            .getString("fullname");
                    String gender = loginObject
                            .getString("gender");
                    String mobile = loginObject
                            .getString("mobile");
                    String dob = loginObject
                            .getString("dob");
                    String country = loginObject
                            .getString("country");
                    String user_photo = loginObject
                            .getString("user_photo");
                    String nationality = loginObject
                            .getString("nationality");

                    editor.putString(Constants.FULL_NAME, fullname);
                    editor.putString(Constants.EMAIL, email);
                    editor.putString(Constants.MOBILE, mobile);
                    editor.putString(Constants.DOB, dob);
                    editor.putString(Constants.COUNTRY, country);
                    editor.putString(Constants.GENDER, gender);
                    editor.putString(Constants.API_USER_PHOTO, user_photo);
                    editor.putString(Constants.USER_ID, userid);
                    editor.putString(Constants.NATIONALITY, nationality);
                    editor.apply();

                    if (loginObject.has("address") && !loginObject.isNull("address")) {
                        JSONArray addressResponseArray = loginObject.getJSONArray("address");
                        statusString = parseAddress(addressResponseArray.toString());
                    } else {
                        statusString = "Updated";
                    }
                } else if (code.equalsIgnoreCase("403")) {
                    SigninActivity.is403Error = true;
                    String email_error = loginObject
                            .getString("email_error");
                    String pass_error = loginObject
                            .getString("pass_error");
                    if (email_error.equalsIgnoreCase(null) || email_error.equalsIgnoreCase("null")) {
                        statusString = pass_error;
                    } else {
                        statusString = email_error;
                    }
                } else {
                    SigninActivity.is403Error = false;
                    statusString = "NotUpdated";
                }
            }
        } catch (JSONException e) {
            SigninActivity.is403Error = false;
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseUpdatedAddress (String response) {
        String statusString = "";
        try {
            JSONObject loginResponse = new JSONObject(response);
            String code = loginResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                JSONArray addressArray = loginResponse.getJSONArray("address");
                statusString = parseAddress(addressArray.toString());
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            SigninActivity.is403Error = false;
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseResetPassword (String response) {
        String statusString = "";
        try {
            JSONArray resetArrayResponse = new JSONArray(response);
            JSONObject resetResponse = resetArrayResponse.getJSONObject(0);
            String code = resetResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                ResetPasswordFragment.resetError = false;
                statusString = resetResponse
                        .getString("massage");
            } else if (code.equalsIgnoreCase("403")) {
                ResetPasswordFragment.resetError = true;
                statusString = resetResponse
                        .getString("error");
            } else {
                ResetPasswordFragment.resetError = true;
                statusString = context.getString(R.string.server_busy);
            }
        } catch (JSONException e) {
            ResetPasswordFragment.resetError = true;
            statusString = context.getString(R.string.server_busy);
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseForgetPassword (String response) {
        String statusString = "";
        try {
            JSONArray forgetArrayResponse = new JSONArray(response);
            JSONObject forgetResponse = forgetArrayResponse.getJSONObject(0);
            String code = forgetResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                ForgetPassword.forgetError = false;
//                ForgetPassword.newpass = forgetResponse
//                        .getString("newpass");
//                statusString = forgetResponse
//                        .getString("massage") + "Your new password is " +
//                        forgetResponse.getString("newpass");
                statusString = forgetResponse
                        .getString("massage");
            } else if (code.equalsIgnoreCase("403")) {
                ForgetPassword.forgetError = true;
//                ForgetPassword.newpass = "";
                statusString = forgetResponse
                        .getString("error");
            } else {
                ForgetPassword.forgetError = true;
//                ForgetPassword.newpass = "";
                statusString = context.getString(R.string.server_busy);
            }
        } catch (JSONException e) {
            ForgetPassword.forgetError = true;
//            ForgetPassword.newpass = "";
            statusString = context.getString(R.string.server_busy);
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseContact (String response) {
        String statusString = "";
        try {
            JSONObject contactResponse = new JSONObject(response);
            String code = contactResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                ContactUsFragment.contactError = false;
                statusString = contactResponse
                        .getString("message");
            } else if (code.equalsIgnoreCase("403")) {
                ContactUsFragment.contactError = true;
                statusString = contactResponse
                        .getString("error");
            } else {
                ContactUsFragment.contactError = true;
                statusString = context.getString(R.string.server_busy);
            }
        } catch (JSONException e) {
            ContactUsFragment.contactError = true;
            statusString = context.getString(R.string.server_busy);
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseAddress(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            dbManager.clearAddress();
            JSONArray addressResponseArray = new JSONArray(response);

            for (int j = 0; j < addressResponseArray.length(); j++) {
                JSONObject addressObject = addressResponseArray
                        .getJSONObject(j);
                Address address = new Address();
                if (addressObject.has("add_id")) {
                    AddressActivity.addressIdNewSelect = addressObject
                            .getString("add_id");
                    address.setId(addressObject
                            .getString("add_id"));
                } else if (addressObject.has("id")) {
                    AddressActivity.addressIdNewSelect = addressObject
                            .getString("id");
                    address.setId(addressObject
                            .getString("id"));
                } else {
                    address.setId("");
                }
                address.setFullname(addressObject
                        .getString("fullname"));
                address.setStreet(addressObject
                        .getString("street"));
                address.setBuilding(addressObject
                        .getString("building"));
                address.setApartment(addressObject
                        .getString("apartment"));
                address.setFloor(addressObject
                        .getString("floor"));
                address.setLandmark(addressObject
                        .getString("landmark"));
                address.setPhone(addressObject
                        .getString("phone"));
                address.setMobile(addressObject
                        .getString("mobile"));
                address.setArea(addressObject
                        .getString("area"));
                address.setLocation(addressObject
                        .getString("location"));
                address.setDelivery(addressObject
                        .getString("delivery"));
                address.setNote(addressObject
                        .getString("note"));
                address.setCity(addressObject
                        .getString("city"));
                address.setCountry(addressObject
                        .getString("country"));
                address.setEnable(addressObject
                        .getString("enabled"));
                dbManager.insertAddress(address);
            }
            statusString = "Updated";
        } catch (JSONException e) {
            SigninActivity.is403Error = false;
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseAddressResponse (String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject addressResponse = new JSONObject(response);

            String code = addressResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                if (addressResponse.has("address") && !addressResponse.isNull("address")) {
                    JSONArray addressResponseArray = addressResponse.getJSONArray("address");
                    statusString = parseAddress(addressResponseArray.toString());
                } else {
                    statusString = "NoData";
                }
            } else {
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseSaveAddress(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONObject loginObject = new JSONObject(response);
            String code = loginObject
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                AddressActivity.is403ErrorAdd = false;
                statusString = loginObject.getString("message");
            } else if (code.equalsIgnoreCase("403")) {
                AddressActivity.is403ErrorAdd = true;
                statusString = loginObject
                        .getString("error");
            } else {
                AddressActivity.is403ErrorAdd = false;
                statusString = context.getString(R.string.server_busy);
            }
        } catch (JSONException e) {
            AddressActivity.is403ErrorAdd = false;
            statusString = context.getString(R.string.server_busy);
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseSavePersonalInfo(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            JSONArray loginResponse = new JSONArray(response);

            for (int i = 0; i < loginResponse.length(); i++) {
                JSONObject loginObject = loginResponse
                        .getJSONObject(i);
                String code = loginObject
                        .getString("code");
                if (code.equalsIgnoreCase("200")) {
                    EditPersonalInfoActivity.is403ErrorProfile = false;
                    statusString = loginObject
                            .getString("message");
                    String userid = loginObject
                            .getString("userid");
                    String email = loginObject
                            .getString("email");
                    String fullname = loginObject
                            .getString("fullname");
                    String gender = loginObject
                            .getString("gender");
                    String mobile = loginObject
                            .getString("mobile");
                    String dob = loginObject
                            .getString("dob");
                    String country = loginObject
                            .getString("country");
                    String user_photo = loginObject
                            .getString("user_photo");
                    String nationality = loginObject
                            .getString("nationality");

                    editor.putString(Constants.FULL_NAME, fullname);
                    editor.putString(Constants.EMAIL, email);
                    editor.putString(Constants.MOBILE, mobile);
                    editor.putString(Constants.DOB, dob);
                    editor.putString(Constants.COUNTRY, country);
                    editor.putString(Constants.NATIONALITY, nationality);
                    editor.putString(Constants.GENDER, gender);
                    editor.putString(Constants.API_USER_PHOTO, user_photo);
                    editor.apply();
                    editor.putString(Constants.USER_ID, userid);
                    editor.apply();

                } else if (code.equalsIgnoreCase("403")) {
                    EditPersonalInfoActivity.is403ErrorProfile = true;
                    statusString = context.getString(R.string.server_busy);
                } else {
                    EditPersonalInfoActivity.is403ErrorProfile = false;
                    statusString = context.getString(R.string.server_busy);
                }
            }
        } catch (JSONException e) {
            EditPersonalInfoActivity.is403ErrorProfile = false;
            statusString = context.getString(R.string.server_busy);
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseOrderGroup(String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            dbManager.clearOrderDetails();
            JSONArray orderGroupArray = new JSONArray(response);

            for (int j = 0; j < orderGroupArray.length(); j++) {
                JSONObject orderGroupObject1 = orderGroupArray
                        .getJSONObject(j);
                String code = orderGroupObject1.getString("code");
                if (code.equalsIgnoreCase("200")) {
                    if (orderGroupObject1.has("orders") && !orderGroupObject1.isNull("orders")) {
                        JSONArray orderGroupArrayList = orderGroupObject1.getJSONArray("orders");
                        for (int k = 0; k < orderGroupArrayList.length(); k++) {
                            String status = "";
                            JSONObject orderGroupObject = orderGroupArrayList
                                    .getJSONObject(k);
                            OrderGroup orderGroup = new OrderGroup();
                            orderGroup.setNo(orderGroupObject
                                    .getString("id"));
                            orderGroup.setTotal(orderGroupObject
                                    .getString("total"));
                            orderGroup.setAddress(orderGroupObject
                                    .getString("address"));
                            status = orderGroupObject
                                    .getString("status");
                            orderGroup.setStatus(status);
                            orderGroup.setPaymentStatus(orderGroupObject
                                    .getString("payment_status"));
                            orderGroup.setDate(orderGroupObject
                                    .getString("date"));
                            orderGroup.setAddressID(orderGroupObject
                                    .getString("address_id"));
                            orderGroup.setAddressName(orderGroupObject
                                    .getString("address_name"));
                            if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Cancelled")
                                    || status.equalsIgnoreCase("Void") || status.equalsIgnoreCase("Return")) {
                                orderGroup.setLevelCompleted("0");
                            } else if (status.equalsIgnoreCase("InProcessing") || status.equalsIgnoreCase("Being Prepared")
                                    || status.equalsIgnoreCase("Ready to Ship") || status.equalsIgnoreCase("Partially Shipped")) {
                                orderGroup.setLevelCompleted("1");
                            } else if (status.equalsIgnoreCase("Shipped") || status.equalsIgnoreCase("On The Way to Delivery")) {
                                orderGroup.setLevelCompleted("2");
                            } else if (status.equalsIgnoreCase("Delivered")) {
                                orderGroup.setLevelCompleted("3");
                            } else {
                                orderGroup.setLevelCompleted("1");
                            }
                            dbManager.insertOrderGroup(orderGroup);

                            if (orderGroupObject.has("order_lines") && !orderGroupObject.isNull("order_lines")) {
                                JSONArray orderProductArray = orderGroupObject.getJSONArray("order_lines");
                                for (int i = 0; i < orderProductArray.length(); i++) {
                                    JSONObject orderProductObject = orderProductArray
                                            .getJSONObject(i);
                                    OrderProduct orderProduct = new OrderProduct();
                                    orderProduct.setNo(orderGroupObject
                                            .getString("id"));
                                    orderProduct.setProductId(orderProductObject
                                            .getString("prod_id"));
                                    orderProduct.setProductCode(orderProductObject
                                            .getString("item_code"));
                                    orderProduct.setName(orderProductObject
                                            .getString("name"));
                                    orderProduct.setColor(orderProductObject
                                            .getString("color"));
                                    orderProduct.setSize(orderProductObject
                                            .getString("size"));
                                    orderProduct.setQty(orderProductObject
                                            .getString("qty"));
                                    orderProduct.setPrice(orderProductObject
                                            .getString("price"));
                                    orderProduct.setSubTotal(String.valueOf(orderProductObject
                                            .getInt("sub_total")));
                                    orderProduct.setImage(orderProductObject
                                            .getString("image"));
                                    dbManager.insertOrderProducts(orderProduct);

                                }
                            }
                        }
                        statusString = "Updated";
                    } else {
                        statusString = "NoData";
                    }
                } else {
                    statusString = "NotUpdated";
                }
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseUploadProfile (String response) {
        String statusString = "";
        try {
            JSONArray profileArrayResponse = new JSONArray(response);
            JSONObject profileResponse = profileArrayResponse.getJSONObject(0);
            String code = profileResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                statusString = profileResponse
                        .getString("message");
            } else {
                statusString = context.getString(R.string.server_busy);
            }
        } catch (JSONException e) {
            statusString = context.getString(R.string.server_busy);
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseSaveOrder (String response, String activity) {
        String statusString = "";
        try {
            sharedPref = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            JSONObject orderResponse = new JSONObject(response);
            String code = orderResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                if (activity.equalsIgnoreCase("review")) {
                    String order = orderResponse
                            .getString("ord_id");
                    statusString = orderResponse
                            .getString("message");
//                    String rowId = orderResponse
//                            .getString("row_id");
//                    editor.putString(Constants.ORDER_ROW_ID,rowId);
                    editor.putString(Constants.ORDER_PLACED_ID,order);
                    editor.apply();
                    ReviewActivity.is403ErrorReview = false;
                } else if (activity.equalsIgnoreCase("credit")) {
                    statusString = orderResponse
                            .getString("message");
                    CreditCardActivity.is403ErrorCard = false;
                } else if (activity.equalsIgnoreCase("cart")) {
                    statusString = "Updated";
                }
            } else if (code.equalsIgnoreCase("403")) {
                statusString = orderResponse
                        .getString("message");
                if (activity.equalsIgnoreCase("review")) {
                    ReviewActivity.is403ErrorReview = true;
                } else if (activity.equalsIgnoreCase("credit")) {
                    CreditCardActivity.is403ErrorCard = true;
                } else if (activity.equalsIgnoreCase("cart")) {
                    statusString = "NotUpdated";
                }
            } else {
                statusString = context.getString(R.string.server_busy);
                if (activity.equalsIgnoreCase("review")) {
                    ReviewActivity.is403ErrorReview = true;
                } else if (activity.equalsIgnoreCase("credit")) {
                    CreditCardActivity.is403ErrorCard = true;
                } else if (activity.equalsIgnoreCase("cart")) {
                    statusString = "NotUpdated";
                }
            }
        } catch (JSONException e) {
            statusString = context.getString(R.string.server_busy);
            if (activity.equalsIgnoreCase("review")) {
                ReviewActivity.is403ErrorReview = true;
            } else if (activity.equalsIgnoreCase("credit")) {
                CreditCardActivity.is403ErrorCard = true;
            } else if (activity.equalsIgnoreCase("cart")) {
                statusString = "NotUpdated";
            }
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parsePayment (String response) {
        //Payment Response
//        {"code":"200","errorTxt":"","merchant_trx_ref":"33-5045994","order_info":"Order id #33","amount":100,"trx_response_code":"0","trx_response_desc":"Transaction Successful",
//                "message":"Approved","receipt_number":"800322097097","trx_number":"32853","acq_response_code":"00","bank_authorized_id":"097097","bank_batch_number":"20180103","card_type":"VC"}
        String statusString = "";
        try {
            JSONObject paymentResponse = new JSONObject(response);
            String code = paymentResponse
                    .getString("code");
            if (code.equalsIgnoreCase("200")) {
                CreditCardActivity.is403ErrorCard = false;
                statusString = "Updated";
            } else {
                CreditCardActivity.is403ErrorCard = true;
                statusString = "NotUpdated";
            }
        } catch (JSONException e) {
            CreditCardActivity.is403ErrorCard = true;
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

    public String parseFaq (String response) {
        DatabaseManager dbManager = new DatabaseManager(context);
        String statusString = "";
        try {
            JSONArray faqResponse = new JSONArray(response);
            dbManager.clearFaq();
            for (int j = 0; j < faqResponse.length(); j++) {
                JSONObject faqObject = faqResponse
                        .getJSONObject(j);
                Faq faq = new Faq();
                String code = faqObject
                        .getString("code");
                if (code.equalsIgnoreCase("200")) {
                    faq.setGroupID(faqObject
                            .getString("id"));
                    faq.setGroupName(faqObject
                            .getString("name").replaceAll("'", "''"));
                    faq.setGroupTotal(faqObject
                            .getString("total_faqs"));

                    if (faqObject.has("faqs") || !faqObject.isNull("faqs")) {
                        JSONArray faqsArray = faqObject.getJSONArray("faqs");
                        for (int k = 0; k < faqsArray.length(); k++) {
                            JSONObject faqsObject = faqsArray.getJSONObject(k);
                            faq.setChildId(faqsObject
                                    .getString("id"));
                            faq.setTitle(faqsObject
                                    .getString("title").replaceAll("'", "''"));
                            faq.setDescription(faqsObject
                                    .getString("desc").replaceAll("'", "''"));
                            faq.setOrder(faqsObject
                                    .getString("order"));
                            dbManager.insertFaq(faq);
                        }
                    }
                    statusString = "Updated";
                } else {
                    statusString = "NotUpdated";
                    break;
                }
            }
        } catch (JSONException e) {
            statusString = "NotUpdated";
            e.printStackTrace();
            Log.v("Exception is " + Log.getStackTraceString(e), statusString, e);
        }
        return statusString;
    }

}
