package com.darshil.grocerybuddy.Order;


import android.media.Image;

public class Product {
    private String ProductId;
    private String ProductName;
    private double ProductPrice;

    public Product()
    {

    }

    public Product(String productId, String productName, Double productPrice) {
        ProductId = productId;
        ProductName = productName;
        ProductPrice = productPrice;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Double getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(Double productPrice) {
        ProductPrice = productPrice;
    }

}
