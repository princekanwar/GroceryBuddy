package com.darshil.grocerybuddy.Order;

public class Order {
   private String ProductId;
    private String ProductName;
    private String ProductPrice;

    public Order(String productId, String productName, String price)
    {

    }

    public Order(String productId, String productName, String productQuantity, String productPrice) {
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


    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }
}

