package com.netsol.atoz.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.netsol.atoz.Activity.CartActivity;
import com.netsol.atoz.Activity.CartSaveLater;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;
import com.netsol.atoz.Util.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macmini on 11/16/17.
 */

public class CartSaveLaterAdapter extends BaseAdapter {
    private Helper helper;
    private final DatabaseManager dbManager;
    private ArrayList<CartItem> cartSaveList;
    private OnCartSaveDeleted callback;
    private OnCartSaveAddedBack callbackAddBack;
    private Context context;
    private static LayoutInflater inflater = null;

    public CartSaveLaterAdapter(CartSaveLater cartContext, ArrayList<CartItem> cartSave, Helper helper1) {
        // TODO Auto-generated constructor stub
        cartSaveList = cartSave;
        context = cartContext;
        helper = helper1;
        dbManager = new DatabaseManager(context);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnCartSaveDeletedListener(OnCartSaveDeleted callback) {
        this.callback = callback;
    }

    public interface OnCartSaveDeleted {
        public void OnCartItemDeleted();
    }

    public void setOnCartSaveAddedBackListener(OnCartSaveAddedBack callback) {
        this.callbackAddBack = callback;
    }

    public interface OnCartSaveAddedBack {
        public void OnCartItemAddedBack(String color, String size, String md5, String productId, String colorID, String sizeID);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return cartSaveList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return cartSaveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class Holder {
        ImageView buttonDelete;
        ImageView buttonCart;
        ImageView productImage;
        TextView productName;
        TextView productCode;
        TextView productColor;
        TextView productSize;
        TextView productQuantity;
        TextView productPrice;
        TextView productSubTotal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.cart_save_later_view, null);
        final CartItem cartDetails = (CartItem) getItem(position);
        holder.productImage = (ImageView) rowView.findViewById(R.id.cart_save_later_image);
        holder.buttonDelete = (ImageView) rowView.findViewById(R.id.button_save_later_delete);
        holder.buttonCart = (ImageView) rowView.findViewById(R.id.button_save_later_cart);
        holder.productName = (TextView) rowView.findViewById(R.id.cart_save_later_product_name);
        holder.productCode = (TextView) rowView.findViewById(R.id.cart_save_later_product_code);
        holder.productColor = (TextView) rowView.findViewById(R.id.cart_save_later_product_color);
        holder.productSize = (TextView) rowView.findViewById(R.id.cart_save_later_product_size);
        holder.productQuantity = (TextView) rowView.findViewById(R.id.cart_save_later_product_qty);
        holder.productPrice = (TextView) rowView.findViewById(R.id.cart_item_save_price);
        holder.productSubTotal = (TextView) rowView.findViewById(R.id.cart_item_save_sub_total_price);

        if (cartDetails.getProductImage().equals("null") || cartDetails.getProductImage().equals("")) {

        } else {
            Glide.with(context).load(cartDetails.getProductImage())
                    .fitCenter()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(null)
                    .into(holder.productImage);
        }

        if (cartDetails.getName().equals("null") || cartDetails.getName().equals("")) {
            holder.productName.setText("");
        } else {
            holder.productName.setText(cartDetails.getName());
        }

        if (cartDetails.getProductCode().equals("null") || cartDetails.getProductCode().equals("")) {
            holder.productCode.setText("");
        } else {
            holder.productCode.setText(String.format("Item code: %s", cartDetails.getProductCode()));
        }

        if (cartDetails.getColor().equals("null") || cartDetails.getColor().equals("")) {
            holder.productColor.setText("");
        } else {
            holder.productColor.setText(String.format("Color: %s", cartDetails.getColor()));
        }

        if (cartDetails.getSize().equals("null") || cartDetails.getSize().equals("")) {
            holder.productSize.setText("");
        } else {
            holder.productSize.setText(String.format("Type/Size: %s", cartDetails.getSize()));
        }

        if (cartDetails.getPrice().equals("null") || cartDetails.getPrice().equals("")) {
            holder.productPrice.setText("");
        } else {
            String price = cartDetails.getPrice();
            double total = Double.parseDouble(price.replaceAll(",",""));
            DecimalFormat precision = new DecimalFormat("0.00");
            holder.productPrice.setText(String.valueOf(precision.format(total)));
//            holder.productPrice.setText(cartDetails.getPrice());
        }

        int quantity = 0;
        if (cartDetails.getQuantity().equals("null") || cartDetails.getQuantity().equals("")) {
            holder.productQuantity.setText("");
        } else {
            quantity = Integer.parseInt(cartDetails.getQuantity());
            holder.productQuantity.setText(String.format("Qty: %s", cartDetails.getQuantity()));
        }

        int price = 0;
        if (cartDetails.getPrice().equals("null") || cartDetails.getPrice().equals("")) {
            holder.productSubTotal.setText("");
        } else {
            if (quantity > 0) {
                price = Integer.parseInt(cartDetails.getPrice().replaceAll(",","")) * quantity;
                double total = Double.parseDouble(String.valueOf(price));
                DecimalFormat precision = new DecimalFormat("0.00");
                holder.productSubTotal.setText(String.valueOf(precision.format(total)));
//                holder.productSubTotal.setText(String.valueOf(price));
            } else {
                String price1 = cartDetails.getPrice().replaceAll(",","");
                double total = Double.parseDouble(price1);
                DecimalFormat precision = new DecimalFormat("0.00");
                holder.productSubTotal.setText(String.valueOf(precision.format(total)));
//                holder.productSubTotal.setText(cartDetails.getPrice());
            }
        }

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //1. create a dialog object 'dialog'
                final AlertDialog alertDialog = builder
                        .setTitle("")
                        .setMessage(context.getResources().getString(R.string.alert_delete))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbManager.deleteCartItem(cartDetails.getProductId(), cartDetails.getColorId(), cartDetails.getSizeId());
                                callback.OnCartItemDeleted();
                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                //2. now setup to change color of the button
                alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                });
                alertDialog.show();
            }
        });

        holder.buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storedmd5 = dbManager.getCartMD5(cartDetails.getProductId(), cartDetails.getColorId(), cartDetails.getSizeId());
                String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String currentMonth = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
                if (currentMonth.length() == 1) {
                    currentMonth = "0" + currentMonth;
                }
                String currentDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                if (currentDate.length() == 1) {
                    currentDate = "0" + currentDate;
                }
                String md5string = cartDetails.getProductId() + cartDetails.getColor() + cartDetails.getSize() + currentYear + currentMonth + currentDate;
                String md5 = helper.MD5(md5string);
                if (!storedmd5.equalsIgnoreCase(md5)) {
                    dbManager.updateMD5(cartDetails.getProductId(), cartDetails.getColorId(), cartDetails.getSizeId(), md5);
                }
                callbackAddBack.OnCartItemAddedBack(cartDetails.getColor(), cartDetails.getSize(), cartDetails.getMd5(), cartDetails.getProductId()
                        , cartDetails.getColorId(), cartDetails.getSizeId());
            }
        });

        return rowView;
    }
}
