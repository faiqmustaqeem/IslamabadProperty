package com.codiansoft.islamabadproperty;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    Activity activity;
    AppCompatSpinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activity=this;
        register=findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(activity , MainActivity.class);
                startActivity(i);
            }
        });

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();


            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        spinner=findViewById(R.id.spinner_country);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, countries);

        spinner.setAdapter(adapter);
    }
    void register()
    {

    }
}
