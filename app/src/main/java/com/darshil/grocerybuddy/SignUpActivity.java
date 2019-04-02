package com.darshil.grocerybuddy;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.darshil.grocerybuddy.Database.Account;
import com.darshil.grocerybuddy.Database.AccountDB;
import com.darshil.grocerybuddy.Database.ImageDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    final int REQUEST_CODE_GALLERY = 100;
    EditText uname, pwd, fname, lname, email, contact, image;
    Button save, cancel;
    ImageView myImgView;
    ImageDatabase imgDb;
    String suname, spwd, sfname, slname, semail, scontact;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );

        uname = (EditText) findViewById( R.id.etUsernamesignUP );
        pwd = (EditText) findViewById( R.id.etPasswordsignUP );
        fname = (EditText) findViewById( R.id.etFnamesignUP );
        lname = (EditText) findViewById( R.id.etLnamesignUP );
        email = (EditText) findViewById( R.id.etemailsignUP );
        contact = (EditText) findViewById( R.id.etNumbersignUP );
        save = (Button) findViewById( R.id.btnSaveSignUP );

        cancel = (Button) findViewById( R.id.btnCancelsignUP );
        loadingBar = new ProgressDialog( this );
        uname.setTypeface( myCustomFont );
        pwd.setTypeface( myCustomFont );
        fname.setTypeface( myCustomFont );
        lname.setTypeface( myCustomFont );
        email.setTypeface( myCustomFont );
        contact.setTypeface( myCustomFont );
        save.setTypeface( myCustomFont );
        cancel.setTypeface( myCustomFont );

        save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // save_onClick( v );
                createAccount();
            }
        } );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent io = new Intent( SignUpActivity.this, LoginActivity.class );
                startActivity( io );
            }
        } );

    }

    private void createAccount() {
        scontact = contact.getText().toString();
        semail = email.getText().toString();
        slname = lname.getText().toString();
        sfname = fname.getText().toString();
        suname = uname.getText().toString();
        spwd = pwd.getText().toString();

        if(TextUtils.isEmpty( scontact ))
        {
            Toast.makeText( this, "Please add your Contact Number!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( semail ))
        {
            Toast.makeText( this, "Please add your Email!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( slname ))
        {
            Toast.makeText( this, "Please add your Last Name!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( sfname ))
        {
            Toast.makeText( this, "Please add your First Name!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( suname))
        {
            Toast.makeText( this, "Please add your Username!", Toast.LENGTH_SHORT ).show();
        }

        else if(TextUtils.isEmpty( spwd )) {
            Toast.makeText( this, "Please add your Password!", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            loadingBar.setTitle( "Create Account" );
            loadingBar.setMessage( "Please wait, while we are checking the credentials" );
            loadingBar.setCanceledOnTouchOutside( false );
            loadingBar.show();

            VlidatePhoneNumber(suname,scontact,spwd);
        }
    }
    private void VlidatePhoneNumber(final String suname, final String scontact, final String spwd) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child( "Users" ).child( scontact ).exists()))
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put( "phone",scontact );
                    userdataMap.put( "name",suname );
                    userdataMap.put( "password",spwd );
                    userdataMap.put( "email",semail );
                    userdataMap.put( "fname",sfname );
                    userdataMap.put( "lname", slname);

                    RootRef.child("Users").child(scontact).updateChildren( userdataMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText( SignUpActivity.this, "Congratulations, Your account has been created.", Toast.LENGTH_SHORT ).show();
                                loadingBar.dismiss();
                                Intent io = new Intent( SignUpActivity.this,LoginActivity.class );
                                startActivity( io );
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText( SignUpActivity.this, "Network Error: Please try again later. ", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );
                }
                else
                {
                    Toast.makeText( SignUpActivity.this, "This "+scontact+ "alreat Exists!", Toast.LENGTH_SHORT ).show();
                    loadingBar.dismiss();
                    Toast.makeText( SignUpActivity.this, "Please try anothr number to sign in!!", Toast.LENGTH_SHORT ).show();
                    Intent io = new Intent( SignUpActivity.this,LoginActivity.class );
                    startActivity( io );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


    public void save_onClick(View v) {
        try {
            AccountDB accountDB = new AccountDB( getApplicationContext() );
            Account account = new Account();
            account.setEmail( email.getText().toString() );
            account.setContact( contact.getText().toString() );
            account.setLastName( lname.getText().toString() );
            account.setFirstName( fname.getText().toString() );
            account.setUsername( uname.getText().toString() );
            account.setPassword( pwd.getText().toString() );
            // account.setImage( myImgView.setImageBitmap( bitmap ); );
            Account temp = accountDB.checkUsername( uname.getText().toString() );
            if (temp == null) {
                if (accountDB.create( account )) {
                    Intent io = new Intent( SignUpActivity.this, LoginActivity.class );
                    startActivity( io );

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder( v.getContext() );
                    builder.setTitle( R.string.error );
                    builder.setMessage( R.string.can_not_create );
                    builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    } );
                    builder.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder( v.getContext() );
                builder.setTitle( R.string.error );
                builder.setMessage( R.string.username_exist );
                builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                } );
                builder.show();
            }
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder( v.getContext() );
            builder.setTitle( R.string.error );
            builder.setMessage( e.getMessage() );
            builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            } );
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        AccountDB accountDB;
        super.onActivityResult( requestCode, resultCode, data );
        Bitmap bitmap = (Bitmap)data.getExtras().get( "data" );
        myImgView.setImageBitmap( bitmap );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress( Bitmap.CompressFormat.PNG,100,stream );
        byte[] byteArray = stream.toByteArray();
        //accountDB.create( byteArray );
    }
}

