package com.darshil.grocerybuddy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darshil.grocerybuddy.Database.Account;
import com.darshil.grocerybuddy.Database.AccountDB;
import com.darshil.grocerybuddy.Model.Prevalent;
import com.darshil.grocerybuddy.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    TextView signUp,logo;
    EditText uName,pass;
    Button login;
    private TextView admin,notAdmin;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        signUp = (TextView) findViewById( R.id.txtSignUp);
        uName = (EditText) findViewById( R.id.editTextUserName );
        pass = (EditText) findViewById( R.id.editTextPassword );
        login = (Button) findViewById( R.id.btnLogin );
        logo = (TextView) findViewById( R.id.txtappName );
        admin = (TextView) findViewById( R.id.txtadmin );
        notAdmin = (TextView) findViewById( R.id.txtNotadmin );
        loadingBar = new ProgressDialog( this );

        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );
        logo.setTypeface( myCustomFont );
        signUp.setTypeface( myCustomFont );
        uName.setTypeface( myCustomFont );
        pass.setTypeface( myCustomFont );
        login.setTypeface( myCustomFont );
        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login_onClick(v);
                LoginUser();

            }
        } );
        notAdmin.setVisibility( View.INVISIBLE );
        signUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp_onClick(v);
            }
        } );
        admin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText( "Login Admin" );
                admin.setVisibility(View.INVISIBLE );
                notAdmin.setVisibility( View.VISIBLE );
                parentDbName = "Admins";

            }
        } );
        notAdmin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText( "Login" );
                admin.setVisibility(View.VISIBLE );
                notAdmin.setVisibility( View.INVISIBLE );
                parentDbName = "Users";
            }
        } );
    }

    private void LoginUser() {
        String sphone = uName.getText().toString();
        String spassword = pass.getText().toString();
        if (TextUtils.isEmpty( sphone ))
        {
            Toast.makeText( this, "Please write your Username!!", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( spassword ))
        {
            Toast.makeText( this, "Please write your Password!!", Toast.LENGTH_SHORT ).show();
        }
        else
        {

            loadingBar.setTitle( "Login Account" );
            loadingBar.setMessage( "Please wait, while we are checking the credentials" );
            loadingBar.setCanceledOnTouchOutside( false );
            loadingBar.show();
            AllowAccessToAccount(sphone,spassword);
        }
    }

    private void AllowAccessToAccount(final String sphone, final String spassword) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child( parentDbName ).child( sphone ).exists())
                {
                    Users userData = dataSnapshot.child( parentDbName).child( sphone).getValue(Users.class);
                    if(userData.getPhone().equals( sphone ))
                    {
                        if(userData.getPassword().equals( spassword ))
                        {
                            if(parentDbName.equals( "Admins" ))
                            {
                                Toast.makeText( LoginActivity.this, "Welcome Admin , You are logged in Succesfully.....", Toast.LENGTH_SHORT ).show();
                                loadingBar.dismiss();
                                Intent io = new Intent( LoginActivity.this,AdminCategoryActivity.class );
                                startActivity( io );
                            }
                            else if(parentDbName.equals( "Users" ))
                            {
                                Toast.makeText( LoginActivity.this, "Welcome User , You are logged in Succesfully.....", Toast.LENGTH_SHORT ).show();
                                loadingBar.dismiss();
                                Intent io = new Intent( LoginActivity.this,MainActivity.class );
                                Prevalent.currentOnlineUser = userData;
                                startActivity( io );

                            }
                        }
                        else
                        {
                            Toast.makeText( LoginActivity.this, "Wron Credentias!!", Toast.LENGTH_SHORT ).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText( LoginActivity.this, "Account with this "+sphone+" number do not exist!!", Toast.LENGTH_SHORT ).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

//    public void login_onClick(View v) {
//        AccountDB accountDB = new AccountDB( getApplicationContext() );
//        String username = uName.getText().toString();
//        String password = pass.getText().toString();
//        Account account = accountDB.login( username, password );
//        if(account == null)
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder( v.getContext() );
//            builder.setTitle( R.string.error );
//            builder.setMessage( R.string.invalid_account );
//            builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            } );
//            builder.show();
//
//        }
//        else
//        {
//            Intent io = new Intent( LoginActivity.this,MainActivity.class );
//            io.putExtra( "account",account );
//            startActivity( io );
//        }
//    }




    public void signUp_onClick(View v) {
        Intent io = new Intent( LoginActivity.this,SignUpActivity.class );
        startActivity( io );
    }

}
