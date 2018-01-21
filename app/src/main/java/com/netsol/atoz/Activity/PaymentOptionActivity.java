package com.netsol.atoz.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.netsol.atoz.R;
import com.netsol.atoz.Util.AlertAction;
import com.netsol.atoz.Util.Constants;
import com.netsol.atoz.Util.Helper;

/**
 * Created by macmini on 11/8/17.
 */

public class PaymentOptionActivity extends AppCompatActivity implements AlertAction {

    Context context;
    LayerDrawable icon;
    RelativeLayout creditCard;
    private Helper helper;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Payment Options");
        }

        context = PaymentOptionActivity.this;
        helper = new Helper(this, this);

        creditCard = (RelativeLayout) findViewById(R.id.payment_credit_card);
        sharedPref = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        creditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentOptionActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                // finish the activity
                onBackPressed();
                break;
            case R.id.cart:
                startActivity(new Intent(PaymentOptionActivity.this, CartActivity.class));
//                Toast.makeText(getApplicationContext(), "No Items added!", Toast.LENGTH_LONG).show();
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
}
