package com.netsol.atoz.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.netsol.atoz.Activity.CartActivity;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.CartItem;
import com.netsol.atoz.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by macmini on 11/16/17.
 */

public class CartItemAdapter extends BaseAdapter {
    private final DatabaseManager dbManager;
    private OnCartDeleted callback;
    private OnSaveLater callbacksave;
    private OnChangeGrandTotal callChangeTotal;
    private ArrayList<CartItem> cartItemList;
    private Context context;
    private static LayoutInflater inflater = null;

    public CartItemAdapter(CartActivity cartContext, ArrayList<CartItem> cartItem) {
        // TODO Auto-generated constructor stub
        cartItemList = cartItem;
        context = cartContext;
        dbManager = new DatabaseManager(context);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnCartDeletedListener(OnCartDeleted callback) {
        this.callback = callback;
    }

    public interface OnCartDeleted {
        public void OnCartItemDeleted(String productID, String color, String size, String colorId, String sizeId, String md5);
    }

    public void setOnSaveLaterListener(OnSaveLater callbacksave) {
        this.callbacksave = callbacksave;
    }

    public interface OnSaveLater {
        public void OnSaveLaterItem(String productID, String color, String size, String colorId, String sizeId, String md5);
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
        Button buttonSaveLater;
        TextView productName;
        TextView productCode;
        TextView productColor;
        TextView productSize;
        TextView productQuantity;
        TextView productUnitPrice;
        TextView productSubTotal;
        ImageView productImage;
        ImageView buttonDelete;
        ImageView buttonFavorite;
        ImageView buttonMinus;
        ImageView buttonPlus;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.cart_item_view, null);
        final CartItem cartDetails = (CartItem) getItem(position);
        holder.productImage = (ImageView) rowView.findViewById(R.id.cart_item_image);
        holder.buttonDelete = (ImageView) rowView.findViewById(R.id.button_cart_delete);
        holder.buttonFavorite = (ImageView) rowView.findViewById(R.id.button_cart_favorite);
        holder.buttonMinus = (ImageView) rowView.findViewById(R.id.button_cart_minus);
        holder.buttonPlus = (ImageView) rowView.findViewById(R.id.button_cart_plus);
        holder.buttonSaveLater = (Button) rowView.findViewById(R.id.button_save_later);
        holder.productName = (TextView) rowView.findViewById(R.id.cart_product_name);
        holder.productColor = (TextView) rowView.findViewById(R.id.cart_product_color);
        holder.productCode = (TextView) rowView.findViewById(R.id.cart_product_item_code);
        holder.productSize = (TextView) rowView.findViewById(R.id.cart_product_size);
        holder.productQuantity = (TextView) rowView.findViewById(R.id.cart_product_quantity);
        holder.productUnitPrice = (TextView) rowView.findViewById(R.id.cart_item_price);
        holder.productSubTotal = (TextView) rowView.findViewById(R.id.cart_item_sub_total_price);

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
            holder.productCode.setText(String.format("Item Code: %s", cartDetails.getProductCode()));
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

        if (cartDetails.getIsFavorite().equals("null") || cartDetails.getIsFavorite().equals("")) {
            holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
        } else {
            if (cartDetails.getIsFavorite().equals("false")) {
                holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
            } else if (cartDetails.getIsFavorite().equals("true")) {
                holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite_selected_filled));
            } else {
                holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
            }
        }

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
//            holder.productUnitPrice.setText(price);
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
                                callback.OnCartItemDeleted(cartDetails.getProductId(), cartDetails.getColor(), cartDetails.getSize(), cartDetails.getColorId(),
                                        cartDetails.getSizeId(), cartDetails.getMd5());
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

        holder.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFavorite = cartDetails.getIsFavorite();
                if (!isFavorite.equals("null") && !isFavorite.equals("")) {
                    if (isFavorite.equalsIgnoreCase("false")) {
                        dbManager.updateFavorite(cartDetails.getProductId(), cartDetails.getColorId(), cartDetails.getSizeId(), "true");
                        holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite_selected_filled));
                        cartDetails.setIsFavorite("true");
                    } else if (isFavorite.equalsIgnoreCase("true")) {
                        dbManager.updateFavorite(cartDetails.getProductId(), cartDetails.getColorId(), cartDetails.getSizeId(), "false");
                        holder.buttonFavorite.setBackground(context.getDrawable(R.drawable.cart_favorite));
                        cartDetails.setIsFavorite("false");
                    }
                }
            }
        });

        holder.buttonSaveLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacksave.OnSaveLaterItem(cartDetails.getProductId(), cartDetails.getColor(), cartDetails.getSize(), cartDetails.getColorId(),
                        cartDetails.getSizeId(), cartDetails.getMd5());
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
                            String price1 = cartDetails.getPrice().replaceAll(",","");
                            double total = Double.parseDouble(price1);
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
