package com.example.root.remember;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.ads.doubleclick.CustomRenderedAd;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyDBHelper extends SQLiteOpenHelper{

    private final Context m_ctx;
    private static final String TAG              = "MyDBHelper";
    private static final String DATABASE_NAME    = "Remind.db";
    private static final int    DATABASE_VERSION = 1;
    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        m_ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS remember('_id' INTEGER PRIMARY KEY AUTOINCREMENT , title VARCHAR, description VARCHAR, date TEXT, time TEXT, latitude REAL, longitude REAL, address VARCHAR, status INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean isAnyInfoAvailable(Context ctx){
        boolean result = false;
        MyDBHelper dbh = null;
        SQLiteDatabase db = null;
        try {
            dbh = new MyDBHelper(ctx);
            db = dbh.getWritableDatabase();
            result = MyDBHelper.is_any_info_available(db);
        } catch (Throwable e) {
            Log.e(TAG, "isAnyInfoAvailable(): Caught - " + e.getClass().getName(), e);
        } finally {
            if (null != db)
                db.close();
            if (null != dbh)
                dbh.close();
        }
        return result;
    }

    public static boolean is_any_info_available(SQLiteDatabase db){
        boolean result = false;

        Cursor cInfo = db.rawQuery(
                "select _id from remember", null);
        if(cInfo != null)
        {
            if(cInfo.moveToFirst())
            {
                result = true;
            }
        }
        if(cInfo != null)
            cInfo.close();
        return result;
    }
////insert remider
    public void insert_db(RemindDetails rd){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", rd._id);// _id
        values.put("title", rd.title); // title
        values.put("description", rd.desc); // title description
        values.put("date", rd.date); // title date
        values.put("time", rd.time); // title time
        values.put("latitude", rd.latLng.latitude); // title latitide
        values.put("longitude", rd.latLng.longitude); // title lobgitude
        values.put("address", rd.address); // title address
        values.put("status", rd.status); // title status
        // values.put("title", rd.title); // title title
        // Inserting Row
        db.insert("remember", null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

    }

    //////////////////getList
    public List<RemindDetails> getAllDetails() throws ParseException {
        List<RemindDetails> remindList = new ArrayList<RemindDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "remember";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RemindDetails remind = new RemindDetails();
                remind._id  = Integer.parseInt(cursor.getString(0));
                remind.title = cursor.getString(1);
                remind.desc = cursor.getString(2);
                remind.time = cursor.getString(4); // Adding contact to list
                remindList.add(remind);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return remindList;
    }

    //////////////delete single rec

    public void delete_rem_db(RemindDetails rd){
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete("remember","_id" + " = ?", new String[] { String.valueOf(rd.getId()) });
            db.close();
        }


    public void delete_db(){

    }

}
