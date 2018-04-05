package com.codiansoft.islamabadproperty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    Button login ;
    Activity activity;
    EditText email , password,register ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;

        login=(Button)findViewById(R.id.login);
        email=(EditText)findViewById(R.id.etEmail);
        password=(EditText)findViewById(R.id.etPassowrd);
        register=(EditText) findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    public boolean checkFields()
    {
        if(email.getText().toString().equals(""))
        {
            printMsg("Please enter Email");
            return false;
        }
        if(!isValidEmail(email.getText().toString()))
        {
            printMsg("Please enter correct Email");
            return false;
        }
        if(password.getText().toString().equals(""))
        {
            printMsg("Please enter password");
            return false;
        }
        return true;
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
    public void login()
    {


        if (checkFields()) {
            final ProgressDialog dialog=new ProgressDialog(activity);
            dialog.setTitle("Loading");
            dialog.setMessage("Wait...");
            dialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, "http://islamabadproperty.pk/app/index.php/api/login",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("login" , response);
                            try {


                                JSONObject job = new JSONObject(response);
                                JSONObject result = job.getJSONObject("result");
                                String status=result.getString("status");
                                String res = result.getString("response");


                                if (status.equals("success"))
                                {
                                    dialog.dismiss();


                                    Log.e("success",res);

                                    JSONObject data=job.getJSONObject("data").getJSONObject("user_data");
                                    String id=data.getString("id");

                                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

                                    editor.putString("email", email.getText().toString());
                                    editor.putString("id" ,id );


                                    editor.apply();

                                    Intent i = new Intent(activity, MainNavigationDrawerActivity.class);
                                    startActivity(i);
                                    finish();


                                }
                                else
                                 {
                                    dialog.dismiss();
                                    Toast.makeText(activity,res, Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e)
                            {
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
                            // Log.e("Volley_error" , error.getMessage() );
                            parseVolleyError(error);
                            dialog.dismiss();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    params.put("email", email.getText().toString());
                    params.put("password", password.getText().toString());
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    params.put("device_token",refreshedToken);

                    Log.e("params_login" , params.toString());

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

}
