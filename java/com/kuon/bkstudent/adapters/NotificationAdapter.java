package com.kuon.bkstudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.models.DateRecord;
import com.kuon.bkstudent.models.Notification;

import java.util.ArrayList;


public class NotificationAdapter extends ArrayAdapter<Notification> {


    private ArrayList<Notification> dataSet;
    private Context mContext;

    public NotificationAdapter(ArrayList<Notification> data, Context context) {
        super(context, R.layout.date_history_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Notification notification = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.notification_item, null);

        }

        TextView tvTitle = v.findViewById(R.id.tv_notification_title);
        TextView tvContent = v.findViewById(R.id.tv_notification_content);
        TextView tvTime = v.findViewById(R.id.tv_notification_time);


        if (tvTitle != null) {
            tvTitle.setText(notification.getTitle());
        }

        if (tvTime != null) {
            tvTime.setText(notification.getTime());
        }
        if (tvContent != null) {
            tvContent.setText(notification.getContent());
        }
        // Return the completed view to render on screen
        return v;
    }

}