package com.codiansoft.islamabadproperty;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CodianSoft on 17/01/2018.
 */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.MyViewHolder> implements Filterable {

    List<PropertyModel> list;
    Context context;
    List<PropertyModel> propertyFilteredList;
    Activity activity;

    public PropertyAdapter(List<PropertyModel> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        propertyFilteredList=list;
        this.activity=activity;
    }

    @Override
    public PropertyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PropertyAdapter.MyViewHolder holder, int position) {
        final PropertyModel model = propertyFilteredList.get(position);

        holder.date.setText(model.getDate());
        holder.description.setText(model.getDescription());

        Glide.with(context)
                .load(model.getImageLink())
                .apply(new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.no_image_available)).into(holder.image);

//        Glide.with(context)
//                .load(model.getImageLink())
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                }).into(holder.image);


//        holder.call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//           getCallPermissions(model.getContactNumber());
//
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalClass.selected_property = model;
                Intent i=new Intent(context , ShowProperty.class);
                context.startActivity(i);
            }
        });
    }
    @Override
    public int getItemCount() {
        if(propertyFilteredList== null)
            return 0;
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
                      //  Log.e("filter_date_adapter",row.getDate().substring(8,10)+"-"+row.getDate().substring(5,7));
                                                        //||charString.substring(3,5).equals(row.getDate().substring(5,7))
                        if (charString.substring(0,2).equals(row.getDate().substring(8,10)))
                        {

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
        ProgressBar progressBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            description=itemView.findViewById(R.id.description);
            image=itemView.findViewById(R.id.image_property);
            call=itemView.findViewById(R.id.call);
            progressBar=itemView.findViewById(R.id.progressbar);
        }
    }
}
