package com.kuon.bkstudent.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.api.API;
import com.kuon.bkstudent.database.MessageDb;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MessageCannotBeSentException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.exceptions.NewMessageNotFoundException;
import com.kuon.bkstudent.models.Message;
import com.kuon.bkstudent.ultils.SettingManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class ChatActivty extends AppCompatActivity {
    MessageDb messageDb;
    ChatView chatView;
    String token;
    String userId;
    SettingManager settingManager;
    Handler handler;
    String conservationId;
    Boolean block =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        conservationId = myIntent.getStringExtra("conservationId");
        setContentView(R.layout.activity_chat_activty);
        settingManager = SettingManager.getInstance(this);
        token = settingManager.getToken();
        userId = settingManager.getUser();
        messageDb = new MessageDb(this);
        chatView = findViewById(R.id.chat_view);
        addHisotryMessage();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!block) {
                    new FetchMessageTask(token, conservationId).execute();
                }
                handler.postDelayed(this,1000);
            }
        },1000);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                new PushMessageTask(token,conservationId,chatMessage.getMessage()).execute();
                chatView.getInputEditText().setText("");
                return false;
            }
        });
    }
    public void addHisotryMessage(){
        ArrayList<Message> messages = messageDb.getAllMessage(this.conservationId);
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        for (Message message: messages){
            chatMessages.add(message.toChatMessage(message.getUserId().equals(this.userId)? ChatMessage.Type.SENT: ChatMessage.Type.RECEIVED));
        }
        chatView.addMessages(chatMessages);
    }
    public class FetchMessageTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        int errorCode;
        ArrayList<Message> newMessage;
        String conservationId;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        FetchMessageTask(String token, String id) {
            this.token = token;
            this.conservationId = id;
        }

        @Override
        protected void onPreExecute() {
            block = true;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String time = messageDb.getMaxTime(this.conservationId);
            try {
                newMessage = API.getNewMessage(token,this.conservationId,time);
                System.out.print("Done ------------------------------");
                if (newMessage==null){
                    errorCode = 1;
                    return false;
                }
                else {
                    return true;
                }
            } catch (IOException e) {
                errorCode = 1;
                return false;

            } catch (JSONException e) {
                errorCode =4;
                e.printStackTrace();
                return false;

            } catch (LoginFailedException e) {
                errorCode = 2;
                return false;

            } catch (MissingApiParametersException e) {
                errorCode = 3;
                return false;
            } catch (NewMessageNotFoundException e) {
                errorCode =5;
                return false;

            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            block =false;

            if (success) {
                if (newMessage!=null&& newMessage.size()>0){
                    for (Message message:newMessage){
                        messageDb.addMessage(message);
                        if (chatView!=null){
                            chatView.addMessage(message.toChatMessage(userId.equals(message.getUserId())? ChatMessage.Type.SENT: ChatMessage.Type.RECEIVED));
                        }
                    }
                }

            }
        }

        @Override
        protected void onCancelled() {

        }
    }


    public class PushMessageTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        private final String content;
        private Message message;
        int errorCode;
        String conservationId;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        PushMessageTask(String token,String id,String content) {
            this.token = token;
            this.content = content;
            this.conservationId = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                message = API.sendMessage(token,this.conservationId,content);
                System.out.print("Done ------------------------------");
                if (message==null){
                    errorCode = 1;
                    return false;
                }
                else {
                    return true;
                }
            } catch (IOException e) {
                errorCode = 1;
                return false;

            } catch (JSONException e) {
                errorCode =4;
                e.printStackTrace();
                return false;

            } catch (LoginFailedException e) {
                errorCode = 2;
                return false;

            } catch (MissingApiParametersException e) {
                errorCode = 3;
                return false;
            }
            catch (MessageCannotBeSentException e) {
                errorCode = 5;
                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) throws NullPointerException{


            if (success) {
                if (message!=null){
                    messageDb.addMessage(message);
                    if (chatView!=null) {
                        chatView.addMessage(message.toChatMessage(ChatMessage.Type.SENT));
                    }
                }
                // TODO add show code here
            } else {
                switch (errorCode){
                    case API_FAILED:
                        Toast.makeText(ChatActivty.this,"Missing api parameter",Toast.LENGTH_SHORT).show();
                        break;
                    case JSON_FAILED:
                        Toast.makeText(getApplicationContext(),"Unknown respond from server",Toast.LENGTH_SHORT).show();

                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(getApplicationContext(),"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        Toast.makeText(getApplicationContext(),"Login using token failed",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"Cant not send message",Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        }

        @Override
        protected void onCancelled() {

        }
    }

}
