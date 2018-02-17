package com.codiansoft.islamabadproperty;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    RecyclerView recyclerView;
    PropertyAdapter adapter;
    Activity activity;
    ImageView calendar;
    DatePickerDialog datePickerDialog;
    TextView showAll;
    List<PropertyModel> propertyModelList=new ArrayList<>();
    ConnectionHelper connectionHelper;


    public static final int CALL_PERMISSION_CONSTANT = 100;
    public static final int REQUEST_PERMISSION_SETTING = 101;
    public static boolean sentToSettings = false;
    public static SharedPreferences permissionStatus;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        connectionHelper=new ConnectionHelper(activity);


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-7007144167760143~2198726500");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        recyclerView=findViewById(R.id.recycle_view);
        calendar=(ImageView) findViewById(R.id.calendar);

        showAll=findViewById(R.id.showAll);



        Calendar c=Calendar.getInstance();

         datePickerDialog = new DatePickerDialog(activity, MainActivity.this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        adapter=new PropertyAdapter(propertyModelList,activity , activity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
       // recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        calendar.setOnClickListener(new View.OnClickListener() {
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

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter(String.valueOf(""));
            }
        });
        try {
            loadData();
        } catch (IOException e) {
            Toast.makeText(activity , " your application is not connected to internet ..." , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Toast.makeText(activity , " your application is not connected to internet ..." , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    @Override
    public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {
        Log.e("filter_date",String.valueOf(dayOfMonth)+"-"+String.valueOf(month));
//        String zero_trailing_month="";
//
//             zero_trailing_month= String.valueOf(month+1);
//            if(zero_trailing_month.length() < 2)
//            {
//                zero_trailing_month="0"+zero_trailing_month;
//            }


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
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CALL_PERMISSION_CONSTANT) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // proceedAfterPermission();
//                Toast.makeText(activity, "Permision Granted !", Toast.LENGTH_SHORT).show();
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
//                    //Show Information about why you need the permission
//                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    builder.setTitle("Need Call Permission");
//                    builder.setMessage("This app needs Call permission");
//                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//
//                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CONSTANT);
//
//
//                        }
//                    });
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    builder.show();
//                } else {
//                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }

//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_PERMISSION_SETTING) {
//            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                //Got Permission
//                // proceedAfterPermission();
//                Toast.makeText(activity, "Permission Granted ! You can call now ", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        if (sentToSettings) {
//            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                //Got Permission
//                //proceedAfterPermission();
//                Toast.makeText(activity, "Permission Granted ! you can call now ", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    void loadData() throws IOException, InterruptedException {

        if(connectionHelper.isConnectingToInternet())
        {
            final ProgressDialog dialog=new ProgressDialog(activity);
            dialog.setTitle("Loading");
            dialog.setMessage("Wait...");
            dialog.show();
            StringRequest request = new StringRequest(Request.Method.GET, "http://tpetroerp.com/isb_petro/isb_test/api/get_posts",
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
                            Log.e("Volley_error" , error.getMessage() );
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
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
