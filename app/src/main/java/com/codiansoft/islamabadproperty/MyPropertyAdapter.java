package com.codiansoft.islamabadproperty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CodianSoft on 06/04/2018.
 */

public class MyPropertyAdapter extends RecyclerView.Adapter<MyPropertyAdapter.MyViewHolder> {
    List<PropertyModel> list;
    Context context;
    Activity activity;
    public MyPropertyAdapter(List<PropertyModel> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity=activity;
    }
    @Override
    public MyPropertyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_property_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyPropertyAdapter.MyViewHolder holder, final int position) {
        final PropertyModel model = list.get(position);

        holder.date.setText(model.getDate());
        holder.description.setText(model.getDescription());


        Glide.with(context)
                .load(model.getImageLink())
                .apply(new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.no_image_available)).into(holder.image);

        final String id=model.getId();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title("Delete")
                        .content("are you sure you want to delete this property ?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                delete(position,id);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });


    }

    void delete(final int position, final String id)
    {

                final ProgressDialog dialog=new ProgressDialog(activity);
                dialog.setTitle("Deleting");
                dialog.setMessage("Wait...");
                dialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, "http://islamabadproperty.pk/app/index.php/api/delete_User_property ",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONObject job = new JSONObject(response);
                                    JSONObject result = job.getJSONObject("result");
                                    String res = result.getString("response");


                                    if (res.equals("Posts List Successfully deleted"))
                                    {

                                        Toast.makeText(activity,"Property successfully deleted !",Toast.LENGTH_LONG).show();

                                        dialog.dismiss();
                                        removeAt(position);



                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(activity,res, Toast.LENGTH_SHORT).show();
                                        // finish();
                                    }
                                } catch (JSONException e) {
                                    Log.e("ErrorMessage", e.getMessage());
                                    e.printStackTrace();
                                    dialog.dismiss();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Log.e("Volley_error" , error.getMessage() );
                                parseVolleyError(error);
                                dialog.dismiss();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        SharedPreferences prefs = activity.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                        String user_id=prefs.getString("id","");
                        params.put("user_id", user_id);
                        params.put("prop_id",id);


                        Log.e("params" , params.toString());

                        return params;
                    }
                };

                Volley.newRequestQueue(activity).add(request);






    }
    public void parseVolleyError(VolleyError error) {
        NetworkResponse response=error.networkResponse;

        if(response!=null && response.data!=null)
        {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                JSONObject result = data.getJSONObject("result");
                String message = result.getString("response");
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
            } catch (UnsupportedEncodingException errorr) {
            }
        }
        else
        {
            Toast.makeText(activity , " your application is not connected to internet ..." , Toast.LENGTH_SHORT).show();
        }
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date , description ;
        ImageView image , delete;
        ProgressBar progressBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            description=itemView.findViewById(R.id.description);
            image=itemView.findViewById(R.id.image_property);
            delete=itemView.findViewById(R.id.delete);
            progressBar=itemView.findViewById(R.id.progressbar);
        }
    }
}
