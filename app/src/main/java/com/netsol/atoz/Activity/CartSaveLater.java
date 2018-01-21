package com.netsol.atoz.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.netsol.atoz.Adapter.CartSaveLaterAdapter;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Controller.JsonParser;
import com.netsol.atoz.Controller.WebserviceManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by macmini on 1/16/18.
 */

public class CartSaveLater extends AppCompatActivity implements CartSaveLaterAdapter.OnCartSaveDeleted, CartSaveLaterAdapter.OnCartSaveAddedBack, AlertAction {

    String addedProductID = "";
    String addedColorID = "";
    String addedSizeID = "";
    MenuItem cart;
    Helper helper;
    Context context;
    JsonParser jsonParser;
    LayerDrawable icon;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ListView cartSaveLaterListView;
    ProgressDialog progressDialog;
    DatabaseManager databaseManager;
    WebserviceManager _webserviceManager;
    public ArrayList<CartItem> cartSaveLaterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_later);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Items");
        }

        context = CartSaveLater.this;
        databaseManager = new DatabaseManager(this);
        _webserviceManager = new WebserviceManager(this);
        helper = new Helper(this, this);
        jsonParser = new JsonParser(this);

        cartSaveLaterListView = (ListView) findViewById(R.id.cart_save_later_list);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        setCartItemSaveLater();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // finish the activity
                onBackPressed();
                break;
            case R.id.cart:
                startActivity(new Intent(CartSaveLater.this, CartActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        cart = menu.findItem(R.id.cart);
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
    public void OnCartItemDeleted() {
        setCartItemSaveLater();
    }

    @Override
    public void OnCartItemAddedBack(String color, String size, String md5, String productId, String colorId, String sizeId) {
        String orderId = sharedPref.getString(Constants.ORDER_PLACED_ID, "");
        String userId = sharedPref.getString(Constants.USER_ID, "");
        addedProductID = productId;
        addedColorID = colorId;
        addedSizeID = sizeId;
        if (orderId.equalsIgnoreCase("")) {
            databaseManager.updateDeletedAPI(addedProductID, addedColorID, addedSizeID, "false");
            databaseManager.updateSaveLater(addedProductID, addedColorID, addedSizeID, "false");
            int badgeCount = databaseManager.getBadgeCount();
            editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
            editor.apply();

            icon = (LayerDrawable) cart.getIcon();
            helper.setBadgeCount(CartSaveLater.this, icon, String.valueOf(badgeCount));
            setCartItemSaveLater();
            Toast.makeText(CartSaveLater.this, "Product has added back to cart.",  Toast.LENGTH_LONG).show();

        } else {
            RequestBody formBody = new FormBody.Builder()
                    .add("line_color", color)
                    .add("line_size", size)
                    .add("line_status", "Placed")
                    .add("row_id", md5)
                    .add("user_id", userId)
                    .add("ord_id", orderId)
                    .add("prod_id", productId)
                    .build();

            new CartSaveLater.AddOrderline().execute(this, "post", formBody);
        }

    }

    public void setCartItemSaveLater() {

        cartSaveLaterList = new ArrayList<>();
        cartSaveLaterList = databaseManager.getCartItemSaveLater();
        CartSaveLaterAdapter cartSaveLaterAdapter = new CartSaveLaterAdapter(CartSaveLater.this, cartSaveLaterList, helper);
        cartSaveLaterAdapter.setOnCartSaveDeletedListener(this);
        cartSaveLaterAdapter.setOnCartSaveAddedBackListener(this);
        cartSaveLaterListView.setAdapter(cartSaveLaterAdapter);

    }

    @Override
    public void onOkClicked() {

    }

    @Override
    public void onCancelClicked() {

    }

    private class AddOrderline extends AsyncTask<Object, Void, Object> {

        private String TAG = CartSaveLater.AddOrderline.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CartSaveLater.this,
                    CartSaveLater.this.getResources().getString(
                            R.string.adding_to_cart),
                    CartSaveLater.this.getResources().getString(
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
            helper.dismissProgressDialog(progressDialog);
            if (result != null) {
                String parseUpdate = jsonParser.parseSaveOrder(result.toString(), "cart");
                if (parseUpdate.equalsIgnoreCase("Updated")) {
                    databaseManager.updateDeletedAPI(addedProductID, addedColorID, addedSizeID, "false");
                    databaseManager.updateSaveLater(addedProductID, addedColorID, addedSizeID, "false");

                    int badgeCount = databaseManager.getBadgeCount();
                    editor.putString(Constants.BADGE_COUNT, String.valueOf(badgeCount));
                    editor.apply();

                    icon = (LayerDrawable) cart.getIcon();
                    helper.setBadgeCount(CartSaveLater.this, icon, String.valueOf(badgeCount));
                    setCartItemSaveLater();
                    Toast.makeText(CartSaveLater.this, "Product has added back to cart.",  Toast.LENGTH_LONG).show();
                } else {
                    helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
                }
            } else {
                helper.cancelableAlertDialog("", context.getString(R.string.server_busy), 1);
            }
        }
    }

}
