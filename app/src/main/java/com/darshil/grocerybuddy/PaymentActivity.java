package com.darshil.grocerybuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {
    EditText etCardNumber,etCvvNumber,etExpiry;
    Button save;
    private ProgressDialog loadingBar;
    String cardNum,cvvNum,cardExp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_payment );
        getSupportActionBar().setTitle( "Payment Details" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );
        loadingBar = new ProgressDialog( this );
        etCardNumber = (EditText)findViewById( R.id.etCardNumber );
        etCvvNumber = (EditText) findViewById( R.id.etCvvNumber );
        etExpiry = (EditText) findViewById( R.id.etExpiry );
        save = (Button) findViewById( R.id.btnSaveCard );
        etCardNumber.setTypeface( myCustomFont );
        etCvvNumber.setTypeface( myCustomFont );
        etExpiry.setTypeface( myCustomFont );
        save.setTypeface( myCustomFont );
        save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDetails();
            }
        } );
    }

    private void checkDetails() {
        cardNum = etCardNumber.getText().toString();
        cvvNum = etCvvNumber.getText().toString();
        cardExp = etExpiry.getText().toString();

        if(TextUtils.isEmpty( cardNum ))
        {
            Toast.makeText( this, "Please add your Card Number!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( cvvNum ))
        {
            Toast.makeText( this, "Please add your 3 digit Cvv Number!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( cardExp ))
        {
            Toast.makeText( this, "Please add your card's Expiry date!", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            loadingBar.setTitle( "Card Details" );
            loadingBar.setMessage( "Please wait, while we are checking your card details" );
            loadingBar.setCanceledOnTouchOutside( false );
            loadingBar.show();

            VlidateCardDetails(cardNum,cvvNum,cardExp);
        }

    }

    private void VlidateCardDetails(final String cardNum, final String cvvNum, final String cardExp) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child( "Cards" ).child( cardNum ).exists()))
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put( "cardNum",cardNum );
                    userdataMap.put( "CardCvv",cvvNum );
                    userdataMap.put( "CardExp",cardExp );

                    RootRef.child( "Cards" ).child( cardNum ).updateChildren( userdataMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText( PaymentActivity.this, "Congratulations, Your cards has been added.", Toast.LENGTH_SHORT ).show();
                                loadingBar.dismiss();
                                Intent io = new Intent( PaymentActivity.this,MainActivity.class );
                                startActivity( io );

                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText( PaymentActivity.this, "Network Error: Please try again later. ", Toast.LENGTH_SHORT ).show();
                            }

                        }
                    } );
                }
                else
                {
                    {
                        Toast.makeText( PaymentActivity.this, "This "+cardNum+ "already Exists!", Toast.LENGTH_SHORT ).show();
                        loadingBar.dismiss();
                        Toast.makeText( PaymentActivity.this, "Please try another card  number to save!!", Toast.LENGTH_SHORT ).show();
                        Intent io = new Intent( PaymentActivity.this,MainActivity.class );
                        startActivity( io );
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
