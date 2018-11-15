package com.kuon.bkstudent.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kuon.bkstudent.models.Message;
import com.kuon.bkstudent.models.Notification;

import java.util.ArrayList;


public class MessageDb extends SQLiteOpenHelper {


    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bkstudent_message_db";
    private static final String TABLE_NAME = "message";
    private static final String USERID = "user_id";
    private static final String USERNAME = "user_name";
    private static final String CONTENT = "content";
    private static final String TIME = "time";

    public MessageDb(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String script = "CREATE TABLE " + TABLE_NAME + " (" +

                USERID + " TEXT, " +
                USERNAME + " TEXT, " +
                CONTENT + " TEXT, " +
                TIME + " TEXT)";
        db.execSQL(script);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addMessage(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERID, message.getUserId());
        values.put(USERNAME, message.getUsername());
        values.put(TIME, message.getTime());
        values.put(CONTENT, message.getContent());
        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public String getMaxTime() {
        String time = "2018-01-01 00:00:00.000000";
        SQLiteDatabase db = this.getReadableDatabase();
        String statement = "SELECT TIME FROM " + TABLE_NAME + " ORDER BY " + TIME + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(statement, null);
        if (cursor != null &&  cursor.moveToFirst()) {
            time = cursor.getString(0);
            db.close();
            return time;
        } else {
            db.close();
            return time;
        }

    }

    public ArrayList<Message> getAllMessage() {

        ArrayList<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {

                String userId = cursor.getString(0);
                String userName = cursor.getString(1);
                String time = cursor.getString(3);
                String content = cursor.getString(2);
                Message message = new Message(userId,userName,content,time);
                messages.add(message);
            } while (cursor.moveToNext());
        }
        db.close();
        return messages;
    }

}