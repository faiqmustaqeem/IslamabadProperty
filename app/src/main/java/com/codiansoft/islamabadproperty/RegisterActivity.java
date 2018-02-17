package com.codiansoft.islamabadproperty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codiansoft.islamabadproperty.R.styleable.View;

public class RegisterActivity extends AppCompatActivity {


    Button register;
    Activity activity;
    AppCompatSpinner spinner;
    String country_name;
    EditText etName, etEmail, etCity, etPhoneNumber;
    ConnectionHelper connectionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activity = this;
        register = findViewById(R.id.register);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etCity = findViewById(R.id.etCity);
        etPhoneNumber = findViewById(R.id.etNumber);
        connectionHelper=new ConnectionHelper(activity);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    register();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Locale[] locale = Locale.getAvailableLocales();
        final ArrayList<String> countries = new ArrayList<String>();
        final String[] country = new String[1];
        for (Locale loc : locale) {
            country[0] = loc.getDisplayCountry();


            if (country[0].length() > 0 && !countries.contains(country[0])) {
                countries.add(country[0]);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        spinner = findViewById(R.id.spinner_country);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, countries);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country_name = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void register() throws IOException, InterruptedException {

        if (checkFields()) {
            final ProgressDialog dialog=new ProgressDialog(activity);
            dialog.setTitle("Registering");
            dialog.setMessage("Wait...");
            dialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, "http://tpetroerp.com/isb_petro/isb_test/api/register",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {


                                JSONObject job = new JSONObject(response);
                                JSONObject result = job.getJSONObject("result");
                                String res = result.getString("response");


                                if (res.equals("Registered Successfully")) {
                                    dialog.dismiss();


                                    Log.e("success",res);
                                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

                                    editor.putString("email", etEmail.getText().toString());
                                    editor.putString("country", country_name);
                                    editor.putString("city", etCity.getText().toString());
                                    editor.putString("device_token","" );
                                    editor.putString("name", etName.getText().toString());

                                    editor.apply();

                                    Intent i = new Intent(activity, MainActivity.class);
                                    startActivity(i);
                                    finish();


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
                    }
                    ,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley_error" , error.getMessage() );
                            parseVolleyError(error);
                            dialog.dismiss();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    params.put("name", etName.getText().toString());
                    params.put("email", etEmail.getText().toString());
                    params.put("number", etPhoneNumber.getText().toString());
                    params.put("city", etCity.getText().toString());
                    params.put("country",country_name);

                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    params.put("device_token",refreshedToken);


                    Log.e("params" , params.toString());

                    return params;
                }
            };

            Volley.newRequestQueue(activity).add(request);

        }
    }
    public void parseVolleyError(VolleyError error) {
        NetworkResponse response=error.networkResponse;

        if(response!=null && response.data!=null) {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                JSONObject result = data.getJSONObject("result");
                String message = result.getString("response");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            } catch (JSONException | UnsupportedEncodingException e) {
                printMsg("internet problem...");
            }
        }
        else
        {
            printMsg("Your application is not connected to internet...");
        }
    }


    boolean checkFields() throws IOException, InterruptedException {
        if(!connectionHelper.isConnectingToInternet())
        {
            printMsg("Your application is not connected to internet...");
            return false;
        }
        if(etName.getText().toString().equals(""))
        {
            printMsg("Enter name");
            return false;
        }

        if(!isValidEmail(etEmail.getText().toString()))
        {
            printMsg("Enter Correct Email");
            return false;
        }
        if(etPhoneNumber.getText().toString().equals(""))
        {
            printMsg("Enter Phone Number");
            return false;
        }
        if(etCity.getText().toString().equals(""))
        {
            printMsg("Enter City");
            return false;
        }
        return  true;
    }
    void printMsg(String msg)
    {
        Toast.makeText(activity ,msg,Toast.LENGTH_SHORT).show();
    }
    public boolean isValidEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
