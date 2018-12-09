package com.kuon.bkstudent.api;
import com.kuon.bkstudent.exceptions.CanNotMakeConservationException;
import com.kuon.bkstudent.exceptions.ConservationNotFound;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MessageCannotBeSentException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.exceptions.NewMessageNotFoundException;
import com.kuon.bkstudent.exceptions.NewNotificationNotFoundException;
import com.kuon.bkstudent.models.Conservation;
import com.kuon.bkstudent.models.DateRecord;
import com.kuon.bkstudent.models.Message;
import com.kuon.bkstudent.models.Notification;
import com.kuon.bkstudent.models.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class API {

    private static String BASE_URL = "http://*/face_recognition/api";
    private static final String RESPONSE_TAG = "response";
    private static final String TOKEN_TAG = "response";
    private static final String REASON_TAG = "response";
    public static String auth(String username, String password) throws IOException, JSONException, LoginFailedException, MissingApiParametersException {

            String apiFullUrl = BASE_URL+"/auth.php?username="+username+"&password="+password;
            JSONObject jsonResult = readJsonFromUrl(apiFullUrl);
            String response = jsonResult.getString("responde");
            if ("ok".equals(response)){

                return jsonResult.getString("token");
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
    public static void setHost(String hostname){
        BASE_URL = BASE_URL.replace("*",hostname);
    }

    public static UserInfo getInfo(String token) throws IOException, JSONException, LoginFailedException, MissingApiParametersException {

        String apiFullUrl = BASE_URL+"/info.php?token="+token;
        JSONObject jsonResult = readJsonFromUrl(apiFullUrl);
        String response = jsonResult.getString("responde");
        if ("ok".equals(response)){

            String name = jsonResult.getString("name");
            String id = jsonResult.getString("id");
            String birthDay = jsonResult.getString("birthday");
            int total = jsonResult.getInt("total");
            int count = jsonResult.getInt("count");
            double percent = jsonResult.getDouble("percent");
            String date = jsonResult.getString("date");
            System.out.print("Date: '"+date+"'");
            ArrayList<DateRecord> drs = new ArrayList<>();
            if (date!=null) {
                String[] dates = date.split(",");

                if (dates.length > 0) {
                    for (String a : dates
                            ) {
                        String[] as = a.split(" ");
                        if (as.length>1) {
                            drs.add(new DateRecord(as[0], as[1]));
                        } else {
                            drs.add(new DateRecord(as[0], "NA"));

                        }
                    }
                }
            }


            return new UserInfo(name,id,birthDay,total,count,percent,drs);
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


    public static boolean changePassword(String token, String oldPassword, String newPassword) throws IOException, JSONException,MissingApiParametersException {

        String apiFullUrl = BASE_URL+"/changepassword.php?token="+token+"&newpass="+newPassword+"&oldpass="+oldPassword;
        JSONObject jsonResult = readJsonFromUrl(apiFullUrl);
        String response = jsonResult.getString("responde");
        if ("ok".equals(response)){
            return true;
        }
        else{
            String reason = jsonResult.getString("reason");
            if (reason!=null&& reason.contains("param")){
                throw  new MissingApiParametersException("Missing Api Parameter");

            } else
            {
                return false;

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


    public static ArrayList<Conservation> getConservation(String token) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, ConservationNotFound {

        String apiFullUrl = BASE_URL+"/getconservation.php?token="+token;

        JSONArray jsonArray = readJsonArrayFromUrl(apiFullUrl);

        JSONObject jsonResult = jsonArray.getJSONObject(0);

        String response = jsonResult.getString("responde");

        if ("ok".equals(response)){

            int row = jsonResult.getInt("row");
            if (row == 0){
                throw new ConservationNotFound("no new notificaiton");
            } else {
                ArrayList<Conservation> conservations = new ArrayList<>();
                for (int i = 1; i < jsonArray.length(); i++) {

                    JSONObject temp = jsonArray.getJSONObject(i);

                    String title = temp.getString("title");
                    String id = temp.getString("id");
                    String numberOfChat = temp.getString("chat_number");
                    String dateTime = temp.getString("date_time");
                    String creatorName = temp.getString("creator_name");
                    Conservation conservation = new Conservation(dateTime, creatorName,numberOfChat,title,id);
                    conservations.add(conservation);
                    System.out.println("add");
                }
                return conservations;
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

    public static ArrayList<Message> getNewMessage(String token,String conservationId, String datetime) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, NewMessageNotFoundException {

        String apiFullUrl = BASE_URL+"/chat.php?token="+token+"&datetime="+datetime+"&conservation_id="+conservationId;

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
                    Message message = new Message(userId,userName,content,dateTime,conservationId);
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


    public static boolean makeConservation(String token, String title) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, CanNotMakeConservationException {

        String apiFullUrl = BASE_URL+"/pushconservation.php?token="+token+"&title="+title;

        JSONObject jsonResult = readJsonFromUrl(apiFullUrl);

        String response = jsonResult.getString("responde");

        if ("ok".equals(response)){

            return true;
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
                throw new CanNotMakeConservationException("Can not make conservation");
            }
        }

    }

    public static Message sendMessage(String token,String conservationId,String content) throws IOException, JSONException, LoginFailedException, MissingApiParametersException, MessageCannotBeSentException {

        String apiFullUrl = BASE_URL+"/pushmessage.php?token="+token+"&content="+content+"&conservation_id="+conservationId;

        JSONObject jsonResult = readJsonFromUrl(apiFullUrl);

        String response = jsonResult.getString("responde");

        if ("ok".equals(response)){

            String datetime = jsonResult.getString("datetime");
            String userId = jsonResult.getString("user_id");
            String userName = jsonResult.getString("user_name");
            return new Message(userId,userName,content,datetime,conservationId);
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



    private static String readAll(Reader rd) throws IOException
    {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);

            return new JSONObject(jsonText);
        }
        }
    private static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);

            return new JSONArray(jsonText);
        }
    }
    }
