package com.codiansoft.islamabadproperty;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CodianSoft on 17/01/2018.
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.MyViewHolder> implements Filterable {

    List<PropertyModel> list;
    Context context;
    List<PropertyModel> propertyFilteredList;

    public PropertyAdapter(List<PropertyModel> list, Context context) {
        this.list = list;
        this.context = context;
        propertyFilteredList=list;
    }

    @Override
    public PropertyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PropertyAdapter.MyViewHolder holder, int position) {
        PropertyModel model = propertyFilteredList.get(position);

        holder.date.setText(model.getDate());
        holder.description.setText(model.getDescription());
        Picasso.with(context)
                .load(model.getImageLink())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image_available)
                .into(holder.image);

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:03222483141"));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(callIntent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context , ShowProperty.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                     propertyFilteredList= list;
                } else {
                    List<PropertyModel> filteredList = new ArrayList<>();
                    for (PropertyModel row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDate().toLowerCase().contains(charString.toLowerCase()) ) {

                            filteredList.add(row);
                        }
                    }

                    propertyFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = propertyFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                propertyFilteredList = (ArrayList<PropertyModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
       TextView date , description ;
        ImageView image , call;
        public MyViewHolder(View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            description=itemView.findViewById(R.id.description);
            image=itemView.findViewById(R.id.image_property);
            call=itemView.findViewById(R.id.call);
        }
    }
}
