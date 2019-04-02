package com.darshil.grocerybuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darshil.grocerybuddy.Model.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    CircleImageView profileImgView;
    EditText name,phone,address,pass;
    TextView profileChange,close,update;
    private Uri imageUri;
    private String myUrl= "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureReferance;
    private String checker = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        storageProfilePictureReferance = FirebaseStorage.getInstance().getReference().child( "Profile pictures" );
        profileChange = (TextView)findViewById( R.id.profile_image_change );
        profileImgView = (CircleImageView) findViewById( R.id.profile_settings );
        name = (EditText) findViewById( R.id.settings_fullName );
        phone = (EditText) findViewById( R.id.settings_phone );
        address = (EditText) findViewById( R.id.settings_address );
        close = (TextView) findViewById( R.id.close_seetings );
        update = (TextView) findViewById( R.id.update_settings );
        pass = (EditText) findViewById( R.id.settings_password );
        userInfoDisplay(profileImgView,name,phone,address,pass);
        close.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
        update.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals( "clicked" ))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        } );
        profileChange.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio( 1,1 )
                        .start(SettingsActivity.this);
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK  && data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            imageUri = result.getUri();
            profileImgView.setImageURI( imageUri );
        }
        else
        {
            Toast.makeText( this, "Error, Try Again..", Toast.LENGTH_SHORT ).show();
            startActivity( new Intent( SettingsActivity.this,SettingsActivity.class ) );
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty( name.getText().toString() ))
        {
            Toast.makeText( this, "Name is mandatory...", Toast.LENGTH_SHORT ).show();
        }
        else if(TextUtils.isEmpty( address.getText().toString() ))
        {
            Toast.makeText( this, "Address is mandatory...", Toast.LENGTH_SHORT ).show();
        }
        else if(TextUtils.isEmpty( phone.getText().toString() ))
        {
            Toast.makeText( this, "Phone is mandatory...", Toast.LENGTH_SHORT ).show();
        }
        else if(TextUtils.isEmpty( pass.getText().toString() ))
        {
            Toast.makeText( this, "Password is mandatory...", Toast.LENGTH_SHORT ).show();
        }
        else if (checker.equals( "clicked" ))
        {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle( "Update Profile" );
        progressDialog.setMessage( "Please Wait,while we are uploading your account information... " );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.show();

        if(imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureReferance
                    .child( Prevalent.currentOnlineUser.getPhone() + ".jpg" );


            uploadTask = fileRef.putFile( imageUri );
            uploadTask.continueWithTask( new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri download = task.getResult();
                        myUrl = download.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child( "Users" );
                        HashMap<String,Object> userMap = new HashMap<>(  );
                        userMap.put( "name",name.getText().toString() );
                        userMap.put( "address",address.getText().toString() );
                        userMap.put( "phoneOrder",phone.getText().toString() );
                        userMap.put( "password",pass.getText().toString() );
                        userMap.put( "image",myUrl);
                        ref.child( Prevalent.currentOnlineUser.getPhone()).updateChildren( userMap );
                        progressDialog.dismiss();
                        startActivity( new Intent( SettingsActivity.this,MainActivity.class ) );
                        Toast.makeText( SettingsActivity.this, "Profile Info updated Successfully...", Toast.LENGTH_SHORT ).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText( SettingsActivity.this, "Error.", Toast.LENGTH_SHORT ).show();
                        progressDialog.dismiss();
                    }
                }
            } );
        }
        else
        {
            Toast.makeText( this, "Image is not Selected...", Toast.LENGTH_SHORT ).show();
        }
    }

    private void updateOnlyUserInfo(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child( "Users" );
        HashMap<String,Object> userMap = new HashMap<>(  );
        userMap.put( "name",name.getText().toString() );
        userMap.put( "address",address.getText().toString() );
        userMap.put( "phoneOrder",phone.getText().toString() );
        userMap.put( "password",pass.getText().toString() );
        ref.child( Prevalent.currentOnlineUser.getPhone()).updateChildren( userMap );
        startActivity( new Intent( SettingsActivity.this,MainActivity.class ) );
        Toast.makeText( SettingsActivity.this, "Profile Info updated Successfully...", Toast.LENGTH_SHORT ).show();
        finish();

    }

    private void userInfoDisplay(final CircleImageView profileImgView, final EditText name, final EditText phone, final EditText address, final EditText password)
    {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( Prevalent.currentOnlineUser.getPhone() );
        UserRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                if(dataSnapshot.child( "image" ).exists())
                {
                    String image = dataSnapshot.child( "image" ).getValue().toString();
                    String nm = dataSnapshot.child( "name" ).getValue().toString();
                    String pass = dataSnapshot.child( "password" ).getValue().toString();
                    String phn = dataSnapshot.child( "phone" ).getValue().toString();
                    String add = dataSnapshot.child( "address" ).getValue().toString();
                    Picasso.get().load( image).into( profileImgView );
                    name.setText( nm );
                    phone.setText( phn );
                    address.setText( add );
                    password.setText( pass );

                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
