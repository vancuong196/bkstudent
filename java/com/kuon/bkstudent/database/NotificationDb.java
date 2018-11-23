package com.kuon.bkstudent.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuon.bkstudent.models.Notification;

import java.util.ArrayList;


public class NotificationDb extends SQLiteOpenHelper {


    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bkstudent_db";
    private static final String TABLE_NAME = "notification";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String TIME = "time";

    public NotificationDb(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String script = "CREATE TABLE " + TABLE_NAME + " (" +

                TITLE + " TEXT, " +
                CONTENT + " TEXT, " +
                TIME + " TEXT)";
        db.execSQL(script);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addNotification(Notification notification) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, notification.getTitle());
        values.put(CONTENT, notification.getContent());
        values.put(TIME, notification.getTime());
        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public String getMaxTime() {
        String time = "2018-01-01 00:00:00.000000";
        SQLiteDatabase db = this.getReadableDatabase();
        String statement = "SELECT "+TIME+" FROM " + TABLE_NAME + " ORDER BY " + TIME + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(statement, null);
        if (cursor != null &&  cursor.moveToFirst()) {
            time = cursor.getString(0);
            db.close();
            cursor.close();
            return time;
        } else {
            db.close();
            return time;
        }

    }

    public ArrayList<Notification> getAllNotifications() {

        ArrayList<Notification> notifications = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(0);
                String content = cursor.getString(1);
                String time = cursor.getString(2);
                Notification notification = new Notification(time,title,content);
                notifications.add(notification);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return notifications;
    }

}