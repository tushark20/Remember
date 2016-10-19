package com.example.root.remember;


import com.google.android.gms.maps.model.LatLng;

import java.sql.Date;
import java.sql.Time;

public class RemindDetails {
    public int _id;
    public String title;
    public String desc;
    public String date;
    public String time;
    public LatLng latLng;
    public String address;
    public int status;

    RemindDetails(int _id, String title, String desc, String date, String time, LatLng latLng, String address, int status) {
        this._id = _id;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.latLng = latLng;
        this.address = address;
        this.status = status;
    }

    RemindDetails(){}

    public void setId(int id)
    {
        this._id = id;
    }

    public int getId(){
        return this._id;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus()
    {
        return this.status;
    }
}