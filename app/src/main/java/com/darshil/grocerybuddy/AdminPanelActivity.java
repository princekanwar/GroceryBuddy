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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminPanelActivity extends AppCompatActivity {
    private String categoryName,Description,Price,Productname , saveCurrentDate,saveCurrentTime;
    private ImageView produtImage;
    private Button add;
    private EditText name,desc,price;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String pRandomKey,downloadImageUrl;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin_panel );
        categoryName = getIntent().getExtras().getString( "category" ).toString();
        add = (Button) findViewById( R.id.add_new_product );
        name =  (EditText) findViewById( R.id.product_name );
        desc = (EditText) findViewById( R.id.product_description );
        price = (EditText) findViewById( R.id.product_price );
        produtImage = (ImageView) findViewById( R.id.select_product_image );
        loadingBar = new ProgressDialog(this);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child( "Product Images" );
        ProductRef = FirebaseDatabase.getInstance().getReference().child( "Products" );
        produtImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        } );
        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }

    private void ValidateProductData() {
        Description = desc.getText().toString();
        Price = price.getText().toString();
        Productname = name.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText( this, "Product Image is Mandatory!!", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Description ))
        {
            Toast.makeText( this, "Please Write product description...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Productname ))
        {
            Toast.makeText( this, "Please Write product name...", Toast.LENGTH_SHORT ).show();
        }
        else if (TextUtils.isEmpty( Price ))
        {
            Toast.makeText( this, "Please Write product price...", Toast.LENGTH_SHORT ).show();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle( "Add New Product" );
        loadingBar.setMessage( "Please wait, while we are adding new product." );
        loadingBar.setCanceledOnTouchOutside( false );
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currenDate = new SimpleDateFormat("MM/dd/yyyy");
        saveCurrentDate = currenDate.format(calendar.getTime());

        SimpleDateFormat currenTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currenTime.format(calendar.getTime());

        pRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child( ImageUri.getLastPathSegment()+pRandomKey + ".jpg" );
        final UploadTask uploadTask = filePath.putFile( ImageUri );
        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText( AdminPanelActivity.this, "Error: "+msg, Toast.LENGTH_SHORT ).show();
                loadingBar.dismiss();
            }
        } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText( AdminPanelActivity.this, "Image Uploaded successfully...", Toast.LENGTH_SHORT ).show();

                Task<Uri> urlTask = uploadTask.continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText( AdminPanelActivity.this, "getting product Image url successfully.", Toast.LENGTH_SHORT ).show();
                            SaveProduct();
                        }
                    }
                } );
            }
        } );
    }

    private void SaveProduct() {
        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put( "pid",pRandomKey);
        productMap.put( "date",saveCurrentDate);
        productMap.put( "time",saveCurrentTime);
        productMap.put( "description",Description);
        productMap.put( "image",downloadImageUrl);
        productMap.put( "category",categoryName);
        productMap.put( "price",Price);
        productMap.put( "pname",Productname);

        ProductRef.child(pRandomKey).updateChildren( productMap).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent io = new Intent( AdminPanelActivity.this,AdminCategoryActivity.class );
                    startActivity( io );
                    loadingBar.dismiss();
                    Toast.makeText( AdminPanelActivity.this, "Product is added Successfully...", Toast.LENGTH_SHORT ).show();
                }
                else
                {
                    loadingBar.dismiss();
                    String msg = task.getException().toString();
                    Toast.makeText( AdminPanelActivity.this, "Error: "+msg, Toast.LENGTH_SHORT ).show();

                }
            }
        } );
    }

    private void OpenGallery() {
        Intent gIn = new Intent();
        gIn.setAction( Intent.ACTION_GET_CONTENT );
        gIn.setType( "image/*" );
        startActivityForResult( gIn,GalleryPick );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
                ImageUri = data.getData();
                produtImage.setImageURI( ImageUri );
        }
    }
}
