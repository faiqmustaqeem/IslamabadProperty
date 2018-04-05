package com.codiansoft.islamabadproperty.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codiansoft.islamabadproperty.ConnectionHelper;
import com.codiansoft.islamabadproperty.MainActivity;
import com.codiansoft.islamabadproperty.MainNavigationDrawerActivity;
import com.codiansoft.islamabadproperty.PropertyAdapter;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment  implements DatePickerDialog.OnDateSetListener {

    RecyclerView recyclerView;
    PropertyAdapter adapter;
    Activity activity;
   // ImageView calendar;
    DatePickerDialog datePickerDialog;
    TextView showAll;
    List<PropertyModel> propertyModelList=new ArrayList<>();
    ConnectionHelper connectionHelper;
    private AdView mAdView;


    public DashboardFragment() {
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
        View view= inflater.inflate(R.layout.fragment_dashboard, container, false);
        activity=getActivity();
        connectionHelper=new ConnectionHelper(activity);


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(getActivity(), "ca-app-pub-7007144167760143~2198726500");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        recyclerView=view.findViewById(R.id.recycle_view);
//        calendar=(ImageView) view.findViewById(R.id.calendar);

        //showAll=view.findViewById(R.id.showAll);



        Calendar c=Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(activity, this , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        adapter=new PropertyAdapter(propertyModelList,activity , activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        MainNavigationDrawerActivity.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do stuff
                        adapter.getFilter().filter(String.valueOf(""));

                    }
                });

//        showAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                adapter.getFilter().filter(String.valueOf(""));
//            }
//        });
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


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Log.e("filter_date",String.valueOf(dayOfMonth)+"-"+String.valueOf(month));



        String zero_trailing_day="";
        if(String.valueOf(dayOfMonth).length() < 2)
        {
            zero_trailing_day= "0"+String.valueOf(dayOfMonth);
        }
        else {
            zero_trailing_day= String.valueOf(dayOfMonth);
        }
        Log.e("filter_date",String.valueOf(zero_trailing_day));
        adapter.getFilter().filter(String.valueOf(zero_trailing_day));
        // 16-12
    }

    void loadData() throws IOException, InterruptedException {

        if(connectionHelper.isConnectingToInternet())
        {
            final ProgressDialog dialog=new ProgressDialog(activity);
            dialog.setTitle("Loading");
            dialog.setMessage("Wait...");
            dialog.show();
            StringRequest request = new StringRequest(Request.Method.GET, "http://islamabadproperty.pk/app/index.php/api/get_posts",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

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
                                        model.setContactNumber(property.getString("number"));
                                        model.setImageLink(property.getString("upload_image"));
                                        model.setDate(property.getString("date").substring(0,10));
                                        model.setDescription(property.getString("description"));

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
                    });

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
