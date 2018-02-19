package com.codiansoft.islamabadproperty;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

public class ShowProperty extends AppCompatActivity {

    ImageView back;
    ImageView call;
    Activity activity;
    public static final int CALL_PERMISSION_CONSTANT = 100;
    public static final int REQUEST_PERMISSION_SETTING = 101;
    public static boolean sentToSettings = false;
    public static SharedPreferences permissionStatus;
    private AdView mAdView;
    ImageView property_image;
    TextView description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_property);

        back=(ImageView)findViewById(R.id.back);
        call=(ImageView)findViewById(R.id.call);
        property_image=findViewById(R.id.image_property);
        description=findViewById(R.id.description);

        activity=this;


        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-7007144167760143~2198726500");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCallPermissions(GlobalClass.selected_property.getContactNumber());
            }
        });
       // Picasso.with(activity).load(GlobalClass.selected_property.getImageLink()).into(property_image);
        Glide.with(activity)
                .load(GlobalClass.selected_property.getImageLink())
                .apply(new RequestOptions().placeholder(R.drawable.loading).error(R.drawable.no_image_available)).into(property_image);

        description.setText(GlobalClass.selected_property.getDescription());

    }
    void getCallPermissions(String contact_number)
    {
        permissionStatus = getSharedPreferences("call_permission",MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Need Call Permission");
                builder.setMessage("This app needs Call permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, MainActivity.CALL_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.CALL_PHONE,false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Need Call Permission");
                builder.setMessage("This app needs Call permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivityForResult(intent, MainActivity.REQUEST_PERMISSION_SETTING);
                        Toast.makeText(activity, "Go to Permissions to Grant Call Permission", Toast.LENGTH_LONG).show();
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
                //just request the permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},MainActivity.CALL_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = MainActivity.permissionStatus.edit();
            editor.putBoolean(Manifest.permission.CALL_PHONE,true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            call(contact_number);
        }
    }

    void call(final String contact_number)
    {
        new MaterialDialog.Builder(activity)
                .title("Call")
                .content("Are you sure you want to call to this number ? " + contact_number)
                .positiveText("Yes , im sure")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact_number));
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .negativeText("Cancel")
                .canceledOnTouchOutside(false)
                .show();
    }


}
