package com.codiansoft.islamabadproperty.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codiansoft.islamabadproperty.ConnectionHelper;
import com.codiansoft.islamabadproperty.MyPropertyAdapter;
import com.codiansoft.islamabadproperty.PropertyModel;
import com.codiansoft.islamabadproperty.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class MyPropertiesFragment extends Fragment {

    RecyclerView recyclerView;
    MyPropertyAdapter adapter;
    Activity activity;
    // ImageView calendar;


    List<PropertyModel> propertyModelList=new ArrayList<>();
    ConnectionHelper connectionHelper;
    private AdView mAdView;

    public MyPropertiesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_properties, container, false);

        activity=getActivity();
        connectionHelper=new ConnectionHelper(activity);


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(getActivity(), "ca-app-pub-7007144167760143~2198726500");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        recyclerView=view.findViewById(R.id.recycle_view);
        adapter=new MyPropertyAdapter(propertyModelList,activity , activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);

        try {
            loadData();
        } catch (IOException e) {
            Toast.makeText(activity , " your application is not connected to internet ..." , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Toast.makeText(activity , " your application is not connected to internet ..." , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return view;
    }
    void loadData() throws IOException, InterruptedException {

        if(connectionHelper.isConnectingToInternet())
        {
            final ProgressDialog dialog=new ProgressDialog(activity);
            dialog.setTitle("Loading");
            dialog.setMessage("Wait...");
            dialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, "http://islamabadproperty.pk/app/index.php/api/get_User_property",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("my_properties",response);
                            try {


                                JSONObject job = new JSONObject(response);
                                JSONObject result = job.getJSONObject("result");
                                String res = result.getString("response");


                                if (res.equals("Posts List Successfully Recieved")) {

                                    JSONArray jsonArray=result.getJSONArray("data");
                                    for(int i=0 ; i < jsonArray.length() ; i++)
                                    {
                                        JSONObject property=jsonArray.getJSONObject(i);

                                        PropertyModel model=new PropertyModel();
                                        model.setId(property.getString("id"));
                                        model.setContactNumber(property.getString("number"));
                                        model.setImageLink(property.getString("upload_image"));
                                        model.setDate(property.getString("date").substring(0,10));
                                        model.setDescription(property.getString("desc"));

                                        propertyModelList.add(model);

                                    }
                                    Collections.reverse(propertyModelList);
                                    adapter.notifyDataSetChanged();

                                    dialog.dismiss();


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
                    String id = prefs.getString("id", "");
                    params.put("user_id", id);


                    Log.e("params", params.toString());

                    return params;
                }
            };

            Volley.newRequestQueue(activity).add(request);

        }
        else {
            Toast.makeText(activity , " your application is not connected to internet ..." , Toast.LENGTH_SHORT).show();
        }
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



}
