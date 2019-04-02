package com.darshil.grocerybuddy;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darshil.grocerybuddy.Database.OrderDatabase;
import com.darshil.grocerybuddy.Order.Order;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.Random;

public class DeliveryActivity extends AppCompatActivity {
        Button Map,confirm;
        TextView m_response;
    PayPalConfiguration m_configuration;
    String m_paypalClientId = "AflLSeNdZqTp-Gn-VzoBvorD9H1-reEL2haMXAJwxjTEqARDzRnQ1HP1uR65h72_bZVy5yzcXUZTzsKx";
    Intent m_service;
    int m_paypalRequestCode = 999;
    NotificationHelper notificationHelper;
    EditText distance,destination,price;
    String dist,dest,amount,convert,lat,lng,OrderAmt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_delivery );
        getSupportActionBar().setTitle( "Delivery Details" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        notificationHelper = new NotificationHelper( this );
        Map = (Button) findViewById( R.id.btnMap );
        m_response = (TextView) findViewById( R.id.m_response );
        confirm = (Button) findViewById( R.id.btnConfirm );
        distance = (EditText) findViewById( R.id.edtDistance );
        distance.setText( "0" );
        destination = (EditText) findViewById( R.id.edtDestination );
        destination.setText( " " );
        price = (EditText) findViewById( R.id.edtEstimatedPrice );
        price.setText( "0" );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );
        Map.setTypeface( myCustomFont );
        m_response.setTypeface( myCustomFont );
        confirm.setTypeface( myCustomFont );
        distance.setTypeface( myCustomFont );
        destination.setTypeface( myCustomFont );
        price.setTypeface( myCustomFont );

        dest = getIntent().getStringExtra( "Destination" );
        lat = getIntent().getStringExtra( "Latitude" );
        lng = getIntent().getStringExtra( "Longitude" );
        OrderAmt = getIntent().getStringExtra( "Amount" );
        m_configuration = new PayPalConfiguration()
                .environment( PayPalConfiguration.ENVIRONMENT_SANDBOX )
                .clientId( m_paypalClientId );
        m_service = new Intent( this,PayPalService.class );
        m_service.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION,m_configuration );
        startService( m_service );

        if(distance.getText().toString() != null) {
            dist = getIntent().getStringExtra("Distance");
            distance.setText( dist );
            double km =Float.valueOf( String.valueOf( distance.getText().toString() ) ) / 1000;
            convert = String.valueOf( km );
            distance.setText( String.format( "%.2f",km ) );
        }
        double val = Float.valueOf( String.valueOf( distance.getText().toString() ) ) * 1.05;
        double TotalVal =Double.parseDouble( OrderAmt );
        final double finalAMt = val + TotalVal;
        amount = String.valueOf( finalAMt );
        destination.setText( dest );
        price.setText( String.format( "%.2f",finalAMt ) );
        Map.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( DeliveryActivity.this,ViewDirection.class );
                io.putExtra( "Latitude",lat );
                io.putExtra( "Destination",dest );
                io.putExtra( "Longitude",lng);
                startActivity( io );
            }
        } );
        confirm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayPalPayment cart = new PayPalPayment
                        ( new BigDecimal(finalAMt),"CAD","Order Amount",PayPalPayment.PAYMENT_INTENT_SALE );
                Intent intent = new Intent( DeliveryActivity.this,PaymentActivity.class );
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,m_configuration  );
                intent.putExtra( PaymentActivity.EXTRA_PAYMENT,cart );
                startActivityForResult(intent,m_paypalRequestCode);


            }
        } );
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
                    if (state.equals( "approved" )) {
                        m_response.setText( "Payment approved" );
                        Intent i = new Intent(this,ViewDirection.class );
                        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
                        String title = "Thanks For Your Order!";
                        String content = "Congratulations! Your Order has been placed.Click here to track your Order.";
                        Notification.Builder builder = notificationHelper.getGroceryNotification( title, content )
                                .setContentIntent( pi )
                                .setAutoCancel( true );
                        notificationHelper.getManager().notify( new Random().nextInt(), builder.build() );
                    }
                    else
                        m_response.setText( "Error in the payment" );
                }
                else
                {
                    m_response.setText( "Confirmation is null" );
                }

            }
        }
    }
}
