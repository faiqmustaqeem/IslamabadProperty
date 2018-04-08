package com.codiansoft.islamabadproperty.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codiansoft.islamabadproperty.ConnectionHelper;
import com.codiansoft.islamabadproperty.PropertyModel;
import com.codiansoft.islamabadproperty.R;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class AddPropertyFragment extends Fragment {


    EditText etDescription , etContactNumber;
    Button   btn_add_property;
    ImageView btn_select_image;
//    public static final int PICK_IMAGE = 1;
    String imageBase64="" , extension="";
    Activity activity;
    ConnectionHelper connectionHelper;
    public static final int GALLERY_CONSTANT = 1;
    public AddPropertyFragment() {
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
        View view= inflater.inflate(R.layout.fragment_add_property, container, false);
        activity=getActivity();
        connectionHelper=new ConnectionHelper(activity);
        etDescription=(EditText)view.findViewById(R.id.etDescription);
        etContactNumber=(EditText)view.findViewById(R.id.etContactNumber);

        btn_select_image=(ImageView) view.findViewById(R.id.btn_select_image);
        btn_add_property=(Button)view.findViewById(R.id.btn_add_property);

        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent,GALLERY_CONSTANT);
               // startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_CONSTANT);

            }
        });

        btn_add_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_property();
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GALLERY_CONSTANT && resultCode == RESULT_OK) {

            //TODO: action
            Uri filePath = data.getData();
                try{

                    String filename=getFileName(filePath);
                    extension= filename.substring(filename.lastIndexOf(".") + 1); // Without dot jpg, png
                    Log.e("extension" , extension);

                    CropImage.activity(filePath)
                            .setAspectRatio(4,3)
                            .setFixAspectRatio(true)
                            .start(getContext(), this);
//
//                    CropImage.activity(filePath)
//                            .setAspectRatio(4,3)
//                            .start(activity);
                  //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                   //  imageBase64 = getBase64EncodedString(bitmap);

                }
                catch (Exception e)
                {

                }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            Log.e("crop_image" , "crop");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
              Uri  uri = result.getUri();
                btn_select_image.setImageURI(uri);

                File file = new File(uri.getPath());



                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                    imageBase64 = getBase64EncodedString(bmp);
                    Log.e("Path_image", imageBase64);


                } catch (IOException e) {
                    Log.e("ExceptionFoundOfImage", e.getMessage());

                    e.printStackTrace();
                }


                Log.e("Uri", String.valueOf(uri));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("error",error.getMessage());
            }
        }

    }
    public String getBase64EncodedString(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private boolean checkFields()
    {
      if(etDescription.getText().toString().equals(""))
      {
          Toast.makeText(activity,"please add description", Toast.LENGTH_SHORT).show();
          return false;
      }
        if(etContactNumber.getText().toString().equals(""))
        {
            Toast.makeText(activity,"please add contact number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(imageBase64.equals(""))
        {
            Toast.makeText(activity,"please select image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void add_property()
    {
        if(connectionHelper.isConnectingToInternet())
        {
            if(checkFields())
            {

                final ProgressDialog dialog=new ProgressDialog(activity);
                dialog.setTitle("Loading");
                dialog.setMessage("Wait...");
                dialog.show();
                StringRequest request = new StringRequest(Request.Method.POST, "http://islamabadproperty.pk/app/index.php/api/add_property",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {


                                    JSONObject job = new JSONObject(response);
                                    JSONObject result = job.getJSONObject("result");
                                    String res = result.getString("response");


                                    if (res.equals("Property Added Successfully Recieved"))
                                    {

                                        Toast.makeText(activity,"Property successfully uploaded !",Toast.LENGTH_LONG).show();

                                        dialog.dismiss();
                                        DashboardFragment nextFrag= new DashboardFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.frame, nextFrag,"dashboard")
                                                .addToBackStack(null)
                                                .commit();


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
                        String id=prefs.getString("id","");
                        params.put("user_id", id);
                        params.put("upload_image",imageBase64);
                        params.put("desc",etDescription.getText().toString());
                        params.put("number",etContactNumber.getText().toString());
                        params.put("extension",extension);

                        Log.e("params" , params.toString());

                        return params;
                    }
                };

                Volley.newRequestQueue(activity).add(request);
            }
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
