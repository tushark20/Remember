package com.example.root.remember;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.Console;
import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {


    ListView tushListView;
    ListAdapter tushAdapter;
    ArrayList<String> list = new ArrayList<>();
    SQLiteDatabase db;
    Cursor c,read_c;
    Activity activity;
    SwitchCompat switchcompat;
    static boolean val = false;
    SharedPreferences prefs;
    private Menu mOptionsMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.add_button);
        tushListView = (ListView)findViewById(R.id.main_list);
        tushAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItem();
            }
        });

        tushListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // Searching roll number
                int pos = position+1;
                c=db.rawQuery("SELECT * FROM remember WHERE _id ='"+pos+"'", null);
                if(c.moveToFirst())
                {
                    // Displaying record if found
                    String msg = c.getString(2);
                    if(msg.equals(""))
                        msg = "No details.";
                   DialogBox(c.getString(1), msg );
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"record not found",Toast.LENGTH_SHORT).show();
                }

            }
        });
        /////////////

        tushListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(position);
            return false;
        }
    });
        tushListView.setAdapter(tushAdapter);

        db = openOrCreateDatabase("RememberDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS remember('_id' INTEGER PRIMARY KEY AUTOINCREMENT , title VARCHAR, description VARCHAR, latitude VARCHAR, longitude VARCHAR, status INTEGER);");

        c=db.rawQuery("SELECT * FROM remember", null);
        while(c.moveToNext())
        {
            list.add(String.valueOf(c.getString(1)));
            tushListView.setAdapter(tushAdapter);
            //Toast.makeText(getApplicationContext(),c.getString(1)+c.getString(2),Toast.LENGTH_SHORT).show();
        }
        //////////////////////////
    }

    public void addItem()
    {
        Intent in = new Intent(this,ListItem.class);
        startActivityForResult(in,1);

    }

    public void deleteItem(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Do you really want to delete the row?");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                      db.execSQL("delete from remember");
                         db.delete("remember", "_id"+ " = ?", new String[] { String.valueOf(position+1) });
                         int poss = position + 1;
                         db.execSQL("update remember SET _id = _id - 1 where _id > "+ poss);
//                         db.close();
                        list.remove(position);
                        tushListView.setAdapter(tushAdapter);
                    }
                });
        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
            if(requestCode==1){

                if(resultCode==RESULT_OK)
                {
                    String out = data.getStringExtra("result");
                    if(!out.equals("-1"))
                    {
                        c=db.rawQuery("SELECT * FROM remember WHERE _id >= "+ out, null);
                        while(c.moveToNext())
                        {
                            list.add(c.getString(1)+", "+String.valueOf(c.getDouble(3))+", "+String.valueOf(c.getDouble(4))+", "+c.getString(5));
                            tushListView.setAdapter(tushAdapter);
                           //    Toast.makeText(getApplicationContext(),String.valueOf(c.getDouble(4)),Toast.LENGTH_LONG).show();
                        }
                    }
                    //Toast.makeText(getApplicationContext(),out,Toast.LENGTH_SHORT).show();
                }
            }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
       mOptionsMenu = menu;
//        MenuItem item = menu.findItem(R.id.switchId);
//        witchAB = (Switch) MenuItemCompat.getActionView(item).findViewById(R.id.switchAB);
        MenuItem switchItem = menu.findItem(R.id.switchId);
        final SwitchCompat switch_item = (SwitchCompat) MenuItemCompat.getActionView(switchItem);

        switch_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefs.edit().putBoolean("switch_status", true).apply();
                    startService(new Intent(activity, MyService.class));
                    //Toast.makeText(getApplicationContext(),"service has started",Toast.LENGTH_LONG).show();
                }else
                {
                    prefs.edit().putBoolean("switch_status", false).apply();
                    stopService(new Intent(activity, MyService.class));
                    //Toast.makeText(getApplicationContext(),"service is stopped",Toast.LENGTH_LONG).show();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem switchItem = menu.findItem(R.id.switchId);
        // set your desired icon here based on a flag if you like
        SwitchCompat take_switch = (SwitchCompat) MenuItemCompat.getActionView(switchItem);
        if(prefs.getBoolean("switch_status", false))
            take_switch.setChecked(true);
        return super.onPrepareOptionsMenu(menu);
    }

    public void DialogBox(String _status, String _message)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setTitle(_status);
        dlgAlert.setMessage(_message);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dlgAlert.create().show();
    }
}
