package com.darshil.grocerybuddy.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.darshil.grocerybuddy.Order.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderDatabase extends SQLiteAssetHelper {
    private static final String DB_NAME = "groceryorder.db";
    private static final int DB_VER = 1;

    public OrderDatabase(Context context) {
        super( context, DB_NAME, null, DB_VER );
    }



    public List<Order> getCarts()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"ProductName","ProductId","Price"};
        String sqlTable = "OrderDetail";
        qb.setTables( sqlTable );
        Cursor c = qb.query( db,sqlSelect,null,null,null,null,null );
        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst())
        {
            do{
                result.add( new Order(c.getString( c.getColumnIndex( "ProductId" )),
                            c.getString( c.getColumnIndex( "ProductName" ) ),
                        c.getString( c.getColumnIndex( "Price" ) )
                ) );
            }while (c.moveToNext());
        }
        return result;
    }
    public void addToCart(Order order)
    {
        SQLiteDatabase  db = getReadableDatabase();
        String query = String.format( "INSERT INTO OrderDetail (ProductId,ProductName,Price) VALUES('%s','%s','%s');" ,
                order.getProductId(),
                order.getProductName(),
                order.getProductPrice());
        db.execSQL( query );
    }
    public void cleanCart()
    {
        SQLiteDatabase  db = getReadableDatabase();
        String query = String.format( "DELETE FROM OrderDetail" );
        db.execSQL( query );
    }
}
