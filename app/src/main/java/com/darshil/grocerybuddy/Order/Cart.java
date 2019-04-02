package com.darshil.grocerybuddy.Order;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Cart {

    Map<Product, Integer>m_cart;
    double m_value = 0;

   public Cart()
    {
        m_cart = new LinkedHashMap<>();
    }

 public void addToCart(Product product)
    {
        if(m_cart.containsKey( product ))
            m_cart.put( product,m_cart.get( product ) + 1 );
        else
            m_cart.put( product,1 );

        m_value += product.getProductPrice();
    }

   public int getQuantity(Product product)
    {
        return m_cart.get( product );
    }
   public Set getProducts()
    {
        return m_cart.keySet();
    }

   public void empty()
    {
        m_cart.clear();
        m_value = 0;
    }
   public double getValue()
    {
        return m_value;
    }
   public int getSize()
    {
        return m_cart.size();
    }
}
