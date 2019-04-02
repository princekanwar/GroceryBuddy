package com.darshil.grocerybuddy.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.darshil.grocerybuddy.Interfaces.ItemClickListener;
import com.darshil.grocerybuddy.R;

import org.w3c.dom.Text;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtPname,txtPdesc,txtPprice;
    public ImageView imgpImage;
    public ItemClickListener itemClickListener;


    public ProductViewHolder(View v)
    {
        super(v);
        imgpImage = (ImageView) v.findViewById( R.id.imgProductImage );
        txtPname = (TextView) v.findViewById( R.id.txtProductName );
        txtPdesc = (TextView) v.findViewById( R.id.txtProductDescription );
        txtPprice = (TextView) v.findViewById( R.id.txtProductPrice );

    }


    public void setItemClickListener (ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {

        itemClickListener.onClick( v,getAdapterPosition(),false );
    }
}
