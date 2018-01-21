package com.netsol.atoz.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by macmini on 11/23/17.
 */

public class MyWhishlistAdapter extends BaseAdapter {
    private final DatabaseManager dbManager;
    private ArrayList<CartItem> cartItemList;
    private OnIsFavoriteDeleted callback;
    private OnChangeGrandTotal callChangeTotal;
    private Context context;
    private static LayoutInflater inflater = null;

    public MyWhishlistAdapter(Context cartContext, ArrayList<CartItem> cartItem) {
        // TODO Auto-generated constructor stub
        cartItemList = cartItem;
        context = cartContext;
        dbManager = new DatabaseManager(context);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnFavoriteDeletedListener(OnIsFavoriteDeleted callback) {
        this.callback = callback;
    }

    public interface OnIsFavoriteDeleted {
        public void OnFavoriteDeleted(String productID, String color, String size, String md5);
    }

    public void setOnChangeTotalListener(OnChangeGrandTotal callChangeTotal) {
        this.callChangeTotal = callChangeTotal;
    }

    public interface OnChangeGrandTotal {
        public void OnChangeTotal(String price, boolean isPlus);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return cartItemList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return cartItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class Holder {
        TextView productName;
        TextView productCode;
        TextView productColor;
        TextView productSize;
        TextView productUnitPrice;
        TextView productSubTotal;
        TextView productQuantity;
        ImageView buttonMinus;
        ImageView buttonPlus;
        ImageView productImage;
        ImageView buttonDelete;
//        ImageView buttonFavorite;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.whishlist_item_view, null);
        final CartItem cartDetails = (CartItem) getItem(position);
        holder.productImage = (ImageView) rowView.findViewById(R.id.whishlist_image);
        holder.buttonDelete = (ImageView) rowView.findViewById(R.id.button_whishlist_delete);
        holder.buttonMinus = (ImageView) rowView.findViewById(R.id.button_whishlist_minus);
        holder.buttonPlus = (ImageView) rowView.findViewById(R.id.button_whishlist_plus);
//        holder.buttonFavorite = (ImageView) rowView.findViewById(R.id.button_whishlist_favorite);
        holder.productName = (TextView) rowView.findViewById(R.id.whishlist_product_name);
        holder.productCode = (TextView) rowView.findViewById(R.id.whishlist_product_code);
        holder.productColor = (TextView) rowView.findViewById(R.id.whishlist_product_color);
        holder.productSize = (TextView) rowView.findViewById(R.id.whishlist_product_size);
        holder.productQuantity = (TextView) rowView.findViewById(R.id.whishlist_product_quantity);
        holder.productUnitPrice = (TextView) rowView.findViewById(R.id.whishlist_price);
        holder.productSubTotal = (TextView) rowView.findViewById(R.id.whishlist_sub_total_price);

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

        if (cartDetails.getColor().equals("null") || cartDetails.getColor().equals("")) {
            holder.productColor.setText("");
        } else {
            holder.productColor.setText(String.format("Color: %s", cartDetails.getColor()));
        }

        if (cartDetails.getProductCode().equals("null") || cartDetails.getProductCode().equals("")) {
            holder.productCode.setText("");
        } else {
            holder.productCode.setText(String.format("Item Code: %s", cartDetails.getProductCode()));
        }

        if (cartDetails.getSize().equals("null") || cartDetails.getSize().equals("")) {
            holder.productSize.setText("");
        } else {
            holder.productSize.setText(String.format("Type/Size: %s", cartDetails.getSize()));
        }

//        if (cartDetails.getIsFavorite().equals("null") || cartDetails.getIsFavorite().equals("")) {
//            holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
//        } else {
//            if (cartDetails.getIsFavorite().equals("false")) {
//                holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
//            } else if (cartDetails.getIsFavorite().equals("true")) {
//                holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite_selected_filled));
//            } else {
//                holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
//            }
//        }

        int quantity = 0;
        if (cartDetails.getQuantity().equals("null") || cartDetails.getQuantity().equals("")) {
            holder.productQuantity.setText("");
        } else {
            quantity = Integer.parseInt(cartDetails.getQuantity());
            holder.productQuantity.setText(cartDetails.getQuantity());
        }

        if (cartDetails.getPrice().equals("null") || cartDetails.getPrice().equals("")) {
            holder.productUnitPrice.setText("");
        } else {
            String price = cartDetails.getPrice().replaceAll(",","");
            double total = Double.parseDouble(price);
            DecimalFormat precision = new DecimalFormat("0.00");
            holder.productUnitPrice.setText(String.valueOf(precision.format(total)));
//            holder.productUnitPrice.setText(cartDetails.getPrice());
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
                                dbManager.updateFavorite(cartDetails.getProductId(), cartDetails.getColorId(), cartDetails.getSizeId(), "false");
                                callback.OnFavoriteDeleted(cartDetails.getProductId(), cartDetails.getColor(), cartDetails.getSize(), cartDetails.getMd5());
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

        holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                int price = 0;
                if (cartDetails.getQuantity().equals("null") || cartDetails.getQuantity().equals("")) {
                    quantity = 1;
                } else {
                    quantity = Integer.parseInt(cartDetails.getQuantity());
                    if (quantity > 1) {
                        quantity = quantity - 1;
                        cartDetails.setQuantity(String.valueOf(quantity));
                        dbManager.updateCartProductQuantity(String.valueOf(cartDetails.getProductId()), cartDetails.getColorId(), cartDetails.getSizeId(), String.valueOf(quantity));
                        holder.productQuantity.setText(String.valueOf(quantity));
                        if (quantity > 0) {
                            price = Integer.parseInt(cartDetails.getPrice().replaceAll(",","")) * quantity;
                            double total = Double.parseDouble(String.valueOf(price));
                            DecimalFormat precision = new DecimalFormat("0.00");
                            holder.productSubTotal.setText(String.valueOf(precision.format(total)));
//                            holder.productSubTotal.setText(String.valueOf(price));
                        } else {
                            double total = Double.parseDouble(String.valueOf(price));
                            DecimalFormat precision = new DecimalFormat("0.00");
                            holder.productSubTotal.setText(String.valueOf(precision.format(total)));
//                            holder.productSubTotal.setText(cartDetails.getPrice());
                        }
                        callChangeTotal.OnChangeTotal(cartDetails.getPrice(), false);
                    }
                }
            }
        });

        holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                int price = 0;
                if (cartDetails.getQuantity().equals("null") || cartDetails.getQuantity().equals("")) {
                    quantity = 1;
                } else {
                    quantity = Integer.parseInt(cartDetails.getQuantity());
                    quantity = quantity + 1;
                    cartDetails.setQuantity(String.valueOf(quantity));
                    dbManager.updateCartProductQuantity(String.valueOf(cartDetails.getProductId()), cartDetails.getColorId(), cartDetails.getSizeId(), String.valueOf(quantity));
                    holder.productQuantity.setText(String.valueOf(quantity));
                    if (quantity > 0) {
                        price = Integer.parseInt(cartDetails.getPrice().replaceAll(",","")) * quantity;
                        double total = Double.parseDouble(String.valueOf(price));
                        DecimalFormat precision = new DecimalFormat("0.00");
                        holder.productSubTotal.setText(String.valueOf(precision.format(total)));
//                        holder.productSubTotal.setText(String.valueOf(price));
                    } else {
                        double total = Double.parseDouble(cartDetails.getPrice().replaceAll(",",""));
                        DecimalFormat precision = new DecimalFormat("0.00");
                        holder.productSubTotal.setText(String.valueOf(precision.format(total)));
//                        holder.productSubTotal.setText(cartDetails.getPrice());
                    }
                    callChangeTotal.OnChangeTotal(cartDetails.getPrice(), true);
                }
            }
        });

        return rowView;
    }
}
