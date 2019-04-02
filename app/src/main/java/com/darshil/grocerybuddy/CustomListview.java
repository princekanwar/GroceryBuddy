package com.darshil.grocerybuddy;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListview  extends ArrayAdapter<String> {
    private String[] groceryName;
    private String[] groceryDesc;
    private Integer[] imgId;
    private Activity context;
    public CustomListview(Activity context,String[] groceryName,String[] groceryDesc,Integer[] imgId) {
        super( context, R.layout.listview_layout, groceryName );
        this.context = context;
        this.groceryDesc = groceryDesc;
        this.groceryName = groceryName;
        this.imgId = imgId;

    }
    @NonNull
    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent){
       View r=convertView;
       ViewHolder viewHolder=null;
       if(r==null)
       {
           LayoutInflater layoutInflater = context.getLayoutInflater();
           r=layoutInflater.inflate( R.layout.listview_layout,null ,true);
           viewHolder = new ViewHolder( r );
           r.setTag(viewHolder);
       }
       else
       {
           viewHolder = (ViewHolder) r.getTag();
       }
         viewHolder.img1.setImageResource(imgId[position]);
        viewHolder.t1.setText(groceryName[position]);
        viewHolder.t2.setText( groceryDesc[position] );

        return r;

    }
    class ViewHolder
    {
        TextView t1;
        TextView t2;
        ImageView img1;
        ViewHolder(View v)
        {
            t1 = (TextView) v.findViewById( R.id.txtGrocerryName);
            t2 = (TextView) v.findViewById( R.id.txtGroceryDescription );
            img1 = (ImageView) v.findViewById( R.id.imgGrocerryImage );
        }

    }
}
