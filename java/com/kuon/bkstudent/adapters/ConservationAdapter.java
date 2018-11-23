package com.kuon.bkstudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.models.Conservation;
import com.kuon.bkstudent.models.DateRecord;
import com.kuon.bkstudent.models.Notification;

import java.util.ArrayList;


public class ConservationAdapter extends ArrayAdapter<Conservation> {


    private ArrayList<Conservation> dataSet;
    private Context mContext;

    public ConservationAdapter(ArrayList<Conservation> data, Context context) {
        super(context, R.layout.date_history_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Conservation conservation = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.conservation_item, null);

        }

        TextView tvTitle = v.findViewById(R.id.tv_conservation_title);
        TextView tvName = v.findViewById(R.id.tv_creator_name);
        TextView tvTime = v.findViewById(R.id.tv_conservation_time);
        TextView tvNummberOfMessage = v.findViewById(R.id.tv_conservation_message);


        if (tvTitle != null) {
            tvTitle.setText(conservation.getTitle());
        }

        if (tvTime != null) {
            tvTime.setText(conservation.getDatetime());
        }
        if (tvNummberOfMessage != null) {
            tvNummberOfMessage.setText(conservation.getNumberOfchat());
        }
        if (tvName != null) {
            tvName.setText(conservation.getCreatorName());
        }
      //   Return the completed view to render on screen
        return v;
    }

}