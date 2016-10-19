package com.example.root.remember;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.List;

public class TestDB extends AppCompatActivity {

    ListAdapter tushAdapter;
    ListView tushListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);
        tushListView = (ListView)findViewById(R.id.main_list);

        MyDBHelper db = new MyDBHelper(this);

        // Inserting Contacts

        LatLng latLng = null;

        Log.d("Insert: ", "Inserting ..");
        db.insert_db(new RemindDetails(1,"Title1", "9100000000", "1992-12-12", "08:35:13",latLng, "aadada", 1 ));
        db.insert_db(new RemindDetails(2,"Title2", "9100000000", "1992-12-12", "08:35:13",latLng, "aadada", 1 ));
        db.insert_db(new RemindDetails(3,"Title3", "9100000000", "1992-12-12", "08:35:13",latLng, "aadada", 1 ));
        db.insert_db(new RemindDetails(4,"Title4", "9100000000", "1992-12-12", "08:35:13",latLng, "aadada", 1 ));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<RemindDetails> remindList = null;
        try {
            remindList = db.getAllDetails();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tushAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,remindList);
        tushListView.setAdapter(tushAdapter);

        for (RemindDetails rd : remindList) {
            String log = "Id: "+rd._id+" ,Title: " + rd.title + " ,Date: " + rd.date;
            // Writing Contacts to log
            Log.d("Name: ", log);
        }
    }
}
