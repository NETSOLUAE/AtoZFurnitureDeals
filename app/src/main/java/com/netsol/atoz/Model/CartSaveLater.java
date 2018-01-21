package com.netsol.atoz.Model;

/**
 * Created by macmini on 11/16/17.
 */

public class CartSaveLater {
    private int cartProductImage;
    private String name;
    private String price;

    public CartSaveLater(int cartProductImage, String name, String price) {
        this.cartProductImage = cartProductImage;
        this.name= name;
        this.price = price;
    }
    public int getCartProductImage() {
        return cartProductImage;
    }

    public void setCartProductImage(int cartProductImage) {
        this.cartProductImage = cartProductImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}