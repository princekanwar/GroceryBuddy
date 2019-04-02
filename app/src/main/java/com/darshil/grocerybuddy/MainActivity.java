package com.darshil.grocerybuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.darshil.grocerybuddy.Database.Account;
import com.darshil.grocerybuddy.Model.Prevalent;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    Button trackme;
    EditText postalCode;
    ActionBarDrawerToggle toggle;
    private Account account;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuitem,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Typeface myCustomFont = Typeface.createFromAsset( getAssets(),"fonts/Walkway_Black.ttf" );
        android.support.v7.widget.Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        postalCode = (EditText) findViewById( R.id.postalcode );
        drawer = findViewById( R.id.drawer_layout );
        toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById( R.id.navigation_view );
        navigationView.setNavigationItemSelectedListener( this );
        View headerView = navigationView.getHeaderView( 0 );
        TextView username = headerView.findViewById( R.id. txtUname);
        CircleImageView profileImage = headerView.findViewById( R.id.profile_image );
        username.setText( Prevalent.currentOnlineUser.getName() );
        Picasso.get().load( Prevalent.currentOnlineUser.getImage()).placeholder( R.drawable.profile ).into( profileImage );
        trackme = (Button) findViewById( R.id.track_me );
        trackme.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String po = postalCode.getText().toString();
                Intent io = new Intent( MainActivity.this, MapsActivity.class );
                io.putExtra( "PostalCode",po );
                startActivity( io );
        }
        } );

        trackme.setTypeface( myCustomFont );
        postalCode.setTypeface( myCustomFont );
        Intent io = getIntent();
        account = (Account) io.getSerializableExtra( "account" );

        //Toast.makeText( this, "Welcome "+""+account.getUsername()+"!", Toast.LENGTH_SHORT ).show();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile: {
//                Intent intent = new Intent( MainActivity.this, ProfileActivity.class );
//                intent.putExtra( "account", account );
//                startActivity( intent );
                Intent io = new Intent( MainActivity.this,SettingsActivity.class );
                startActivity( io );
                break;
            }
            case R.id.nav_payment: {
                Intent intent = new Intent( MainActivity.this, PaymentActivity.class );
                startActivity( intent );
                break;
            }
            case R.id.nav_delivery: {
                AlertDialog.Builder builder = new AlertDialog.Builder( this );
                builder.setTitle( "Delivery Details" );
                builder.setMessage( "If you want to place an order for delivery then you have to select the nearest grocery store first!!" );
                builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                } );
                builder.show();
                break;
            }
            case R.id.nav_order: {
//                Intent intent = new Intent( MainActivity.this, OrderActivity.class );
//                startActivity( intent );
//                Toast.makeText( this, "Order", Toast.LENGTH_SHORT ).show();
//                AlertDialog.Builder builder = new AlertDialog.Builder( this );
//                builder.setTitle( "Order Details" );
//                builder.setMessage( "If you want to place an order you have to select the nearest grocery store first!!" );
//                builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                } );
//                builder.show();
                Intent io = new Intent( MainActivity.this,OrdersFirebaseActivity.class );
                startActivity( io );
                break;
            }
            case R.id.nav_orders: {
                Toast.makeText( this, "New Orders", Toast.LENGTH_SHORT ).show();
                break;
            }
            
            case R.id.nav_category:{
                Toast.makeText( this, "New Category", Toast.LENGTH_SHORT ).show();
                break;
            }

            case R.id.nav_settings:{
                Intent io = new Intent( MainActivity.this,SettingsActivity.class );
                startActivity( io );
                break;
            }

            case R.id.nav_logout: {
                Intent io = new Intent( MainActivity.this,LoginActivity.class );
                startActivity( io );
                finish();
            }

        }


  drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen( GravityCompat.START ))
        {
            drawer.closeDrawer( GravityCompat.START );
        }
        else {
            super.onBackPressed();
        }
        }

    private void setSupportActionBar(Toolbar toolbar) {

    }

}
