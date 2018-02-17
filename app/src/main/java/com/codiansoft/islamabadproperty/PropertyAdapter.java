package com.codiansoft.islamabadproperty;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
    public void onBindViewHolder(PropertyAdapter.MyViewHolder holder, int position) {
        final PropertyModel model = propertyFilteredList.get(position);

        holder.date.setText(model.getDate());
        holder.description.setText(model.getDescription());

        Picasso.with(context)
                .load(model.getImageLink())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image_available)
                .into(holder.image);

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

//    void getCallPermissions(String contact_number)
//    {
//        MainActivity.permissionStatus = activity.getSharedPreferences("call_permission",MODE_PRIVATE);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
//                //Show Information about why you need the permission
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("Need Call Permission");
//                builder.setMessage("This app needs Call permission.");
//                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, MainActivity.CALL_PERMISSION_CONSTANT);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else if (MainActivity.permissionStatus.getBoolean(Manifest.permission.CALL_PHONE,false)) {
//                //Previously Permission Request was cancelled with 'Dont Ask Again',
//                // Redirect to Settings after showing Information about why you need the permission
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("Need Call Permission");
//                builder.setMessage("This app needs Call permission.");
//                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.cancel();
//                        MainActivity.sentToSettings = true;
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//                        intent.setData(uri);
//                        activity.startActivityForResult(intent, MainActivity.REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(context, "Go to Permissions to Grant Call Permission", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//            } else {
//                //just request the permission
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},MainActivity.CALL_PERMISSION_CONSTANT);
//            }
//
//            SharedPreferences.Editor editor = MainActivity.permissionStatus.edit();
//            editor.putBoolean(Manifest.permission.CALL_PHONE,true);
//            editor.commit();
//
//
//        } else {
//            //You already have the permission, just go ahead.
//            call(contact_number);
//        }
//    }

//    void call(final String contact_number)
//    {
//        new MaterialDialog.Builder(activity)
//                .title("Call")
//                .content("Are you sure you want to call to this number ? " + contact_number)
//                .positiveText("Yes , im sure")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_number));
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                })
//                .negativeText("Cancel")
//                .canceledOnTouchOutside(false)
//                .show();
//    }
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
        public MyViewHolder(View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            description=itemView.findViewById(R.id.description);
            image=itemView.findViewById(R.id.image_property);
            call=itemView.findViewById(R.id.call);
        }
    }
}
