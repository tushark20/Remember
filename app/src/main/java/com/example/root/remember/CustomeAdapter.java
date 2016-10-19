package com.example.root.remember;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CustomeAdapter extends ArrayAdapter<String> {

    Context context;
    TextView arryText;
    ToggleButton togg;
    public CustomeAdapter(Context context,int textViewResourceId) {
        super(context,textViewResourceId);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tushInflator =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = tushInflator.inflate(R.layout.new_list,null);
        arryText = (TextView) customView.findViewById(R.id.textTitle);
        togg = (ToggleButton) customView.findViewById(R.id.toggleButton);
        //customView.setTag(new RecyclerView.ViewHolder(arryText, togg));
        arryText.setText("testing");
        togg.setChecked(true);
        return  customView;
    }
}