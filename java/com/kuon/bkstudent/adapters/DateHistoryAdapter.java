package com.kuon.bkstudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.models.DateRecord;

import java.util.ArrayList;


public class DateHistoryAdapter extends ArrayAdapter<DateRecord> {


    private ArrayList<DateRecord> dataSet;
    private Context mContext;

    public DateHistoryAdapter(ArrayList<DateRecord> data, Context context) {
        super(context, R.layout.date_history_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DateRecord dateRecord = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.date_history_item, null);

        }

        TextView tvDate = v.findViewById(R.id.tv_date);
        TextView tvTime = v.findViewById(R.id.tv_time);


        if (tvDate != null) {
            tvDate.setText(dateRecord.getDate());
        }

        if (tvTime != null) {
            tvTime.setText(dateRecord.getTime());
        }
        // Return the completed view to render on screen
        return v;
    }

}