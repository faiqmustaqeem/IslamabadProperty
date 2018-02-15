package com.codiansoft.islamabadproperty;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    RecyclerView recyclerView;
    PropertyAdapter adapter;
    Activity activity;
    ImageView calendar;
    DatePickerDialog datePickerDialog;


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


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-7007144167760143~2198726500");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        recyclerView=findViewById(R.id.recycle_view);
        calendar=(ImageView) findViewById(R.id.calendar);

        Calendar c=Calendar.getInstance();

         datePickerDialog = new DatePickerDialog(activity, MainActivity.this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        adapter=new PropertyAdapter(getProperties(),activity , activity);
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


    }
    List<PropertyModel> getProperties()
    {
        List<PropertyModel> list=new ArrayList<>();
        list.add(new PropertyModel("11 january 2018","03128676054","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("12 january 2018","03128676054","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("13 january 2018","03128676054","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("28 July 2018","03128676054","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("11 December 2018","03128676054","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        return list;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        adapter.getFilter().filter(String.valueOf(i2));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // proceedAfterPermission();
                Toast.makeText(activity, "Permision Granted !", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Need Call Permission");
                    builder.setMessage("This app needs Call permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CONSTANT);


                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                // proceedAfterPermission();
                Toast.makeText(activity, "Permission Granted ! You can call now ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                //proceedAfterPermission();
                Toast.makeText(activity, "Permission Granted ! you can call now ", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
