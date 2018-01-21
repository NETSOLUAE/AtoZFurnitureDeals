package com.netsol.atoz.Model;

/**
 * Created by macmini on 12/12/17.
 */

public class Color {
    private String categoryId;
    private String productId;
    private String colorId;
    private String colorName;
    private String colorImage;

    public Color() {

    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorImage() {
        return colorImage;
    }

    public void setColorImage(String colorImage) {
        this.colorImage = colorImage;
    }
}
