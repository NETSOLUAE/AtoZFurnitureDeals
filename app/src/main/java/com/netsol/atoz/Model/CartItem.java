package com.netsol.atoz.Model;

/**
 * Created by macmini on 11/16/17.
 */

public class CartItem {
    private String categoryId;
    private String productId;
    private String productCode;
    private String colorId;
    private String sizeId;
    private String name;
    private String color;
    private String size;
    private String quantity;
    private String price;
    private String productImage;
    private String isSaveLater;
    private String isFavorite;
    private String isDeletedApi;
    private String md5;

    public CartItem() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String isSaveLater() {
        return isSaveLater;
    }

    public void setSaveLater(String saveLater) {
        isSaveLater = saveLater;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getIsSaveLater() {
        return isSaveLater;
    }

    public void setIsSaveLater(String isSaveLater) {
        this.isSaveLater = isSaveLater;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getIsDeletedApi() {
        return isDeletedApi;
    }

    public void setIsDeletedApi(String isDeletedApi) {
        this.isDeletedApi = isDeletedApi;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
