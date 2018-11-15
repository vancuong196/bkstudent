package com.kuon.bkstudent.api;
import android.util.JsonReader;

import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MessageCannotBeSentException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.exceptions.NewMessageNotFoundException;
import com.kuon.bkstudent.exceptions.NewNotificationNotFoundException;
import com.kuon.bkstudent.models.DateRecord;
import com.kuon.bkstudent.models.Message;
import com.kuon.bkstudent.models.Notification;
import com.kuon.bkstudent.models.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class API {

    private static final String BASE_URL = "http://192.168.43.177/face_recognition/api";
    private static final String RESPONSE_TAG = "response";
    private static final String TOKEN_TAG = "response";
    private static final String REASON_TAG = "response";

    public static String auth(String username, String password) throws IOException, JSONException, LoginFailedException, MissingApiParametersException {

            String apiFullUrl = BASE_URL+"/auth.php?username="+username+"&password="+password;
            JSONObject jsonResult = readJsonFromUrl(apiFullUrl);
            String response = jsonResult.getString("responde");
            if ("ok".equals(response)){

                String token = jsonResult.getString("token");
                return token;
            }
            else{
                String reason = jsonResult.getString("reason");
                if (reason!=null&& reason.contains("param")){
                    throw  new MissingApiParametersException("Missing Api Parameter");

                } else
                    {
                    throw  new LoginFailedException("Wrong password or username");

                }
            }

    }
    public static UserInfo getInfo(String token) throws IOException, JSONException, LoginFailedException, MissingApiParametersException {

        String apiFullUrl = BASE_URL+"/info.php?token="+token;
        JSONObject jsonResult = readJsonFromUrl(apiFullUrl);
        String response = jsonResult.getString("responde");
        if ("ok".equals(response)){

            String name = jsonResult.getString("name");
            String id = jsonResult.getString("id");
            int total = jsonResult.getInt("total");
            int count = jsonResult.getInt("count");
            double percent = jsonResult.getDouble("percent");
            String date = jsonResult.getString("date");
            System.out.print("Date: '"+date+"'");
            ArrayList<DateRecord> drs = new ArrayList<>();
            if (date!=null || date =="") {
                String[] dates = date.split(",");

                if (dates.length > 0) {
                    for (String a : dates
                            ) {
                        String[] as = a.split(" ");
                        drs.add(new DateRecord(as[0], as[1]));
                    }
                }
            }


            return new UserInfo(name,id,total,count,percent,drs);
        }
        else{
            String reason = jsonResult.getString("reason");
            if (reason!=null&& reason.contains("param")){
                throw  new MissingApiParametersException("Missing Api Parameter");

            } else
            {
                throw  new LoginFailedException("Wrong token");

            }
        }

    }


    public static ArrayList<Notification> getNewNotification(String token,String datetime) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, NewNotificationNotFoundException {

        String apiFullUrl = BASE_URL+"/notification.php?token="+token+"&datetime="+datetime;

        JSONArray jsonArray = readJsonArrayFromUrl(apiFullUrl);

        JSONObject jsonResult = jsonArray.getJSONObject(0);

        String response = jsonResult.getString("responde");

        if ("ok".equals(response)){

            int row = jsonResult.getInt("row");
            if (row == 0){
                throw new NewNotificationNotFoundException("no new notificaiton");
            } else {
                ArrayList<Notification> notifications = new ArrayList<>();
                for (int i = 1; i < jsonArray.length(); i++) {

                        JSONObject temp = jsonArray.getJSONObject(i);

                        String title = temp.getString("title");
                        String content = temp.getString("content");
                        String dateTime = temp.getString("date_time");
                        Notification notification = new Notification(dateTime, title, content);
                        notifications.add(notification);
                        System.out.println("add");
                }
                return notifications;
            }
        }
        else{
            String reason = jsonResult.getString("reason");
            if (reason!=null&& reason.contains("param")){
                throw  new MissingApiParametersException("Missing Api Parameter");

            } else
            {
                throw  new LoginFailedException("Wrong token");

            }
        }

    }

    public static ArrayList<Message> getNewMessage(String token, String datetime) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, NewMessageNotFoundException {

        String apiFullUrl = BASE_URL+"/chat.php?token="+token+"&datetime="+datetime;

        JSONArray jsonArray = readJsonArrayFromUrl(apiFullUrl);

        JSONObject jsonResult = jsonArray.getJSONObject(0);

        String response = jsonResult.getString("responde");

        if ("ok".equals(response)){

            int row = jsonResult.getInt("row");
            if (row == 0){
                throw new NewMessageNotFoundException("no new message");
            } else {
                ArrayList<Message> messages = new ArrayList<>();
                for (int i = 1; i < jsonArray.length(); i++) {

                    JSONObject temp = jsonArray.getJSONObject(i);

                    String userId = temp.getString("user_id");
                    String userName = temp.getString("user_name");
                    String dateTime = temp.getString("datetime");
                    String content = temp.getString("content");
                    Message message = new Message(userId,userName,content,dateTime);
                    messages.add(message);

                }
                return messages;
            }
        }
        else{
            String reason = jsonResult.getString("reason");
            if (reason!=null&& reason.contains("param")){
                throw  new MissingApiParametersException("Missing Api Parameter");

            } else
            {
                throw  new LoginFailedException("Wrong token");

            }
        }

    }


    public static Message sendMessage(String token, String content) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, MessageCannotBeSentException {

        String apiFullUrl = BASE_URL+"/pushmessage.php?token="+token+"&content="+content;

        JSONObject jsonResult = readJsonFromUrl(apiFullUrl);

        String response = jsonResult.getString("responde");

        if ("ok".equals(response)){

            String datetime = jsonResult.getString("datetime");
            String userId = jsonResult.getString("user_id");
            String userName = jsonResult.getString("user_name");

            return new Message(userId,userName,content,datetime);
        }
        else
            {
            String reason = jsonResult.getString("reason");
            if (reason!=null&& reason.contains("param")){
                throw  new MissingApiParametersException("Missing Api Parameter");

            } else if (reason!=null&& reason.contains("token"))
            {
                throw  new LoginFailedException("Wrong token");

            }
            else {
                throw new MessageCannotBeSentException("This message cant not be sent by server");
            }
        }

    }



    public static String readAll(Reader rd) throws IOException
    {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader
                        (new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                System.out.println(jsonText);

                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            JSONArray jsonArray = new JSONArray(jsonText);

            return jsonArray;
        } finally {
            is.close();
        }
    }
    }
