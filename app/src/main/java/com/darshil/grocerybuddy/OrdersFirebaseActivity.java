package com.darshil.grocerybuddy;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darshil.grocerybuddy.Model.FirebaseProducts;
import com.darshil.grocerybuddy.Model.Products;
import com.darshil.grocerybuddy.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class OrdersFirebaseActivity extends AppCompatActivity {
    private DatabaseReference ProductRef;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_orders_firebase );
        ProductRef = FirebaseDatabase.getInstance().getReference().child( "Products" );
       recyclerView =findViewById( R.id.recycler_product );
        recyclerView.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<FirebaseProducts> options = new FirebaseRecyclerOptions.Builder<FirebaseProducts>(  )
                .setQuery(ProductRef,FirebaseProducts.class)
                .build();
        FirebaseRecyclerAdapter<FirebaseProducts,ProductViewHolder> adapter = new FirebaseRecyclerAdapter<FirebaseProducts, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull FirebaseProducts model) {
                holder.txtPname.setText( model.getPname() );
                holder.txtPdesc.setText( model.getDescription() );
                holder.txtPprice.setText( "Price = " +model.getPrice()+ "$");
                Picasso.get().load( model.getImage() ).into( holder.imgpImage );

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.product_items_layout,viewGroup,false );
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter( adapter );
        adapter.startListening();
    }

}
