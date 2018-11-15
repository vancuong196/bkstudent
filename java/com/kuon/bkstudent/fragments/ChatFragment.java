package com.kuon.bkstudent.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Timer;
import java.util.TimerTask;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;


public class ChatFragment extends Fragment {
    MessageDb messageDb;
    ChatView chatView;
    String token;
    String userId;
    SettingManager settingManager;
    Handler handler;

    Boolean block =false;
    private static ChatFragment fragment = new ChatFragment();
    public ChatFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
      //  Bundle args = new Bundle();
      //  args.putString(ARG_PARAM1, param1);
       // args.putString(ARG_PARAM2, param2);
      //  fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingManager = SettingManager.getInstance(getActivity());
        token = settingManager.getToken();
        userId = settingManager.getUser();
        messageDb = new MessageDb(getActivity());

    }
    public void addHisotryMessage(){
        ArrayList<Message> messages = messageDb.getAllMessage();
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        for (Message message: messages){
            chatMessages.add(message.toChatMessage(message.getUserId().equals(this.userId)? ChatMessage.Type.SENT: ChatMessage.Type.RECEIVED));
        }
        chatView.addMessages(chatMessages);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatView = view.findViewById(R.id.chat_view);
        addHisotryMessage();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!block) {
                    new FetchMessageTask(token).execute();
                }
                handler.postDelayed(this,1000);
            }
        },1000);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                new PushMessageTask(token,chatMessage.getMessage()).execute();
                chatView.getInputEditText().setText("");
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat_fragment_layout, container, false);
    }


    public class FetchMessageTask extends AsyncTask<Void, Void, Boolean> {

        private final String token;
        int errorCode;
        ArrayList<Message> newMessage;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        FetchMessageTask(String token) {
            this.token = token;

        }

        @Override
        protected void onPreExecute() {
            block = true;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String time = messageDb.getMaxTime();
            try {
                newMessage = API.getNewMessage(token,time);
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
// else {
//                switch (errorCode){
//                    case API_FAILED:
//                        Toast.makeText(getActivity(),"Missing api parameter",Toast.LENGTH_SHORT).show();
//                        break;
//                    case JSON_FAILED:
//                        Toast.makeText(getActivity(),"Unknown respond from server",Toast.LENGTH_SHORT).show();
//
//                        break;
//                    case CONNECTION_FAILED:
//                        Toast.makeText(getActivity(),"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
//                        break;
//                    case LOGIN_FAILED:
//                        Toast.makeText(getActivity(),"Login using token failed",Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        Toast.makeText(getActivity(),"New message not found",Toast.LENGTH_SHORT).show();
//
//                        break;
//                }


//            }

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
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        PushMessageTask(String token,String content) {
            this.token = token;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                message = API.sendMessage(token,content);
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
                        Toast.makeText(getActivity(),"Missing api parameter",Toast.LENGTH_SHORT).show();
                        break;
                    case JSON_FAILED:
                        Toast.makeText(getActivity(),"Unknown respond from server",Toast.LENGTH_SHORT).show();

                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(getActivity(),"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        Toast.makeText(getActivity(),"Login using token failed",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getActivity(),"Cant not send message",Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        }

        @Override
        protected void onCancelled() {

        }
    }


}
