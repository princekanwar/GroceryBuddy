package com.darshil.grocerybuddy;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView Veg,Fruits,Egg,NonVeg,Milk,Spices,Dairy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_category );
        Veg = (ImageView) findViewById( R.id.I_Veg );
        Fruits = (ImageView)findViewById( R.id.I_Fruits );
        Egg = (ImageView) findViewById( R.id.I_Eggs );
        NonVeg = (ImageView) findViewById( R.id.I_NonVeg );
        Milk = (ImageView) findViewById( R.id.I_Milk );
        Spices = (ImageView) findViewById( R.id.I_Spices );
        Dairy = (ImageView) findViewById( R.id.I_Dairy );

        Veg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","Vegetables" );
                startActivity( io );
            }
        } );
        Fruits.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","Fruits" );
                startActivity( io );
            }
        } );

        Egg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","Eggs" );
                startActivity( io );
            }
        } );

        Milk.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","Milks" );
                startActivity( io );
            }
        } );

        Spices.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","Spices" );
                startActivity( io );
            }
        } );

        NonVeg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","NonVeg" );
                startActivity( io );
            }
        } );

        Dairy.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( AdminCategoryActivity.this,AdminPanelActivity.class );
                io.putExtra( "category","Dairy" );
                startActivity( io );
            }
        } );
    }
}
