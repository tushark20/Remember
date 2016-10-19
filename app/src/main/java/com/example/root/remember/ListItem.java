package com.example.root.remember;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import static com.example.root.remember.R.id.editLoc;
import static com.example.root.remember.R.id.spin;

public class ListItem extends AppCompatActivity implements  View.OnClickListener {

    private  EditText editTitle;
    private  EditText editDes;
    private Button btnAdd;
    private SQLiteDatabase db;
    int PLACE_PICKER_REQUEST = 1;
    private  boolean flag = false;
    private ProgressBar progressBar;
    private GoogleApiClient mClient;
    private EditText location;
    private int posi = -1;
    private LatLng latlong;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custome_list);

        progressBar = (ProgressBar) findViewById(spin);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editDes = (EditText) findViewById(R.id.editDes);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        location = (EditText) findViewById(R.id.editLoc);

        btnAdd.setOnClickListener(this);

        db = openOrCreateDatabase("RememberDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS remember('_id' INTEGER PRIMARY KEY AUTOINCREMENT , title VARCHAR," +
                " description VARCHAR, latitude VARCHAR, longitude VARCHAR, status INTEGER);");


    }

    public void clearText() {
        editTitle.setText("");
        editDes.setText("");
        location.setText("");

    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("result",String.valueOf(posi));
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();  // optional depending on your needs
    }

    public void setlocation(View v) {

        progressBar.setVisibility(View.VISIBLE);
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.INVISIBLE);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            latlong = place.getLatLng();
            final CharSequence latitude = String.valueOf(latlong.latitude);
            final CharSequence longitude = String.valueOf(latlong.longitude);
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            location.setText(name+","+address);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            // Checking empty fields
            if (editTitle.getText().toString().trim().length() == 0) {
                //showMessage("Error", "Please enter all values");
                Toast.makeText(getApplicationContext(),"Title is mandatory.",Toast.LENGTH_LONG).show();
                return;
            }
            if (location.getText().toString().trim().length() == 0) {
                //showMessage("Error", "Please enter all values");
                Toast.makeText(getApplicationContext(),"Location is not set.",Toast.LENGTH_LONG).show();
                return;
            }

            // Inserting record
            long cnt  = DatabaseUtils.queryNumEntries(db, "remember")+1;
            if(!flag)
            {posi = (int) cnt;flag=true;}
            db.execSQL("INSERT INTO remember( _id, title, description, latitude, longitude, status)  VALUES('" + (int) cnt + "','"
                    + editTitle.getText() + "','" + editDes.getText() + "','" + String.valueOf(latlong.latitude) + "','" +
                    String.valueOf(latlong.longitude) + "','" + 1 + "');");
            Toast.makeText(getApplicationContext(),"Success, remainder added!"+"\n"+latlong.longitude,Toast.LENGTH_LONG).show();
            //showMessage("Success", "Record added");
           clearText();
        }
    }



}
