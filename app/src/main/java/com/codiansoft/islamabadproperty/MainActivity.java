package com.codiansoft.islamabadproperty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        recyclerView=findViewById(R.id.recycle_view);
        calendar=(ImageView) findViewById(R.id.calendar);

        Calendar c=Calendar.getInstance();

         datePickerDialog = new DatePickerDialog(activity, MainActivity.this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        adapter=new PropertyAdapter(getProperties(),activity);
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

    }
    List<PropertyModel> getProperties()
    {
        List<PropertyModel> list=new ArrayList<>();
        list.add(new PropertyModel("11 january 2018","090078601","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("12 january 2018","090078601","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("13 january 2018","090078601","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("28 July 2018","090078601","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        list.add(new PropertyModel("11 December 2018","090078601","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.","https://315wf32byijl3k75j711kbzw-wpengine.netdna-ssl.com/wp-content/uploads/house-facade-grady-homes-builder-magnetic-e1455241142151-739x417.jpg"));
        return list;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        adapter.getFilter().filter(String.valueOf(i2));
    }
}
