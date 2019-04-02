package com.darshil.grocerybuddy;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darshil.grocerybuddy.Database.OrderDatabase;
import com.darshil.grocerybuddy.Order.Cart;
import com.darshil.grocerybuddy.Order.Order;
import com.darshil.grocerybuddy.Order.Product;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.Random;

public class OrderActivity extends AppCompatActivity {

    //    ListView lst;
//    String[] groceryName = {"Milk","Bread","Tea","Coffee","Tomatoes","Lettuce","Green Onion","garlic","banana","Apple","strawberry","Flour"};
//    String[] groceryDesc = {"This is Milk","This is Bread","This is Tea","This is Coffee","This is Tomatoes","This is Lettuce","This is Green Onions","This is garlic","This is banana","This is Apple","This is strawberry","This is Flour"};
//    Integer[] imgId = {R.drawable.milk,R.drawable.bread,R.drawable.tea,R.drawable.coffee,R.drawable.tomatoes,R.drawable.lettuce,R.drawable.greenonion,R.drawable.garlic,R.drawable.banana,R.drawable.apple,R.drawable.strawberry,R.drawable.flour};
    LinearLayout list;
    int i;
    NotificationHelper notificationHelper;
   static Cart m_cart;
   Product currentProduct;
   static OrderDatabase m_order;
   PayPalConfiguration m_configuration;
   String m_paypalClientId = "AflLSeNdZqTp-Gn-VzoBvorD9H1-reEL2haMXAJwxjTEqARDzRnQ1HP1uR65h72_bZVy5yzcXUZTzsKx";
   Intent m_service;
    String dist,dest,amount,lat,lng;
   int m_paypalRequestCode = 999;
        TextView m_response;
        Button order,reset,viewOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_order );
        getSupportActionBar().setTitle( "Make Your Order" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
//        lst = (ListView) findViewById( R.id.lst_order );
//        CustomListview customListview = new CustomListview( this,groceryName,groceryDesc,imgId );
//        lst.setAdapter(customListview);
        notificationHelper = new NotificationHelper( this );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );
        dest = getIntent().getStringExtra( "Destination" );
        lat = getIntent().getStringExtra( "Latitude" );
        lng = getIntent().getStringExtra( "Longitude" );
        dist = getIntent().getStringExtra("Distance");
        order = (Button) findViewById( R.id.placeOrder );
        reset = (Button) findViewById( R.id.reset );
        viewOrder = (Button) findViewById( R.id.viewOrder ) ;
        order.setTypeface( myCustomFont );
        reset.setTypeface( myCustomFont );
        viewOrder.setTypeface( myCustomFont );
        m_cart = new Cart();
        currentProduct = new Product(  );
        m_response = (TextView) findViewById( R.id.response );
        m_response.setTypeface( myCustomFont );
        m_response.setText( "" );
        list = (LinearLayout) findViewById( R.id.list );
        m_configuration = new PayPalConfiguration()
                .environment( PayPalConfiguration.ENVIRONMENT_SANDBOX )
                .clientId( m_paypalClientId );
        m_service = new Intent( this,PayPalService.class );
        m_service.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION,m_configuration );
        startService( m_service );
       final Product products[] =
                {
                        new Product("1","Milk",5.20),
                        new Product("2","Sugar",8.21),
                        new Product("3","Eggs",3.20),
                        new Product("4","Banana",6.20),
                        new Product("5","Apples",9.50),
                        new Product("6","Cleaning Stuffs",15.90),
                        new Product("7","Bread",5.10),
                        new Product("8","Vegetables",25.20),
                        new Product("9","Guacamole",35.20),
                        new Product("10","Tea",5.20)
                };

        for( i=0; i < products.length; i++)
        {
            Drawable d = getResources().getDrawable( R.drawable.btn_rectangle_pink );
            Button button = new Button(this);
            button.setText( products[i].getProductName()+ " --- " + products[i].getProductPrice() + " $" );
            button.setTag( products[i] );
            button.setTextSize(20);
            button.setBackgroundDrawable( d );
            button.setTextColor( Color.parseColor( "#ffffff" ) );
            button.setTypeface( myCustomFont );
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,200,Gravity.CENTER );
            layoutParams.setMargins(  20,50,20,50);
            button.setLayoutParams( layoutParams );
            button.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Button button = (Button) v;
                     currentProduct = (Product) button.getTag();
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                    builder.setTitle( "Item Details");
                    builder.setMessage(" This is : "+button.getText().toString());
                    builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    } );
                    builder.show();
                    return true;
                }
            } );

            button.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Button button = (Button) v;
                    currentProduct = (Product) button.getTag();
                    m_cart.addToCart(currentProduct );

                    m_response.setText("Total Cart Value= "+String.format( "%.2f",m_cart.getValue())+" $");

                }
            } );

                     list.addView( button );

        }


        order.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent( OrderActivity.this,DeliveryActivity.class );
                PendingIntent pi = PendingIntent.getActivity(OrderActivity.this,0,i,0);
                    String title = "Order Details";
                    String content = "Woah! Your Order is in Process State.";
                    Notification.Builder builder = notificationHelper.getGroceryNotification( title, content )
                            .setAutoCancel( true )
                            .setContentIntent( pi );
                    notificationHelper.getManager().notify( new Random().nextInt(), builder.build() );

                    new OrderDatabase( getBaseContext() ).addToCart( new Order(
                            currentProduct.getProductId(),
                            currentProduct.getProductName(),
                            currentProduct.getProductPrice().toString()

                    ) );
                    amount = String.format( "%.2f", m_cart.getValue() );
                    Intent io = new Intent( OrderActivity.this, DeliveryActivity.class );
                    io.putExtra( "Distance", dist );
                    io.putExtra( "Destination", dest );
                    io.putExtra( "Latitude", lat );
                    io.putExtra( "Longitude", lng );
                    io.putExtra( "Amount", amount );
                    startActivity( io );

                }

        } );

    }

    void reset(View view)
    {
        m_response.setText( "" );
        m_cart.empty();
    }
    void viewCart(View view)
    {
        Intent intent = new Intent( this,ViewCart.class );
        m_cart = m_cart;
        startActivity( intent );
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == m_paypalRequestCode)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra( PaymentActivity.EXTRA_RESULT_CONFIRMATION );
                if (confirmation != null)
                {
                    String state = confirmation.getProofOfPayment().getState();
                    if (state.equals( "approved" ))
                        m_response.setText( "payment approved" );
                    else
                        m_response.setText( "error in the payment" );
                }
                else
                {
                    m_response.setText( "confirmation is null" );
                }

            }
        }
    }
}
