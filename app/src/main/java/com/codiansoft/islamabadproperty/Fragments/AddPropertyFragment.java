package com.codiansoft.islamabadproperty.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.codiansoft.islamabadproperty.R;

import java.io.ByteArrayOutputStream;

public class AddPropertyFragment extends Fragment {


    EditText etDescription , etContactNumber;
    Button btn_select_image , btn_add_property;
    public static final int PICK_IMAGE = 1;
    String imageBase64="" , extension="";
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
        etDescription=(EditText)view.findViewById(R.id.etDescription);
        etContactNumber=(EditText)view.findViewById(R.id.etContactNumber);

        btn_select_image=(Button)view.findViewById(R.id.btn_select_image);
        btn_add_property=(Button)view.findViewById(R.id.btn_add_property);

        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE & resultCode != Activity.RESULT_CANCELED) {

            //TODO: action
            Uri filePath = data.getData();
                try{

                    String filename=getFileName(filePath);
                    extension= filename.substring(filename.lastIndexOf(".") + 1); // Without dot jpg, png
                    Log.e("extension" , extension);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                     imageBase64 = getBase64EncodedString(bitmap);

                }
                catch (Exception e)
                {

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



}
