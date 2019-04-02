package com.darshil.grocerybuddy;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class PaymentActivity extends AppCompatActivity {
    EditText etCardNumber,etCvvNumber,etExpiry;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_payment );
        getSupportActionBar().setTitle( "Payment Details" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );
        etCardNumber = (EditText)findViewById( R.id.etCardNumber );
        etCvvNumber = (EditText) findViewById( R.id.etCvvNumber );
        etExpiry = (EditText) findViewById( R.id.etExpiry );
        save = (Button) findViewById( R.id.btnSaveCard );
        etCardNumber.setTypeface( myCustomFont );
        etCvvNumber.setTypeface( myCustomFont );
        etExpiry.setTypeface( myCustomFont );
        save.setTypeface( myCustomFont );


    }
}
